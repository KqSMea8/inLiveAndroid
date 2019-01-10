package tw.chiae.inlive.util.ffmpegutil;

import android.content.Context;

/*import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;*/

/**
 * Created by Administrator on 2017/2/13 0013.
 */

public class FFmpegUtil {

    /*private FFmpeg mFFmpeg;

    private Context mContext;

    private FFmpegResponseInterface fFmpegResponseInterface;

    public void initFFmpegLibrary(Context context, FFmpegResponseInterface fFmpegResponseInterface){
        this.mContext=context;
        this.mFFmpeg=FFmpegInstance.getInstance(mContext);
        this.fFmpegResponseInterface=fFmpegResponseInterface;
    }

    public void loadFFmpegLibrary(){
        if (fFmpegResponseInterface==null||mFFmpeg==null) {
            return;
        }
        try {
            mFFmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                    fFmpegResponseInterface.loadFFmpegFailure();
                }

                @Override
                public void onSuccess() {
                    fFmpegResponseInterface.loadFFmpegSuccess();
                }
            });
        } catch (FFmpegNotSupportedException e) {
            fFmpegResponseInterface.loadFFmpegFailure();
        }
    }

    public void executeCmd(String cmd){
        if (fFmpegResponseInterface!=null||mFFmpeg==null){
            if (cmd!=null){
                String[] command = cmd.split(" ");
                if (command.length<1){
                    try {
                        mFFmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                            @Override
                            public void onFailure(String s) {
                                fFmpegResponseInterface.executeCmdFailure(s);
                            }

                            @Override
                            public void onSuccess(String s) {
                                fFmpegResponseInterface.executeCmdSuccess(s);
                            }

                            @Override
                            public void onProgress(String s) {
                                fFmpegResponseInterface.executeCmdProgress(s);
                            }

                            @Override
                            public void onStart() {
                               fFmpegResponseInterface.executeCmdStart();
                            }

                            @Override
                            public void onFinish() {
                                fFmpegResponseInterface.executeCmdFinish();
                            }
                        });
                    } catch (FFmpegCommandAlreadyRunningException e) {
                        // do nothing for now
                        fFmpegResponseInterface.executeCmdFailure(e.toString());
                    }
                }else {
                    fFmpegResponseInterface.executeCmdNull();
                }
            }else {
                fFmpegResponseInterface.executeCmdNull();
            }
        }
    }

    public FFmpeg getmFFmpeg(){
        return mFFmpeg;
    }*/
}
