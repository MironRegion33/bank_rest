package com.example.bankcards.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class CardCrypto {

    private static final String ALG = "AES";
    private static final String TRANS = "AES/GCM/NoPadding";
    private static final int GCM_TAG_BITS = 128;
    private static final int IV_LEN = 12;

    private final SecretKeySpec key;
    private final SecureRandom secureRandom = new SecureRandom();

    public CardCrypto(@Value("${app.card.crypto.key}") String b64key) {
        byte[] k = Base64.getDecoder().decode(b64key);
        this.key = new SecretKeySpec(k, ALG);
    }

    public String encrypt(String plain) {
        try {
            byte[] iv = new byte[IV_LEN];
            secureRandom.nextBytes(iv);
            Cipher c = Cipher.getInstance(TRANS);
            c.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_BITS, iv));
            byte[] ct = c.doFinal(plain.getBytes());
            return Base64.getEncoder().encodeToString(iv) + ":" +
                    Base64.getEncoder().encodeToString(ct);
        } catch (Exception e) {
            throw new IllegalStateException("Card encrypt failed", e);
        }
    }

    public String decrypt(String stored) {
        try {
            String[] parts = stored.split(":");
            byte[] iv = Base64.getDecoder().decode(parts[0]);
            byte[] ct = Base64.getDecoder().decode(parts[1]);
            Cipher c = Cipher.getInstance(TRANS);
            c.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_BITS, iv));
            return new String(c.doFinal(ct));
        } catch (Exception e) {
            throw new IllegalStateException("Card decrypt failed", e);
        }
    }
}
