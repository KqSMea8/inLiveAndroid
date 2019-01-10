package tw.chiae.inlive.util.Event;

/**
 * Created by Bin Li on 2015/7/14.
 */
public class EventPayment {

    private String payType;
    private String resultStatus;
    private String message;

    public EventPayment(String type, String status){
        this.payType = type;
        this.resultStatus = status;
    }

    public EventPayment(String type, String status, String message){
        this.payType = type;
        this.resultStatus = status;
        this.message = message;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(String resultStatus) {
        this.resultStatus = resultStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
