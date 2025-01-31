package bg.sava.warehouse.api.controllers;

import bg.sava.warehouse.api.models.dtos.InvocieDtos.InvoiceReadDto;
import bg.sava.warehouse.api.services.InvoiceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PostMapping("/{orderId}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public InvoiceReadDto createInvoice(@PathVariable @Valid UUID orderId) {
        return invoiceService.generateInvoice(orderId);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteInvoice(@PathVariable @Valid Long id) {
        invoiceService.deleteInvoice(id);
    }
}
