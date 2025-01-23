package bg.sava.warehouse.api.controllers;

import bg.sava.warehouse.api.models.dtos.CustomerDtos.CustomerCreateDto;
import bg.sava.warehouse.api.models.dtos.CustomerDtos.CustomerPageReadDto;
import bg.sava.warehouse.api.models.dtos.CustomerDtos.CustomerReadDto;
import bg.sava.warehouse.api.models.dtos.CustomerDtos.CustomerUpdateDto;
import bg.sava.warehouse.api.services.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
public class CustomersController {
    private final CustomerService customerService;

    @Autowired
    public CustomersController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public CustomerPageReadDto getCustomers(
            @RequestParam(required = false, defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {
        return customerService.getCustomers(pageNumber - 1, pageSize);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CustomerReadDto getCustomerById(@PathVariable @Valid UUID id) {
        return customerService.getCustomerById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerReadDto createCustomer(@RequestBody @Valid CustomerCreateDto customerCreateDto) {
        return customerService.createCustomer(customerCreateDto);
    }


    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateCustomer(@PathVariable UUID id, @RequestBody @Valid CustomerUpdateDto customerUpdateDto) {
        customerService.updateCustomer(id, customerUpdateDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomer(@PathVariable UUID id) {
        customerService.deleteCustomer(id);
    }
}
