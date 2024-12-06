package dev.risas.sukagepond.utilities;

import lombok.experimental.UtilityClass;

@UtilityClass
public class JavaUtil {

    public Integer tryParseInt(String string) {
        try {
            return Integer.parseInt(string);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    public String randomAlphaNumeric(int count) {
        String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder builder = new StringBuilder();

        while (count-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }

        return builder.toString();
    }

    public String join(String[] args, char separator, int startIndex, int endIndex) {
        StringBuilder builder = new StringBuilder();

        for (int i = startIndex; i < endIndex; i++) {
            builder.append(args[i]).append(separator);
        }

        return builder.toString().trim();
    }
}
