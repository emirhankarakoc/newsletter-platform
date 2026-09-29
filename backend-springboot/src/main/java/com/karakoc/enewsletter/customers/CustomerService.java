package com.karakoc.enewsletter.customers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface CustomerService {

    ResponseEntity subscribeToNewsletter(String newsletterId, CreateCustomerRequest r);
    List<Customer> getAllCustomers(String userId, String newsletterId);
    ResponseEntity deleteCustomer(String newsletterOwnerId, String id);
    Customer updateCustomer(String newsletterOwnerId, String id, UpdateCustomerRequest r);
    public void unsubscribe(String customerId, String newsletterId);
    }
