package tw.chiae.inlive.util;

import java.text.DecimalFormat;

/**
 * Created by Administrator on 2017/3/10 0010.
 */

public class ConversionUtil {

    public static String conversionNumber(int number){
        if(number/100000000>0){
            return number/100000000+"億";
        }else if(number/10000>0){
            return number/10000+"萬";
        }else{
            return Integer.toString(number);
        }
    }

    public static String conversionNumber(Long number){
        if(number/100000000>0){
            return number/100000000+"億";
        }else if(number/10000>0){
            return number/10000+"萬";
        }else{
            return String.valueOf(number);
        }
    }

    //    单位换算
    public static String conversionNumberDoubel(double number) {
        if (number / 100000000.0d >=1.0d) {
            return new DecimalFormat("#").format(number / 100000000d)+"億";
        } else if (number / 10000.0d >= 1.0d) {
            return new DecimalFormat("#").format(number / 10000d)+"萬";
        } else {
            return new DecimalFormat("#").format(number);
        }
    }


    public static String conversionNumberWorld(double number){
        if (number/1000000000000.0d>=1.0d){
            return new DecimalFormat("#").format(number/1000000000000d)+"t";
        }else if (number/1000000000.0d>=1.0d){
            return new DecimalFormat("#").format(number/1000000000d)+"g";
        }else if (number/1000000.0d>=1.0d){
            return new DecimalFormat("#").format(number/1000000d)+"m";
        }else if (number/1000.0d>=1.0d){
            return new DecimalFormat("#").format(number/1000000d)+"k";
        }else {
            return new DecimalFormat("#").format(number);
        }
    }
}
