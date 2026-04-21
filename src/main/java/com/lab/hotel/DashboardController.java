package com.lab.hotel;

import java.util.ArrayList;
import java.util.Comparator;
import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

public class DashboardController {
    private static final double SINGLE_PRICE = 3000;
    private static final double DOUBLE_PRICE = 5000;
    private static final double DELUXE_PRICE = 10000;
    private static final double GST_RATE = 0.12;

    private final HotelStore store = new HotelStore();
    private final ObservableList<HotelRoom> rooms = FXCollections.observableArrayList();
    private final ObservableList<HotelRoom> visibleRooms = FXCollections.observableArrayList();
    private final ObservableList<HotelRoom> availableRooms = FXCollections.observableArrayList();
    private final ObservableList<BookingRecord> bookings = FXCollections.observableArrayList();
    private final ObservableList<BookingRecord> activeBookings = FXCollections.observableArrayList();

    @FXML
    private TabPane mainTabs;
    @FXML
    private Tab dashboardTab;
    @FXML
    private Tab addRoomTab;
    @FXML
    private Tab checkInTab;
    @FXML
    private Tab checkOutTab;
    @FXML
    private Tab customerDatabaseTab;
    @FXML
    private Tab analyticsTab;

    @FXML
    private TextField roomNumberField;
    @FXML
    private ComboBox<String> roomTypeComboBox;
    @FXML
    private TextField pricePerDayField;
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private TableView<HotelRoom> roomTable;
    @FXML
    private TableColumn<HotelRoom, String> roomNumberColumn;
    @FXML
    private TableColumn<HotelRoom, String> roomTypeColumn;
    @FXML
    private TableColumn<HotelRoom, Double> priceColumn;
    @FXML
    private TableColumn<HotelRoom, String> availabilityColumn;

    @FXML
    private TextField customerNameField;
    @FXML
    private TextField contactNumberField;
    @FXML
    private ComboBox<HotelRoom> checkInRoomComboBox;
    @FXML
    private DatePicker checkInDatePicker;
    @FXML
    private Spinner<Integer> nightsSpinner;
    @FXML
    private TextArea bookingDetailsArea;
    @FXML
    private Label contactErrorLabel;

    @FXML
    private TableView<BookingRecord> checkoutTable;
    @FXML
    private TableColumn<BookingRecord, String> bookingIdColumn;
    @FXML
    private TableColumn<BookingRecord, String> customerColumn;
    @FXML
    private TableColumn<BookingRecord, String> contactColumn;
    @FXML
    private TableColumn<BookingRecord, String> bookedRoomColumn;
    @FXML
    private TableColumn<BookingRecord, String> bookingStatusColumn;
    @FXML
    private TextArea checkoutDetailsArea;

    @FXML
    private TableView<BookingRecord> customerDatabaseTable;
    @FXML
    private TableColumn<BookingRecord, String> databaseCustomerColumn;
    @FXML
    private TableColumn<BookingRecord, String> databaseContactColumn;
    @FXML
    private TableColumn<BookingRecord, String> databaseRoomColumn;
    @FXML
    private TableColumn<BookingRecord, String> databaseRoomTypeColumn;
    @FXML
    private TableColumn<BookingRecord, Double> databaseAmountColumn;
    @FXML
    private TableColumn<BookingRecord, String> databaseStatusColumn;
    @FXML
    private TextArea customerDatabaseDetailsArea;

    @FXML
    private Label totalRoomsLabel;
    @FXML
    private Label dashboardAvailableLabel;
    @FXML
    private Label bookedRoomsLabel;
    @FXML
    private Label availableCountLabel;
    @FXML
    private Label occupiedCountLabel;
    @FXML
    private BarChart<String, Number> revenueChart;
    @FXML
    private BarChart<String, Number> bookingsChart;
    @FXML
    private Label statusLabel;

    private final Object transactionLock = new Object();

    public void setRole(String role) {
        if ("user".equalsIgnoreCase(role)) {
            mainTabs.getTabs().remove(dashboardTab);
            mainTabs.getTabs().remove(addRoomTab);
            mainTabs.getTabs().remove(customerDatabaseTab);
            mainTabs.getTabs().remove(analyticsTab);
            mainTabs.getSelectionModel().select(checkInTab);
            setStatus("Logged in as user. Check-in and checkout access enabled.");
            return;
        }
        mainTabs.getSelectionModel().select(dashboardTab);
        setStatus("Logged in as admin. Full access enabled.");
    }

