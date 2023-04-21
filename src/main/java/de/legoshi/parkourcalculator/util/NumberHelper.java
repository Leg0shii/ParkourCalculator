package de.legoshi.parkourcalculator.util;

import java.text.DecimalFormat;

public class NumberHelper {

    public static Double parseDouble(String string) {
        try {
            return Double.parseDouble(string);
        } catch (NumberFormatException | NullPointerException e) {
            return null;
        }
    }

    public static double roundDouble(double val, int precision) {
        String s = "#.";
        for (int i = 0; i < precision; i++) s = s + "#";
        DecimalFormat df = new DecimalFormat(s);
        return Double.parseDouble(df.format(val).replace(",", "."));
    }

}
