package tw.chiae.inlive.util;

import android.content.Context;

import tw.chiae.inlive.R;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class LocaleFormats {

    /**
     * Format a amount to app-common format. The amount param must can be cast to a long number,
     * or no format would be applied.
     * @see #formatMoney(Context, long)
     */
    public static String formatMoney(Context context, String amount){
        try{
            long number = Long.parseLong(amount);
            return formatMoney(context, number);
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
        return amount;
    }

    /**
     * Format a amount to app-common format.
     */
    public static String formatMoney(Context context, long amount) {
        double result = 0;
        if (amount / (10000 * 10000) > 0) {
            result = amount / (10000 * 10000D);
            return context.getString(R.string.currency_hundred_million, result);
        } else if (amount / 10000 > 0) {
            result = amount / 10000D;
            return context.getString(R.string.currency_ten_thousand, result);
        }
        return String.valueOf(amount);
    }
}
