module org.mg {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.mg to javafx.fxml;
    exports org.mg;
}