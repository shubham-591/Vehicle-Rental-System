package com.qsp.vehicle_rental_system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CustomerRequest {

    @Size(
        min = 2,
        max = 50,
        message = "Name must contain between 2 and 50 characters"
    )
    private String cname;

    @Pattern(
        regexp = "^[6-9][0-9]{9}$",
        message = "Mobile number must contain 10 valid digits"
    )
    private String mobileNo;

    @Email(message = "Please provide a valid email")
    private String email;


    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
