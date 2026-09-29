package com.karakoc.enewsletter.customers;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer,String> {
    Optional<Customer> findByEmailAndNewsletterId(String email,String newsletterId);
}
