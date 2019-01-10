package tw.chiae.inlive.presentation.ui.room.create;

import android.content.Context;

import tw.chiae.inlive.util.share.ShareHelper;

import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.google.GooglePlus;

import cn.sharesdk.line.Line;
import rx.functions.Func1;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class CreateRoomShareHelper extends ShareHelper {

    private ShareParamProvider provider;
    private PlatformActionListener listener;

    public CreateRoomShareHelper(Context context, String roomId, String anchorName,String imgUrl,
                                 PlatformActionListener listener) {
        provider = new RoomShareParam(context, roomId, anchorName,imgUrl);
        this.listener = listener;
    }

    public void share(Platform platform){
        invokeShare(platform.getName(), listener, parseParam(platform));
    }

    private Func1<Void, ? extends Platform.ShareParams> parseParam(Platform platform){
        if (platform instanceof Facebook){
            return new FacebookParamBuilder(provider);
        }
        else if (platform instanceof GooglePlus){
            return new GooglePlusParamBuilder(provider);
        }
        else if (platform instanceof Line){
            return new LineParamBuilder(provider);
        }
        /*else if (platform instanceof Instagram){
            return new InstagramParamBuilder(provider);
        }*/
        throw new IllegalArgumentException("Unsupported platform!");
    }
}
