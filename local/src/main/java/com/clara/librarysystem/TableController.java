package com.clara.librarysystem;

import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TableController {

    @FXML
    private TableView<Reservation> tableView;

    @FXML
    private TableColumn<Reservation, String> nameColumn;

    @FXML
    private TableColumn<Reservation, String> emailColumn;

    @FXML
    private TableColumn<Reservation, String> qcuianColumn;

    @FXML
    private TableColumn<Reservation, String> dateColumn;

    @FXML
    private TableColumn<Reservation, String> timeColumn;

    @FXML
    private TableColumn<Reservation, Integer> numStudentsColumn;

    @FXML
    private TableColumn<Reservation, String> roomColumn;

    @FXML
    private TableColumn<Reservation, String> purposeColumn;

    @FXML
    private Button refreshButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button editButton;

    @FXML
    private Button addButton;

    private final ObservableList<Reservation> reservationList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Setup column bindings
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        qcuianColumn.setCellValueFactory(new PropertyValueFactory<>("qcuian"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("reservationDate"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        numStudentsColumn.setCellValueFactory(new PropertyValueFactory<>("numStudents"));
        roomColumn.setCellValueFactory(new PropertyValueFactory<>("room"));
        purposeColumn.setCellValueFactory(new PropertyValueFactory<>("purpose"));

        // Load initial data
        loadData();

        // Button actions
        refreshButton.setOnAction(e -> handleRefresh());
        deleteButton.setOnAction(e -> deleteSelected());
        editButton.setOnAction(e -> editSelected());
        addButton.setOnAction(e -> openRoomReservationForm());
    }

    private void handleRefresh() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), tableView);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> {
            loadData(); // reload data
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), tableView);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.setOnFinished(e -> showAlert("Refreshed", "Your table has been refreshed successfully."));
            fadeIn.play();
        });
        fadeOut.play();
    }

    private void loadData() {
        String query = "SELECT * FROM reservations";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            reservationList.clear();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String qcuian = resultSet.getString("qcuian");
                String room = resultSet.getString("room");
                int numStudents = resultSet.getInt("num_students");
                String reservationDate = resultSet.getString("reservation_date");
                String time = resultSet.getString("time");
                String purpose = resultSet.getString("purpose");

                reservationList.add(new Reservation(id, name, email, qcuian, room, numStudents, reservationDate, time, purpose));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load reservations from database.");
        }

        tableView.setItems(reservationList);
    }

    private void deleteSelected() {
        Reservation selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            deleteReservationFromDatabase(selected);
            loadData();
        } else {
            showAlert("No selection", "Please select a reservation to delete.");
        }
    }

    private void deleteReservationFromDatabase(Reservation selected) {
        String query = "DELETE FROM reservations WHERE id = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, selected.getId());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                showAlert("Deleted", "Reservation deleted successfully.");
            } else {
                showAlert("Error", "Failed to delete reservation.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to delete reservation from database.");
        }
    }

    private void editSelected() {
        Reservation selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            openRoomReservationFormForEditing(selected);
        } else {
            showAlert("No selection", "Please select a reservation to edit.");
        }
    }

    private void openRoomReservationFormForEditing(Reservation selected) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/clara/librarysystem/RoomReservationForm.fxml"));
            Stage stage = new Stage();

            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("Edit Reservation");

            stage.initOwner(editButton.getScene().getWindow());
            stage.setResizable(false);
            stage.initStyle(javafx.stage.StageStyle.UTILITY);
            stage.show();

            RoomReservationFormController controller = loader.getController();
            controller.setCloseButtonAction(stage);
            controller.setReservationData(selected);
            controller.setRefreshTableCallback(this::loadData);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load Room Reservation Form.");
        }
    }

    private void openRoomReservationForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/clara/librarysystem/RoomReservationForm.fxml"));
            Stage stage = new Stage();

            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("Add Reservation");

            stage.initOwner(addButton.getScene().getWindow());
            stage.setResizable(false);
            stage.initStyle(javafx.stage.StageStyle.UTILITY);
            stage.show();

            RoomReservationFormController controller = loader.getController();
            controller.setCloseButtonAction(stage);
            controller.setRefreshTableCallback(this::loadData);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load Room Reservation Form.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
