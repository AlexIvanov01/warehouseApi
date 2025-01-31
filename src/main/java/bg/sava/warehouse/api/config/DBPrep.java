package bg.sava.warehouse.api.config;

import bg.sava.warehouse.api.models.*;
import bg.sava.warehouse.api.models.dtos.UserDto.UserCreateDto;
import bg.sava.warehouse.api.repository.*;
import bg.sava.warehouse.api.services.AuthenticationService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class DBPrep implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DBPrep.class);

    private final ProductRepository productRepository;
    private final BatchRepository batchRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final InvoiceRepository invoiceRepository;
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    @Autowired
    public DBPrep(ProductRepository productRepository, BatchRepository batchRepository, CustomerRepository customerRepository, OrderRepository orderRepository, InvoiceRepository invoiceRepository, UserRepository userRepository, AuthenticationService authenticationService) {
        this.productRepository = productRepository;
        this.batchRepository = batchRepository;
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.invoiceRepository = invoiceRepository;
        this.authenticationService = authenticationService;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void run(String... args){

        if(userRepository.count() == 0){
            new UserCreateDto();
            UserCreateDto user = UserCreateDto.builder()
                    .username("admin")
                    .password("admin")
                    .role(Role.ADMIN)
                    .build();
            authenticationService.register(user);
        }

        if (productRepository.count() == 0) {
            logger.info("Seeding process started");
            // Create dummy products
            Product product1 = Product.builder()
                    .name("Product 1")
                    .description("Description for Product 1")
                    .brand("Brand A")
                    .supplier("Supplier X")
                    .imageURL("http://example.com/product1.jpg")
                    .status("AVAILABLE")
                    .barcode("1234567890123")
                    .reorderLevel(10)
                    .weight(1.5f)
                    .build();

            Product product2 = Product.builder()
                    .name("Product 2")
                    .description("Description for Product 2")
                    .brand("Brand B")
                    .supplier("Supplier Y")
                    .imageURL("http://example.com/product2.jpg")
                    .status("OUT_OF_STOCK")
                    .barcode("9876543210987")
                    .reorderLevel(5)
                    .weight(2.3f)
                    .build();

            productRepository.saveAll(List.of(product1, product2));

            // Create dummy batches
            Batch batch1 = Batch.builder()
                    .product(product1)
                    .quantity(100)
                    .purchasePrice(8.0)
                    .sellPrice(12.0)
                    .lot("LOT001")
                    .expirationDate(LocalDate.now().plusMonths(6))
                    .orders(new ArrayList<>())
                    .build();

            Batch batch2 = Batch.builder()
                    .product(product2)
                    .quantity(200)
                    .purchasePrice(15.0)
                    .sellPrice(20.0)
                    .lot("LOT002")
                    .expirationDate(LocalDate.now().plusMonths(12))
                    .orders(new ArrayList<>())
                    .build();

            Batch batch3 = Batch.builder()
                    .product(product1)
                    .quantity(150)
                    .purchasePrice(7.5)
                    .sellPrice(11.0)
                    .lot("LOT003")
                    .expirationDate(LocalDate.now().plusMonths(8))
                    .orders(new ArrayList<>())
                    .build();

            Batch batch4 = Batch.builder()
                    .product(product2)
                    .quantity(250)
                    .purchasePrice(14.0)
                    .sellPrice(18.0)
                    .lot("LOT004")
                    .expirationDate(LocalDate.now().plusMonths(14))
                    .orders(new ArrayList<>())
                    .build();

            Batch batch5 = Batch.builder()
                    .product(product1)
                    .quantity(120)
                    .purchasePrice(9.0)
                    .sellPrice(13.0)
                    .lot("LOT005")
                    .expirationDate(LocalDate.now().plusMonths(9))
                    .orders(new ArrayList<>())
                    .build();

            Batch batch6 = Batch.builder()
                    .product(product2)
                    .quantity(180)
                    .purchasePrice(16.0)
                    .sellPrice(22.0)
                    .lot("LOT006")
                    .expirationDate(LocalDate.now().plusMonths(10))
                    .orders(new ArrayList<>())
                    .build();

            batchRepository.saveAll(List.of(batch1, batch2, batch3, batch4, batch5, batch6));

            // Create dummy customers
            Customer customer1 = Customer.builder()
                    .name("John Doe")
                    .email("john.doe@example.com")
                    .phoneNumber("1234567890")
                    .address("123 Street, Cityville")
                    .build();

            Customer customer2 = Customer.builder()
                    .name("Jane Smith")
                    .email("jane.smith@example.com")
                    .phoneNumber("9876543210")
                    .address("456 Avenue, Townsville")
                    .build();

            Customer customer3 = Customer.builder()
                    .name("Mike Johnson")
                    .email("mike.johnson@example.com")
                    .phoneNumber("5551234567")
                    .address("789 Boulevard, Villageton")
                    .build();

            Customer customer4 = Customer.builder()
                    .name("Emily Davis")
                    .email("emily.davis@example.com")
                    .phoneNumber("1112223333")
                    .address("321 Road, Countryside")
                    .build();

            customerRepository.saveAll(List.of(customer1, customer2, customer3, customer4));

            // Create dummy orders with batches
            Order order1 = Order.builder()
                    .orderDate(LocalDate.now())
                    .shippedDate(LocalDate.now().plusDays(3))
                    .shippingAddress("123 Street")
                    .customer(customer1)
                    .status("SHIPPED")
                    .batches(new ArrayList<>())
                    .build();
            order1.addBatch(batch1, 10);
            order1.addBatch(batch2, 5);

            Order order2 = Order.builder()
                    .orderDate(LocalDate.now().minusDays(2))
                    .shippedDate(LocalDate.now().plusDays(2))
                    .shippingAddress("456 Avenue")
                    .customer(customer2)
                    .status("SHIPPED")
                    .batches(new ArrayList<>())
                    .build();
            order2.addBatch(batch3, 15);
            order2.addBatch(batch4, 10);

            Order order3 = Order.builder()
                    .orderDate(LocalDate.now().minusDays(5))
                    .shippedDate(LocalDate.now().plusDays(1))
                    .shippingAddress("789 Boulevard")
                    .customer(customer3)
                    .status("PROCESSING")
                    .batches(new ArrayList<>())
                    .build();
            order3.addBatch(batch5, 20);
            order3.addBatch(batch6, 30);

            Order order4 = Order.builder()
                    .orderDate(LocalDate.now().minusDays(10))
                    .shippedDate(LocalDate.now().plusDays(5))
                    .shippingAddress("321 Road")
                    .customer(customer4)
                    .status("DELIVERED")
                    .batches(new ArrayList<>())
                    .build();
            order4.addBatch(batch1, 25);
            order4.addBatch(batch4, 12);

            orderRepository.saveAll(List.of(order1, order2, order3, order4));

            // Create dummy invoices for 2 orders
            Invoice invoice1 = Invoice.builder()
                    .invoiceDate(LocalDate.now())
                    .order(order1)
                    .invoiceStatus("PAID")
                    .totalAmount(300.0)
                    .build();

            Invoice invoice2 = Invoice.builder()
                    .invoiceDate(LocalDate.now().minusDays(1))
                    .order(order2)
                    .invoiceStatus("UNPAID")
                    .totalAmount(200.0)
                    .build();

            invoiceRepository.saveAll(List.of(invoice1, invoice2));

            logger.info("Seeding process completed");
        }
        else {
            logger.info("Seeding process skipped");
        }
    }
}
