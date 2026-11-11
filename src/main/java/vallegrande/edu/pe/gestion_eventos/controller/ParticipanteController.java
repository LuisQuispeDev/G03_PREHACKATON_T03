package vallegrande.edu.pe.gestion_eventos.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import vallegrande.edu.pe.gestion_eventos.dao.ParticipanteDAO;
import vallegrande.edu.pe.gestion_eventos.model.Participante;

import java.util.Optional;

public class ParticipanteController {

    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtDni;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtInstitucion;
    @FXML private TextField txtBuscar;

    @FXML private TableView<Participante> tablaParticipantes;
    @FXML private TableColumn<Participante, Integer> colId;
    @FXML private TableColumn<Participante, String> colNombres;
    @FXML private TableColumn<Participante, String> colApellidos;
    @FXML private TableColumn<Participante, String> colDni;
    @FXML private TableColumn<Participante, String> colCorreo;
    @FXML private TableColumn<Participante, String> colTelefono;
    @FXML private TableColumn<Participante, String> colInstitucion;

    @FXML private Label lblMensaje;

    private final ParticipanteDAO participanteDAO = new ParticipanteDAO();
    private final ObservableList<Participante> listaParticipantes = FXCollections.observableArrayList();
    private Participante participanteSeleccionado = null;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idParticipante"));
        colNombres.setCellValueFactory(new PropertyValueFactory<>("nombres"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colInstitucion.setCellValueFactory(new PropertyValueFactory<>("institucion"));

        cargarTabla();

        tablaParticipantes.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                participanteSeleccionado = newSel;
                txtNombres.setText(newSel.getNombres());
                txtApellidos.setText(newSel.getApellidos());
                txtDni.setText(newSel.getDni());
                txtCorreo.setText(newSel.getCorreo());
                txtTelefono.setText(newSel.getTelefono());
                txtInstitucion.setText(newSel.getInstitucion());
                lblMensaje.setText("Editando participante ID: " + newSel.getIdParticipante());
            }
        });
    }

    private void cargarTabla() {
        listaParticipantes.setAll(participanteDAO.listar());
        tablaParticipantes.setItems(listaParticipantes);
    }

    @FXML
    private void guardarParticipante() {
        String nombres = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String dni = txtDni.getText().trim();
        String correo = txtCorreo.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String institucion = txtInstitucion.getText().trim();

        if (nombres.isEmpty() || apellidos.isEmpty() || dni.isEmpty() ||
                correo.isEmpty() || telefono.isEmpty() || institucion.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos vacíos", "Por favor, complete todos los campos obligatorios.");
            return;
        }

        if (!dni.matches("\\d{8}")) {
            mostrarAlerta(Alert.AlertType.WARNING, "DNI inválido", "El DNI debe contener exactamente 8 dígitos numéricos.");
            return;
        }

        if (!correo.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            mostrarAlerta(Alert.AlertType.WARNING, "Correo inválido", "Ingrese un correo electrónico válido (ejemplo@dominio.com).");
            return;
        }

        if (!telefono.matches("\\d{7,15}")) {
            mostrarAlerta(Alert.AlertType.WARNING, "Teléfono inválido", "El teléfono debe contener entre 7 y 15 dígitos numéricos.");
            return;
        }

        int idActual = (participanteSeleccionado == null) ? 0 : participanteSeleccionado.getIdParticipante();
        if (participanteDAO.existeDni(dni, idActual)) {
            mostrarAlerta(Alert.AlertType.ERROR, "DNI duplicado", "Ya existe otro participante registrado con el DNI " + dni + ".");
            return;
        }

        if (participanteSeleccionado == null) {
            Participante nuevo = new Participante(0, nombres, apellidos, dni, correo, telefono, institucion);
            if (participanteDAO.insertar(nuevo)) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Registro exitoso", "Participante registrado correctamente.");
                limpiarFormulario();
                cargarTabla();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo registrar el participante en la base de datos.");
            }
        } else {
            participanteSeleccionado.setNombres(nombres);
            participanteSeleccionado.setApellidos(apellidos);
            participanteSeleccionado.setDni(dni);
            participanteSeleccionado.setCorreo(correo);
            participanteSeleccionado.setTelefono(telefono);
            participanteSeleccionado.setInstitucion(institucion);

            if (participanteDAO.actualizar(participanteSeleccionado)) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Actualización exitosa", "Datos del participante actualizados.");
                limpiarFormulario();
                cargarTabla();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudieron actualizar los datos.");
            }
        }
    }

    @FXML
    private void eliminarParticipante() {
        if (participanteSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin selección", "Seleccione un participante de la tabla para eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar participante?");
        confirmacion.setContentText("¿Está seguro de eliminar a " + participanteSeleccionado.getNombres() + " " + participanteSeleccionado.getApellidos() + "?");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            if (participanteDAO.eliminar(participanteSeleccionado.getIdParticipante())) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Eliminado", "Participante eliminado correctamente.");
                limpiarFormulario();
                cargarTabla();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo eliminar el participante.");
            }
        }
    }

    @FXML
    private void buscarParticipantes() {
        String filtro = txtBuscar.getText().trim();
        if (filtro.isEmpty()) {
            cargarTabla();
            lblMensaje.setText("Mostrando todos los participantes.");
        } else {
            listaParticipantes.setAll(participanteDAO.buscar(filtro));
            tablaParticipantes.setItems(listaParticipantes);
            lblMensaje.setText("Resultados encontrados: " + listaParticipantes.size());
        }
    }

    @FXML
    private void limpiarFormulario() {
        txtNombres.clear();
        txtApellidos.clear();
        txtDni.clear();
        txtCorreo.clear();
        txtTelefono.clear();
        txtInstitucion.clear();
        participanteSeleccionado = null;
        tablaParticipantes.getSelectionModel().clearSelection();
        lblMensaje.setText("Formulario limpio. Listo para nuevo registro.");
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
        lblMensaje.setText(mensaje);
    }
}
