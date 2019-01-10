package tw.chiae.inlive.presentation.ui.main.mergefilm;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;


// K歌大明星活動
public class KaraStar
{
	private static KaraStar instance = null;

	public static final int REQUEST_VIDEO_RECORD	= 10119;

	public interface ViewHandler
	{
		void switchToRecordView(KSWebActivity act, int starId, String starVideoUrl, String videoname);
		void switchToUserView(KSWebActivity act, String userId);
	}
	ViewHandler viewHandler;

	private KaraStar()
	{
	}

	// 取得唯一的 Karastar 物件
	public static KaraStar getInstance()
	{
		if (instance == null)
			instance = new KaraStar();

		return instance;
	}

	// 開啟活動頁
	public void open(final Activity currentActivity, final String userId, final ViewHandler _viewHandler)
	{

		viewHandler = _viewHandler;

		// 直接切換到 LongE_Activity
		Intent act = new Intent(currentActivity, KSWebActivity.class);

		act.putExtra("userId", userId);

		currentActivity.startActivity(act);

	}

    private void showErrorMsgDiaLog(Activity currentActivity, String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity)
                .setTitle("公告")
                .setMessage(str)
                .setPositiveButton("關閉", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }
}
