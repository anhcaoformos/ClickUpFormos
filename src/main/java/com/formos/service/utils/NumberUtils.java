package com.formos.service.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;

/**
 * @author mtunnell
 * @version 1.0
 * @copyright Formos 2008
 */
public class NumberUtils {

    public static NumberFormat CURRENCY_FORMAT;
    public static NumberFormat PERCENT_FORMAT;

    public static final int CURRENCY = 1;
    public static final int PERCENT = 2;

    public static final int SC_NUMBER_OF_DECIMAL = 1;

    private static NumberFormat currencyInNF;
    private static NumberFormat currencyOutNF;
    private static NumberFormat decimalInNF;
    private static NumberFormat percentNF;
    private static DecimalFormat CURRENCY_NEG_SIGN; //Sign for negative currency
    public static DecimalFormat CURRENCY_DOLLAR_FORMAT_NO_DECIMAL; //Sign for negative currency
    public static DecimalFormat CURRENCY_DOLLAR_FORMAT_ONE_DECIMAL; //Sign for negative currency

    public static final double FULL_PERCENT = 100.0;
    public static final double HALF_PERCENT = 50.0;

    @SuppressWarnings("unused")
    private static NumberFormat integerNF;

    @SuppressWarnings("unused")
    private static NumberFormat floatNF;

    private static NumberFormat hourNF;

    static {
        CURRENCY_FORMAT = NumberFormat.getCurrencyInstance();
        CURRENCY_FORMAT.setMinimumFractionDigits(2);
        CURRENCY_FORMAT.setMaximumFractionDigits(2);

        PERCENT_FORMAT = NumberFormat.getPercentInstance();
        PERCENT_FORMAT.setMinimumFractionDigits(2);
        PERCENT_FORMAT.setMaximumFractionDigits(2);

        currencyInNF = NumberFormat.getCurrencyInstance();
        currencyInNF.setMaximumFractionDigits(4);

        currencyOutNF = NumberFormat.getCurrencyInstance();
        currencyOutNF.setMaximumFractionDigits(0);

        decimalInNF = DecimalFormat.getInstance();
        decimalInNF.setMaximumFractionDigits(4);

        percentNF = NumberFormat.getPercentInstance();
        percentNF.setMaximumIntegerDigits(3);
        percentNF.setMaximumFractionDigits(0);

        integerNF = NumberFormat.getIntegerInstance();
        floatNF = NumberFormat.getIntegerInstance();

        hourNF = NumberFormat.getInstance();
        hourNF.setMaximumIntegerDigits(2);
        hourNF.setMaximumFractionDigits(1);

        Currency localCurrency = NumberUtils.getLocalCurrency();
        CURRENCY_NEG_SIGN = new DecimalFormat("###,###,##0.00");
        CURRENCY_NEG_SIGN.setPositivePrefix(localCurrency.getSymbol());
        CURRENCY_NEG_SIGN.setNegativePrefix(localCurrency.getSymbol() + "-");
        CURRENCY_NEG_SIGN.setMaximumFractionDigits(2);
        CURRENCY_NEG_SIGN.setMinimumIntegerDigits(2);
        CURRENCY_NEG_SIGN.setMinimumIntegerDigits(1);

        CURRENCY_DOLLAR_FORMAT_NO_DECIMAL = new DecimalFormat("$###,###");
        CURRENCY_DOLLAR_FORMAT_ONE_DECIMAL = new DecimalFormat("$###,###.#");
    }

    public static Currency getLocalCurrency() {
        try {
            return Currency.getInstance(Locale.getDefault());
        } catch (NullPointerException | IllegalArgumentException ex) {
            return Currency.getInstance(new Locale("en", "US"));
        }
    }

    public static synchronized String formatCurrency(double number, int decimals) {
        number = roundUpDouble(number, decimals);
        currencyOutNF.setMaximumFractionDigits(decimals);
        currencyOutNF.setMinimumFractionDigits(decimals);
        return currencyOutNF.format(number);
    }

