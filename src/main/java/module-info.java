module vallegrande.edu.pe.gestion_eventos {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires kotlin.stdlib;

    opens vallegrande.edu.pe.gestion_eventos.controller to javafx.fxml;
    opens vallegrande.edu.pe.gestion_eventos.view to javafx.fxml;
    opens vallegrande.edu.pe.gestion_eventos.model to javafx.base;

    exports vallegrande.edu.pe.gestion_eventos.controller;
    exports vallegrande.edu.pe.gestion_eventos.model;
    exports vallegrande.edu.pe.gestion_eventos.view;
    exports vallegrande.edu.pe.gestion_eventos.dao;
}
