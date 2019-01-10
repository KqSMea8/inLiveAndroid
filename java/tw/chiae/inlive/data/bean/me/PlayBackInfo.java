package tw.chiae.inlive.data.bean.me;


import java.util.List;

/**
 * Created by Administrator on 2016/7/12 0012.
 */
public class PlayBackInfo {
    String title;
    String roomid;
    long starttime;
    long endtime;
    String streamstatus;
    String localtime;
    List<Topic> topic;
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRoomid() {
        return roomid;
    }

    public void setRoomid(String roomid) {
        this.roomid = roomid;
    }

    public long getStarttime() {
        return starttime;
    }

    public void setStarttime(long starttime) {
        this.starttime = starttime;
    }

    public long getEndtime() {
        return endtime;
    }

    public void setEndtime(long endtime) {
        this.endtime = endtime;
    }

    public String getStreamstatus() {
        return streamstatus;
    }

    public void setStreamstatus(String streamstatus) {
        this.streamstatus = streamstatus;
    }

    public long getStart() {
        return starttime;
    }

    public void setStart(long start) {
        this.starttime = start;
    }

    public long getEnd() {
        return endtime;
    }

    public void setEnd(long end) {
        this.endtime = end;
    }

    public String getLocaltime() {
        return localtime;
    }

    public List<Topic> getTopic() {
        return topic;
    }

    public class Topic{
        //      当前话题的id
        String id;
        //        当前话题的标题
        String title;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
