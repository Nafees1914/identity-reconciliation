package com.bitespeed.identity_reconciliation.service;

import com.bitespeed.identity_reconciliation.dto.IdentityReconciliationResponseDTO;

public interface ContactService {
    IdentityReconciliationResponseDTO identifyOrCreateContact(String email, String phoneNumber);
}
