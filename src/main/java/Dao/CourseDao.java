/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import model.Course;
import model.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mauricioteranlimari
 */
public class CourseDao {
    
    private Database CourseConnection;

    private String[] optionsGrade = {"Primero", "Segundo", "Tercero", "Cuarto", "Quinto", "Sexto"};

    private String[] optionsLevel = {"Primaria","Secundaria"};
    
    public CourseDao() throws ClassNotFoundException, SQLException {

        this.CourseConnection = new Database();

    }

    public boolean register(Course course) {

        try {

            String SQL = "INSERT INTO curso(nivel, grado, paralelo) VALUES (?, ?, ?)";

            Connection connection = this.CourseConnection.getConnection();

            PreparedStatement sentence = connection.prepareStatement(SQL);

            sentence.setInt(1, course.getNivel());
            sentence.setInt(2, course.getGrado());
            sentence.setString(3, String.valueOf(course.getParalelo()));

            sentence.executeUpdate();
            sentence.close();

            return true;

        } catch (Exception e) {
            System.err.println("Ocurrio un error al registrar curso");
            System.err.println("Mensaje del error: " + e.getMessage());
            System.err.println("Detalle del error: ");

            e.printStackTrace();

            return false;
        }
    }

    public List<Course> toList() {

        List<Course> listCourse = new ArrayList<>();

        try {

            String SQL = "SELECT DISTINCT curso.*, CONCAT(usuario.nombre, ' ', usuario.apellido), asesor.fecha_inicio,asesor.fecha_fin "
                    + "FROM curso "
                    + "LEFT JOIN asesor ON curso.idcurso = asesor.idcurso "
                    + "LEFT JOIN usuario ON asesor.idusuario = usuario.idusuario";

            Connection connection = this.CourseConnection.getConnection();

            PreparedStatement sentence = connection.prepareStatement(SQL);

            ResultSet data = sentence.executeQuery();

            while (data.next()) {

                Course course = new Course();

                course.setIdcurso(data.getInt(1));
                course.setNivel(data.getInt(2));
                course.setGrado(data.getInt(3));
                course.setParalelo(data.getString(4).charAt(0));
                course.setCupo_max(data.getInt(5));
                course.setAdmite_nuevos(data.getBoolean(6));
                String nameA = data.getString(7);

                if (nameA != null) {
                    course.setAsesor(nameA);
                } else {
                    course.setAsesor(null);
                }

                if(data.getDate(8) != null){
                    course.setFechai(data.getDate(8).toLocalDate());
                }else{
                    course.setFechai(null);
                }
                
                if(data.getDate(9) != null){
                    course.setFechaf(data.getDate(9).toLocalDate());
                }else{
                    course.setFechaf(null);
                }
                
                

                listCourse.add(course);

            }

        } catch (Exception e) {
            System.err.println("Ocurrio un error al listar cursos");
            System.err.println("Mensaje del error: " + e.getMessage());
            System.err.println("Detalle del error: ");

            e.printStackTrace();
        }
        return listCourse;
    }

    public boolean editCourse(Course course) {

        try {

            String SQL = "UPDATE curso SET nivel = ?, grado = ?, paralelo = ?"
                    + "WHERE idcurso = ?";

            Connection connection = this.CourseConnection.getConnection();

            PreparedStatement sentence = connection.prepareStatement(SQL);

            sentence.setInt(1, course.getNivel());
            sentence.setInt(2, course.getGrado());
            sentence.setString(3, String.valueOf(course.getParalelo()));

            sentence.setInt(4, course.getIdcurso());

            sentence.executeUpdate();

            sentence.close();

            return true;

        } catch (Exception e) {

            System.err.println("Ocurrio un error al editar el curso");
            System.err.println("Mensaje del error: " + e.getMessage());
            System.err.println("Detalle del error: ");

            e.printStackTrace();

            return false;
        }
    }

    public boolean deleteCourse(int idcourse) {

        try {

            String SQL = "DELETE FROM curso WHERE idcurso = ?";

            Connection connection = this.CourseConnection.getConnection();

            PreparedStatement sentence = connection.prepareStatement(SQL);

            sentence.setInt(1, idcourse);

            sentence.executeUpdate();

            sentence.close();

            return true;

        } catch (Exception e) {

            System.err.println("Ocurrio un error al eliminar el curso");
            System.err.println("Mensaje del error: " + e.getMessage());
            System.err.println("Detalle del error: ");

            e.printStackTrace();

            return false;
        }
    }

