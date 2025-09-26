module com.clara.librarysystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires java.sql; 

    opens com.clara.librarysystem to javafx.fxml;
    exports com.clara.librarysystem;
}
