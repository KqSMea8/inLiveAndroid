package tw.chiae.inlive.presentation.ui.main.mergefilm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

import tw.chiae.inlive.data.bean.BaseResponse;

/**
 * Created by rayyeh on 2017/4/6.
 */

public class EventActivity implements Serializable {

    ArrayList<EventItem> events ;

    public ArrayList<EventItem> getEvents() {
        if(events==null)
            events = new ArrayList<>();
        return events;
    }

    public void setEvents(ArrayList<EventItem> events) {
        this.events = events;
    }

    public class EventItem {

        private int id ;
        //活動名稱
        private String name;
        //是否顯示  0:  1:顯示 0:不顯示
        private int isViewable;
        //是否啟動活動  1:有作用 0:無作用
        private int isActive;
        //是否可投票    1:可以 0:不可以
        private int isVotable;
        //顯示起始時間
        private String viewTimeBegin;
        //顯示結束時間
        private String viewTimeEnd;
        //活動起始時間
        private String activeTimeBegin;
        //活動結束時間
        private String activeTimeEnd;
        //投票起始時間
        private String voteTimeBegin;
        //投票結束時間
        private String voteTimeEnd;
        //活動網址
        private String url;
        //活動圖標
        private String imgUrl;
        private int status;

        private String fullUrl;


        public String getFullUrl() {
            return fullUrl;
        }

        public void setFullUrl(String fullUrl) {
            this.fullUrl = fullUrl;
        }

        public EventItem(int id, String name, int isViewable, int isActive, int isVotable, String viewTimeBegin, String viewTimeEnd, String activeTimeBegin, String activeTimeEnd, String voteTimeBegin, String voteTimeEnd) {
            this.id = id;
            this.name = name;
            this.isViewable = isViewable;
            this.isActive = isActive;
            this.isVotable = isVotable;
            this.viewTimeBegin = viewTimeBegin;
            this.viewTimeEnd = viewTimeEnd;
            this.activeTimeBegin = activeTimeBegin;
            this.activeTimeEnd = activeTimeEnd;
            this.voteTimeBegin = voteTimeBegin;
            this.voteTimeEnd = voteTimeEnd;
        }

        public String toString(){
            String str = "==============\n"+
                    "id: "+ id +"\n"+
                    "name: "+ name +"\n"+
                    "isViewable: "+ isViewable +"\n"+
                    "isActive: "+ isActive +"\n"+
                    "isVotable: "+ isVotable +"\n"+
                    "viewTimeBegin: "+ viewTimeBegin +"\n"+
                    "viewTimeEnd: "+ viewTimeEnd +"\n"+
                    "activeTimeBegin: "+ activeTimeBegin +"\n"+
                    "activeTimeEnd: "+ activeTimeEnd +"\n"+
                    "voteTimeBegin: "+ voteTimeBegin +"\n"+
                    "status: "+ status +"\n"+
                    "voteTimeEnd: "+ voteTimeEnd +"\n"
                    ;
            return str;
        }
        public int getId() {

            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int isViewable() {

            return (int)isViewable;
        }

        public void setViewable(int viewable) {
            isViewable = viewable;
        }

        public boolean isActive() {
            if(isActive<1)
                return false;
            else
                return true;
        }

        public void setActive(int active) {
            isActive= active;
        }

        public int isVotable() {
            return isVotable;
        }

        public void setVotable(int votable) {
            isVotable = votable;
        }

        public String getViewTimeBegin() {
            return viewTimeBegin;
        }

        public void setViewTimeBegin(String viewTimeBegin) {
            this.viewTimeBegin = viewTimeBegin;
        }

        public String getViewTimeEnd() {
            return viewTimeEnd;
        }

        public void setViewTimeEnd(String viewTimeEnd) {
            this.viewTimeEnd = viewTimeEnd;
        }

        public String getActiveTimeBegin() {
            return activeTimeBegin;
        }

        public void setActiveTimeBegin(String activeTimeBegin) {
            this.activeTimeBegin = activeTimeBegin;
        }

        public String getActiveTimeEnd() {
            return activeTimeEnd;
        }

        public void setActiveTimeEnd(String activeTimeEnd) {
            this.activeTimeEnd = activeTimeEnd;
        }

        public String getVoteTimeBegin() {
            return voteTimeBegin;
        }

        public void setVoteTimeBegin(String voteTimeBegin) {
            this.voteTimeBegin = voteTimeBegin;
        }

        public String getVoteTimeEnd() {
            return voteTimeEnd;
        }

        public void setVoteTimeEnd(String voteTimeEnd) {
            this.voteTimeEnd = voteTimeEnd;
        }

        public String getEventUrl() {
            return url;
        }

        public void setEventUrl(String eventUrl) {
            this.url = eventUrl;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public int getStatus() {
            Log.i("RayTest",getId()+" getStatus: "+status);
           /* int stat = Integer.parseInt(status);
            if(stat<0)
                return 0;
            else
                return Integer.parseInt(status);*/
           return status;
        }

        public void setStatus(int status) {
            Log.i("RayTest",getId()+" setStatus: "+status);
            this.status = status;
        }
    }

}
