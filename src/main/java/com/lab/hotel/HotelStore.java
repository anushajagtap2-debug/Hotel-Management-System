package com.lab.hotel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class HotelStore {
    private static final double GST_RATE = 0.12;
    private static final Path DATA_DIR = Path.of("data");
    private static final Path ROOMS_FILE = DATA_DIR.resolve("rooms.csv");
    private static final Path BOOKINGS_FILE = DATA_DIR.resolve("bookings.csv");

    public synchronized List<HotelRoom> loadRooms() {
        ensureFiles();
        try {
            List<HotelRoom> rooms = new ArrayList<>();
            for (String line : Files.readAllLines(ROOMS_FILE, StandardCharsets.UTF_8)) {
                if (line.isBlank() || line.startsWith("roomNumber,")) {
                    continue;
                }
                String[] parts = line.split(",", -1);
                if (parts.length >= 4) {
                    rooms.add(new HotelRoom(parts[0].trim(), parts[1].trim(),
                            Double.parseDouble(parts[2].trim()), parts[3].trim()));
                }
            }
            return rooms;
        } catch (IOException ex) {
            throw new IllegalStateException("Could not read rooms", ex);
        }
    }

    public synchronized List<BookingRecord> loadBookings() {
        ensureFiles();
        try {
            List<BookingRecord> bookings = new ArrayList<>();
            for (String line : Files.readAllLines(BOOKINGS_FILE, StandardCharsets.UTF_8)) {
                if (line.isBlank() || line.startsWith("bookingId,")) {
                    continue;
                }
                String[] parts = line.split(",", -1);
                if (parts.length >= 9) {
                    bookings.add(new BookingRecord(parts[0].trim(), parts[1].trim(), parts[2].trim(),
                            parts[3].trim(), parts[4].trim(), parts[5].trim(), Integer.parseInt(parts[6].trim()),
                            Double.parseDouble(parts[7].trim()), parts[8].trim()));
                } else if (parts.length >= 8) {
                    bookings.add(new BookingRecord(parts[0].trim(), parts[1].trim(), parts[2].trim(),
                            parts[3].trim(), parts[4].trim(), "N/A", Integer.parseInt(parts[5].trim()),
                            Double.parseDouble(parts[6].trim()), parts[7].trim()));
                }
            }
            return bookings;
        } catch (IOException ex) {
            throw new IllegalStateException("Could not read bookings", ex);
        }
    }

    public synchronized void saveRooms(List<HotelRoom> rooms) {
        ensureFiles();
        List<String> lines = new ArrayList<>();
        lines.add("roomNumber,roomType,pricePerDay,status");
        for (HotelRoom room : rooms) {
            lines.add(String.join(",",
                    room.getRoomNumber(),
                    room.getRoomType(),
                    String.valueOf(room.getPricePerDay()),
                    room.getStatus()));
        }
        writeLines(ROOMS_FILE, lines);
    }

    public synchronized void saveBookings(List<BookingRecord> bookings) {
        ensureFiles();
        List<String> lines = new ArrayList<>();
        lines.add("bookingId,customerName,contactNumber,roomNumber,roomType,checkInDate,nights,totalAmount,status");
        for (BookingRecord booking : bookings) {
            lines.add(String.join(",",
                    booking.getBookingId(),
                    booking.getGuestName(),
                    booking.getContactNumber(),
                    booking.getRoomNumber(),
                    booking.getRoomType(),
                    booking.getCheckInDate(),
                    String.valueOf(booking.getNights()),
                    String.valueOf(booking.getTotalAmount()),
                    booking.getStatus()));
        }
        writeLines(BOOKINGS_FILE, lines);
    }

    public BookingRecord createBooking(String customerName, String contactNumber, HotelRoom room, String checkInDate, int nights) {
        String bookingId = "BK-" + System.currentTimeMillis();
        double roomCharge = nights * room.getPricePerDay();
        double total = roomCharge + roomCharge * GST_RATE;
        return new BookingRecord(bookingId, customerName, contactNumber, room.getRoomNumber(),
                room.getRoomType(), checkInDate, nights, total, "Active");
    }

    private void writeLines(Path path, List<String> lines) {
        try {
            Files.write(path, lines, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalStateException("Could not save " + path.getFileName(), ex);
        }
    }

    private void ensureFiles() {
        try {
            Files.createDirectories(DATA_DIR);
            if (Files.notExists(ROOMS_FILE)) {
                Files.write(ROOMS_FILE, List.of(
                        "roomNumber,roomType,pricePerDay,status",
                        "101,Single,3000,Available",
                        "102,Single,3000,Available",
                        "201,Double,5000,Occupied",
                        "301,Deluxe,10000,Available"), StandardCharsets.UTF_8);
            }
            if (Files.notExists(BOOKINGS_FILE)) {
                Files.write(BOOKINGS_FILE, List.of(
                        "bookingId,customerName,contactNumber,roomNumber,roomType,checkInDate,nights,totalAmount,status",
                        "BK-DEMO,Asha Nair,9876543210,201,Double,2025-10-09,2,11200,Active"), StandardCharsets.UTF_8);
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Could not prepare hotel data files", ex);
        }
    }
}
