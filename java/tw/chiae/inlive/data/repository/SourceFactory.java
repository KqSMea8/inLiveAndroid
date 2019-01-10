package tw.chiae.inlive.data.repository;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.L;

/**
 * 数据源工厂类。
 * Created by huanzhang on 2016/4/11.
 */
public class SourceFactory {

    private static ISource source;
    private static ISourceApi3 sourceApi3String;
    private static ISourceApi3 sourceApi3Json;
    private static ISourceApi2 sourceApi2Json;
    private static ISourceApi2 sourceApi2String;

    //Should not be instantiable
    private SourceFactory() {

    }

    /**
     * 获取数据源。
     */
    public static ISource create() {
        if (source == null) {
            synchronized (SourceFactory.class) {
                if (source == null) {
                    source = new RetrofitSource();
//                    source = new FakeSource();
                }
            }
        }
        return source;
    }

    /*public static ISourceApi2 createApi2Json() {
        if (sourceApi2Json == null) {
            synchronized (SourceFactory.class) {
                if (sourceApi2Json == null) {
                    sourceApi2Json = new RetrofitSourceApi2(true);
                }
            }
        }
        return sourceApi2Json;
    }*/

/*
    public static ISourceApi2 createApi2String() {
        if (sourceApi2String == null) {
            synchronized (SourceFactory.class) {
                if (sourceApi2String == null) {
                    sourceApi2String = new RetrofitSourceApi2(false);
                }
            }
        }
        return sourceApi2String;
    }
*/

    public static ISourceApi3 createApi3Json() {
        if (sourceApi3Json == null) {
            synchronized (SourceFactory.class) {
                if (sourceApi3Json == null) {
                    sourceApi3Json = new RetrofitSourceApi3(true);
                }
            }
        }
        return sourceApi3Json;
    }



    public static ISourceApi3 createApi3String() {
        if (sourceApi3String == null) {
            synchronized (SourceFactory.class) {
                if (sourceApi3String == null) {
                    Log.i("RayTest","new RetrofitSourceApi3");
                    sourceApi3String = new RetrofitSourceApi3(false);
                }
            }
        }
        return sourceApi3String;
    }

    public static String wrapPath(String path) {
        if (!path.startsWith("http")) {
            path = Const.MAIN_HOST_URL + path;
        }
        L.e("huan",path);
        return path;
    }

    public static Uri wrapPathToUri(@NonNull String path) {
        return Uri.parse(wrapPath(path));
    }


    /**
     * 按照与服务器的约定判断一个指定的数字代表男性还是女性。
     *
     * @return 如果是男性则返回true, 否则返回false。
     */
    public static boolean isMale(int gender) {
        return gender == 0;
    }


}
