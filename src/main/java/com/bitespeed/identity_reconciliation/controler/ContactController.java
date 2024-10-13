package com.bitespeed.identity_reconciliation.controler;

import com.bitespeed.identity_reconciliation.dto.ContactRequestDTO;
import com.bitespeed.identity_reconciliation.dto.IdentityReconciliationResponseDTO;
import com.bitespeed.identity_reconciliation.service.ContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/identify")
@Tag(name = "Contact Controller", description = "API to identify or create contact based on email and phone number")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @Operation(summary = "Identify or Create Contact",
            description = "This endpoint takes an email and/or phone number and identifies or creates a contact.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully identified or created contact"),
            @ApiResponse(responseCode = "400", description = "Invalid input: Both email and phone number are missing"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<IdentityReconciliationResponseDTO> identifyContact(@RequestBody ContactRequestDTO contactRequestDTO) {
        String email = contactRequestDTO.getEmail();
        String phoneNumber = contactRequestDTO.getPhoneNumber();

        if (StringUtils.isEmpty(email) && StringUtils.isEmpty(phoneNumber)) {
            throw new IllegalArgumentException("Both email and phoneNumber cannot be null or empty.");
        }

        IdentityReconciliationResponseDTO response = contactService.identifyOrCreateContact(email, phoneNumber);
        return ResponseEntity.ok(response);
    }
}
