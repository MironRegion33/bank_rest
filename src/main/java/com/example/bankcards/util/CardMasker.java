package com.example.bankcards.util;

public final class CardMasker {

    private CardMasker() {}

    public static String mask(String cardNumber) {
        if (cardNumber == null) {
            return null;
        }
        String design = cardNumber.replaceAll("\\D", "");
        if (design.length() < 4) {
            return "****";
        }
        String last4 = design.substring(design.length() - 4);
        return "**** **** **** " + last4;
    }
}
