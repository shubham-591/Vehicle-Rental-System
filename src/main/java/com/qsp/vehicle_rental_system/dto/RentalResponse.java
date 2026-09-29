package com.qsp.vehicle_rental_system.dto;

import java.time.LocalDate;

public class RentalResponse {

    private int rid;
    private LocalDate startDate;
    private LocalDate endDate;
    private double totalAmount;

    private VehicleResponse vehicle;
    
	public int getRid() {
		return rid;
	}
	public void setRid(int rid) {
		this.rid = rid;
	}
	public LocalDate getStartDate() {
		return startDate;
	}
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}
	public LocalDate getEndDate() {
		return endDate;
	}
	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
	public double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}
	public VehicleResponse getVehicle() {
		return vehicle;
	}
	public void setVehicle(VehicleResponse vehicle) {
		this.vehicle = vehicle;
	}
	

}
