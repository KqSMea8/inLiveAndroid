package tw.chiae.inlive.presentation.ui.login.splash;

/**
 * Created by rayyeh on 2017/11/14.
 */

public class PackageVersionInfo {

    //{"update":1510647216974,
    // "app":"tw.chiae.inlive",
    // "fetch":false,
    // "status":"1",
    // "name":null,
    // "locale":"en",
    // "dialog":{
    // "update":"Update",
    // "title":"Update Notice",
    // "rate":"Review",
    // "skip":"Skip",
    // "wbody":"Version:  1.1.21-build0513 (November 13, 2017)<br\/><br\/>K歌大明星合唱錄製優化<br\/>"
    // },
    // "published":"November 13, 2017",
    // "version":"1.1.21-build0513"
    // }


    long update;
    String app;
    boolean fetch;
    String status;
    String name;
    String locale;
    Dialog dialog;
    String published;
    String version;

    public long getUpdate() {
        return update;
    }

    public void setUpdate(long update) {
        this.update = update;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public boolean isFetch() {
        return fetch;
    }

    public void setFetch(boolean fetch) {
        this.fetch = fetch;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public Dialog getDialog() {
        return dialog;
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }


    public class Dialog {
        String update;
        String title;
        String rate;
        String skip;
        String wbody;


        public String getUpdate() {
            return update;
        }

        public void setUpdate(String update) {
            this.update = update;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getRate() {
            return rate;
        }

        public void setRate(String rate) {
            this.rate = rate;
        }

        public String getSkip() {
            return skip;
        }

        public void setSkip(String skip) {
            this.skip = skip;
        }

        public String getWbody() {
            return wbody;
        }

        public void setWbody(String wbody) {
            this.wbody = wbody;
        }




    }
}
