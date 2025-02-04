package bg.sava.warehouse.api.controllers;

import bg.sava.warehouse.api.models.dtos.CustomerDtos.CustomerCreateDto;
import bg.sava.warehouse.api.models.dtos.CustomerDtos.CustomerPageReadDto;
import bg.sava.warehouse.api.models.dtos.CustomerDtos.CustomerReadDto;
import bg.sava.warehouse.api.models.dtos.CustomerDtos.CustomerUpdateDto;
import bg.sava.warehouse.api.services.CustomerService;
import bg.sava.warehouse.api.services.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.springframework.web.server.ResponseStatusException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectWriter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CustomersController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CustomersControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    JwtService jwtService;

    @MockitoBean
    CustomerService customerService;

    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    ModelMapper modelMapper = new ModelMapper();

    List<CustomerReadDto> customers = new ArrayList<>();

    EasyRandom easyRandom;

    @BeforeEach
    void setUp() {
        EasyRandomParameters parameters = new EasyRandomParameters()
                .objectPoolSize(100)
                .randomizationDepth(3)
                .stringLengthRange(5, 50)
                .dateRange(LocalDate.of(2023, 1, 1), LocalDate.of(2024, 12, 31))
                .collectionSizeRange(1, 10);
        easyRandom = new EasyRandom(parameters);

        customers = easyRandom.objects(CustomerReadDto.class, 10).collect(Collectors.toList());
    }

    @Test
    void shouldFindAllCustomers() throws Exception {
        CustomerPageReadDto customerPageReadDto = new CustomerPageReadDto(customers, 1);

        when(customerService.getCustomers(0, 10)).thenReturn(customerPageReadDto);

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(content().json(ow.writeValueAsString(customerPageReadDto)));

        verify(customerService, times(1)).getCustomers(0, 10);
    }

    @Test
    void shouldFindCustomerById() throws Exception {
        CustomerReadDto customer = customers.getFirst();

        when(customerService.getCustomerById(customer.getId())).thenReturn(customer);

        mockMvc.perform(get("/api/customers/" + customer.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(ow.writeValueAsString(customer)));

        verify(customerService, times(1)).getCustomerById(customer.getId());
    }

    @Test
    void shouldNotFindCustomerById() throws Exception {
        UUID id = UUID.randomUUID();

        when(customerService.getCustomerById(id)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));

        mockMvc.perform(get("/api/customers/" + id))
                .andExpect(status().isNotFound());

        verify(customerService, times(1)).getCustomerById(id);
    }

    @Test
    void shouldCreateCustomer() throws Exception {
        CustomerCreateDto customerCreateDto = easyRandom.nextObject(CustomerCreateDto.class);

        CustomerReadDto customerReadDto = modelMapper.map(customerCreateDto, CustomerReadDto.class);

        when(customerService.createCustomer(any(CustomerCreateDto.class))).thenReturn(customerReadDto);

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ow.writeValueAsString(customerCreateDto)))
                .andExpect(status().isCreated())
                .andExpect(content().json(ow.writeValueAsString(customerReadDto)));

        ArgumentCaptor<CustomerCreateDto> customerCreateDtoArgumentCaptor = ArgumentCaptor.forClass(CustomerCreateDto.class);
        verify(customerService, times(1)).createCustomer(customerCreateDtoArgumentCaptor.capture());
        assertThat(customerCreateDtoArgumentCaptor.getValue().getName()).isEqualTo(customerCreateDto.getName());
    }

    @Test
    void shouldNotCreateCustomerWhenCustomerIsInvalid() throws Exception {
        CustomerCreateDto customerCreateDto = easyRandom.nextObject(CustomerCreateDto.class);

        customerCreateDto.setName(null);

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ow.writeValueAsString(customerCreateDto)))
                .andExpect(status().isBadRequest());

        verify(customerService, never()).createCustomer(any(CustomerCreateDto.class));
    }

    @Test
    void shouldUpdateCustomer() throws Exception {
        CustomerReadDto updatedCustomer = easyRandom.nextObject(CustomerReadDto.class);
        updatedCustomer.setId(customers.getFirst().getId());

        CustomerUpdateDto customerUpdateDto = modelMapper.map(updatedCustomer, CustomerUpdateDto.class);

        doNothing().when(customerService).updateCustomer(eq(updatedCustomer.getId()), any(CustomerUpdateDto.class));

        mockMvc.perform(put("/api/customers/" + updatedCustomer.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ow.writeValueAsString(customerUpdateDto)))
                .andExpect(status().isOk());

        verify(customerService, times(1)).updateCustomer(eq(updatedCustomer.getId()), any(CustomerUpdateDto.class));
    }

    @Test
    void shouldDeleteCustomer() throws Exception {
        CustomerReadDto customer = customers.getFirst();

        doNothing().when(customerService).deleteCustomer(customer.getId());

        mockMvc.perform(delete("/api/customers/" + customer.getId()))
                .andExpect(status().isNoContent());

        verify(customerService, times(1)).deleteCustomer(customer.getId());
    }
}