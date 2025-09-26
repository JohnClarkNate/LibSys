package com.clara.librarysystem;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ReservationSummaryController {

    @FXML
    private TextArea summaryTextArea;

    @FXML
    private Button closeButton;

    @FXML
    private void initialize() {
        // Set the close button to close the modal window
        closeButton.setOnAction(event -> {
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.close();
        });

        // Make the TextArea non-editable
        summaryTextArea.setEditable(false);
    }

    /**
     * Set the reservation summary content to display in the TextArea.
     * @param summary the summary text to display
     */
    public void setSummary(String summary) {
        summaryTextArea.setText(summary);
    }
}
