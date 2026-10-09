package com.qsp.vehicle_rental_system.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class VehicleUpdateRequest {

    @Size(min = 2, max = 50)
    private String vname;

    @Positive
    private Double rentPerDay;

    @Size(min = 2, max = 50)
    private String company;

	public String getVname() {
		return vname;
	}

	public void setVname(String vname) {
		this.vname = vname;
	}

	public Double getRentPerDay() {
		return rentPerDay;
	}

	public void setRentPerDay(Double rentPerDay) {
		this.rentPerDay = rentPerDay;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

}
