package com.contact.reconciliation.Contact.project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

public class ContactResponseDTO {

    @JsonProperty
    private int primaryContactId;

    @JsonProperty
    private String[] emails;

    @JsonProperty
    private String[] phoneNumbers;

    @JsonProperty
    private Integer[] secondaryContactIds;

    public int getPrimaryContactId() {
        return primaryContactId;
    }

    public void setPrimaryContactId(int primaryContactId) {
        this.primaryContactId = primaryContactId;
    }

    public String[] getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(String[] phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public String[] getEmails() {
        return emails;
    }

    public void setEmails(String[] emails) {
        this.emails = emails;
    }

    public Integer[] getSecondaryContactIds() {
        return secondaryContactIds;
    }

    public void setSecondaryContactIds(Integer[] secondaryContactIds) {
        this.secondaryContactIds = secondaryContactIds;
    }
}
