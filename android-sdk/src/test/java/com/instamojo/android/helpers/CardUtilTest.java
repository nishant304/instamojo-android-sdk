package com.instamojo.android.helpers;

import org.junit.Assert;
import org.junit.Test;

public class CardUtilTest {

    @Test
    public void cardNumberValidator_ValidNumber_ReturnsTrue() {
        Assert.assertTrue(CardUtil.isCardNumberValid("4242424242424242"));
    }
}