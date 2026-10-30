package org.example;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class BillingAdjustmentSigner {

    // Demo secret key. In production, load this securely
    // from an environment variable or secret manager.
    private static final String SECRET_KEY =
            "MoveInSync-Demo-Secret-Key-2026";

    public static String sign(BillingAdjustment adjustment) {

        try {
            String data =
                    adjustment.getPreviouslyBilledKm()
                            + "|"
                            + adjustment.getRecalculatedKm()
                            + "|"
                            + adjustment.getDeltaKm()
                            + "|"
                            + adjustment.getAdjustmentType();

            Mac mac = Mac.getInstance("HmacSHA256");

            SecretKeySpec key = new SecretKeySpec(
                    SECRET_KEY.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"
            );

            mac.init(key);

            byte[] signature = mac.doFinal(
                    data.getBytes(StandardCharsets.UTF_8)
            );

            StringBuilder hex = new StringBuilder();

            for (byte b : signature) {
                hex.append(String.format("%02x", b));
            }

            return hex.toString();

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Unable to sign billing adjustment."
            );
        }
    }

    public static boolean verify(
            BillingAdjustment adjustment,
            String signature) {

        if (signature == null) {
            return false;
        }

        String expectedSignature = sign(adjustment);

        return MessageDigest.isEqual(
                expectedSignature.getBytes(StandardCharsets.UTF_8),
                signature.getBytes(StandardCharsets.UTF_8)
        );
    }
}
