/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validation {
    private final String[] departments = {"LP", "SCZ", "CBBA", "OR", "PT", "CH", "TJA", "BE", "PD"};
    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    public static boolean VerifyEmailUser(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    public boolean civalid(String number, String cm, int dp) {
        boolean numberValid = number != null && number.matches("\\d{5,8}");
        boolean cmValid = cm == null || cm.trim().isEmpty() || cm.matches("[A-Z]{1,2}");
        boolean dpValid = dp != -1;
        return numberValid && cmValid && dpValid;
    }
    
    public static boolean VerifyNumberUser(String number) {
        return number != null && number.matches("^(?:[76]\\d{7}|52\\d{5})$");
    }
    
    public static boolean VerifyAddress(String address) {
        return address != null && address.matches("(?i)^(Calle|Barrio|Zona|Urb\\.?)(\\s+.*)?$");
    }

    public static boolean VerifyName(String name) {
        return name != null && name.matches("^([A-ZÁÉÍÓÚÑ][a-záéíóúñ]+)(\\s[A-ZÁÉÍÓÚÑ][a-záéíóúñ]+)*$");
    }
    public boolean ciValid(String input) {
        return input.matches("^\\d{5,8}(-[A-Z]{1,2})?-(LP|SCZ|CBBA|OR|PT|CH|TJA|BE|PD)$");
    }

}
