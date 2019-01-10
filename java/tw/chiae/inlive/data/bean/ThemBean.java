package tw.chiae.inlive.data.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/7/18 0018.
 * 话题
 */
public class ThemBean {

    List<Topic> topic;
    String all_num;

    public List<Topic> getTopic() {
        return topic;
    }

    public String getAll_num() {
        return all_num;
    }

    @Override
    public String toString() {
        return "ThemBean{" +
                "topic=" + topic +
                ", all_num='" + all_num + '\'' +
                '}';
    }

    public class Topic{
//      当前话题的id
        String topic_id;
//        当前话题的标题
        String topic_title;
//        当前话题直播的人数
        String user_num;

        public String getTopic_id() {
            return topic_id;
        }

        public String getTopic_title() {
            return topic_title;
        }

        public String getTopic_num() {
            return user_num;
        }
    }
}
