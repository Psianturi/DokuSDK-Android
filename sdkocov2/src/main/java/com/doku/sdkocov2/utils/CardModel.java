package com.doku.sdkocov2.utils;

/**
 * Created by zaki on 12/18/15.
 */

import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

/**
 * Enumerates each supported card type. see http://en.wikipedia.org/wiki/Bank_card_number for more
 * details.
 *
 * @version 1.0
 */
public enum CardModel {
    /**
     * American Express cards start in 34 or 37
     */
    AMEX("AmEx"),
    /**
     * Diners Club
     */
    DINERSCLUB("DinersClub"),
    /**
     * Discover starts with 6x for some values of x.
     */
    DISCOVER("Discover"),
    /**
     * JCB (see http://www.jcbusa.com/) cards start with 35
     */
    JCB("JCB"),
    /**
     * Mastercard starts with 51-55
     */
    MASTERCARD("MasterCard"),
    /**
     * Visa starts with 4
     */
    VISA("Visa"),
    /**
     * Maestro
     */
    MAESTRO("Maestro"),
    /**
     * Unknown card type.
     */
    UNKNOWN("Unknown"),
    /**
     * Not enough information given.
     * <br><br>
     * More digits are required to know the card type. (e.g. all we have is a 3, so we don't know if
     * it's JCB or AmEx)
     */
    INSUFFICIENT_DIGITS("More digits required");

    private static int minDigits = 1;
    private static HashMap<Pair<String, String>, CardModel> intervalLookup;

    static {
        // initialize
        intervalLookup = new HashMap<Pair<String, String>, CardModel>();
        intervalLookup.put(getNewPair("300", "305"), CardModel.DINERSCLUB);      // Diners Club (Discover)
        intervalLookup.put(getNewPair("309", null), CardModel.DINERSCLUB);       // Diners Club (Discover)
        intervalLookup.put(getNewPair("34", null), CardModel.AMEX);              // AmEx
        intervalLookup.put(getNewPair("3528", "3589"), CardModel.JCB);           // JCB
        intervalLookup.put(getNewPair("36", null), CardModel.DINERSCLUB);        // Diners Club (Discover)
        intervalLookup.put(getNewPair("37", null), CardModel.AMEX);              // AmEx
        intervalLookup.put(getNewPair("38", "39"), CardModel.DINERSCLUB);        // Diners Club (Discover)
        intervalLookup.put(getNewPair("4", null), CardModel.VISA);               // Visa
        intervalLookup.put(getNewPair("50", null), CardModel.MAESTRO);           // Maestro
        intervalLookup.put(getNewPair("51", "55"), CardModel.MASTERCARD);        // MasterCard
        intervalLookup.put(getNewPair("20", "29"), CardModel.MASTERCARD);        // MasterCard
        intervalLookup.put(getNewPair("56", "59"), CardModel.MAESTRO);           // Maestro
        intervalLookup.put(getNewPair("6011", null), CardModel.DISCOVER);        // Discover
        intervalLookup.put(getNewPair("61", null), CardModel.MAESTRO);           // Maestro
        intervalLookup.put(getNewPair("62", null), CardModel.DISCOVER);          // China UnionPay (Discover)
        intervalLookup.put(getNewPair("63", null), CardModel.MAESTRO);           // Maestro
        intervalLookup.put(getNewPair("644", "649"), CardModel.DISCOVER);        // Discover
        intervalLookup.put(getNewPair("65", null), CardModel.DISCOVER);          // Discover
        intervalLookup.put(getNewPair("66", "69"), CardModel.MAESTRO);           // Maestro
        intervalLookup.put(getNewPair("88", null), CardModel.DISCOVER);          // China UnionPay (Discover)

        for (Entry<Pair<String, String>, CardModel> entry : getIntervalLookup().entrySet()) {
            minDigits = Math.max(minDigits, entry.getKey().first.length());
            if (entry.getKey().second != null) {
                minDigits = Math.max(minDigits, entry.getKey().second.length());
            }
        }
    }

    public final String name;

    private CardModel(String name) {
        this.name = name;
    }

