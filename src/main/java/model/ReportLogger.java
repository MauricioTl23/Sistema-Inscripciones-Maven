/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author mauricioteranlimari
 */
public class ReportLogger {

    private static final String BASE_DIR = System.getProperty("working.dir", "src/main/resources");
    private static final String FILE_NAME = "NumberReports.txt";
    private static final Path FILE_PATH = Paths.get(BASE_DIR).resolve(FILE_NAME);

    private static final String HEADER = "Número Reporte | Tipo de Reporte | Fecha Reporte";

    public static void logReport(String tipoReporte) {
        int nextReportNumber = 1;

        try {
            // Crear carpeta si no existe
            if (!Files.exists(FILE_PATH.getParent())) {
                Files.createDirectories(FILE_PATH.getParent());
            }

            if (!Files.exists(FILE_PATH)) {
                Files.createFile(FILE_PATH);
                Files.write(FILE_PATH, (HEADER + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
            }

            try (BufferedReader reader = Files.newBufferedReader(FILE_PATH)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty() && !line.trim().equals(HEADER)) {
                        String[] parts = line.split("\\|");
                        if (parts.length >= 2) {
                            String numeroStr = parts[0].trim();
                            String tipo = parts[1].trim();

                            if (tipo.equalsIgnoreCase(tipoReporte)) {
                                try {
                                    int numero = Integer.parseInt(numeroStr);
                                    if (numero >= nextReportNumber) {
                                        nextReportNumber = numero + 1;
                                    }
                                } catch (NumberFormatException ignored) {
                                }
                            }
                        }
                    }
                }
            }

            String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String newLine = String.format("%d | %s | %s", nextReportNumber, tipoReporte, date);

            Files.write(FILE_PATH, (newLine + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);

            System.out.println("Reporte guardado correctamente en: " + FILE_PATH);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para obtener el próximo número de reporte, igual usando FILE_PATH
    public static int getNextReportNumber(String tipoReporte) {
        int nextReportNumber = 1;

        try {
            if (Files.exists(FILE_PATH)) {
                try (BufferedReader reader = Files.newBufferedReader(FILE_PATH)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (!line.trim().isEmpty() && !line.trim().equals(HEADER)) {
                            String[] parts = line.split("\\|");
                            if (parts.length >= 2) {
                                String numeroStr = parts[0].trim();
                                String tipo = parts[1].trim();
                                if (tipo.equalsIgnoreCase(tipoReporte)) {
                                    try {
                                        int numero = Integer.parseInt(numeroStr);
                                        if (numero >= nextReportNumber) {
                                            nextReportNumber = numero + 1;
                                        }
                                    } catch (NumberFormatException ignored) {
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return nextReportNumber;
    }

    // Método para obtener máximos por tipo, igual usando FILE_PATH
    public static List<String> getMaxReportNumbersAllTypes() {
        Map<String, Integer> maxNumbersByType = new HashMap<>();

        try {
            if (Files.exists(FILE_PATH)) {
                try (BufferedReader reader = Files.newBufferedReader(FILE_PATH)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (!line.trim().isEmpty() && !line.trim().equals(HEADER)) {
                            String[] parts = line.split("\\|");
                            if (parts.length >= 2) {
                                String numeroStr = parts[0].trim();
                                String tipo = parts[1].trim();

                                try {
                                    int numero = Integer.parseInt(numeroStr);
                                    maxNumbersByType.merge(tipo, numero, Math::max);
                                } catch (NumberFormatException ignored) {
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : maxNumbersByType.entrySet()) {
            result.add(entry.getKey() + "," + entry.getValue());
        }

        return result;
    }

}
