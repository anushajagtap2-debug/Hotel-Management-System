# OOSD Lab Mini Project - Avery Inn

This is a JavaFX mini project for hotel room management.

## Login

- Username: `admin`
- Password: `hotel123`
- User login: `user`
- User password: `user123`

The login is intentionally hardcoded for lab demonstration. Admin can access everything. User can only use check-in and checkout, and cannot see Add Room or Customer Database.

## Features Covered

- JavaFX GUI with multiple screens: login and dashboard.
- FXML screens compatible with Scene Builder.
- Different layouts: `BorderPane`, `VBox`, `HBox`, `GridPane` and `TabPane`.
- CSS styling through `app.css`.
- Maven project setup through `pom.xml`.
- Permanent storage in files:
  - `data/rooms.csv` stores room number, room type, price per day and availability status.
  - `data/bookings.csv` stores customer and booking history.
- Hotel management features:
  - Admin-only dashboard showing total rooms, available rooms and booked rooms.
  - Show main options after login: Dashboard, Add Room, Check In, Check Out, Customer Database and Analytics.
  - User login only shows Check In and Check Out.
  - Add and delete rooms.
  - Display room number, room type, price per day and availability status.
  - Fixed room prices:
    - Single: Rs. 3000
    - Double: Rs. 5000
    - Deluxe: Rs. 10000
  - View all rooms or show available rooms only.
  - Capture customer name, contact number and selected room number.
  - Display customer booking details in the GUI.
  - Book rooms with a button click.
  - Prevent booking of already occupied rooms by only showing available rooms at check-in.
  - Checkout customers and release the room back to Available.
  - Customer database section showing customer name, contact number, room, total amount and booking status.
  - Admin-only revenue and analytics tab showing side-by-side graphs for revenue by room type and bookings by room type.
  - Bill details with room charge, GST 12% and grand total.

## How To Run

Run from this folder:

```powershell
mvn clean javafx:run
```

If using IntelliJ IDEA, Eclipse or NetBeans, import the folder as a Maven project and run `com.lab.hotel.App`.

