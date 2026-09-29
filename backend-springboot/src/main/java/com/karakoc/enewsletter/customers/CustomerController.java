package com.karakoc.enewsletter.customers;

import com.karakoc.enewsletter.newsletters.NewsletterRepository;
import com.karakoc.enewsletter.security.UserPrincipal;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/customers")
@AllArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping("/newsletters/{newsletterId}")
    public ResponseEntity subscribeToNewsletter(@PathVariable String newsletterId, @RequestBody CreateCustomerRequest r){
        log.info("yeni biri kaydoldu.");
       return customerService.subscribeToNewsletter(newsletterId, r);
    }


    @GetMapping("/newsletters/{newsletterId}")
    public List<Customer> getAllCustomers(@AuthenticationPrincipal UserPrincipal principal, @PathVariable String newsletterId){
        return customerService.getAllCustomers(principal.getUserId(),newsletterId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteCustomer(@AuthenticationPrincipal UserPrincipal principal,@PathVariable String id) {
        return customerService.deleteCustomer(principal.getUserId(),id);
    }

    @PutMapping("/{userId}")
    public Customer updateCustomer(@AuthenticationPrincipal UserPrincipal principal, @PathVariable String userId, @RequestBody UpdateCustomerRequest r){
        return customerService.updateCustomer(principal.getUserId(),userId,r);
    }

    @GetMapping("/{customerId}/newsletters/{newsletterId}/unsubscribe")
    public String unsubscribe(@PathVariable String customerId,@PathVariable String newsletterId){
        customerService.unsubscribe(customerId,newsletterId);
        return "Unsubscribe successfully.";
    }
}