    @FXML
    private void initialize() {
        roomTypeComboBox.setItems(FXCollections.observableArrayList("Single", "Double", "Deluxe"));
        roomTypeComboBox.getSelectionModel().selectFirst();
        updatePriceForRoomType();
        statusComboBox.setItems(FXCollections.observableArrayList("Available", "Occupied"));
        statusComboBox.getSelectionModel().selectFirst();
        nightsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 30, 1));
        if (checkInDatePicker != null) {
            checkInDatePicker.setValue(LocalDate.now());
        }

        configureRoomTable();
        configureCheckoutTable();
        configureCustomerDatabaseTable();

        rooms.setAll(store.loadRooms());
        bookings.setAll(store.loadBookings());
        visibleRooms.setAll(rooms);
        roomTable.setItems(visibleRooms);
        checkoutTable.setItems(activeBookings);
        customerDatabaseTable.setItems(bookings);

        refreshDerivedLists();
        refreshBookingPreview();
        refreshCheckoutPreview();
        refreshAnalytics();
    }

    @FXML
    private void showDashboard() {
        if (!mainTabs.getTabs().contains(dashboardTab)) {
            setStatus("Dashboard is available only for admin login.");
            return;
        }
        mainTabs.getSelectionModel().select(dashboardTab);
    }

    @FXML
    private void showAddRoom() {
        if (!mainTabs.getTabs().contains(addRoomTab)) {
            setStatus("Add Room is available only for admin login.");
            return;
        }
        mainTabs.getSelectionModel().select(addRoomTab);
    }

    @FXML
    private void showCheckIn() {
        mainTabs.getSelectionModel().select(checkInTab);
    }

    @FXML
    private void showCheckOut() {
        mainTabs.getSelectionModel().select(checkOutTab);
    }

    @FXML
    private void showCustomerDatabase() {
        if (!mainTabs.getTabs().contains(customerDatabaseTab)) {
            setStatus("Customer database is available only for admin login.");
            return;
        }
        mainTabs.getSelectionModel().select(customerDatabaseTab);
        refreshCustomerDatabasePreview();
    }

    @FXML
    private void showAnalytics() {
        if (!mainTabs.getTabs().contains(analyticsTab)) {
            setStatus("Revenue and analytics are available only for admin login.");
            return;
        }
        mainTabs.getSelectionModel().select(analyticsTab);
        refreshAnalytics();
    }

    @FXML
    private void handleRoomTypeChanged() {
        updatePriceForRoomType();
    }

    @FXML
    private void handleAddRoom() {
        try {
            String roomNumber = clean(roomNumberField);
            String roomType = roomTypeComboBox.getValue();
            double pricePerDay = priceForRoomType(roomType);
            String status = statusComboBox.getValue();

            if (roomNumber.isBlank() || roomType == null || pricePerDay <= 0 || status == null) {
                setStatus("Enter room number, room type, price per day and status.");
                return;
            }
            if (rooms.stream().anyMatch(room -> room.getRoomNumber().equalsIgnoreCase(roomNumber))) {
                setStatus("Room number already exists.");
                return;
            }

            rooms.add(new HotelRoom(roomNumber, roomType, pricePerDay, status));
            rooms.sort(Comparator.comparing(HotelRoom::getRoomNumber));
            store.saveRooms(new ArrayList<>(rooms));
            clearRoomForm();
            showAllRooms();
            refreshDerivedLists();
            setStatus("Room " + roomNumber + " added.");
        } catch (RuntimeException ex) {
            setStatus("Could not add room. Check the room details.");
        }
    }

    @FXML
    private void handleDeleteRoom() {
        HotelRoom selected = roomTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            setStatus("Select a room to delete.");
            return;
        }
        if ("Occupied".equals(selected.getStatus())) {
            setStatus("Checkout the customer before deleting an occupied room.");
            return;
        }

        rooms.remove(selected);
        store.saveRooms(new ArrayList<>(rooms));
        showAllRooms();
        refreshDerivedLists();
        setStatus("Room " + selected.getRoomNumber() + " deleted.");
    }

    @FXML
    private void showAllRooms() {
        visibleRooms.setAll(rooms);
        setStatus("Showing all rooms.");
    }

    @FXML
    private void showAvailableRoomsOnly() {
        visibleRooms.setAll(rooms.stream()
                .filter(room -> "Available".equals(room.getStatus()))
                .toList());
        setStatus("Showing available rooms only.");
    }

    @FXML
    private void handleBookingChanged() {
        String contactNumber = clean(contactNumberField);
        if (!contactNumber.isEmpty() && !contactNumber.matches("\\d{10}")) {
            if (contactErrorLabel != null) contactErrorLabel.setText("Please write a valid 10-digit phone number.");
        } else {
            if (contactErrorLabel != null) contactErrorLabel.setText("");
        }
        refreshBookingPreview();
    }

    @FXML
    private void handleBookRoom() {
        String customerName = clean(customerNameField);
        String contactNumber = clean(contactNumberField);
        HotelRoom room = checkInRoomComboBox.getValue();

        if (customerName.isBlank() || contactNumber.isBlank() || room == null) {
            setStatus("Enter customer name, contact number and selected room.");
            return;
        }

        LocalDate date = checkInDatePicker.getValue();
        if (date == null) {
            setStatus("Please select a check-in date.");
            return;
        }
        String checkInDate = date.toString();
        
        if (!contactNumber.matches("\\d{10}")) {
            if (contactErrorLabel != null) contactErrorLabel.setText("Please write a valid 10-digit phone number.");
            setStatus("Booking failed. Please write a valid phone number.");
            return;
        }
        
        synchronized (transactionLock) {
            if (!"Available".equals(room.getStatus())) {
                setStatus("This room is already occupied. Choose an available room.");
                refreshDerivedLists();
                return;
            }

            BookingRecord booking = store.createBooking(customerName, contactNumber, room, checkInDate, nightsSpinner.getValue());
            bookings.add(booking);
            replaceRoom(room, new HotelRoom(room.getRoomNumber(), room.getRoomType(), room.getPricePerDay(), "Occupied"));
            store.saveBookings(new ArrayList<>(bookings));
            store.saveRooms(new ArrayList<>(rooms));
            customerNameField.clear();
            contactNumberField.clear();
            refreshDerivedLists();
            refreshBookingPreview();
            refreshCheckoutPreview();
            refreshCustomerDatabasePreview();
            refreshAnalytics();
            setStatus("Booked room " + room.getRoomNumber() + " for " + customerName + ".");
        }
    }

    @FXML
    private void handleCheckoutSelection() {
        refreshCheckoutPreview();
    }

    @FXML
    private void handleCheckout() {
        BookingRecord selected = checkoutTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            setStatus("Select an active booking to check out.");
            return;
        }

        synchronized (transactionLock) {
            BookingRecord checkedOut = new BookingRecord(selected.getBookingId(), selected.getGuestName(),
                    selected.getContactNumber(), selected.getRoomNumber(), selected.getRoomType(),
                    selected.getCheckInDate(), selected.getNights(), selected.getTotalAmount(), "Checked Out");
            int bookingIndex = bookings.indexOf(selected);
            if (bookingIndex >= 0) {
                bookings.set(bookingIndex, checkedOut);
            }

            HotelRoom room = findRoom(selected.getRoomNumber());
            if (room != null) {
                replaceRoom(room, new HotelRoom(room.getRoomNumber(), room.getRoomType(), room.getPricePerDay(), "Available"));
            }

            store.saveBookings(new ArrayList<>(bookings));
            store.saveRooms(new ArrayList<>(rooms));
            refreshDerivedLists();
            refreshCheckoutPreview();
            refreshCustomerDatabasePreview();
            refreshAnalytics();
            setStatus("Checked out " + selected.getGuestName() + " from room " + selected.getRoomNumber() + ".");
        }
    }

    @FXML
    private void handleShowCheckoutBill() {
        BookingRecord selected = checkoutTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            setStatus("Select an active booking to export the bill.");
            return;
        }
        
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Save Invoice");
        fileChooser.setInitialFileName("Invoice_" + selected.getBookingId() + ".txt");
        fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("Text Files", "*.txt"));
        
        java.io.File file = fileChooser.showSaveDialog(checkoutTable.getScene().getWindow());
        if (file != null) {
            try {
                java.nio.file.Files.writeString(file.toPath(), checkoutDetailsArea.getText());
                setStatus("Invoice exported successfully to " + file.getName());
            } catch (java.io.IOException e) {
                setStatus("Failed to export invoice.");
            }
        }
    }

    @FXML
    private void handleCustomerDatabaseSelection() {
        refreshCustomerDatabasePreview();
    }

    @FXML
    private void handleLogout() {
        SceneManager.showLogin();
    }

    private void configureRoomTable() {
        roomNumberColumn.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        roomTypeColumn.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("pricePerDay"));
        availabilityColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void configureCheckoutTable() {
        bookingIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        customerColumn.setCellValueFactory(new PropertyValueFactory<>("guestName"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));
        bookedRoomColumn.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        bookingStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void configureCustomerDatabaseTable() {
        databaseCustomerColumn.setCellValueFactory(new PropertyValueFactory<>("guestName"));
        databaseContactColumn.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));
        databaseRoomColumn.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        databaseRoomTypeColumn.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        databaseAmountColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        databaseStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void refreshDerivedLists() {
        availableRooms.setAll(rooms.stream()
                .filter(room -> "Available".equals(room.getStatus()))
                .toList());
        activeBookings.setAll(bookings.stream()
                .filter(booking -> "Active".equals(booking.getStatus()))
                .toList());
        checkInRoomComboBox.setItems(availableRooms);
        if (availableRooms.isEmpty()) {
            checkInRoomComboBox.getSelectionModel().clearSelection();
            checkInRoomComboBox.setValue(null);
        } else {
            checkInRoomComboBox.getSelectionModel().selectFirst();
        }
        availableCountLabel.setText(String.valueOf(availableRooms.size()));
        occupiedCountLabel.setText(String.valueOf(countOccupiedRooms()));
        totalRoomsLabel.setText(String.valueOf(rooms.size()));
        dashboardAvailableLabel.setText(String.valueOf(availableRooms.size()));
        bookedRoomsLabel.setText(String.valueOf(countOccupiedRooms()));
        refreshAnalytics();
    }

    private long countOccupiedRooms() {
        return rooms.stream().filter(room -> "Occupied".equals(room.getStatus())).count();
    }

    private void refreshBookingPreview() {
        HotelRoom room = checkInRoomComboBox.getValue();
        String customerName = clean(customerNameField);
        String contactNumber = clean(contactNumberField);
        int nights = nightsSpinner.getValue() == null ? 1 : nightsSpinner.getValue();

        LocalDate date = checkInDatePicker.getValue();
        String dateString = date == null ? "[Not selected]" : date.toString();

        StringBuilder preview = new StringBuilder();
        preview.append("BOOKING PREVIEW\n\n");
        preview.append("GUEST INFORMATION\n");
        preview.append("Name:    ").append(customerName.isBlank() ? "[Not entered]" : customerName).append("\n");
        preview.append("Contact: ").append(contactNumber.isBlank() ? "[Not entered]" : contactNumber).append("\n\n");
        
        if (room == null) {
            preview.append("ROOM DETAILS\n");
            preview.append("-----------------------------------------\n");
            preview.append("Status:  No available room selected\n");
        } else {
            appendBillDetails(preview, room.getRoomNumber(), room.getRoomType(), dateString, room.getPricePerDay(), nights);
        }
        bookingDetailsArea.setText(preview.toString());
    }

    private void refreshCheckoutPreview() {
        BookingRecord selected = checkoutTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            checkoutDetailsArea.setText("Select an active booking to view checkout details.");
            return;
        }

        checkoutDetailsArea.setText("""
                FINAL CHECKOUT INVOICE
                
                GUEST INFORMATION
                Booking ID: %s
                Name:       %s
                Contact:    %s
                Status:     %s
                
                %s
                """.formatted(selected.getBookingId(), selected.getGuestName(), selected.getContactNumber(),
                selected.getStatus(), billDetailsFor(selected)));
    }

    private void refreshCustomerDatabasePreview() {
        BookingRecord selected = customerDatabaseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            customerDatabaseDetailsArea.setText("Select a customer record to view full booking details.");
            return;
        }

        customerDatabaseDetailsArea.setText("""
                CUSTOMER DATABASE RECORD
                
                GUEST INFORMATION
                Booking ID: %s
                Name:       %s
                Contact:    %s
                Status:     %s
                
                %s
                """.formatted(selected.getBookingId(), selected.getGuestName(), selected.getContactNumber(),
                selected.getStatus(), billDetailsFor(selected)));
    }

    private void refreshAnalytics() {
        revenueChart.getData().setAll(series("Revenue",
                revenueForRoomType("Single"),
                revenueForRoomType("Double"),
                revenueForRoomType("Deluxe")));
        bookingsChart.getData().setAll(series("Bookings",
                bookingsForRoomType("Single"),
                bookingsForRoomType("Double"),
                bookingsForRoomType("Deluxe")));
    }

    private double revenueForRoomType(String roomType) {
        return bookings.stream()
                .filter(booking -> roomType.equals(booking.getRoomType()))
                .mapToDouble(BookingRecord::getTotalAmount)
                .sum();
    }

    private long bookingsForRoomType(String roomType) {
        return bookings.stream()
                .filter(booking -> roomType.equals(booking.getRoomType()))
                .count();
    }

    private XYChart.Series<String, Number> series(String name, Number single, Number doubleRoom, Number deluxe) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(name);
        series.getData().add(new XYChart.Data<>("Single", single));
        series.getData().add(new XYChart.Data<>("Double", doubleRoom));
        series.getData().add(new XYChart.Data<>("Deluxe", deluxe));
        return series;
    }

    private void appendBillDetails(StringBuilder preview, String roomNumber, String roomType, String checkInDate, double pricePerDay, int nights) {
        double roomCharge = pricePerDay * nights;
        double gst = roomCharge * GST_RATE;
        double grandTotal = roomCharge + gst;

        preview.append("\nROOM SELECTION\n");
        preview.append("Date:    ").append(checkInDate).append("\n");
        preview.append("Room:    ").append(roomNumber).append(" (").append(roomType).append("\n");
        preview.append("Rate:    Rs. ").append(String.format("%.2f", pricePerDay)).append(" / night\n");
        preview.append("Nights:  ").append(nights).append("\n\n");
        preview.append("EXPECTED TOTAL: Rs. ").append(String.format("%.2f", grandTotal)).append("\n");
    }

    private String billDetailsFor(BookingRecord booking) {
        double pricePerDay = priceForRoomType(booking.getRoomType());
        double roomCharge = pricePerDay * booking.getNights();
        double gst = roomCharge * GST_RATE;
        double grandTotal = roomCharge + gst;
        
        return """
                ROOM DETAILS
                Date:    %s
                Room:    %s (%s)
                Rate:    Rs. %.2f / night
                Nights:  %d
                
                BILLING BREAKDOWN
                Subtotal:       Rs. %.2f
                Taxes (12%%):    Rs. %.2f
                
                GRAND TOTAL:    Rs. %.2f""".formatted(
                booking.getCheckInDate(), booking.getRoomNumber(), booking.getRoomType(),
                pricePerDay, booking.getNights(),
                roomCharge, gst, grandTotal);
    }

    private void updatePriceForRoomType() {
        pricePerDayField.setText(String.format("%.0f", priceForRoomType(roomTypeComboBox.getValue())));
    }

    private double priceForRoomType(String roomType) {
        if ("Double".equals(roomType)) {
            return DOUBLE_PRICE;
        }
        if ("Deluxe".equals(roomType)) {
            return DELUXE_PRICE;
        }
        return SINGLE_PRICE;
    }

    private void replaceRoom(HotelRoom oldRoom, HotelRoom newRoom) {
        int index = rooms.indexOf(oldRoom);
        if (index >= 0) {
            rooms.set(index, newRoom);
        }
    }

    private HotelRoom findRoom(String roomNumber) {
        return rooms.stream()
                .filter(room -> room.getRoomNumber().equalsIgnoreCase(roomNumber))
                .findFirst()
                .orElse(null);
    }

    private String clean(TextField field) {
        return field.getText() == null ? "" : field.getText().trim();
    }

    private void clearRoomForm() {
        roomNumberField.clear();
        roomTypeComboBox.getSelectionModel().selectFirst();
        updatePriceForRoomType();
        statusComboBox.getSelectionModel().selectFirst();
    }

    private void setStatus(String message) {
        statusLabel.setText(message);
    }
}
