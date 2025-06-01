/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Database;
import model.SubmittedDocument;

public class SubmittedDocumentDao {
    private Database submitDocumentConnection;
    
    public SubmittedDocumentDao()throws ClassNotFoundException, SQLException {
        this.submitDocumentConnection = new Database();
    }
    public void register(int idInscripcion) throws Exception {
        Connection conn = null;
        PreparedStatement psSelect = null;
        PreparedStatement psInsert = null;
        PreparedStatement psCheck = null;
        ResultSet rs = null;
        ResultSet rsCheck = null;

        try {
            conn = submitDocumentConnection.getConnection(); 

            String selectSql = "SELECT idtipo_documento FROM tipo_documento";
            psSelect = conn.prepareStatement(selectSql);
            rs = psSelect.executeQuery();

            String checkSql = """
                SELECT 1 FROM documentacion_entregada 
                WHERE id_inscripcion = ? AND id_tipodocumento = ?
            """;
            psCheck = conn.prepareStatement(checkSql);

            String insertSql = """
                INSERT INTO documentacion_entregada (id_inscripcion, id_tipodocumento, fecha_entrega, estado)
                VALUES (?, ?, NOW(), 0)
            """;
            psInsert = conn.prepareStatement(insertSql);

            while (rs.next()) {
                int idTipoDocumento = rs.getInt("idtipo_documento");

                psCheck.setInt(1, idInscripcion);
                psCheck.setInt(2, idTipoDocumento);
                rsCheck = psCheck.executeQuery();

                if (!rsCheck.next()) {
                    psInsert.setInt(1, idInscripcion);
                    psInsert.setInt(2, idTipoDocumento);
                    psInsert.executeUpdate();
                }

                if (rsCheck != null) {
                    rsCheck.close(); // cerrar para el siguiente ciclo
                }
            }

        } catch (SQLException e) {
            throw new Exception("Error al registrar documentación: " + e.getMessage(), e);
        } finally {
            if (rsCheck != null) rsCheck.close();
            if (rs != null) rs.close();
            if (psSelect != null) psSelect.close();
            if (psInsert != null) psInsert.close();
            if (psCheck != null) psCheck.close();
            if (conn != null) conn.close();
        }
    }

    
    public List<SubmittedDocument> getByInscripcion(int idInscripcion) throws Exception {
        List<SubmittedDocument> documentos = new ArrayList<>();
        String sql = "SELECT * FROM documentacion_entregada WHERE id_inscripcion = ?";

        try (Connection conn = submitDocumentConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idInscripcion);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                SubmittedDocument doc = new SubmittedDocument();
                doc.setId_inscripcion(rs.getInt("id_inscripcion"));
                doc.setId_tipodocumento(rs.getInt("id_tipodocumento"));
                doc.setFecha_entrega(rs.getDate("fecha_entrega"));
                doc.setEstado(rs.getInt("estado"));
                documentos.add(doc);
            }
        }

        return documentos;
    }
    public void update(SubmittedDocument doc) throws Exception {
        String sql = """
            UPDATE documentacion_entregada 
            SET fecha_entrega = ?, estado = ? 
            WHERE id_inscripcion = ? AND id_tipodocumento = ?
        """;

        try (Connection conn = submitDocumentConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, new java.sql.Timestamp(doc.getFecha_entrega().getTime()));
            ps.setInt(2, doc.getEstado());
            ps.setInt(3, doc.getId_inscripcion());
            ps.setInt(4, doc.getId_tipodocumento());

            ps.executeUpdate();
        }
    }
}
