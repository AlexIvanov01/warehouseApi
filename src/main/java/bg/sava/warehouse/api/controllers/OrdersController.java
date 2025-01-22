package bg.sava.warehouse.api.controllers;

import bg.sava.warehouse.api.models.dtos.OrderDtos.OrderCreateDto;
import bg.sava.warehouse.api.models.dtos.OrderDtos.OrderPageReadDto;
import bg.sava.warehouse.api.models.dtos.OrderDtos.OrderReadDto;
import bg.sava.warehouse.api.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrdersController {

    private final OrderService orderService;

    @Autowired
    public OrdersController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderReadDto createOrder(@RequestBody OrderCreateDto orderCreateDto) {
        return orderService.createOrder(orderCreateDto);
    }

    @GetMapping("/{id}")
    public OrderReadDto getOrderById(@PathVariable UUID id) {
        return orderService.getOrderById(id);
    }

    @GetMapping
    public OrderPageReadDto getOrders( @RequestParam(required = false, defaultValue = "0") int pageNumber,
                                       @RequestParam(defaultValue = "10") int pageSize) {
        return orderService.getOrders(pageNumber, pageSize);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable UUID id) {
        orderService.deleteOrder(id);
    }
}
