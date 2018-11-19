package com.instamojo.android.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class to validate card details.
 */
public class CardUtil {

    private static final String TAG = CardUtil.class.getSimpleName();

    /**
     * Luhn's algorithm implementation to validate the card passed.
     *
     * @param cardNumber Card number. Require atleast first 4 to give a valid result.
     * @return 1 for valid card , 0 for invalid card.
     */
    public static boolean isValid(String cardNumber) {

        if (cardNumber == null || cardNumber.isEmpty() || cardNumber.length() < 4) {
            return false;
        }

        int cardLength = cardNumber.length();
        CardType cardType = CardUtil.getCardType(cardNumber);

        // No length check for MAESTRO
        if (cardType != CardType.MAESTRO && cardType.getNumberLength() != cardLength) {
            return false;
        }

        // If the card number starts with 0
        if (cardNumber.charAt(0) == 0) {
            return false;
        }

        int total = 0;
        boolean isEvenPosition = false;
        for (int i = cardLength - 1; i >= 0; i--) {

            int digit = cardNumber.charAt(i);

            if (isEvenPosition) {
                digit *= 2;
            }

            total += digit / 10; // For 'digit' with two digits
            total += digit % 10;

            isEvenPosition = !isEvenPosition;
        }

        return (total % 10 == 0);
    }

    /**
     * Check for Maestro Card.
     *
     * @param cardNumber Card Number to be validated, requires atleast first four digits of the card.
     * @return True if card is Maestro else False.
     */
    public static boolean isMaestroCard(String cardNumber) {
        return CardType.MAESTRO.matches(cardNumber);
    }

    /**
     * Returns the CardType of the card issuer.
     *
     * @param cardNumber Card number.
     * @return CardType
     */
    public static CardType getCardType(String cardNumber) {
        for (CardType cardType : CardType.values()) {
            if (cardType.matches(cardNumber)) {
                return cardType;
            }
        }

        return CardType.UNKNOWN;
    }

    /**
     * Check method to see if the card expiry date is valid.
     *
     * @param expiry Date string in the formt - MM/yy.
     * @return True if the Date is expired Else False.
     */

    public static boolean isDateExpired(String expiry) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yy", Locale.ENGLISH);
        dateFormat.setLenient(false);
        try {
            Date expiryDate = dateFormat.parse(expiry);
            return expiryDate.before(new Date());
        } catch (ParseException e) {
            Logger.e(TAG, "Invalid Date - " + expiry);
            return true;
        }
    }
}
