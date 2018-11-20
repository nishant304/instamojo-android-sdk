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

        CardType cardType = CardUtil.getCardType(cardNumber);
        // No length check for MAESTRO
        if (cardType != CardType.MAESTRO && cardType.getNumberLength() != cardNumber.length()) {
            return false;
        }

        long number = Long.valueOf(cardNumber);
        int total = sumOfDoubleEvenPlace(number) + sumOfOddPlace(number);

        if ((total % 10 == 0) && (prefixMatched(number, 1) != 0)) {
            return true;

        } else {
            return false;
        }
    }

    private static int getDigit(int number) {

        if (number <= 9) {
            return number;
        } else {
            int firstDigit = number % 10;
            int secondDigit = number / 10;

            return firstDigit + secondDigit;
        }
    }

    private static int sumOfOddPlace(long number) {
        int result = 0;

        while (number > 0) {
            result += (int) (number % 10);
            number = number / 100;
        }

        return result;
    }

    private static int sumOfDoubleEvenPlace(long number) {

        int result = 0;
        long temp;

        while (number > 0) {
            temp = number % 100;
            result += getDigit((int) (temp / 10) * 2);
            number = number / 100;
        }

        return result;
    }

    private static int prefixMatched(long number, int d) {

        if ((getPrefix(number, d) == 3)
                || (getPrefix(number, d) == 4)
                || (getPrefix(number, d) == 5)
                || (getPrefix(number, d) == 6)) {

            if (getPrefix(number, d) == 3) {
                return 3;
            } else if (getPrefix(number, d) == 4) {
                return 4;
            } else if (getPrefix(number, d) == 5) {
                return 5;
            } else if (getPrefix(number, d) == 6) {
                return 6;
            }

            return 1;
        } else {
            return 0;

        }
    }

    private static int getSize(long d) {

        int count = 0;

        while (d > 0) {
            d = d / 10;

            count++;
        }

        return count;

    }

    private static long getPrefix(long number, int k) {

        if (getSize(number) < k) {
            return number;
        } else {

            int size = getSize(number);

            for (int i = 0; i < (size - k); i++) {
                number = number / 10;
            }

            return number;

        }

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
