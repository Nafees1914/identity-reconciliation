package com.bitespeed.identity_reconciliation.controler;

import com.bitespeed.identity_reconciliation.dto.ContactRequestDTO;
import com.bitespeed.identity_reconciliation.dto.IdentityReconciliationResponseDTO;
import com.bitespeed.identity_reconciliation.service.ContactService;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/identify")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

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
