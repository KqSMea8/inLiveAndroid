package tw.chiae.inlive.data.bean;

/**
 * Created by Administrator on 2016/11/25 0025.
 */

public class LineLoginBean {
    private Meta meta;
    public class Meta{
        private int code;
        public Meta(int code) {
            this.code = code;
        }
        public int getCode() {
            return code;
        }
        public void setCode(int code) {
            this.code = code;
        }
    }

    private Data data;
    public class Data{
        private String id;
        private String username;
        private String profile_picture;
        public String getProfile_picture() {
            return profile_picture;
        }
        public void setProfile_picture(String profile_picture) {
            this.profile_picture = profile_picture;
        }
        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
        public String getUsername() {
            return username;
        }
        public void setUsername(String username) {
            this.username = username;
        }
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "LineLoginBean{" +
                "meta=" + meta +
                ", data=" + data +
                '}';
    }
}
