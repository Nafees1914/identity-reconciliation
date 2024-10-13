package com.bitespeed.identity_reconciliation.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Builder
@Getter
@Setter
@Data
public class AllContactsDTO {
    private int primaryContactId;
    private Set<String> emails;
    private Set<String> phoneNumbers;
    private Set<Integer> secondaryContactIds;
}
