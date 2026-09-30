package com.qsp.vehicle_rental_system.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public class RentalRequest {


    @NotNull(message = "Vehicle ID cannot be null")
    private Integer vehicleId;

    @NotNull(message = "Start date cannot be null")
    private LocalDate startDate;

    public Integer getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
}
