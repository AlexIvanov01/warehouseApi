package bg.sava.warehouse.api.services;

import bg.sava.warehouse.api.models.Batch;
import bg.sava.warehouse.api.models.Customer;
import bg.sava.warehouse.api.models.Order;
import bg.sava.warehouse.api.models.OrderBatch;
import bg.sava.warehouse.api.models.dtos.OrderDtos.OrderCreateDto;
import bg.sava.warehouse.api.models.dtos.OrderDtos.OrderPageReadDto;
import bg.sava.warehouse.api.models.dtos.OrderDtos.OrderReadDto;
import bg.sava.warehouse.api.repository.BatchRepository;
import bg.sava.warehouse.api.repository.CustomerRepository;
import bg.sava.warehouse.api.repository.OrderBatchRepository;
import bg.sava.warehouse.api.repository.OrdersRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrdersRepository ordersRepository;
    private final ModelMapper orderMapper;
    private final CustomerRepository customerRepository;
    private final BatchRepository batchRepository;
    private final OrderBatchRepository orderBatchRepository;

    @Autowired
    public OrderService(OrdersRepository ordersRepository, ModelMapper orderMapper, CustomerRepository customerRepository, BatchRepository batchRepository, OrderBatchRepository orderBatchRepository) {
        this.ordersRepository = ordersRepository;
        this.orderMapper = orderMapper;
        this.customerRepository = customerRepository;
        this.batchRepository = batchRepository;
        this.orderBatchRepository = orderBatchRepository;
    }

    public OrderReadDto createOrder(OrderCreateDto orderCreateDto) {

        Order order = orderMapper.map(orderCreateDto, Order.class);
        order.setId(null);
        Optional<Customer> customer = customerRepository.findById(orderCreateDto.getCustomerId());
        if (customer.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
        }
        order.setCustomer(customer.get());
        List<Batch> batches = new ArrayList<>();

        for (Map.Entry<UUID, Integer> batchEntry : orderCreateDto.getBatchesMap().entrySet()) {
            UUID batchId = batchEntry.getKey();
            int count = batchEntry.getValue();

            Optional<Batch> batch = batchRepository.findById(batchId);
            if (batch.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Batch with ID " + batchId + " not found");
            }
            
            batches.add(batch.get());
            order.addBatch(batch.get(), count);
        }
        ordersRepository.save(order);
        orderBatchRepository.saveAll(order.getBatches());
        
        for (Batch batch : batches) {
            Integer newQuantity = batch.getQuantity() - orderCreateDto.getBatchesMap().get(batch.getId());
            batchRepository.updateQuantityById(batch.getId(), newQuantity);
        }
        OrderReadDto dto = orderMapper.map(order, OrderReadDto.class);
        dto.setBatches(orderCreateDto.getBatchesMap());
        
        return dto;
    }

    public OrderPageReadDto getOrders(int pageNumber, int pageSize) {
        Pageable page = PageRequest.of(pageNumber, pageSize);
        List<Order> orders = ordersRepository.findAll(page).getContent();

        List<OrderReadDto> dtos = new ArrayList<>();

        for(Order order : orders) {
            OrderReadDto dto = orderMapper.map(order, OrderReadDto.class);
            dto.setBatches(order.getBatches().stream()
                    .collect(Collectors.toMap(batch -> batch.getBatch().getId(), OrderBatch::getCount)));
            dtos.add(dto);
        }
        
        int pageCount = (int) Math.ceil(ordersRepository.count() / (float) pageSize);


        OrderPageReadDto orderPageReadDto = new OrderPageReadDto();
        orderPageReadDto.setOrders(dtos);
        orderPageReadDto.setTotalPages(pageCount);

        return orderPageReadDto;
    }

    public OrderReadDto getOrderById(UUID id) {
        Optional<Order> order = ordersRepository.findById(id);
        if (order.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        }
        OrderReadDto dto = orderMapper.map(order.get(), OrderReadDto.class);
        dto.setBatches(order.get().getBatches().stream()
                .collect(Collectors.toMap(batch -> batch.getBatch().getId(), OrderBatch::getCount)));

        return dto;
    }

    public void deleteOrder(UUID id) {
        if (ordersRepository.existsById(id)) {
            ordersRepository.deleteById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        }
    }
}
