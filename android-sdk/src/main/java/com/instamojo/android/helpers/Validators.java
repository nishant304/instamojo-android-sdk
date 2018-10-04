package com.instamojo.android.helpers;

import android.support.annotation.NonNull;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.METValidator;

/**
 * {@link MaterialEditText} related Validators.
 * Can be assigned through {@link MaterialEditText#addValidator(METValidator)}
 * and {@link MaterialEditText#validate()} should be called to validate the Edit Text.
 */
public class Validators {

    /**
     * Empty field validator with Required as Default Message.
     * Use {@link #EmptyFieldValidator(String)} to set a custom error message.
     */
    public static class EmptyFieldValidator extends METValidator {

        public EmptyFieldValidator(String errorMessage) {
            super(errorMessage);
        }

        public EmptyFieldValidator() {
            super("Required");
        }

        @Override
        public boolean isValid(@NonNull CharSequence charSequence, boolean result) {
            return !result;
        }
    }

    /**
     * Date validator to check the expiry date of the card.
     */
    public static class DateValidator extends METValidator {

        public DateValidator() {
            super("Invalid Date");
        }

        @Override
        public boolean isValid(@NonNull CharSequence text, boolean isEmpty) {
            return !com.instamojo.android.helpers.CardValidator.isDateExpired(text.toString());
        }
    }

    /**
     * Card validator to check the validity of the card number with all the Edge cases considered.
     */
    public static class CardValidator extends METValidator {

        public CardValidator() {
            super("Invalid Card");
        }

        @Override
        public boolean isValid(@NonNull CharSequence text, boolean isEmpty) {
            String card = text.toString().trim().replaceAll(" ", "");
            if (card.length() < 4) {
                return false;
            }
            int result;
            if (com.instamojo.android.helpers.CardValidator.masterCardWithoutLength(card)
                    || com.instamojo.android.helpers.CardValidator.dinnersClubIntWithoutLength(card)
                    || com.instamojo.android.helpers.CardValidator.visaCardWithoutLength(card)
                    || com.instamojo.android.helpers.CardValidator.amexCardWithoutLength(card)
                    || com.instamojo.android.helpers.CardValidator.discoverCardWithoutLength(card)) {
                result = com.instamojo.android.helpers.CardValidator.isValid(card, false);
            } else {
                result = com.instamojo.android.helpers.CardValidator.isValid(card, true);
            }
            return result != 0;
        }
    }

    /**
     * Virtual Payment address Validator
     */
    public static class VPAValidator extends METValidator {


        public VPAValidator() {
            super("Invalid Virtual Payment Address");
        }

        @Override
        public boolean isValid(@NonNull CharSequence text, boolean isEmpty) {
            if (isEmpty) {
                return false;
            }

            String[] splitData = text.toString().split("@");
            if (splitData.length != 2) {
                return false;
            }

            return !text.toString().contains(".com");
        }
    }
}
