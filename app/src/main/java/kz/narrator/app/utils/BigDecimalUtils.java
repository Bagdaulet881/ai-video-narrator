package kz.narrator.app.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class BigDecimalUtils {

    private static DecimalFormat decimalFormat;
    private static DecimalFormat decimalFormatForRegistry;
    private static DecimalFormat decimalFormatForSocialHealthRegistry;

    private static DecimalFormat getDecimalFormat() {

        if (decimalFormat == null) {

            DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
            decimalFormatSymbols.setGroupingSeparator(' ');
            decimalFormatSymbols.setDecimalSeparator(',');
            decimalFormat = new DecimalFormat("##0,00", decimalFormatSymbols);
        }

        return decimalFormat;
    }

    private static DecimalFormat getDecimalFormatForRegistry() {

        if (decimalFormatForRegistry == null) {

            DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
            decimalFormatSymbols.setDecimalSeparator('.');
            decimalFormatForRegistry = new DecimalFormat("#0.00", decimalFormatSymbols);
        }

        return decimalFormatForRegistry;
    }


    private static DecimalFormat getDecimalFormatForSocialHealthRegistry() {

        if (decimalFormatForSocialHealthRegistry == null) {

            DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
            decimalFormatSymbols.setDecimalSeparator('.');
            decimalFormatForSocialHealthRegistry = new DecimalFormat("###", decimalFormatSymbols);
        }

        return decimalFormatForSocialHealthRegistry;
    }

    public static String format(BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            return "";
        }

        return getDecimalFormat().format(bigDecimal);
    }

    public static String formatRegistry(BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            return "";
        }

        return getDecimalFormatForRegistry().format(bigDecimal);
    }

    public static String formatSocialHealthRegistry(BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            return "";
        }

        return getDecimalFormatForSocialHealthRegistry().format(bigDecimal);
    }

}
