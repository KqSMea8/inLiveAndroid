package tw.chiae.inlive.presentation.ui.photoselect;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import android.provider.MediaStore.Images.Media;

import tw.chiae.inlive.util.L;

public class AlbumHelper {
    private final ContentResolver mCr;


    public AlbumHelper(Context context) {
        mCr = context.getContentResolver();
    }

    public ArrayList<ImageItem> getImages() {
        String columns[] = new String[]{Media._ID, Media.BUCKET_ID,
                Media.PICASA_ID, Media.DATA, Media.DISPLAY_NAME, Media.TITLE,
                Media.SIZE, Media.BUCKET_DISPLAY_NAME};
        Cursor cur = mCr.query(Media.EXTERNAL_CONTENT_URI, columns, null, null,
                               null);
        return extractImagesFromCursor(cur);
    }


    private ArrayList<ImageItem> extractImagesFromCursor(Cursor cur) {
        ArrayList<ImageItem> images = new ArrayList<>();
        if (cur != null) {
            if (cur.moveToFirst()) {
                int photoIDIndex = cur.getColumnIndexOrThrow(Media._ID);
                int photoPathIndex = cur.getColumnIndexOrThrow(Media.DATA);
                int photoNameIndex = cur.getColumnIndexOrThrow(Media.DISPLAY_NAME);

                int photoTitleIndex = cur.getColumnIndexOrThrow(Media.TITLE);
                int photoSizeIndex = cur.getColumnIndexOrThrow(Media.SIZE);
                int bucketDisplayNameIndex = cur.getColumnIndexOrThrow(Media.BUCKET_DISPLAY_NAME);
                int bucketIdIndex = cur.getColumnIndexOrThrow(Media.BUCKET_ID);
                int picasaIdIndex = cur.getColumnIndexOrThrow(Media.PICASA_ID);
                do {
                    String id = cur.getString(photoIDIndex);
                    String name = cur.getString(photoNameIndex);
                    String path = cur.getString(photoPathIndex);
                    String title = cur.getString(photoTitleIndex);
                    String size = cur.getString(photoSizeIndex);
                    String bucketName = cur.getString(bucketDisplayNameIndex);
                    String bucketId = cur.getString(bucketIdIndex);
                    String picasaId = cur.getString(picasaIdIndex);

                    L.d("AlbumHelper",id + ", bucketId: " + bucketId + ", picasaId: "
                                      + picasaId + " name:" + name + " path:" + path
                                      + " title: " + title + " size: " + size + " bucket: "
                                      + bucketName + "---");

                    if (!name.endsWith(".gif") && !name.endsWith(".GIF")) {
                        ImageItem imageItem = new ImageItem();
                        imageItem.setImageId(id);
                        imageItem.setImagePath(path);
                        images.add(imageItem);
                    }
                } while (cur.moveToNext());
            }
        }
        return images;
    }

}
