package tw.chiae.inlive.presentation.ui.room.publish;

import android.hardware.Camera;
import android.support.annotation.NonNull;

import tw.chiae.inlive.data.bean.CameraSize;

import java.util.List;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public interface CameraSizePicker {

    int STD_WIDTH = 800;
    int STD_HEIGHT = 480;

    CameraSize[] WHITE_LIST = new CameraSize[]{new CameraSize(800, 480), new CameraSize(640, 480)};

    /**
     * Choose the best size for the camera.
     */
    @NonNull
    CameraSize selectBestSize(List<Camera.Size> list);
}
