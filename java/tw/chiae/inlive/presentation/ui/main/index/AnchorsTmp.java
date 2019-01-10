package tw.chiae.inlive.presentation.ui.main.index;

import java.util.List;

import tw.chiae.inlive.data.bean.HotAnchorSummary;
import tw.chiae.inlive.presentation.ui.main.mergefilm.Log;

/**
 * Created by rayyeh on 2017/7/24.
 */

public class AnchorsTmp {

    private static AnchorsTmp infotmp;
    public List<HotAnchorSummary> anchorsInfo = null;


    public static AnchorsTmp newInstance() {
        if(infotmp==null)
            infotmp = new AnchorsTmp();
        return infotmp;
    }


    public List<HotAnchorSummary> getAnchorsInfo() {
        return anchorsInfo;
    }

    public void setAnchorsInfo(List<HotAnchorSummary> anchorsInfo) {
        this.anchorsInfo = anchorsInfo;
    }


    public HotAnchorSummary getUserInfo(String id) {
        if(anchorsInfo==null)
            return null;
        for(HotAnchorSummary userinfo : anchorsInfo){
            if(userinfo.getId().equals(id) ){
                return userinfo;
            }
        }
        return null;
    }
}
