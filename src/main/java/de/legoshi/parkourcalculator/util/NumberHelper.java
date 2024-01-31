package de.legoshi.parkourcalculator.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DecimalFormat;

public class NumberHelper {

    private static final Logger logger = LogManager.getLogger(NumberHelper.class.getName());

    public static Double parseDouble(String string) {
        try {
            return Double.parseDouble(string);
        } catch (NumberFormatException | NullPointerException e) {
            return null;
        }
    }

    public static Float parseFloat(String string) {
        try {
            return Float.parseFloat(string);
        } catch (NumberFormatException | NullPointerException e) {
            return null;
        }
    }

    public static double parseDoubleOrZero(String text) {
        Double d = parseDouble(text);
        return d == null ? 0.0D : d;
    }

    public static float parseFloatOrZero(String text) {
        Float f = parseFloat(text);
        return f == null ? 0.0F : f;
    }

    public static double getDoubleOrOld(double oldVal, String text) {
        Double d = NumberHelper.parseDouble(text);
        return d == null ? oldVal : d;
    }

    public static float getFloatOrOld(float oldVal, String text) {
        Float f = NumberHelper.parseFloat(text);
        return f == null ? oldVal : f;
    }

    public static String replaceNegZero(double val) {
        String s = val + "";
        if (val == 0.0) s = s.replace("-", "");
        return s;
    }

    public static double roundDouble(double val, int precision) {
        String s = "#.";
        for (int i = 0; i < precision; i++) s = s + "#";
        DecimalFormat df = new DecimalFormat(s);
        return Double.parseDouble(df.format(val).replace(",", "."));
    }

}
