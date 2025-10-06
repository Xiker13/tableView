package org.example.database;

import model.Person;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GestorBD {

    private static final String URL = "jdbc:mariadb://localhost:3306/personadb";
    private static final String USER = "root";
    private static final String PASSWORD = "admin";

    private Connection conn;

    public GestorBD() throws SQLException {
        conn = DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public void cerrarConexion() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    public int agregarPersona(Person p) throws SQLException {
        String sql = "INSERT INTO personas (first_name, last_name, birth_date) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, p.getFirstName());
            stmt.setString(2, p.getLastName());
            stmt.setDate(3, Date.valueOf(p.getBirthDate()));
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    public List<Person> obtenerPersonas() throws SQLException {
        List<Person> lista = new ArrayList<>();
        String sql = "SELECT id, first_name, last_name, birth_date FROM personas";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Person p = new Person(rs.getString("first_name"), rs.getString("last_name"), rs.getDate("birth_date").toLocalDate());
                p.setPersonId(rs.getInt("id"));
                lista.add(p);
            }
        }
        return lista;
    }

    public void eliminarPersona(int id) throws SQLException {
        String sql = "DELETE FROM personas WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
