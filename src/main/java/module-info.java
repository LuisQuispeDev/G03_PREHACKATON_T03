module vallegrande.edu.pe.gestion_eventos {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens vallegrande.edu.pe.gestion_eventos.controller to javafx.fxml;
    opens vallegrande.edu.pe.gestion_eventos.view to javafx.fxml;

    exports vallegrande.edu.pe.gestion_eventos.controller;
    exports vallegrande.edu.pe.gestion_eventos.model;
    exports vallegrande.edu.pe.gestion_eventos.view;
}