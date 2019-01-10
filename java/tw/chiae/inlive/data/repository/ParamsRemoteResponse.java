package tw.chiae.inlive.data.repository;

import java.util.List;

import tw.chiae.inlive.data.bean.BaseResponse;

/**
 * Created by rayyeh on 2017/8/9.
 */

public class ParamsRemoteResponse {
//{"env":0,"toast":0,"auth":1000518,"mainOfficial":"100518","officiList":[100518,100787,1155914]}
    int env ;
    int toast;
    int gash;
    String auth;
    String mainOfficial;
    List<String> officiList;


    public int getEnv() {
        return env;
    }

    public void setEnv(int env) {
        this.env = env;
    }

    public int getToast() {
        return toast;
    }

    public void setToast(int toast) {
        this.toast = toast;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getMainOfficial() {
        return mainOfficial;
    }

    public void setMainOfficial(String mainOfficial) {
        this.mainOfficial = mainOfficial;
    }

    public List<String> getOfficiList() {
        return officiList;
    }

    public void setOfficiList(List<String> officiList) {
        this.officiList = officiList;
    }


    public int getGash() {
        return gash;
    }

    public void setGash(int gash) {
        this.gash = gash;
    }


}
