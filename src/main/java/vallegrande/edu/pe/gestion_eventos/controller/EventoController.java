package vallegrande.edu.pe.gestion_eventos.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javafx.scene.control.cell.PropertyValueFactory;

import vallegrande.edu.pe.gestion_eventos.model.Evento;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.LocalDate;

public class EventoController {

    // CONEXIÓN MYSQL

    private final String URL =
            "jdbc:mysql://localhost:3306/db_eventos?serverTimezone=America/Lima";

    private final String USUARIO = "root";

    private final String CLAVE = "123456";

    private Connection conectar() throws SQLException {

        return DriverManager.getConnection(
                URL, USUARIO, CLAVE
        );
    }

    // COMPONENTES DE LA VISTA

    @FXML
    private TextField txtNombre;

    @FXML
    private TextArea txtDescripcion;

    @FXML
    private DatePicker dpFecha;

    @FXML
    private TextField txtLugar;

    @FXML
    private ComboBox<String> cbModalidad;

    @FXML
    private ComboBox<String> cbEstado;

    @FXML
    private TextField txtBuscar;

    @FXML
    private TableView<Evento> tablaEventos;

    @FXML
    private TableColumn<Evento, Integer> colId;

    @FXML
    private TableColumn<Evento, String> colNombre;

    @FXML
    private TableColumn<Evento, String> colDescripcion;

    @FXML
    private TableColumn<Evento, LocalDate> colFecha;

    @FXML
    private TableColumn<Evento, String> colLugar;

    @FXML
    private TableColumn<Evento, String> colModalidad;

    @FXML
    private TableColumn<Evento, String> colEstado;

    @FXML
    private Label lblMensaje;

    private final ObservableList<Evento> listaEventos =
            FXCollections.observableArrayList();

    private int idSeleccionado = 0;

    // INICIALIZAR VISTA

