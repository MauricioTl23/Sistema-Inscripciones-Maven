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

    private static final String FILE_NAME = "NumberReports.txt";
    private static final String HEADER = "Número Reporte | Tipo de Reporte | Fecha Reporte";

    public static void logReport(String tipoReporte) {
        Path filePath = Paths.get(FILE_NAME);
        int nextReportNumber = 1;

        try {
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
                Files.write(filePath, (HEADER + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
            }

            try (BufferedReader reader = Files.newBufferedReader(filePath)) {
                String line;
                for (line = reader.readLine(); line != null; line = reader.readLine()) {
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

            // Preparar nueva línea de reporte
            String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String newLine = String.format("%d | %s | %s", nextReportNumber, tipoReporte, date);

            // Guardar en archivo
            Files.write(filePath, (newLine + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);

            System.out.println("Reporte guardado correctamente.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getNextReportNumber(String tipoReporte) {
        Path filePath = Paths.get(FILE_NAME);
        int nextReportNumber = 1;

        try {
            if (Files.exists(filePath)) {
                try (BufferedReader reader = Files.newBufferedReader(filePath)) {
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

    public static List<String> getMaxReportNumbersAllTypes() {
        Path filePath = Paths.get(FILE_NAME);
        Map<String, Integer> maxNumbersByType = new HashMap<>();

        try {
            if (Files.exists(filePath)) {
                try (BufferedReader reader = Files.newBufferedReader(filePath)) {
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
            result.add(entry.getKey() + "," + entry.getValue()); // <-- tipo primero, luego número
        }

        return result;
    }

}