    public static synchronized String formatSignedCurrency(double number, int decimals) {
        number = roundUpDouble(number, decimals);
        CURRENCY_NEG_SIGN.setMaximumFractionDigits(decimals);
        CURRENCY_NEG_SIGN.setMinimumFractionDigits(decimals);
        return CURRENCY_NEG_SIGN.format(number);
    }

    /**
     * Format currency
     *
     * @param number
     * @return
     */
    public static synchronized String formatCurrency(BigDecimal number) {
        if (number == null) {
            number = new BigDecimal(0);
        }

        String strValue = number.toString();
        int decPos = strValue.indexOf(".");
        int newScale;
        BigDecimal newBigD;
        // Strip trailing zeros after cents
        if (decPos > -1) {
            while (((strValue.length() - 1) - decPos) > 2 && strValue.charAt(strValue.length() - 1) == '0') {
                strValue = strValue.substring(0, strValue.length() - 1);
            }
            newScale = strValue.length() - 1 - decPos;
        } else {
            newScale = 0;
        }
        newBigD = number.setScale(newScale, BigDecimal.ROUND_HALF_UP);
        CURRENCY_FORMAT.setMaximumFractionDigits(newScale);
        CURRENCY_FORMAT.setMinimumFractionDigits(newScale);
        return CURRENCY_FORMAT.format(newBigD.doubleValue());
    }

