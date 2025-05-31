/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.security.SecureRandom;
import java.time.LocalDateTime;

/**
 *
 * @author mauricioteranlimari
 */
public class OPTManager {

    private String currentOTP;
    private LocalDateTime creationTime;
    private final int EXPIRATION_MINUTES = 10;
    private SecureRandom random = new SecureRandom();

    public String generateOTP() {
        int number = random.nextInt(900000) + 100000;
        currentOTP = String.valueOf(number);
        creationTime = LocalDateTime.now();
        return currentOTP;
    }

    public boolean validateOTP(String code) {
        if (currentOTP == null || creationTime == null) {
            return false;
        }

        if (!currentOTP.equals(code)) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        return !now.isAfter(creationTime.plusMinutes(EXPIRATION_MINUTES));
    }

    public boolean isOTPExpired() {
        if (creationTime == null) {
            return true;
        }
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(creationTime.plusMinutes(EXPIRATION_MINUTES));
    }

}
