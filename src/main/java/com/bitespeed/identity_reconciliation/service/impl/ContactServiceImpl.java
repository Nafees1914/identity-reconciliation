package com.bitespeed.identity_reconciliation.service.impl;

import com.bitespeed.identity_reconciliation.dto.IdentityReconciliationResponseDTO;
import com.bitespeed.identity_reconciliation.model.Contact;
import com.bitespeed.identity_reconciliation.repository.ContactRepository;
import com.bitespeed.identity_reconciliation.service.ContactService;
import com.bitespeed.identity_reconciliation.utils.IdentityReconciliationResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;
    private final IdentityReconciliationResponse identityReconciliationResponse;

    @Autowired
    public ContactServiceImpl(ContactRepository contactRepository, IdentityReconciliationResponse identityReconciliationResponse) {
        this.contactRepository = contactRepository;
        this.identityReconciliationResponse = identityReconciliationResponse;
    }

    /**
     * Identify or create a contact and prepare the response
     * Handle case when no existing contacts are found
     * Retrieve the primary contact from existing contacts
     * Check if the incoming email or phone number exists in existing contacts
     * Handle cases for multiple primary contacts
     * Handle creation of secondary contacts based on email/phone presence
     * Collect secondary contact information
     * then call to prepare response
     */
    public IdentityReconciliationResponseDTO identifyOrCreateContact(String email, String phoneNumber) {
        List<Contact> existingContacts = contactRepository.findByEmailOrPhoneNumber(
                StringUtils.hasText(email) ? email : "",
                StringUtils.hasText(phoneNumber) ? phoneNumber : ""
        );

        Set<String> emailSet = new HashSet<>();
        Set<String> phoneSet = new HashSet<>();
        Set<Integer> secondaryIds = new HashSet<>();

        if (existingContacts.isEmpty()) {
            return handleNewContactCreation(email, phoneNumber, emailSet, phoneSet, secondaryIds);
        }

        Contact primaryContact = getPrimaryContact(existingContacts);
        populatePrimaryContactDetails(primaryContact, emailSet, phoneSet);

        boolean emailExists = checkEmailExists(email, existingContacts);
        boolean phoneNumberExists = checkPhoneNumberExists(phoneNumber, existingContacts);

        handleMultiplePrimaryContacts(existingContacts);
        handleSecondaryContactCreation(primaryContact, email, phoneNumber, emailExists, phoneNumberExists, emailSet, phoneSet);
        collectSecondaryContactInfo(primaryContact, emailSet, phoneSet, secondaryIds);

        return identityReconciliationResponse.prepareResponse(primaryContact, emailSet, phoneSet, secondaryIds);
    }

    /**
     * Handle the creation of a new contact when no existing contacts are found.
     */
    private IdentityReconciliationResponseDTO handleNewContactCreation(String email, String phoneNumber, Set<String> emailSet, Set<String> phoneSet, Set<Integer> secondaryIds) {
        Contact primaryContact = createNewContact(email, phoneNumber);
        if (StringUtils.hasText(email)) {
            emailSet.add(email);
        }
        if (StringUtils.hasText(phoneNumber)) {
            phoneSet.add(phoneNumber);
        }
        return identityReconciliationResponse.prepareResponse(primaryContact, emailSet, phoneSet, secondaryIds);
    }

    /**
     * Populate the email and phone number sets from the primary contact.
     */
    private void populatePrimaryContactDetails(Contact primaryContact, Set<String> emailSet, Set<String> phoneSet) {
        if (StringUtils.hasText(primaryContact.getEmail())) {
            emailSet.add(primaryContact.getEmail());
        }
        if (StringUtils.hasText(primaryContact.getPhoneNumber())) {
            phoneSet.add(primaryContact.getPhoneNumber());
        }
    }

    /**
     * Check if the incoming email exists among existing contacts.
     */
    private boolean checkEmailExists(String email, List<Contact> existingContacts) {
        return StringUtils.isEmpty(email) || existingContacts.stream()
                .anyMatch(c -> !StringUtils.isEmpty(c.getEmail()) && c.getEmail().equalsIgnoreCase(email));
    }

    /**
     * Check if the incoming phone number exists among existing contacts.
     */
    private boolean checkPhoneNumberExists(String phoneNumber, List<Contact> existingContacts) {
        return StringUtils.isEmpty(phoneNumber) || existingContacts.stream()
                .anyMatch(c -> !StringUtils.isEmpty(c.getPhoneNumber()) && c.getPhoneNumber().equals(phoneNumber));
    }

    /**
     * Handle the scenario where multiple primary contacts are found.
     * Demote newerPrimary to secondary and update all its secondaries
     */
    private void handleMultiplePrimaryContacts(List<Contact> existingContacts) {
        List<Contact> primaryContacts = existingContacts.stream()
                .filter(c -> c.getLinkPrecedence() == Contact.LinkPrecedence.PRIMARY)
                .collect(Collectors.toList());

        if (primaryContacts.size() > 1) {
            Contact olderPrimary = primaryContacts.get(0);
            Contact newerPrimary = primaryContacts.get(1);

            if (newerPrimary.getCreatedAt().isBefore(olderPrimary.getCreatedAt())) {
                Contact temp = olderPrimary;
                olderPrimary = newerPrimary;
                newerPrimary = temp;
            }
            demotePrimaryToSecondary(newerPrimary, olderPrimary);
        }
    }

    /**
     * Demote the newer primary to secondary and re-link all secondaries.
     */
    private void demotePrimaryToSecondary(Contact newerPrimary, Contact olderPrimary) {
        newerPrimary.setLinkPrecedence(Contact.LinkPrecedence.SECONDARY);
        newerPrimary.setLinkedId(olderPrimary.getId());
        newerPrimary.setUpdatedAt(LocalDateTime.now());
        contactRepository.save(newerPrimary);

        List<Contact> secondaryContacts = contactRepository.findByLinkedId(newerPrimary.getId());
        for (Contact secondary : secondaryContacts) {
            secondary.setLinkedId(olderPrimary.getId());
            secondary.setUpdatedAt(LocalDateTime.now());
        }
        contactRepository.saveAll(secondaryContacts);
    }

    /**
     * Handle the creation of secondary contacts based on the presence of email or phone number.
     */
    private void handleSecondaryContactCreation(Contact primaryContact, String email, String phoneNumber, boolean emailExists, boolean phoneNumberExists, Set<String> emailSet, Set<String> phoneSet) {
        if (!StringUtils.isEmpty(phoneNumber) && emailExists && !phoneNumberExists) {
            createSecondaryContact(primaryContact, email, phoneNumber);
            if (!StringUtils.isEmpty(phoneNumber)) {
                phoneSet.add(phoneNumber);
            }
        } else if (!StringUtils.isEmpty(email) && !emailExists && phoneNumberExists) {
            createSecondaryContact(primaryContact, email, phoneNumber);
            if (!StringUtils.isEmpty(email)) {
                emailSet.add(email);
            }
        }
    }

    /**
     * Collect information of secondary contacts and add to the response details.
     */
    private void collectSecondaryContactInfo(Contact primaryContact, Set<String> emailSet, Set<String> phoneSet, Set<Integer> secondaryIds) {
        List<Contact> secondaryContact = contactRepository.findByLinkedId(primaryContact.getId());
        for (Contact contact : secondaryContact) {
            if (contact.getLinkPrecedence() == Contact.LinkPrecedence.SECONDARY) {
                String email = contact.getEmail();
                String phoneNumber = contact.getPhoneNumber();
                if (!StringUtils.isEmpty(email))
                    emailSet.add(email);
                if (!StringUtils.isEmpty(phoneNumber))
                    phoneSet.add(phoneNumber);
                secondaryIds.add(contact.getId());
            }
        }
    }

    /**
     * Create a new contact when no existing contacts are found.
     */
    private Contact createNewContact(String email, String phoneNumber) {
        Contact newContact = Contact.builder()
                .email(email)
                .phoneNumber(phoneNumber)
                .linkPrecedence(Contact.LinkPrecedence.PRIMARY)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return contactRepository.save(newContact);
    }

    /**
     * Get the primary contact from existing contacts.
     */
    private Contact getPrimaryContact(List<Contact> contacts) {
        return contacts.stream()
                .filter(c -> c.getLinkPrecedence() == Contact.LinkPrecedence.PRIMARY)
                .findFirst()
                .orElseGet(() -> {
                    Integer linkedId = contacts.get(0).getLinkedId();
                    return contactRepository.findById(linkedId);
                });
    }

    /**
     * Create a secondary contact when a new email or phone number is provided.
     */
    private void createSecondaryContact(Contact primaryContact, String email, String phoneNumber) {
        if (email != null || phoneNumber != null) {
            Contact secondaryContact = Contact.builder()
                    .email(email)
                    .phoneNumber(phoneNumber)
                    .linkedId(primaryContact.getId())
                    .linkPrecedence(Contact.LinkPrecedence.SECONDARY)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            contactRepository.save(secondaryContact);
        }
    }
}
