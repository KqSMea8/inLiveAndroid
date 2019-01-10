package tw.chiae.inlive.util.Jpush;

import java.util.Set;

import static tw.chiae.inlive.util.Jpush.TagAliasOperatorHelper.ACTION_SET;
import static tw.chiae.inlive.util.Jpush.TagAliasOperatorHelper.sequence;

/**
 * Created by rayyeh on 2017/12/4.
 */

public class TagAliasBean{
    int action;
    Set<String> tags;
    String alias;
    boolean isAliasAction;

    public TagAliasBean(String userId) {
        setAction(ACTION_SET) ;
        setAlias(userId);
        setAliasAction(true);
        sequence++;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public boolean isAliasAction() {
        return isAliasAction;
    }

    public void setAliasAction(boolean aliasAction) {
        isAliasAction = aliasAction;
    }



    @Override
    public String toString() {
        return "TagAliasBean{" +
                "action=" + action +
                ", tags=" + tags +
                ", alias='" + alias + '\'' +
                ", isAliasAction=" + isAliasAction +
                '}';
    }
}

