package com.clara.librarysystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.sql.Date;

public class RoomReservationFormController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private ChoiceBox<String> qcuianChoiceBox;
    @FXML private ChoiceBox<String> roomChoiceBox;
    @FXML private ChoiceBox<Integer> numStudentsChoiceBox;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> timeComboBox;
    @FXML private TextArea purposeField;
    @FXML private Button clearButton;
    @FXML private Button submitButton;

    private final ObservableList<Reservation> reservationList = FXCollections.observableArrayList();
    private Runnable refreshTableCallback;
    private boolean isEditMode = false;
    private Reservation reservationToEdit;

    private final List<String> ALL_TIMES = Arrays.asList(
        "8:00 AM", "9:00 AM", "10:00 AM", "11:00 AM",
        "12:00 PM", "1:00 PM", "2:00 PM", "3:00 PM",
        "4:00 PM", "5:00 PM"
    );

    public void setCloseButtonAction(Stage stage) {
        stage.setOnCloseRequest(event -> stage.close());
    }

    public void setRefreshTableCallback(Runnable refreshTableCallback) {
        this.refreshTableCallback = refreshTableCallback;
    }

    public void setReservationToEdit(Reservation reservation) {
        this.reservationToEdit = reservation;
        this.isEditMode = true;

        nameField.setText(reservation.getName());
        emailField.setText(reservation.getEmail());
        qcuianChoiceBox.setValue(reservation.getQcuian());
        roomChoiceBox.setValue(reservation.getRoom());
        numStudentsChoiceBox.setValue(reservation.getNumStudents());
        datePicker.setValue(LocalDate.parse(reservation.getReservationDate()));
        timeComboBox.setValue(reservation.getTime());
        purposeField.setText(reservation.getPurpose());

        submitButton.setText("Update");
    }

    public void setReservationData(Reservation reservation) {
        setReservationToEdit(reservation);
    }

    public void setSaveButtonAction(Runnable saveAction) {
        submitButton.setOnAction(e -> saveAction.run());
    }

    @FXML
    private void initialize() {
        qcuianChoiceBox.getItems().addAll("Students", "Admin", "Faculty");
        roomChoiceBox.getItems().addAll("Room A", "Room B", "Room C", "Room D");
        for (int i = 4; i <= 10; i++) {
            numStudentsChoiceBox.getItems().add(i);
        }

        timeComboBox.setDisable(true);

        clearButton.setOnAction(e -> clearForm());
        submitButton.setOnAction(e -> handleSubmit());

        datePicker.valueProperty().addListener((obs, oldDate, newDate) -> updateAvailableTimes());
        roomChoiceBox.valueProperty().addListener((obs, oldRoom, newRoom) -> updateAvailableTimes());
    }

    private void updateAvailableTimes() {
        timeComboBox.getItems().clear();

        LocalDate selectedDate = datePicker.getValue();
        String selectedRoom = roomChoiceBox.getValue();

        if (selectedDate == null || selectedRoom == null) {
            timeComboBox.setDisable(true);
            return;
        }

        List<String> bookedTimes = getBookedTimesForDate(selectedDate, selectedRoom);
        List<String> availableTimes = new ArrayList<>();

        for (String time : ALL_TIMES) {
            if (!bookedTimes.contains(time)) {
                availableTimes.add(time);
            }
        }

        timeComboBox.getItems().addAll(availableTimes);
        timeComboBox.setDisable(availableTimes.isEmpty());
    }

    private List<String> getBookedTimesForDate(LocalDate date, String room) {
        List<String> booked = new ArrayList<>();
        String query = "SELECT time FROM reservations WHERE reservation_date = ? AND room = ?";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setDate(1, Date.valueOf(date));
            statement.setString(2, room);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                booked.add(rs.getString("time"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return booked;
    }

    private void clearForm() {
        nameField.clear();
        emailField.clear();
        qcuianChoiceBox.setValue(null);
        roomChoiceBox.setValue(null);
        numStudentsChoiceBox.setValue(null);
        datePicker.setValue(null);
        timeComboBox.getItems().clear();
        timeComboBox.setDisable(true);
        purposeField.clear();
        isEditMode = false;
        reservationToEdit = null;
        submitButton.setText("Submit");
    }

    private void handleSubmit() {
        if (nameField.getText().isEmpty() || emailField.getText().isEmpty() ||
            qcuianChoiceBox.getValue() == null || roomChoiceBox.getValue() == null ||
            numStudentsChoiceBox.getValue() == null || datePicker.getValue() == null ||
            timeComboBox.getValue() == null || purposeField.getText().isEmpty()) {

            showAlert(AlertType.ERROR, "Form Error", "Please complete all fields.");
            return;
        }

        if (isEditMode) {
            updateReservationInDatabase();
        } else {
            insertReservationToDatabase();
        }

        fetchReservationsFromDatabase();

        if (refreshTableCallback != null) {
            refreshTableCallback.run();
        }

        clearForm();
    }

    private void insertReservationToDatabase() {
        String query = "INSERT INTO reservations (name, email, qcuian, room, num_students, reservation_date, time, purpose) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, nameField.getText());
            statement.setString(2, emailField.getText());
            statement.setString(3, qcuianChoiceBox.getValue());
            statement.setString(4, roomChoiceBox.getValue());
            statement.setInt(5, numStudentsChoiceBox.getValue());
            statement.setDate(6, Date.valueOf(datePicker.getValue()));
            statement.setString(7, timeComboBox.getValue());
            statement.setString(8, purposeField.getText());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                showAlert(AlertType.INFORMATION, "Reservation Saved", "Your reservation has been successfully saved.");
            } else {
                showAlert(AlertType.ERROR, "Error", "Failed to save the reservation.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Database Error", "Failed to connect to the database.");
        }
    }

    private void updateReservationInDatabase() {
        if (reservationToEdit == null || reservationToEdit.getId() == 0) {
            showAlert(AlertType.ERROR, "Error", "Invalid reservation ID.");
            return;
        }

        String query = "UPDATE reservations SET name = ?, email = ?, qcuian = ?, room = ?, num_students = ?, reservation_date = ?, time = ?, purpose = ? WHERE id = ?";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, nameField.getText());
            statement.setString(2, emailField.getText());
            statement.setString(3, qcuianChoiceBox.getValue());
            statement.setString(4, roomChoiceBox.getValue());
            statement.setInt(5, numStudentsChoiceBox.getValue());
            statement.setDate(6, Date.valueOf(datePicker.getValue()));
            statement.setString(7, timeComboBox.getValue());
            statement.setString(8, purposeField.getText());
            statement.setInt(9, reservationToEdit.getId());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Reservation Updated");
                alert.setHeaderText(null);
                alert.setContentText("Reservation has been successfully updated.");
                alert.showAndWait();

                // Close the window after OK is clicked
                Stage stage = (Stage) submitButton.getScene().getWindow();
                stage.close();

            } else {
                showAlert(AlertType.ERROR, "Error", "Failed to update the reservation.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Database Error", "Failed to update reservation.");
        }
    }

    private void fetchReservationsFromDatabase() {
        String query = "SELECT * FROM reservations";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            reservationList.clear();

            while (resultSet.next()) {
                reservationList.add(new Reservation(
                    resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getString("email"),
                    resultSet.getString("qcuian"),
                    resultSet.getString("room"),
                    resultSet.getInt("num_students"),
                    resultSet.getString("reservation_date"),
                    resultSet.getString("time"),
                    resultSet.getString("purpose")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Database Error", "Failed to fetch data from the database.");
        }
    }

    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
