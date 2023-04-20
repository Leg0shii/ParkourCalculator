package de.legoshi.parkourcalculator.util;

public class NumberHelper {

    public static Double parseDouble(String string) {
        try {
            return Double.parseDouble(string);
        } catch (NumberFormatException | NullPointerException e) {
            return null;
        }
    }

}
