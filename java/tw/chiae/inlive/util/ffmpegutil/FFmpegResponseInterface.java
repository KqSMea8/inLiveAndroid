package tw.chiae.inlive.util.ffmpegutil;

/**
 * Created by Administrator on 2017/2/13 0013.
 */

public interface FFmpegResponseInterface {
    /**
     * 加載lib失敗
     */
    void loadFFmpegFailure();

    /**
     * 加載lib成功
     */
    void loadFFmpegSuccess();

    /**
     * 空的cmd
     */
    void executeCmdNull();

    /**
     * 失敗了
     * @param s
     */
    void executeCmdFailure(String s);

    /**
     * 成功了
     * @param s
     */
    void executeCmdSuccess(String s);

    /**
     * 進度
     * @param progress
     */
    void executeCmdProgress(String progress);

    /**
     * 開始
     */
    void executeCmdStart();

    /**
     * 結束
     */
    void executeCmdFinish();


}