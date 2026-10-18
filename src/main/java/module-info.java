module org.csillagkoz {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    opens org.csillagkoz to javafx.fxml;
    exports org.csillagkoz;
    exports org.csillagkoz.controller;
    opens org.csillagkoz.controller to javafx.fxml;
}