/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.*;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author mauricioteranlimari
 */
public class OPTManager {

    private final int EXPIRATION_MINUTES = 10;
    private final SecureRandom random = new SecureRandom();
    private final String FILE_PATH = "otps_log.txt";
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public String generateOTP() {
        int number = random.nextInt(900000) + 100000;
        String code = String.valueOf(number);
        LocalDateTime now = LocalDateTime.now();
        logOTPToFile(code, now);
        return code;
    }

    public boolean validateOTP(String input) {
        OTPEntry last = readLastOTPFromFile();
        if (last == null) {
            return false;
        }
        if (!last.code.equals(input)) {
            return false;
        }
        return !LocalDateTime.now().isAfter(last.timestamp.plusMinutes(EXPIRATION_MINUTES));
    }

    public boolean isOTPExpired() {
        OTPEntry last = readLastOTPFromFile();
        if (last == null) {
            return true;
        }
        return LocalDateTime.now().isAfter(last.timestamp.plusMinutes(EXPIRATION_MINUTES));
    }

    private void logOTPToFile(String code, LocalDateTime time) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write("\"" + code + "\", Fecha: " + time.format(formatter));
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error al guardar el OTP: " + e.getMessage());
        }
    }

    private OTPEntry readLastOTPFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            try {
                file.createNewFile(); // crea archivo vacío si no existe
            } catch (IOException e) {
                System.err.println("Error creando el archivo OTP: " + e.getMessage());
                return null;
            }
        }

        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            long length = raf.length();
            if (length == 0) {
                return null;
            }

            long pos = length - 1;
            while (pos > 0) {
                pos--;
                raf.seek(pos);
                if (raf.readByte() == '\n') {
                    break;
                }
            }

            String lastLine = raf.readLine();
            if (lastLine == null || !lastLine.contains(",")) {
                return null;
            }

            String[] parts = lastLine.split(",");
            String code = parts[0].replace("\"", "").trim();
            String datePart = parts[1].replace("Fecha:", "").trim();
            LocalDateTime time = LocalDateTime.parse(datePart, formatter);
            return new OTPEntry(code, time);

        } catch (IOException e) {
            System.err.println("Error leyendo el archivo OTP: " + e.getMessage());
            return null;
        }
    }

    public String getLastValidOTP() {
        OTPEntry last = readLastOTPFromFile();
        if (last == null) {
            return null;
        }
        if (LocalDateTime.now().isAfter(last.timestamp.plusMinutes(EXPIRATION_MINUTES))) {
            return null;
        }
        return last.code;
    }

    public static class OTPEntry {

        private final String code;
        private final LocalDateTime timestamp;

        public OTPEntry(String code, LocalDateTime timestamp) {
            this.code = code;
            this.timestamp = timestamp;
        }

        public String getCode() {
            return code;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }

}
