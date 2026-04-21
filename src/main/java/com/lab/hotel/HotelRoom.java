package com.lab.hotel;

public class HotelRoom {
    private final String roomNumber;
    private final String roomType;
    private final double pricePerDay;
    private final String status;

    public HotelRoom(String roomNumber, String roomType, double pricePerDay, String status) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.pricePerDay = pricePerDay;
        this.status = status;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public double getPricePerDay() {
        return pricePerDay;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return roomNumber + " - " + roomType;
    }
}
