package com.contact.reconciliation.Contact.project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

public class IdentityResponseDTO {

    @JsonProperty
    ContactResponseDTO contact;

    public ContactResponseDTO getContact() {
        return contact;
    }

    public void setContact(ContactResponseDTO contact) {
        this.contact = contact;
    }
}
