package com.lab.hotel;

public class BookingRecord {
    private final String bookingId;
    private final String guestName;
    private final String contactNumber;
    private final String roomNumber;
    private final String roomType;
    private final String checkInDate;
    private final int nights;
    private final double totalAmount;
    private final String status;

    public BookingRecord(String bookingId, String guestName, String contactNumber, String roomNumber,
                         String roomType, String checkInDate, int nights, double totalAmount, String status) {
        this.bookingId = bookingId;
        this.guestName = guestName;
        this.contactNumber = contactNumber;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.checkInDate = checkInDate;
        this.nights = nights;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public int getNights() {
        return nights;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
    }
}
