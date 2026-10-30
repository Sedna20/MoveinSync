package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BillingAdjustmentTest {

    // TEST 1: Calculate billing difference
    @Test
    void testBillingAdjustment() {

        BillingAdjustment adjustment =
                new BillingAdjustment(18.4, 20.2);

        assertEquals(
                1.8,
                adjustment.getDeltaKm(),
                0.000001
        );

        assertEquals(
                "ADDITIONAL_DISTANCE",
                adjustment.getAdjustmentType()
        );
    }

    // TEST 2: Verify original signature
    @Test
    void testValidSignature() {

        BillingAdjustment adjustment =
                new BillingAdjustment(18.4, 20.2);

        String signature =
                BillingAdjustmentSigner.sign(adjustment);

        assertTrue(
                BillingAdjustmentSigner.verify(
                        adjustment, signature
                )
        );
    }

    // TEST 3: Detect modified adjustment
    @Test
    void testTamperedAdjustment() {

        BillingAdjustment original =
                new BillingAdjustment(18.4, 20.2);

        String signature =
                BillingAdjustmentSigner.sign(original);

        BillingAdjustment modified =
                new BillingAdjustment(18.4, 21.2);

        assertFalse(
                BillingAdjustmentSigner.verify(
                        modified, signature
                )
        );
    }
}