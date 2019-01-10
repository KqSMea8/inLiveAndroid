package tw.chiae.inlive.presentation.ui.chatting.utils;

import android.os.Environment;
import android.text.format.DateFormat;

import tw.chiae.inlive.BeautyLiveApplication;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Ken on 2015/11/13.
 */
public class FileHelper {

    private static FileHelper mInstance = new FileHelper();

    public static FileHelper getInstance() {
        return mInstance;
    }

    public static boolean isSdCardExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static String getUserAvatarPath(String userName) {
        return BeautyLiveApplication.PICTURE_DIR + userName + ".png";
    }

    public static String createAvatarPath(String userName) {
        String dir = BeautyLiveApplication.PICTURE_DIR;
        File destDir = new File(dir);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        File file;
        if (userName != null) {
            file = new File(dir, userName + ".png");
        }else {
            file = new File(dir, new DateFormat().format("yyyy_MMdd_hhmmss",
                    Calendar.getInstance(Locale.CHINA)) + ".png");
        }
        return file.getAbsolutePath();
    }
}
