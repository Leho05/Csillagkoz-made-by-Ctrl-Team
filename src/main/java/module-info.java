module org.csillagkoz {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.csillagkoz to javafx.fxml;
    exports org.csillagkoz;
}