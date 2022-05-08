module com.example.finalpro {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    requires org.kordamp.bootstrapfx.core;

    opens com.example.finalpro to javafx.fxml;
    exports com.example.finalpro;
}