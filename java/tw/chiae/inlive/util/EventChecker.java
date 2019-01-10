package tw.chiae.inlive.util;

import android.content.Context;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.gson.Gson;

import java.util.ArrayList;

import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.main.mergefilm.EventActivity;
import tw.chiae.inlive.presentation.ui.main.mergefilm.Log;

/**
 * Created by rayyeh on 2017/11/16.
 */

public class EventChecker {
    private final Gson gson;
    private AQuery aq;
    private Context mContext;
    private String apiUrl = "https://api2.inlive.tw/api2/";
    private String url ;
    private ArrayList<EventActivity.EventItem> events;

    public EventChecker(Context applicationContext) {

        this.mContext = applicationContext;
        url = apiUrl + "get_event_settings?uid=" + LocalDataManager.getInstance().getLoginInfo().getUserId();
        aq = new AQuery(mContext);
        gson = new Gson();
    }

    public String getUrlbyId(final int eventid, final EventCallback callback) {
        aq.ajax(url,String.class,new AjaxCallback<String>(){
            @Override
            public void callback(String url, String json, AjaxStatus status) {
                if(json != null){
                    events = gson.fromJson(json,EventActivity.class).getEvents();
                    for(EventActivity.EventItem item : events){
                        Log.i("RayTest","id:"+item.getId()+" Name:"+item.getName() + " Url:"+item.getFullUrl());
                        if(item.getId()==eventid){
                            callback.getUrl(item.getId(),item.getName(),item.getFullUrl()+"?uid="+LocalDataManager.getInstance().getLoginInfo().getUserId());
                        }

                    }
                }
            }
        });
        return url;
    }


    public interface EventCallback {

        void getUrl(int id, String name, String fullUrl);
    }
}
