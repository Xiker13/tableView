package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.model.Person;
import org.example.database.GestorBD; // <-- GestorBD en carpeta database

import java.sql.SQLException;
import java.time.LocalDate;

public class ControladorPrincipal {

    @FXML private TextField txtNombre;
    @FXML private TextField txtLastName;
    @FXML private DatePicker datePicker;
    @FXML private TableView<Person> tableView;
    @FXML private TableColumn<Person, Integer> colId;
    @FXML private TableColumn<Person, String> colNombre;
    @FXML private TableColumn<Person, String> colLastName;
    @FXML private TableColumn<Person, String> colBirthDate;
    @FXML private Button btnAdd;
    @FXML private Button btnDelete;
    @FXML private Button btnRestore;

    private ObservableList<Person> data = FXCollections.observableArrayList();
    private GestorBD gestorBD;

    @FXML
    private void initialize() {
        try {
            gestorBD = new GestorBD();
            data.addAll(gestorBD.obtenerPersonas()); // Cargar datos desde DB
        } catch (SQLException e) {
            mostrarAlerta("Error al conectar con la base de datos: " + e.getMessage());
        }

        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getPersonId()).asObject());
        colNombre.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getFirstName()));
        colLastName.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getLastName()));
        colBirthDate.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getBirthDate() != null ? c.getValue().getBirthDate().toString() : ""));

        tableView.setItems(data);
    }

    @FXML
    private void onAdd() {
        String nombre = txtNombre.getText();
        String apellido = txtLastName.getText();
        LocalDate fecha = datePicker.getValue();

        if (nombre.isBlank() || apellido.isBlank() || fecha == null) {
            mostrarAlerta("Por favor, completa todos los campos.");
            return;
        }

        Person person = new Person(nombre, apellido, fecha);

        try {
            int id = gestorBD.agregarPersona(person); // Guardar en DB
            person.setPersonId(id);                    // Asignar ID generado
            data.add(person);                          // Agregar al TableView
        } catch (SQLException e) {
            mostrarAlerta("Error al guardar en la base de datos: " + e.getMessage());
        }

        txtNombre.clear();
        txtLastName.clear();
        datePicker.setValue(null);
    }

    @FXML
    private void onDelete() {
        Person selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                gestorBD.eliminarPersona(selected.getPersonId()); // Borrar de DB
                data.remove(selected);                              // Borrar de TableView
            } catch (SQLException e) {
                mostrarAlerta("Error al eliminar de la base de datos: " + e.getMessage());
            }
        } else {
            mostrarAlerta("Selecciona una fila para eliminar.");
        }
    }

    @FXML
    private void onRestore() {
        try {
            data.clear();
            data.addAll(gestorBD.obtenerPersonas()); // Recargar datos desde DB
        } catch (SQLException e) {
            mostrarAlerta("Error al restaurar datos: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
