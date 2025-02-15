package com.contact.reconciliation.Contact.project.service;

import com.contact.reconciliation.Contact.project.dto.ContactResponseDTO;
import com.contact.reconciliation.Contact.project.dto.IdentityRequestDTO;
import com.contact.reconciliation.Contact.project.dto.IdentityResponseDTO;
import com.contact.reconciliation.Contact.project.entity.Contact;
import com.contact.reconciliation.Contact.project.entity.LinkPrecedenceType;
import com.contact.reconciliation.Contact.project.repository.IdentityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Service
public class IdentityServiceImpl {

    @Autowired
    IdentityRepository identityRepository;


    public IdentityResponseDTO findContactInfo(IdentityRequestDTO identityRequest) {
        String requestEmail = identityRequest.getEmail();
        String requestPhone = identityRequest.getPhoneNumber();

        if(requestEmail == null){
            List<Contact> linkedContactList = identityRepository.findByPhoneNumber(requestPhone);
            if(!CollectionUtils.isEmpty(linkedContactList)){
                requestEmail = linkedContactList.get(0).getEmail();
            }
        }
        else if(requestPhone == null){
            List<Contact> linkedContactList = identityRepository.findByEmail(requestEmail);
            if(!CollectionUtils.isEmpty(linkedContactList)){
                requestPhone = linkedContactList.get(0).getPhoneNumber();
            }
        }

        IdentityResponseDTO idResponse = new IdentityResponseDTO();
        ContactResponseDTO contactResponse = new ContactResponseDTO();
        List<Contact> contactList = identityRepository.findByPhoneNumberOrEmailOrderByCreatedAtAsc(requestPhone, requestEmail);
        if(CollectionUtils.isEmpty(contactList)){
            Contact contact = new Contact();
            contact.setPhoneNumber(requestPhone);
            contact.setEmail(requestEmail);
            contact.setCreatedAt(LocalDateTime.now());
            contact.setUpdatedAt(LocalDateTime.now());
            contact.setLinkPrecedence(LinkPrecedenceType.primary.toString());
            Contact savedContact = identityRepository.save(contact);

            //prepare contact and id response for new contact
            prepareIdResponseForNewContact(savedContact, contactResponse, idResponse);
        }
        else{
            List<Integer> idList = new ArrayList<>();
            List<String> emailsList = new ArrayList<>();
            List<String> phoneNumbersList = new ArrayList<>();

            HashSet<String> emailsSet = new HashSet<>();
            HashSet<String> phoneNumbersSet = new HashSet<>();


            boolean hasEmail = false;
            boolean hasPhoneNumber = false;

            // since first contact of contact list is oldest, it will be marked primary
            Integer oldestContactId = contactList.get(0).getId();
            for(int i = 0; i < contactList.size() ; i++){
                if(i == 0){
                    // if oldest createdAt contact is not primary, then make it primary
                    if(contactList.get(i).getLinkPrecedence().equalsIgnoreCase(LinkPrecedenceType.secondary.toString())) {
                        contactList.get(i).setLinkPrecedence(LinkPrecedenceType.primary.toString());
                        contactList.get(i).setLinkedId(null);
                        contactList.get(i).setUpdatedAt(LocalDateTime.now());
                    }
                }
                else if(!Objects.equals(contactList.get(i).getLinkedId(), oldestContactId)) {
                    contactList.get(i).setLinkPrecedence(LinkPrecedenceType.secondary.toString());
                    contactList.get(i).setLinkedId(oldestContactId);
                    contactList.get(i).setUpdatedAt(LocalDateTime.now());
                }

                // check email and phone
                if(contactList.get(i).getEmail().equals(requestEmail) || requestEmail == null)
                    hasEmail = true;
                if(contactList.get(i).getPhoneNumber().equals(requestPhone) || requestPhone == null)
                    hasPhoneNumber = true;

                idList.add(contactList.get(i).getId());
                //add unique phone and email to response list
                if(!emailsSet.contains(contactList.get(i).getEmail())) {
                    emailsList.add(contactList.get(i).getEmail());
                    emailsSet.add(contactList.get(i).getEmail());
                }
                if(!phoneNumbersSet.contains(contactList.get(i).getPhoneNumber())) {
                    phoneNumbersList.add(contactList.get(i).getPhoneNumber());
                    phoneNumbersSet.add(contactList.get(i).getPhoneNumber());
                }
            }

            //if only one of email or phone is already present then we have some new info
            //so new secondary contact will be created
            //if both email or phone are already present, secondary contact will not be created
            boolean createNewSecondaryContact = (hasEmail || hasPhoneNumber) && !(hasEmail && hasPhoneNumber);


            if(createNewSecondaryContact){
                // either email or phone number is missing in existing list
                // new secondary contact will be added
                Contact secondaryContact = new Contact();
                secondaryContact.setPhoneNumber(requestPhone);
                secondaryContact.setEmail(requestEmail);
                secondaryContact.setCreatedAt(LocalDateTime.now());
                secondaryContact.setUpdatedAt(LocalDateTime.now());
                secondaryContact.setLinkPrecedence(LinkPrecedenceType.secondary.toString());
                secondaryContact.setLinkedId(oldestContactId);

                contactList.add(secondaryContact);

                //update new contact in email, phone in list
                if(hasEmail){
                    //new phone number
                    phoneNumbersList.add(requestPhone);
                }
                else emailsList.add(requestEmail);
            }
            //save updated list to db
            List<Contact> savedList = identityRepository.saveAll(contactList);

            if(createNewSecondaryContact && !CollectionUtils.isEmpty(savedList)){
                Contact newContact = savedList.get(savedList.size() - 1);
                idList.add(newContact.getId());
            }

            prepareIdResponseForNewContactList(idList, emailsList, phoneNumbersList, contactResponse, idResponse);
        }
        return idResponse;
    }

    private void prepareIdResponseForNewContactList(List<Integer> idList, List<String> emailsList, List<String> phoneNumbersList, ContactResponseDTO contactResponse, IdentityResponseDTO idResponse) {
        contactResponse.setPrimaryContactId(idList.get(0));
        contactResponse.setEmails(emailsList.toArray(new String[0]));
        contactResponse.setPhoneNumbers(phoneNumbersList.toArray(new String[0]));

        //since first index id is primary id
        idList.remove(0);
        contactResponse.setSecondaryContactIds(idList.toArray(new Integer[0]));

        idResponse.setContact(contactResponse);
    }


    private void prepareIdResponseForNewContact(Contact contact, ContactResponseDTO contactResponse, IdentityResponseDTO idResponse) {
        contactResponse.setPrimaryContactId(contact.getId());
        contactResponse.setEmails(new String[]{contact.getEmail()});
        contactResponse.setPhoneNumbers(new String[]{contact.getPhoneNumber()});
        contactResponse.setSecondaryContactIds(new Integer[]{});

        idResponse.setContact(contactResponse);
    }
}
