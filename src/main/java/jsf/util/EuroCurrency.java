package jsf.util;

import java.text.DecimalFormat;

public class EuroCurrency {

    public static long getAsLong(String value) {
        Long longValue = null;

        if (value == null) {
            return new Long(0);
        }

        value = value.trim();
        if (value.indexOf(' ') != -1) {
            value = value.substring(0, value.indexOf(' '));
        }

        String sign = "";
        if (value.charAt(0) == '-') {
            sign = "-";
            value = value.substring(1);
        }

        char separator = 0;
        if (value.lastIndexOf('.') > value.lastIndexOf(',')) {
            separator = '.';
            value = value.replace(",", "");
        } else if (value.lastIndexOf('.') < value.lastIndexOf(',')) {
            separator = ',';
            value = value.replace(".", "");
        }

        switch (separator) {
            case 0:
                longValue = new Long(sign + value + "000");
                break;
            default:
                String intPart;

                if (value.indexOf(separator) != 0) {
                    intPart = value.substring(0, value.indexOf(separator));
                } else {
                    intPart = "0";
                }

                String decPart = value.substring(value.indexOf(separator) + 1);
                if (decPart.length() == 0) {
                    decPart = "000";
                } else if (decPart.length() == 1) {
                    decPart = decPart + "00";
                } else if (decPart.length() == 2) {
                    decPart = decPart + "0";
                } else if (decPart.length() > 3) {
                    decPart = Long.toString(Long.parseLong(decPart.substring(0, 3)) + Math.round(Double.parseDouble("0." + decPart.substring(3))));
                }

                longValue = new Long(sign + intPart + decPart);
                break;
        }

        return longValue.longValue();

    }

    public static String getAsString(Object value) {
        if (value == null) {
            return "0,00 €";
        }
        if (value instanceof Long) {
            Long o = (Long) value;
            String rawValue = o.toString();
            String sign = "";
            if (rawValue.charAt(0) == '-') {
                sign = "-";
                rawValue = rawValue.substring(1);
            }

            if (rawValue.length() == 0) {
                return "0,00 €";
            } else if (rawValue.length() == 1) {
                if (rawValue.charAt(0) == '0') {
                    return "0,00 €";
                } else {
                    return sign + "0,00" + rawValue + " €";
                }
            } else if (rawValue.length() == 2) {
                if (rawValue.charAt(1) == '0') {
                    return sign + "0,0" + rawValue.charAt(0) + " €";
                } else {
                    return sign + "0,0" + rawValue + " €";
                }
            } else if (rawValue.length() == 3) {
                if (rawValue.charAt(2) == '0') {
                    return sign + "0," + rawValue.substring(0, rawValue.length() - 1) + " €";
                } else {
                    return sign + "0," + rawValue + " €";
                }
            } else {
                String formattedValue;
                DecimalFormat euroCurrencyFormatter = new DecimalFormat("#,###");
                formattedValue = euroCurrencyFormatter.format(new Long(sign + rawValue.substring(0, rawValue.length() - 3)));
                if (rawValue.charAt(rawValue.length() - 1) == '0') {
                    formattedValue += "," + rawValue.substring(rawValue.length() - 3, rawValue.length() - 1) + " €";
                } else {
                    formattedValue += "," + rawValue.substring(rawValue.length() - 3, rawValue.length()) + " €";
                }

                return formattedValue;
            }
        } else {
            throw new IllegalArgumentException("object " + value + " is of type " + value.getClass().getName() + "; expected type: Long");
        }
    }
}
