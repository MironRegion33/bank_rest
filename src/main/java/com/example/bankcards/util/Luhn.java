package com.example.bankcards.util;

public final class Luhn {
    private Luhn() {}
    public static boolean isValid(String number) {
        if (number == null) return false;
        String s = number.replaceAll("\\D", "");
        int sum = 0; boolean alt = false;
        for (int i = s.length() - 1; i >= 0; i--) {
            int n = s.charAt(i) - '0';
            if (alt) { n *= 2; if (n > 9) n -= 9; }
            sum += n; alt = !alt;
        }
        return s.length() >= 12 && s.length() <= 19 && sum % 10 == 0;
    }
}
