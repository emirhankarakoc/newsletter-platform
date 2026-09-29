package com.karakoc.enewsletter.customers;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Customer {
    @Id
    private String id;
    private String name;
    private String email;
    private LocalDateTime registerDate;
    private String newsletterId;
}
