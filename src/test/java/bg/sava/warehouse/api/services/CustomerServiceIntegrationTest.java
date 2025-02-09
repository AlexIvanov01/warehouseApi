package bg.sava.warehouse.api.services;

import bg.sava.warehouse.api.models.dtos.CustomerDtos.CustomerCreateDto;
import bg.sava.warehouse.api.models.dtos.CustomerDtos.CustomerPageReadDto;
import bg.sava.warehouse.api.models.dtos.CustomerDtos.CustomerReadDto;
import bg.sava.warehouse.api.models.dtos.CustomerDtos.CustomerUpdateDto;
import bg.sava.warehouse.api.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class CustomerServiceIntegrationTest extends BaseTest {

    @Autowired
    private CustomerService customerService;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Test
    void testGetCustomers() {
        int pageNumber = 0;
        int pageSize = 5;

        List<CustomerCreateDto> customers = getEasyRandom().objects(CustomerCreateDto.class, 5).toList();
        customers.forEach(customerService::createCustomer);

        CustomerPageReadDto customerPageReadDto = customerService.getCustomers(pageNumber, pageSize);

        assertThat(customerPageReadDto).isNotNull();
        assertThat(customerPageReadDto.getCustomers().size()).isEqualTo(5);
    }

    @Test
    void testGetCustomerById() {
        CustomerCreateDto customer = getEasyRandom().nextObject(CustomerCreateDto.class);
        CustomerReadDto createdCustomer = customerService.createCustomer(customer);

        CustomerReadDto customerById = customerService.getCustomerById(createdCustomer.getId());

        assertThat(customerById).isNotNull();
        assertThat(customerById.getId()).isEqualTo(createdCustomer.getId());
        assertThat(customerById.getName()).isEqualTo(customer.getName());
    }

    @Test
    void testCreateCustomer() {
        CustomerCreateDto customerCreateDto = getEasyRandom().nextObject(CustomerCreateDto.class);

        CustomerReadDto customerReadDto = customerService.createCustomer(customerCreateDto);

        assertThat(customerReadDto).isNotNull();
        assertThat(customerReadDto.getId()).isNotNull();
        assertThat(customerReadDto.getName()).isEqualTo(customerCreateDto.getName());
    }

    @Test
    void testUpdateCustomer() {
        CustomerCreateDto customerCreateDto = getEasyRandom().nextObject(CustomerCreateDto.class);

        CustomerReadDto savedCustomer = customerService.createCustomer(customerCreateDto);

        CustomerUpdateDto customerUpdateDto = modelMapper.map(savedCustomer, CustomerUpdateDto.class);
        customerUpdateDto.setName("Updated name");

        customerService.updateCustomer(savedCustomer.getId(), customerUpdateDto);

        CustomerReadDto updatedCustomer = customerService.getCustomerById(savedCustomer.getId());

        assertThat(updatedCustomer).isNotNull();
        assertThat(updatedCustomer.getId()).isEqualTo(savedCustomer.getId());
        assertThat(updatedCustomer.getName()).isEqualTo(customerUpdateDto.getName());
    }

    @Test
    void testDeleteCustomer() {
        CustomerCreateDto customerCreateDto = getEasyRandom().nextObject(CustomerCreateDto.class);

        CustomerReadDto savedCustomer = customerService.createCustomer(customerCreateDto);

        customerService.deleteCustomer(savedCustomer.getId());

        assertThat(customerRepository.findById(savedCustomer.getId())).isEmpty();
    }

    @Test
    void testDeleteCustomerWithInvalidId() {
        assertThatThrownBy(() -> customerService.deleteCustomer(UUID.randomUUID()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessage("404 NOT_FOUND \"Customer not found\"");
    }

    @Test
    void testGetCustomerByIdWithInvalidId() {
        assertThatThrownBy(() -> customerService.getCustomerById(UUID.randomUUID()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessage("404 NOT_FOUND \"Customer not found\"");
    }

    @Test
    void testUpdateCustomerWithInvalidId() {
        assertThatThrownBy(() -> customerService.updateCustomer(UUID.randomUUID(), getEasyRandom().nextObject(CustomerUpdateDto.class)))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessage("404 NOT_FOUND \"Customer not found\"");
    }

}