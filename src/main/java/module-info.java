module org.ordermanagement {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.java;
    requires java.desktop;
    opens org.ordermanagement.presentation to javafx.fxml;
    opens org.ordermanagement to javafx.fxml;
    opens org.ordermanagement.model to javafx.base;
    exports org.ordermanagement;
    exports org.ordermanagement.presentation;
    exports org.ordermanagement.model;
}