package com.karakoc.enewsletter.newsletters;


import com.karakoc.enewsletter.customers.Customer;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Newsletter {
    @Id
    private String id;
    private String name;
    private String ownerUserId;
    private String description;
    private String imageKey;

    @OneToMany
    @JoinColumn(name = "customerId")
    private List<Customer> customers;

}
