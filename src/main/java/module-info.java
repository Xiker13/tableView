module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens org.example to javafx.fxml;
    exports org.example;
    exports model;
    opens model to javafx.fxml;
    exports org.example.controller;
    opens org.example.controller to javafx.fxml;
}