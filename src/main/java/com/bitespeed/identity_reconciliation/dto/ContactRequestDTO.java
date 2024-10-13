package com.bitespeed.identity_reconciliation.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class ContactRequestDTO {

    private String email;
    private String phoneNumber;
}
