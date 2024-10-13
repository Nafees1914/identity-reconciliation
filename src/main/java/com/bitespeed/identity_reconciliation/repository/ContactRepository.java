package com.bitespeed.identity_reconciliation.repository;

import com.bitespeed.identity_reconciliation.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    @Query("SELECT c FROM Contact c WHERE c.email = :email OR c.phoneNumber = :phoneNumber")
    List<Contact> findByEmailOrPhoneNumber(String email, String phoneNumber);

    List<Contact> findByLinkedId(int id);

    Contact findById(Integer id);
}
