package org.vgcs.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RowOrder {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String supplierPid;
    private String creditCardNumber;
    private String creditCardType;
    private String orderId;
    private String productPid;
    private String shippingAddress;
    private String country;
    private String dateCreated;
    private int quantity;
    private String fullName;
    private int orderStatus;
}