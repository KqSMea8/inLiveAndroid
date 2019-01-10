package tw.chiae.inlive.data.repository;

import tw.chiae.inlive.data.bean.BaseResponse;

/**
 * Created by rayyeh on 2017/7/6.
 */

public class ServerEventResponse<T> extends BaseResponse {

    private int status;

    private String content;


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }




    @Override
    public String toString() {
        return "ServerEventResponse{" +
                "status=" + status +
                ", content='" + content + '\'' +
                '}';
    }
}
