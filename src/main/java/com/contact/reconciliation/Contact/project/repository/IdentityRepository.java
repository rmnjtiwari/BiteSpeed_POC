package com.contact.reconciliation.Contact.project.repository;

import com.contact.reconciliation.Contact.project.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IdentityRepository extends JpaRepository<Contact, Integer> {

    List<Contact> findByPhoneNumberOrEmailOrderByCreatedAtAsc(String phoneNumber, String email);

    List<Contact> findByEmail(String email);

    List<Contact> findByPhoneNumber(String phoneNumber);
}