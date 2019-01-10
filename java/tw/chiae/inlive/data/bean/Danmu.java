package tw.chiae.inlive.data.bean;

import android.graphics.Bitmap;

import tw.chiae.inlive.data.bean.websocket.UserPublicMsg;

/**
 * Created by Administrator on 2016/6/27 0027.
 */
public class Danmu {
    public UserPublicMsg publicMsg;
    public long   id;
    public int    userId;
    public String type;
    public Bitmap avatarUrl;
    public String content;

    public Danmu(long id, int userId, String type,String content, UserPublicMsg msg) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.content = content;
        this.publicMsg = msg;
    }

    public void setAvatarUrl(Bitmap avatarUrl) {
        this.avatarUrl = avatarUrl;
    }


}
