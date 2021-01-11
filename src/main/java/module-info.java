module org.mg {
    requires javafx.controls;
    requires javafx.fxml;
    requires json.simple;


    opens org.mg to javafx.fxml;
    exports org.mg;
}