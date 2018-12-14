package com.instamojo.android.helpers;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class CardUtilTest {

    private static final String VALID_VISA_CC_1 = "4242424242424242";
    private static final String VALID_VISA_CC_2 = "4900000000000086";
    private static final String VALID_MASTERCARD_CC = "5100000000000016";

    private static final String INVALID_VISA_CC_1 = "4242424242424243";
    private static final String INVALID_MASTERCARD_CC = "5100000000000012";


    private Object[] validCardNumbers() {
        return new Object[]{
                VALID_VISA_CC_1,
                VALID_VISA_CC_2,
                VALID_MASTERCARD_CC
        };
    }

    private Object[] invalidCardNumbers() {
        return new Object[]{
                INVALID_VISA_CC_1,
                INVALID_MASTERCARD_CC
        };
    }

    @Test
    @Parameters(method = "validCardNumbers")
    public void cardNumberValidator_ValidNumber_ReturnsTrue(String validCardNumber) {
        Assert.assertTrue(CardUtil.isCardNumberValid(validCardNumber));
    }

    @Test
    @Parameters(method = "invalidCardNumbers")
    public void cardNumberValidator_InvalidNumber_ReturnsFalse(String invalidCardNumber) {
        Assert.assertFalse(CardUtil.isCardNumberValid(invalidCardNumber));
    }
}