    @FXML
    public void initialize() {

        cbModalidad.setItems(
                FXCollections.observableArrayList(
                        "Presencial",
                        "Virtual",
                        "Híbrida"
                )
        );

        cbEstado.setItems(
                FXCollections.observableArrayList(
                        "Programado",
                        "En curso",
                        "Finalizado",
                        "Cancelado"
                )
        );

        colId.setCellValueFactory(
                new PropertyValueFactory<>("idEvento")
        );

        colNombre.setCellValueFactory(
                new PropertyValueFactory<>("nombre")
        );

        colDescripcion.setCellValueFactory(
                new PropertyValueFactory<>("descripcion")
        );

        colFecha.setCellValueFactory(
                new PropertyValueFactory<>("fecha")
        );

        colLugar.setCellValueFactory(
                new PropertyValueFactory<>("lugar")
        );

        colModalidad.setCellValueFactory(
                new PropertyValueFactory<>("modalidad")
        );

        colEstado.setCellValueFactory(
                new PropertyValueFactory<>("estado")
        );

        tablaEventos.setItems(listaEventos);

        tablaEventos.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, anterior, seleccionado) -> {

                    if (seleccionado != null) {
                        cargarDatos(seleccionado);
                    }
                });

        listarEventos();
    }

    // REGISTRAR EVENTO

    @FXML
    private void guardarEvento() {

        if (!validarCampos()) {
            return;
        }

        String sql;

        if (idSeleccionado == 0) {

            sql = "INSERT INTO eventos " +
                    "(nombre, descripcion, fecha, lugar, modalidad, estado) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

        } else {

            sql = "UPDATE eventos SET nombre=?, descripcion=?, " +
                    "fecha=?, lugar=?, modalidad=?, estado=? " +
                    "WHERE id_evento=?";
        }

        try (Connection cn = conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, txtNombre.getText().trim());
            ps.setString(2, txtDescripcion.getText().trim());
            ps.setDate(3, java.sql.Date.valueOf(dpFecha.getValue()));
            ps.setString(4, txtLugar.getText().trim());
            ps.setString(5, cbModalidad.getValue());
            ps.setString(6, cbEstado.getValue());

            if (idSeleccionado != 0) {
                ps.setInt(7, idSeleccionado);
            }

            ps.executeUpdate();

            mostrarMensaje(
                    idSeleccionado == 0
                            ? "Evento registrado correctamente."
                            : "Evento actualizado correctamente.",
                    false
            );

            limpiarFormulario();
            listarEventos();

        } catch (SQLException e) {

            mostrarMensaje(
                    "Error al guardar: " + e.getMessage(),
                    true
            );
        }
    }

    // LISTAR EVENTOS

    private void listarEventos() {

        listaEventos.clear();

        String sql = "SELECT * FROM eventos ORDER BY id_evento DESC";

        try (Connection cn = conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Evento evento = new Evento(
                        rs.getInt("id_evento"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getDate("fecha").toLocalDate(),
                        rs.getString("lugar"),
                        rs.getString("modalidad"),
                        rs.getString("estado")
                );

                listaEventos.add(evento);
            }

        } catch (SQLException e) {

            mostrarMensaje(
                    "Error al listar: " + e.getMessage(),
                    true
            );
        }
    }

    // BUSCAR EVENTOS

    @FXML
    private void buscarEventos() {

        listaEventos.clear();

        String sql = "SELECT * FROM eventos WHERE " +
                "nombre LIKE ? OR lugar LIKE ? OR " +
                "modalidad LIKE ? OR estado LIKE ? " +
                "ORDER BY id_evento DESC";

        String buscar = "%" + txtBuscar.getText().trim() + "%";

        try (Connection cn = conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            for (int i = 1; i <= 4; i++) {
                ps.setString(i, buscar);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Evento evento = new Evento(
                        rs.getInt("id_evento"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getDate("fecha").toLocalDate(),
                        rs.getString("lugar"),
                        rs.getString("modalidad"),
                        rs.getString("estado")
                );

                listaEventos.add(evento);
            }

        } catch (SQLException e) {

            mostrarMensaje(
                    "Error en la búsqueda: " + e.getMessage(),
                    true
            );
        }
    }

    // ELIMINAR EVENTO

    @FXML
    private void eliminarEvento() {

        Evento seleccionado =
                tablaEventos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {

            mostrarMensaje(
                    "Selecciona un evento para eliminar.",
                    true
            );

            return;
        }

        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);

        alerta.setTitle("Eliminar evento");
        alerta.setHeaderText("¿Estás seguro de eliminar este evento?");
        alerta.setContentText(seleccionado.getNombre());

        if (alerta.showAndWait().orElse(ButtonType.CANCEL)
                != ButtonType.OK) {
            return;
        }

        String sql = "DELETE FROM eventos WHERE id_evento=?";

        try (Connection cn = conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, seleccionado.getIdEvento());

            ps.executeUpdate();

            mostrarMensaje(
                    "Evento eliminado correctamente.",
                    false
            );

            limpiarFormulario();
            listarEventos();

        } catch (SQLException e) {

            mostrarMensaje(
                    "Error al eliminar: " + e.getMessage(),
                    true
            );
        }
    }

    // CARGAR DATOS PARA EDITAR

    private void cargarDatos(Evento evento) {

        idSeleccionado = evento.getIdEvento();

        txtNombre.setText(evento.getNombre());

        txtDescripcion.setText(evento.getDescripcion());

        dpFecha.setValue(evento.getFecha());

        txtLugar.setText(evento.getLugar());

        cbModalidad.setValue(evento.getModalidad());

        cbEstado.setValue(evento.getEstado());
    }

    // LIMPIAR FORMULARIO

    @FXML
    private void limpiarFormulario() {

        idSeleccionado = 0;

        txtNombre.clear();

        txtDescripcion.clear();

        dpFecha.setValue(null);

        txtLugar.clear();

        cbModalidad.setValue(null);

        cbEstado.setValue(null);

        tablaEventos.getSelectionModel().clearSelection();

    }

    // VALIDAR CAMPOS

    private boolean validarCampos() {

        if (txtNombre.getText().isBlank()
                || txtDescripcion.getText().isBlank()
                || dpFecha.getValue() == null
                || txtLugar.getText().isBlank()
                || cbModalidad.getValue() == null
                || cbEstado.getValue() == null) {

            mostrarMensaje(
                    "Todos los campos son obligatorios.",
                    true
            );

            return false;
        }

        if (txtNombre.getText().trim().length() < 3) {

            mostrarMensaje(
                    "El nombre debe tener al menos 3 caracteres.",
                    true
            );

            return false;
        }

        if (dpFecha.getValue().isBefore(LocalDate.now())) {

            mostrarMensaje(
                    "La fecha del evento no puede ser anterior a hoy.",
                    true
            );

            return false;
        }

        return true;
    }

    // MOSTRAR MENSAJES

    private void mostrarMensaje(String mensaje, boolean error) {

        lblMensaje.setText(mensaje);

        lblMensaje.setStyle(
                error
                        ? "-fx-text-fill: #dc2626;"
                        : "-fx-text-fill: #15803d;"
        );
    }
}