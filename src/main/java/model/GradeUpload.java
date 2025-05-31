/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Properties;

/**
 *
 * @author mauricioteranlimari
 */
public class GradeUpload {

    private static String getFileName(int idCurso, int gestion) {
        return "notas_fecha_" + idCurso + "_" + gestion + ".properties";
    }

    public static void guardarFechaSubida(int idCurso, int gestion) {
        Properties props = new Properties();
        String fileName = getFileName(idCurso, gestion);

        try (FileInputStream in = new FileInputStream(fileName)) {
            props.load(in);
        } catch (IOException e) {
           
        }

        props.setProperty("fechaSubida", LocalDate.now().toString());

        try (FileOutputStream out = new FileOutputStream(fileName)) {
            props.store(out, "Fecha de subida de notas para curso " + idCurso + " gestion " + gestion);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static boolean puedeModificarNotas(int idCurso, int gestion) {
        Properties props = new Properties();
        String fileName = getFileName(idCurso, gestion);

        try (FileInputStream in = new FileInputStream(fileName)) {
            props.load(in);
            String fechaStr = props.getProperty("fechaSubida");

            if (fechaStr == null) {

                return true;
            }

            LocalDate fechaSubida = LocalDate.parse(fechaStr);
            long dias = ChronoUnit.DAYS.between(fechaSubida, LocalDate.now());

            return dias <= 7;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

}
