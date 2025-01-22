package bg.sava.warehouse.api.services;

import bg.sava.warehouse.api.models.Customer;
import bg.sava.warehouse.api.models.dtos.CustomerDtos.CustomerCreateDto;
import bg.sava.warehouse.api.models.dtos.CustomerDtos.CustomerPageReadDto;
import bg.sava.warehouse.api.models.dtos.CustomerDtos.CustomerReadDto;
import bg.sava.warehouse.api.models.dtos.CustomerDtos.CustomerUpdateDto;
import bg.sava.warehouse.api.repository.CustomerRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final ModelMapper mapper;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, ModelMapper mapper) {
        this.customerRepository = customerRepository;
        this.mapper = mapper;
    }

    public CustomerPageReadDto getCustomers(int pageNumber, int pageSize) {
        Pageable page = PageRequest.of(pageNumber, pageSize);
        List<Customer> CustomerItems = customerRepository.findAll(page).getContent();
        Type listType = new TypeToken<List<CustomerReadDto>>() {}.getType();

        List<CustomerReadDto> dtos = mapper.map(CustomerItems, listType);

        int pageCount = (int) Math.ceil(customerRepository.count() / (float) pageSize);

        CustomerPageReadDto CustomerPageReadDto = new CustomerPageReadDto();
        CustomerPageReadDto.setCustomers(dtos);
        CustomerPageReadDto.setTotalPages(pageCount);

        return CustomerPageReadDto;
    }

    public CustomerReadDto getCustomerById(UUID id) {
        Optional<Customer> CustomerItem = customerRepository.findById(id);
        if (CustomerItem.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
        }
        return mapper.map(CustomerItem.get(), CustomerReadDto.class);
    }

    public CustomerReadDto createCustomer(CustomerCreateDto CustomerCreateDto) {
        Customer Customer = mapper.map(CustomerCreateDto, Customer.class);
        customerRepository.save(Customer);
        return mapper.map(Customer, CustomerReadDto.class);
    }

    public void updateCustomer(UUID id, CustomerUpdateDto CustomerUpdateDto) {
        Optional<Customer> CustomerItem = customerRepository.findById(id);
        if (CustomerItem.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
        }
        Customer Customer = mapper.map(CustomerUpdateDto, Customer.class);
        Customer.setId(id);
        customerRepository.save(Customer);
    }

    public void deleteCustomer(UUID id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id); // Delete by ID
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
        }
    }
}
