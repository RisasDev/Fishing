package dev.risas.sukagepond.utilities;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TimeUtil {

    public Integer formatInt(String input) {
        if (input == null || input.isEmpty()) return -1;

        int result = 0;
        StringBuilder number = new StringBuilder();

        for (int i = 0; i < input.length(); ++i) {
            char c = input.charAt(i);

            if (Character.isDigit(c)) {
                number.append(c);
            }
            else {
                String str;
                if (Character.isLetter(c) && !(str = number.toString()).isEmpty()) {
                    result += convertInt(Integer.parseInt(str), c);
                    number = new StringBuilder();
                }
            }
        }
        return result;
    }

    private int convertInt(int value, char unit) {
        return switch (unit) {
            case 's' -> value;
            case 'm' -> value * 60;
            case 'h' -> value * 60 * 60;
            case 'd' -> value * 60 * 60 * 24;
            case 'w' -> value * 60 * 60 * 24 * 7;
            default -> -1;
        };
    }
}
