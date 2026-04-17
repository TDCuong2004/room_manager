package com.example.server.dto;


import com.example.server.entity.ContractCustomer;
import com.example.server.entity.Customer;
import lombok.Data;
// ================= TENANT =================
@Data
public class ContractCustomerResponse {

    private Long id;
    private String fullName;
    private String phone;
    private Integer birthYear;
    private String gender;

    private String cccdFrontImage;
    private String cccdBackImage;

    private boolean representative;

    public ContractCustomerResponse(ContractCustomer cc) {
        Customer c = cc.getCustomer();

        this.id = c.getId();
        this.fullName = c.getFullName();
        this.phone = c.getPhone();
        this.birthYear = c.getBirthYear();
        this.gender = c.getGender() != null ? c.getGender().name() : null;

        this.cccdFrontImage = c.getCccdFrontImage();
        this.cccdBackImage = c.getCccdBackImage();

        this.representative = cc.isRepresentative();
    }
}