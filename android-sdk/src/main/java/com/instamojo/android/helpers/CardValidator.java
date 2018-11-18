package com.instamojo.android.helpers;


import com.instamojo.android.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Card Validator class to validate card details.
 */
public class CardValidator {

    private static final String TAG = CardValidator.class.getSimpleName();

    /**
     * Luhn's algorithm implementation to validate the card passed.
     *
     * @param card            Card number. Require atleast first 4 to give a valid result.
     * @param skipLengthCheck skip the length check for Maestro cards.
     * @return 1 for valid card , 0 for invalid card.
     */
    public static int isValid(String card, boolean skipLengthCheck) {

        if (card.length() < 4) {
            return 0;
        }

        if (!skipLengthCheck) {
            if (validateCardTypeWithoutLengthForLimit(card) != card.length()) {
                return 0;
            }
        }

        long number = Long.valueOf(card);
        int total = sumOfDoubleEvenPlace(number) + sumOfOddPlace(number);

        if ((total % 10 == 0) && (prefixMatched(number, 1) != 0)) {
            return prefixMatched(number, 1);
        } else {
            return 0;
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
     * Check for Visa Card.
     *
     * @param card Card number to be validated.
     * @return True if card is Visa.
     */

    public static boolean visaCard(String card) {
        String PREFIX = "4";
        return (
                card.substring(0, 1).equals(PREFIX)
                        && (card.length() == 13 || card.length() == 16));
    }

    /**
     * Check for Discover Card.
     *
     * @param card Card number to be validated.
     * @return True if card is Discover else False.
     */
    public static boolean discoverCard(String card) {
        String PREFIX = "6011";
        return (card.substring(0, 4).equals(PREFIX) && (card.length() == 16));
    }

    /**
     * Check for Dinner's club International Card.
     *
     * @param card Card Number to be validated.
     * @return True if card is Dinner's club International else False.
     */
    public static boolean dinnersClubInternationalCard(String card) {
        String PREFIX = "36";
        return (card.substring(0, 2).equals(PREFIX) && (card.length() == 14));
    }

    /**
     * Check for Amex Card.
     *
     * @param card Card Number to be validated.
     * @return True if card is Amex else False.
     */
    public static boolean amexCard(String card) {
        String PREFIX = "34,37,";
        String prefix2 = card.substring(0, 2) + ",";
        return ((PREFIX.contains(prefix2)) && (card.length() == 15));
    }

    /**
     * Check for Master Card.
     *
     * @param card Card Number to be validated.
     * @return True if card is Master else False.
     */
    public static boolean masterCard(String card) {
        String PREFIX = "51,52,53,54,55,";
        String prefix2 = card.substring(0, 2) + ",";
        return ((PREFIX.contains(prefix2)) && (card.length() == 16));
    }

    /**
     * Check for Visa Card.
     *
     * @param card Card Number to be validated, requires atleast first digit of the card.
     * @return True if card is Visa else False.
     */
    public static boolean visaCardWithoutLength(String card) {
        String PREFIX = "4";
        return (card.substring(0, 1).equals(PREFIX));
    }

    /**
     * Check for Discover Card.
     *
     * @param card Card Number to be validated, requires atleast first four digits of the card.
     * @return True if card is Discover else False.
     */
    public static boolean discoverCardWithoutLength(String card) {
        String PREFIX = "6011";
        return (card.substring(0, 4).equals(PREFIX));
    }

    /**
     * Check for Dinner's club International Card.
     *
     * @param card Card Number to be validated, requires atleast first two digits of the card.
     * @return True if card is Dinner's club International else False.
     */
    public static boolean dinnersClubIntWithoutLength(String card) {
        String PREFIX = "36";
        return (card.substring(0, 2).equals(PREFIX));
    }

    /**
     * Check for Amex Card.
     *
     * @param card Card Number to be validated, requires atleast first two digits of the card.
     * @return True if card is Amex else False.
     */
    public static boolean amexCardWithoutLength(String card) {
        String PREFIX = "34,37,";
        String prefix2 = card.substring(0, 2) + ",";
        return ((PREFIX.contains(prefix2)));
    }

    /**
     * Check for Master Card.
     *
     * @param card Card Number to be validated, requires atleast first two digits of the card.
     * @return True if card is Master else False.
     */
    public static boolean masterCardWithoutLength(String card) {
        String PREFIX = "51,52,53,54,55,";
        String prefix2 = card.substring(0, 2) + ",";
        return ((PREFIX.contains(prefix2)));
    }

    /**
     * Check for Maestro Card.
     *
     * @param card Card Number to be validated, requires atleast first four digits of the card.
     * @return True if card is Maestro else False.
     */
    public static boolean maestroCard(String card) {
        String PREFIX = "5018,5044,5020,5038,5893,6304,6759,6761,6762,6763,6220,";
        String prefix2 = card.substring(0, 4) + ",";
        return ((PREFIX.contains(prefix2)));
    }

    /**
     * Returns the drawable of the card issuer.
     *
     * @param card Card number with atleast first four digits.
     * @return Drawable int of appropriate issuer if found.
     * Else returns drawable of {@link com.instamojo.android.R.drawable#ic_unknown_card}.
     */
    public static int getCardDrawable(String card) {

        if (visaCardWithoutLength(card)) {
            return R.drawable.ic_visa_card;
        }
        if (discoverCardWithoutLength(card)) {
            return R.drawable.ic_discover_card;
        }
        if (dinnersClubIntWithoutLength(card)) {
            return R.drawable.ic_dinners_club_int_card;
        }
        if (amexCardWithoutLength(card)) {
            return R.drawable.ic_amex_card;
        }
        if (masterCardWithoutLength(card)) {
            return R.drawable.ic_master_card;
        }
        if (maestroCard(card)) {
            return R.drawable.ic_maestro_card;
        }
        return R.drawable.ic_unknown_card;
    }

    /**
     * Returns the String Name of the card issuer.
     *
     * @param card Card number with atleast first four digits.
     * @return Drawable int of appropriate issuer if found. Else returns Unknown.
     */
    public static String getCardType(String card) {

        if (visaCardWithoutLength(card)) {
            return "Visa";
        }
        if (discoverCardWithoutLength(card)) {
            return "Discover";
        }
        if (dinnersClubIntWithoutLength(card)) {
            return "Dinners club Int";
        }
        if (amexCardWithoutLength(card)) {
            return "Amex";
        }
        if (masterCardWithoutLength(card)) {
            return "Master";
        }
        if (maestroCard(card)) {
            return "Maestro";
        }
        return "Unknown";
    }

    /**
     * Method to get the supposed length of the card type.
     *
     * @param card Card Number to validate, requires atleast first four digits of the card
     *             to return the valid length.
     * @return Either actual length of the card type else default 19.
     */
    public static int validateCardTypeWithoutLengthForLimit(String card) {
        if (card.contains(" ")) {
            card = card.replace(" ", "");
        }
        card = card.trim();
        if (visaCardWithoutLength(card)) {
            return 16;
        }
        if (discoverCard(card)) {
            return 16;
        }
        if (dinnersClubIntWithoutLength(card)) {
            return 14;
        }
        if (amexCardWithoutLength(card)) {
            return 15;
        }
        if (masterCardWithoutLength(card)) {
            return 16;
        }
        if (maestroCard(card)) {
            return 19;
        }
        return 19;
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