    /**
     * Determine if a number matches a prefix interval
     *
     * @param number        credit card number
     * @param intervalStart prefix (e.g. "4") or prefix interval start (e.g. "51")
     * @param intervalEnd   prefix interval end (e.g. "55") or null for non-intervals
     * @return -1 for insufficient digits, 0 for no, 1 for yes.
     */
    private static boolean isNumberInInterval(String number, String intervalStart,
                                              String intervalEnd) {
        int numCompareStart = Math.min(number.length(), intervalStart.length());
        int numCompareEnd = Math.min(number.length(), intervalEnd.length());

        if (Integer.parseInt(number.substring(0, numCompareStart)) < Integer.parseInt(intervalStart
                .substring(0, numCompareStart))) {
            // number is too low
            return false;
        } else if (Integer.parseInt(number.substring(0, numCompareEnd)) > Integer
                .parseInt(intervalEnd.substring(0, numCompareEnd))) {
            // number is too high
            return false;
        }

        return true;
    }

    private static HashMap<Pair<String, String>, CardModel> getIntervalLookup() {
        return intervalLookup;
    }

    private static Pair<String, String> getNewPair(String intervalStart, String intervalEnd) {
        if (intervalEnd == null) {
            // set intervalEnd to intervalStart before creating the Pair object, because apparently
            // Pair.hashCode() can't handle nulls on some devices/versions. WTF.
            intervalEnd = intervalStart;
        }
        return new Pair<String, String>(intervalStart, intervalEnd);
    }

    /**
     * Infer the card type from a string.
     *
     * @param typeStr The String value of this enum
     * @return the matched real type
     */
    public static CardModel fromString(String typeStr) {
        if (typeStr == null) {
            return CardModel.UNKNOWN;
        }

        for (CardModel type : CardModel.values()) {
            if (type == CardModel.UNKNOWN || type == CardModel.INSUFFICIENT_DIGITS) {
                continue;
            }

            if (typeStr.equalsIgnoreCase(type.toString())) {
                return type;
            }
        }
        return CardModel.UNKNOWN;
    }

    /**
     * Infer the CardModel from the number string. See http://en.wikipedia.org/wiki/Bank_card_number
     * for these ranges (last checked: 19 Feb 2013)
     *
     * @param numStr A string containing only the card number.
     * @return the inferred card type
     */
    public static CardModel fromCardNumber(String numStr) {
        if (TextUtils.isEmpty(numStr)) {
            return CardModel.UNKNOWN;
        }
        if (isInteger(numStr) == true && numStr.length() == 16) {

            HashSet<CardModel> possibleCardModels = new HashSet<CardModel>();
            for (Entry<Pair<String, String>, CardModel> entry : getIntervalLookup().entrySet()) {
                boolean isPossibleCard = isNumberInInterval(numStr, entry.getKey().first,
                        entry.getKey().second);
                if (isPossibleCard) {
                    possibleCardModels.add(entry.getValue());
                }
            }

            if (possibleCardModels.size() > 1) {
                return CardModel.INSUFFICIENT_DIGITS;
            } else if (possibleCardModels.size() == 1) {
                return possibleCardModels.iterator().next();
            } else {
                return CardModel.UNKNOWN;
            }

        }
        return CardModel.UNKNOWN;
    }


    public static boolean isInteger(String s) {

        if (s.matches("\\d+(?:\\.\\d+)?")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return name;
    }

    public String getDisplayName(String languageOrLocale) {
        switch (this) {
            case AMEX:
                return "American Express";
            case DINERSCLUB:
            case DISCOVER:
                return "Discover";
            case JCB:
                return "JCB";
            case MASTERCARD:
                return "Master Card";
            case VISA:
                return "Visa";
            default:
                break;
        }

        return null;
    }

    /**
     * @return 15 for AmEx, -1 for unknown, 16 for others.
     */
    public int numberLength() {
        int result;
        switch (this) {
            case AMEX:
                result = 15;
                break;
            case JCB:
            case MASTERCARD:
            case VISA:
            case DISCOVER:
                result = 16;
                break;
            case DINERSCLUB:
                result = 14;
                break;
            case INSUFFICIENT_DIGITS:
                // this represents the maximum number of digits before we can know the card type
                result = minDigits;
                break;
            case UNKNOWN:
            default:
                result = -1;
                break;
        }
        return result;
    }

    /**
     * @return 4 for Amex, 3 for others, -1 for unknown
     */
    public int cvvLength() {
        int result;
        switch (this) {
            case AMEX:
                result = 4;
                break;
            case JCB:
            case MASTERCARD:
            case VISA:
            case DISCOVER:
            case DINERSCLUB:
                result = 3;
                break;
            case UNKNOWN:
            default:
                result = -1;
                break;
        }

        return result;
    }
}