    public char reeturnParallel(int level, int grade) {
        char parallel = 'A';
        try {

            String SQL = "SELECT paralelo FROM curso WHERE nivel = ? AND grado = ? ORDER BY paralelo DESC LIMIT 1";

            Connection connection = this.CourseConnection.getConnection();

            PreparedStatement sentence = connection.prepareStatement(SQL);

            sentence.setInt(1, level);
            sentence.setInt(2, grade);

            ResultSet result = sentence.executeQuery();

            if (result.next()) {
                parallel = result.getString("paralelo").charAt(0);

            }

            result.close();
            sentence.close();

        } catch (Exception e) {
            System.err.println("Ocurrio un error al buscar paralelo");
            System.err.println("Mensaje del error: " + e.getMessage());
            System.err.println("Detalle del error: ");

            e.printStackTrace();

            return '-';
        }

        return parallel;
    }

    public int returnIdcurso() {
        int idcurso = -1;
        try {

            String SQL = "SELECT MAX(idcurso) AS maxid FROM curso";

            Connection connection = this.CourseConnection.getConnection();

            PreparedStatement sentence = connection.prepareStatement(SQL);

            ResultSet result = sentence.executeQuery();

            if (result.next()) {

                idcurso = result.getInt("maxid");

            }

            result.close();
            sentence.close();

        } catch (Exception e) {
            System.err.println("Ocurrio un error al retornar id curso");
            System.err.println("Mensaje del error: " + e.getMessage());
            System.err.println("Detalle del error: ");

            e.printStackTrace();

            return -1;
        }
        return idcurso;
    }

    public List<String> CoursesAdvisors() {
        List<String> ListCAdvisors = new ArrayList<>();
        try {

            String SQL = "SELECT c.grado,c.paralelo,c.nivel FROM curso c "
                    + "LEFT JOIN asesor a ON c.idcurso = a.idcurso "
                    + "GROUP BY c.idcurso, c.grado, c.paralelo "
                    + "HAVING COUNT(a.idusuario) < 2";

            Connection connection = this.CourseConnection.getConnection();
            PreparedStatement sentence = connection.prepareStatement(SQL);

            ResultSet data = sentence.executeQuery();

            while (data.next() == true) {

                ListCAdvisors.add(optionsGrade[data.getInt("grado")] + " " + data.getString("paralelo")+"("+optionsLevel[data.getInt("nivel")]+")");

            }
            data.close();
            sentence.close();

        } catch (Exception e) {
            System.err.println("Ocurrio un error al listar cursos");
            System.err.println("Mensaje del error: " + e.getMessage());
            System.err.println("Detalle del error: ");

            e.printStackTrace();

        }
        return ListCAdvisors;
    }
    
    public List<String> CoursesAdvisorsC(int idusuario) {
        List<String> ListCAdvisors = new ArrayList<>();
        try {

            String SQL = "SELECT c.grado,c.paralelo,c.nivel FROM curso c "
                    + "LEFT JOIN asesor a ON c.idcurso = a.idcurso "
                    + "GROUP BY c.idcurso, c.grado, c.paralelo "
                    + "HAVING COUNT(CASE WHEN a.idusuario IS NOT NULL THEN 1 END) < 2 "
                    + "AND SUM(CASE WHEN a.idusuario = ? THEN 1 ELSE 0 END) = 0";

            Connection connection = this.CourseConnection.getConnection();
            PreparedStatement sentence = connection.prepareStatement(SQL);
            
            sentence.setInt(1, idusuario);

            ResultSet data = sentence.executeQuery();

            while (data.next() == true) {

                ListCAdvisors.add(optionsGrade[data.getInt("grado")] + " " + data.getString("paralelo")+"("+optionsLevel[data.getInt("nivel")]+")");

            }
            data.close();
            sentence.close();

        } catch (Exception e) {
            System.err.println("Ocurrio un error al listar cursos");
            System.err.println("Mensaje del error: " + e.getMessage());
            System.err.println("Detalle del error: ");

            e.printStackTrace();

        }
        return ListCAdvisors;
    }

    public int idcourse(int level,String fullname) {
        int idcurso = 0;
        try {

            String SQL = "SELECT idcurso FROM curso WHERE CONCAT(grado,' ',paralelo) = ? "
                    + "AND nivel = ?";

            Connection connection = this.CourseConnection.getConnection();

            PreparedStatement sentence = connection.prepareStatement(SQL);

            sentence.setString(1, fullname);
            sentence.setInt(2, level);

            ResultSet data = sentence.executeQuery();

            if (data.next()) {

                idcurso = data.getInt("idcurso");

            }

            data.close();
            sentence.close();
        } catch (Exception e) {
            System.err.println("Error al verificar la existencia del curso");
            e.printStackTrace();
        }
        return idcurso;
    }
    
}
