package tw.chiae.inlive.presentation.ui.chatting.utils;

import android.content.Context;
import android.content.SharedPreferences;

import tw.chiae.inlive.presentation.ui.main.mergefilm.Log;

import static android.R.attr.data;
import static android.R.attr.name;
import static tw.chiae.inlive.R.string.sex;

/**
 * Created by rayyeh on 2017/9/19.
 */

public class ConfigSharePreference {
    private static SharedPreferences settings;
    private static final String ENVIROMENT = "ENVIROMENT";

    public static void init(Context context, String name) {
        settings = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public static boolean readEnviroment(){
        if(settings!=null)
            return settings.getBoolean(ENVIROMENT, false);
        else
        {
            Log.i("RayTest","ConfigSharePreference setting null");
        }

            return false;
    }
    public static void saveEnviroment(boolean envStat){
        if(settings!=null){
            settings.edit()
                    .putBoolean(ENVIROMENT, envStat)
                    .commit();
        }else
        {
            Log.i("RayTest","ConfigSharePreference setting null");
        }

    }

}

