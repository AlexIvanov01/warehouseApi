package bg.sava.warehouse.api.controllers;

import bg.sava.warehouse.api.models.dtos.InvocieDtos.InvoiceReadDto;
import bg.sava.warehouse.api.services.InvoiceService;
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
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = InvoicesController.class)
@AutoConfigureMockMvc(addFilters = false)
public class InvoicesControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    JwtService jwtService;

    @MockitoBean
    InvoiceService invoiceService;

    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    ModelMapper modelMapper = new ModelMapper();

    List<InvoiceReadDto> invoices = new ArrayList<>();

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

        invoices = easyRandom.objects(InvoiceReadDto.class, 10).collect(Collectors.toList());
    }

    @Test
    void shouldFindAllInvoices() throws Exception {
        when(invoiceService.getAllInvoices()).thenReturn(invoices);

        mockMvc.perform(get("/api/invoices"))
                .andExpect(status().isOk());

        verify(invoiceService, times(1)).getAllInvoices();
    }

    @Test
    void shouldFindInvoiceById() throws Exception {
        InvoiceReadDto invoice = invoices.getFirst();

        when(invoiceService.getInvoiceById(invoice.getId())).thenReturn(invoice);

        Locale.setDefault(Locale.US);
        String json = """
        {
            "id": %d,
            "invoiceDate": "%s",
            "orderId": "%s",
            "invoiceStatus": "%s",
            "totalAmount": %.17f
        }
        """.formatted(
                invoice.getId(),
                invoice.getInvoiceDate().toString(),
                invoice.getOrderId().toString(),
                invoice.getInvoiceStatus(),
                invoice.getTotalAmount()
        );

        mockMvc.perform(get("/api/invoices/" + invoice.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(json));

        verify(invoiceService, times(1)).getInvoiceById(invoice.getId());
    }

    @Test
    void shouldNotFindInvoiceById() throws Exception {
        long id = 1L;

        when(invoiceService.getInvoiceById(id)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found"));

        mockMvc.perform(get("/api/invoices/" + id))
                .andExpect(status().isNotFound());

        verify(invoiceService, times(1)).getInvoiceById(id);
    }

    @Test
    void shouldCreateInvoice() throws Exception {
        UUID orderId = UUID.randomUUID();
        InvoiceReadDto invoice = easyRandom.nextObject(InvoiceReadDto.class);

        when(invoiceService.generateInvoice(orderId)).thenReturn(invoice);

        Locale.setDefault(Locale.US);
        String json = """
        {
            "id": %d,
            "invoiceDate": "%s",
            "orderId": "%s",
            "invoiceStatus": "%s",
            "totalAmount": %.17f
        }
        """.formatted(
                invoice.getId(),
                invoice.getInvoiceDate().toString(),
                invoice.getOrderId().toString(),
                invoice.getInvoiceStatus(),
                invoice.getTotalAmount()
        );

        mockMvc.perform(post("/api/invoices/" + orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(json));

        ArgumentCaptor<UUID> orderIdArgumentCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(invoiceService, times(1)).generateInvoice(orderIdArgumentCaptor.capture());
        assertThat(orderIdArgumentCaptor.getValue()).isEqualTo(orderId);
    }

    @Test
    void shouldDeleteInvoice() throws Exception {
        long id = invoices.getFirst().getId();

        doNothing().when(invoiceService).deleteInvoice(id);

        mockMvc.perform(delete("/api/invoices/" + id))
                .andExpect(status().isNoContent());

        verify(invoiceService, times(1)).deleteInvoice(id);
    }
}