package com.contact.reconciliation.Contact.project.controllers;

import com.contact.reconciliation.Contact.project.dto.IdentityRequestDTO;
import com.contact.reconciliation.Contact.project.dto.IdentityResponseDTO;
import com.contact.reconciliation.Contact.project.service.IdentityServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class IdentityController {

    @Autowired
    IdentityServiceImpl identityService;

    @PostMapping(value = "/identify", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IdentityResponseDTO> getContact(@RequestBody IdentityRequestDTO identityRequest){
        if(ObjectUtils.isEmpty(identityRequest) || (identityRequest.getEmail() == null && identityRequest.getPhoneNumber() == null)){
            return new ResponseEntity<>(new IdentityResponseDTO(), HttpStatus.BAD_REQUEST);
        }
        IdentityResponseDTO identityResponse = identityService.findContactInfo(identityRequest);
        return new ResponseEntity<>(identityResponse, HttpStatus.OK);
    }
}
