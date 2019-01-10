package tw.chiae.inlive.data.bean;

/**
 * Created by Administrator on 2016/12/6 0006.
 */

public class StartConferenceBean {
    private String conference_invitation;
    private String conference_type;
    private int conference_invitation_return;

    public String getConference_type() {
        return conference_type;
    }

    public void setConference_type(String conference_type) {
        this.conference_type = conference_type;
    }

    public String getConference_invitation() {
        return conference_invitation;
    }

    public void setConference_invitation(String conference_invitation) {
        this.conference_invitation = conference_invitation;
    }

    public int getConference_invitation_return() {
        return conference_invitation_return;
    }

    public void setConference_invitation_return(int conference_invitation_return) {
        this.conference_invitation_return = conference_invitation_return;
    }

    @Override
    public String toString() {
        return "ContentMsg{" +
                "conference_invitation='" + conference_invitation + '\'' +
                ", conference_type='" + conference_type + '\'' +
                ", conference_invitation_return='" + conference_invitation_return + '\'' +
                '}';
    }
}
