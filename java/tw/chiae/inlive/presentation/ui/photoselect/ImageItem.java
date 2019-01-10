package tw.chiae.inlive.presentation.ui.photoselect;

import java.io.Serializable;

/**
 * Created by ymlong on Oct 26, 2015.
 */
public class ImageItem implements Serializable {
    private String mImageId;
    private String mImagePath;
    private String mThumbnailPath;
    private boolean mIsSelected = false;

    public String getImageId() {
        return mImageId;
    }

    public void setImageId(String imageId) {
        this.mImageId = imageId;
    }

    public String getThumbnailPath() {
        return mThumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.mThumbnailPath = thumbnailPath;
    }

    public String getImagePath() {
        return mImagePath;
    }

    public void setImagePath(String imagePath) {
        this.mImagePath = imagePath;
    }

    public boolean isSelected() {
        return mIsSelected;
    }

    public void setSelected(boolean isSelected) {
        this.mIsSelected = isSelected;
    }
}