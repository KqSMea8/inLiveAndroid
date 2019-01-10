package tw.chiae.inlive.presentation.ui.photoselect;

import tw.chiae.inlive.presentation.ui.base.BaseUiInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ymlong on Nov 18, 2015.
 */
interface IPhotoSelect extends BaseUiInterface{
    void onSelected(ImageItem selectedImages);

    void showPhotos(List<ImageItem> photoList);
}