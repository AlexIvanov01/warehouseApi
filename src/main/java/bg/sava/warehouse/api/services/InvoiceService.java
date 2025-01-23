package bg.sava.warehouse.api.services;

import bg.sava.warehouse.api.models.Invoice;
import bg.sava.warehouse.api.models.Order;
import bg.sava.warehouse.api.models.dtos.InvocieDtos.InvoiceReadDto;
import bg.sava.warehouse.api.repository.InvoiceRepository;
import bg.sava.warehouse.api.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final OrderRepository orderRepository;
    private final ModelMapper mapper;

    public InvoiceService(InvoiceRepository invoiceRepository, OrderRepository orderRepository, ModelMapper mapper) {
        this.invoiceRepository = invoiceRepository;
        this.orderRepository = orderRepository;
        this.mapper = mapper;
    }

    public List<InvoiceReadDto> getAllInvoices() {
        return invoiceRepository.findAll()
                .stream()
                .map(invoice -> mapper.map(invoice, InvoiceReadDto.class))
                .toList();
    }

    public InvoiceReadDto getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found"));
        return mapper.map(invoice, InvoiceReadDto.class);
    }

    @Transactional
    public void deleteInvoice(long id) {
        if (!invoiceRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found");
        }
        invoiceRepository.deleteById(id);
    }

    public InvoiceReadDto generateInvoice(UUID orderId) {
        if (invoiceRepository.existsByOrderId(orderId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Invoice already exists for this order");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        Invoice invoice = Invoice.builder()
                .invoiceDate(LocalDate.now())
                .order(order)
                .totalAmount(calculateTotalAmount(order))
                .invoiceStatus("GENERATED")
                .build();

        Invoice savedInvoice = invoiceRepository.save(invoice);
        return mapper.map(savedInvoice, InvoiceReadDto.class);
    }

    private Double calculateTotalAmount(Order order) {
        return order.getBatches().stream()
                .mapToDouble(batch -> batch.getBatch().getPurchasePrice() * batch.getCount())
                .sum();
    }
}
