module client.inventory {
    requires java.naming;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires WaifUPnP;
    requires java.desktop;


    opens client.inventory to javafx.fxml;
    exports client.inventory;
}