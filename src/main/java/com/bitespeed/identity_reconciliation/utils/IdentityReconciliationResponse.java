package com.bitespeed.identity_reconciliation.utils;

import com.bitespeed.identity_reconciliation.dto.AllContactsDTO;
import com.bitespeed.identity_reconciliation.dto.IdentityReconciliationResponseDTO;
import com.bitespeed.identity_reconciliation.model.Contact;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class IdentityReconciliationResponse {

    /*
        Prepare the response for the contact
        And collect all unique emails and phone numbers, and secondary contact IDs
     */
    public IdentityReconciliationResponseDTO prepareResponse(Contact primaryContact, Set<String> emailSet, Set<String> phoneSet, Set<Integer> secondaryIds) {
        IdentityReconciliationResponseDTO responseDTO = new IdentityReconciliationResponseDTO();
        AllContactsDTO contacts = AllContactsDTO.builder()
                .primaryContactId(primaryContact.getId())
                .emails(emailSet)
                .phoneNumbers(phoneSet)
                .secondaryContactIds(secondaryIds)
                .build();
        responseDTO.setContact(contacts);
        return responseDTO;
    }
}
