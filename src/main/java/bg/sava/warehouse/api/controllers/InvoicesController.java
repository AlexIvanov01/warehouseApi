package bg.sava.warehouse.api.controllers;

import bg.sava.warehouse.api.models.dtos.InvocieDtos.InvoiceReadDto;
import bg.sava.warehouse.api.services.InvoiceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/invoices")
public class InvoicesController {
    private final InvoiceService invoiceService;

    @Autowired
    public InvoicesController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InvoiceReadDto> getInvoices(){
        return invoiceService.getAllInvoices();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public InvoiceReadDto getInvoiceById(@PathVariable @Valid Long id) {
        return invoiceService.getInvoiceById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InvoiceReadDto createInvoice(@RequestParam @Valid UUID orderId) {
        return invoiceService.generateInvoice(orderId);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteInvoice(@PathVariable @Valid Long id) {
        invoiceService.deleteInvoice(id);
    }
}
