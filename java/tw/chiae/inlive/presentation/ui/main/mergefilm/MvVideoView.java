package tw.chiae.inlive.presentation.ui.main.mergefilm;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.*;
import android.view.View;

import com.yqritc.scalablevideoview.ScalableVideoView;

/**
 * Created by Administrator on 2017/2/21 0021.
 */

public class MvVideoView extends ScalableVideoView {


    public MvVideoView(Context context) {
        super(context);
        if(isInEditMode())return;
    }

    public MvVideoView(Context context, AttributeSet attrs) {

        super(context, attrs);
        if(isInEditMode())return;
    }

    public MvVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if(isInEditMode())return;
    }

    public MediaPlayer getMediaPlayer(){
        return mMediaPlayer;
    }

    @Override
    public void start() {
        if (mMediaPlayer==null)
            return;

        super.start();
    }

    @Override
    public void stop() {
        if (mMediaPlayer==null)
            return;
        super.stop();
    }

    @Override
    public void reset() {
        if (mMediaPlayer==null)
            return;
        super.reset();
    }

    @Override
    public void release() {
        if (mMediaPlayer==null)
            return;
        super.release();
    }

    @Override
    public void pause() {
        if (mMediaPlayer==null)
            return;
        super.pause();
    }

    @Override
    public boolean isPlaying() {
        if (mMediaPlayer==null)
            return false;
        return super.isPlaying();
    }

}