    public static Double roundUpDouble(Double num, int decPlaces) {
        if (num.isInfinite() || num.isNaN()) return 0.0;
        return new BigDecimal(Double.toString(num)).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static String formatDouble(Double number, int decimals) {
        if (number == null) {
            return "";
        } else {
            String format = "%,." + decimals + "f";
            return String.format(format, number);
        }
    }

    public static String formatInteger(Integer number) {
        if (number == null) {
            return "";
        } else {
            return String.format("%,d", number);
        }
    }

    public static int significantDecimalPlaces(Double num) {
        if (num == null) return 0;
        return significantDecimalPlaces(num.toString());
    }

    public static int significantDecimalPlaces(BigDecimal num) {
        if (num == null) return 0;
        return significantDecimalPlaces(num.toString());
    }

    public static int significantDecimalPlaces(String strNum) {
        String part = strNum.substring(strNum.indexOf('.') + 1);
        for (int i = part.length(); i > 0; i--) {
            char c = part.charAt(i - 1);
            if (c != '0') {
                return i;
            }
        }
        return 0;
    }

    public static Double parseDouble(String str) throws NumberFormatException {
        Double ret = null;
        if (str != null && str != "") {
            str = str.trim();
            boolean isNeg = false;
            if (str.length() > 1 && str.charAt(0) == '(' && str.charAt(str.length() - 1) == ')') {
                str = str.substring(1);
                str = str.substring(0, str.length() - 1);
                str = str.trim();
                isNeg = true;
            }
            if (str.length() > 0 && str.charAt(0) == '-') {
                if (isNeg) {
                    throw new NumberFormatException("Double negative.");
                }
                str = str.substring(1);
                str = str.trim();
                isNeg = true;
            }

            if (str.length() > 0 && str.charAt(0) == '$') {
                str = str.substring(1);
                str = str.trim();
            }
            if (str.length() > 0 && isNeg && str.charAt(0) == '-') {
                throw new NumberFormatException("Double negative.");
            }
            str = str.replace(",", "");
            if (isNeg) {
                str = "-" + str;
            }
            return Double.parseDouble(str);
        }
        return ret;
    }

    /**
     * parse Integer
     *
     * @param value
     * @return Integer
     */
    public static Integer parseInteger(String value) throws NumberFormatException {
        Integer ret = null;
        try {
            if (value != null && value != "") {
                ret = Integer.valueOf(value);
            }
        } catch (Exception e) {
            ret = null;
            throw new NumberFormatException(e.getMessage());
        }
        return ret;
    }

    /**
     * parse BigDecimal
     *
     * @return BigDecimal
     */
    public static BigDecimal parseBigDecimal(String str) throws NumberFormatException {
        BigDecimal ret = null;
        if (str != null && str != "") {
            str = str.trim();
            boolean isNeg = false;
            if (str.length() > 1 && str.charAt(0) == '(' && str.charAt(str.length() - 1) == ')') {
                str = str.substring(1);
                str = str.substring(0, str.length() - 1);
                str = str.trim();
                isNeg = true;
            }
            if (str.length() > 0 && str.charAt(0) == '-') {
                if (isNeg) {
                    throw new NumberFormatException("BigDecimal negative.");
                }
                str = str.substring(1);
                str = str.trim();
                isNeg = true;
            }

            if (str.length() > 0 && str.charAt(0) == '$') {
                str = str.substring(1);
                str = str.trim();
            }
            if (str.length() > 0 && isNeg && str.charAt(0) == '-') {
                throw new NumberFormatException("BigDecimal negative.");
            }
            str = str.replace(",", "");
            if (isNeg) {
                str = "-" + str;
            }
            return new BigDecimal(str);
        }
        return ret;
    }

    // returns the maximum of v1 or v2.
    public static BigDecimal max(BigDecimal v1, BigDecimal v2) {
        return v1.max(v2);
    }

    // Convert a number to the nearest quarter
    public static BigDecimal roundToQuarter(BigDecimal number) {
        return new BigDecimal(Math.round(number.doubleValue() * 4) / 4.0);
    }

    // Check a string if it is a Positive integer.
    public static boolean isNumeric(String s) {
        if (s == null) return true;
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String formatBigDecimal(BigDecimal number, int decimals) {
        return formatBigDecimal(number, decimals, false);
    }

    public static String formatBigDecimal(BigDecimal number, int decimals, boolean isRemoveAllZeroTrailing) {
        if (number == null) return "";

        if (isRemoveAllZeroTrailing) {
            String strNum = number.toString();
            if (strNum.indexOf('.') != -1) {
                int numOfRemoveZero = 0;
                String part = strNum.substring(strNum.indexOf('.') + 1);
                for (int i = part.length() - 1; i >= 0; i--) {
                    char c = part.charAt(i);
                    if (c == '0') {
                        numOfRemoveZero++;
                    } else {
                        break;
                    }
                }
                int scale = part.length() - numOfRemoveZero;
                decimals = (scale > decimals) ? decimals : scale;

                if (decimals == 0 && number.longValue() == 0) {
                    return "0";
                }
            } else {
                decimals = 0;
            }
        }

        String ret = null;
        try {
            ret = formatNumber(number, decimals);
        } catch (IllegalArgumentException e) {
            decimals = number.scale();
            try {
                ret = formatNumber(number, decimals);
            } catch (IllegalArgumentException e2) {
                ret = number.toString();
            }
        }
        return ret;
    }

    private static String formatNumber(BigDecimal number, int decimals) throws IllegalArgumentException {
        number.setScale(decimals, BigDecimal.ROUND_HALF_UP);
        String format = "%,." + decimals + "f";
        return String.format(format, number);
    }

    public static String formatCurrencyForScoreCard(BigDecimal number) {
        String s = number.toString();
        if (s.indexOf(".0") > -1) {
            number.setScale(0);
            return CURRENCY_DOLLAR_FORMAT_NO_DECIMAL.format(number);
        } else {
            number.setScale(1, BigDecimal.ROUND_HALF_UP);
            return CURRENCY_DOLLAR_FORMAT_ONE_DECIMAL.format(number);
        }
    }

    public static String formatPhoneNumberForScoreCard(String phone) {
        if (StringUtils.isNotBlank(phone) && phone.length() < 10) {
            String output = "(" + phone.substring(0, 3) + ") " + phone.substring(3, 6) + "-" + phone.substring(6, 10);
            return output;
        }
        return phone;
    }
}
