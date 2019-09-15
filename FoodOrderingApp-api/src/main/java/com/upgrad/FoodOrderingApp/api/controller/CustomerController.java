package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.api.provider.BasicAuthDecoder;
import com.upgrad.FoodOrderingApp.service.business.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @CrossOrigin
    @RequestMapping(method = POST, path = "/customer/signup", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupCustomerResponse> signup(@RequestBody final SignupCustomerRequest signupCustomerRequest) throws SignUpRestrictedException {
        final CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setFirstName(signupCustomerRequest.getFirstName());
        customerEntity.setLastName(signupCustomerRequest.getLastName());
        customerEntity.setEmail(signupCustomerRequest.getEmailAddress());
        customerEntity.setContactNumber(signupCustomerRequest.getContactNumber());
        customerEntity.setPassword(signupCustomerRequest.getPassword());
        CustomerEntity createdCustomer = customerService.saveCustomer(customerEntity);
        SignupCustomerResponse signupCustomerResponse = new SignupCustomerResponse()
                .id(createdCustomer.getUuid())
                .status("CUSTOMER SUCCESSFULLY REGISTERED");

        return new ResponseEntity<SignupCustomerResponse>(signupCustomerResponse, HttpStatus.CREATED);

    }

    @CrossOrigin
    @RequestMapping(method = POST, path = "/customer/login", produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LoginResponse> login(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {
        BasicAuthDecoder basicAuthDecoder = new BasicAuthDecoder(authorization);
        CustomerAuthEntity authorizedCustomer = customerService.authenticate(basicAuthDecoder.getUsername(), basicAuthDecoder.getPassword());
        CustomerEntity customer = authorizedCustomer.getCustomer();
        LoginResponse loginResponse = new LoginResponse()
                .id(customer.getUuid())
                .contactNumber(customer.getContactNumber())
                .emailAddress(customer.getEmail())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .message("LOGGED IN SUCCESSFULLY");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("access-token", authorizedCustomer.getAccessToken());
        return new ResponseEntity<LoginResponse>(loginResponse, httpHeaders, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(method = POST, path = "/customer/logout", produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LogoutResponse> logout(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {
        final String accessToken;

        if (authorization.split("Bearer ").length == 2)
            accessToken = authorization.split("Bearer ")[1];
        else
            accessToken = authorization;

        CustomerAuthEntity loggedOutCustomerAuth = customerService.logout(accessToken);
        LogoutResponse logoutResponse = new LogoutResponse()
                .id(loggedOutCustomerAuth.getCustomer().getUuid())
                .message("LOGGED OUT SUCCESSFULLY");
        return new ResponseEntity<LogoutResponse>(logoutResponse, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(method = PUT, path = "/customer", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdateCustomerResponse> updateCustomer(@RequestHeader("authorization") final String authorization, @RequestBody final UpdateCustomerRequest updateCustomerRequest) throws UpdateCustomerException, AuthorizationFailedException {
        final ZonedDateTime now;
        //final BearerAuthDecoder bearerAuthDecoder = new BearerAuthDecoder(authorization);
        //final String accessToken = bearerAuthDecoder.getAccessToken();
        final String accessToken;

        if (authorization.split("Bearer ").length == 2) {
            System.out.println("length is 2");
            accessToken = authorization.split("Bearer ")[1];
        }
        else
            accessToken = authorization;
        if (updateCustomerRequest.getFirstName() == null || updateCustomerRequest.getFirstName().isEmpty()) {
            throw new UpdateCustomerException("UCR-002", "First name field should not be empty");
        }
        CustomerEntity customer = customerService.getCustomer(accessToken);

        customer.setFirstName(updateCustomerRequest.getFirstName());
        customer.setLastName(updateCustomerRequest.getLastName());
        //CustomerEntity updatedCustomer = customerService.updateCustomer(accessToken, customer);
        CustomerEntity updatedCustomer = customerService.updateCustomer(customer);
        UpdateCustomerResponse updateCustomerResponse = new UpdateCustomerResponse()
                .id(updatedCustomer.getUuid())
                .firstName(updatedCustomer.getFirstName())
                .lastName(updatedCustomer.getLastName())
                .status("CUSTOMER DETAILS UPDATED SUCCESSFULLY");
        return new ResponseEntity<UpdateCustomerResponse>(updateCustomerResponse, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(method = PUT, path = "/customer/password", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdatePasswordResponse> updatePassword(@RequestHeader("authorization") final String authorization, @RequestBody final UpdatePasswordRequest updatePasswordRequest) throws AuthorizationFailedException, UpdateCustomerException {

//        final BearerAuthDecoder bearerAuthDecoder = new BearerAuthDecoder(authorization);
//        final String accessToken = bearerAuthDecoder.getAccessToken();
        final String accessToken;

        if (authorization.split("Bearer ").length == 2)
            accessToken = authorization.split("Bearer ")[1];
        else
            accessToken = authorization;

        final String oldPassword = updatePasswordRequest.getOldPassword();
        final String newPassword = updatePasswordRequest.getNewPassword();
        if (oldPassword == "" || newPassword == "") {
            throw new UpdateCustomerException("UCR-003", "No field should be empty");
        }
        CustomerEntity customer= customerService.getCustomer(accessToken);
        CustomerEntity updatedCustomer = customerService.updateCustomerPassword(oldPassword, newPassword, customer);
        UpdatePasswordResponse updatePasswordResponse = new UpdatePasswordResponse()
                .id(updatedCustomer.getUuid())
                .status("CUSTOMER PASSWORD UPDATED SUCCESSFULLY");
        return new ResponseEntity<UpdatePasswordResponse>(updatePasswordResponse, HttpStatus.OK);
    }
}