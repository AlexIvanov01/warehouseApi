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
import bg.sava.warehouse.api.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository ordersRepository;
    private final ModelMapper orderMapper;
    private final CustomerRepository customerRepository;
    private final BatchRepository batchRepository;

    @Autowired
    public OrderService(OrderRepository ordersRepository, ModelMapper orderMapper, CustomerRepository customerRepository, BatchRepository batchRepository) {
        this.ordersRepository = ordersRepository;
        this.orderMapper = orderMapper;
        this.customerRepository = customerRepository;
        this.batchRepository = batchRepository;
    }

    public OrderReadDto createOrder(OrderCreateDto orderCreateDto){

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
        Page<Order> orders = ordersRepository.findAll(page);

        List<OrderReadDto> dtos = new ArrayList<>();

        for(Order order : orders.getContent()) {
            OrderReadDto dto = orderMapper.map(order, OrderReadDto.class);
            dto.setBatches(order.getBatches().stream()
                    .collect(Collectors.toMap(batch -> batch.getBatch().getId(), OrderBatch::getCount)));
            dtos.add(dto);
        }
        
        int pageCount = orders.getTotalPages();


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

    @Transactional
    public void deleteOrder(UUID id) {
        Optional<Order> order = ordersRepository.findById(id);
        if (order.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        }
        if(order.get().getInvoice() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Order has invoice and cannot be deleted");
        }

        for (OrderBatch orderBatch : order.get().getBatches()) {
            int updatedQuantity = orderBatch.getBatch().getQuantity() + orderBatch.getCount();
            batchRepository.updateQuantityById(orderBatch.getBatch().getId(), updatedQuantity);
        }
        ordersRepository.delete(order.get());
    }
}
