package tw.chiae.inlive.presentation.ui.chatting;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;

import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;

import java.util.Locale;

import tw.chiae.inlive.R;

/**
 * Created by rayyeh on 2017/7/26.
 */

public class mImagePickerActivity extends ImagePickerActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar)this.findViewById(com.nguyenhoanglam.imagepicker.R.id.toolbar);
        this.setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.yunkacolor_60));
        Resources res2 = getApplicationContext().getResources();
        DisplayMetrics dm2 = res2.getDisplayMetrics();
        android.content.res.Configuration conf2 = res2.getConfiguration();
        conf2.locale = new Locale("zh");
        res2.updateConfiguration(conf2, dm2);
    }
}
