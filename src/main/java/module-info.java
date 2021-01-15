module org.mg {
    requires javafx.controls;
    requires javafx.fxml;
    requires json.simple;
    requires itextpdf;


    opens org.mg to javafx.fxml;
    exports org.mg;
}