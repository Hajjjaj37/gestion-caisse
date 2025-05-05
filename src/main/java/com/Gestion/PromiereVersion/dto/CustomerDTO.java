package com.Gestion.PromiereVersion.dto;

import com.Gestion.PromiereVersion.model.Customer;
import com.Gestion.PromiereVersion.model.Tax;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String customerCardNumber;
    private Tax tax;

    public static CustomerDTO fromCustomer(Customer customer) {
        return CustomerDTO.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .address(customer.getAddress())
                .customerCardNumber(customer.getCustomerCardNumber())
                .tax(customer.getTax())
                .build();
    }
} 