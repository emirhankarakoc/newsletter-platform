package com.karakoc.enewsletter.customers;

import lombok.Data;

@Data
public class CreateCustomerRequest {
    private String name;
    private String email;

}
