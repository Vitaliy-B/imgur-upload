package engidea.imgurupload.model;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ImageItemContent {
//    private final static String TAG = "ei_i";

    public static class ImageItem {
        public final String path;

        ImageItem(String path) {
            this.path = path;
        }

        @Override @NonNull
        public String toString() {
            return path;
        }
    }

    public final List<ImageItem> ITEMS = new ArrayList<>();

    public ImageItemContent(Context context) {
        ArrayList<ImageItem> alImages = getAllImagesPath(context);
//        aLog.w(TAG, "All images: " + alImages);
        ITEMS.clear();
        ITEMS.addAll(alImages);
    }

    private ArrayList<ImageItem> getAllImagesPath(Context context) {
        Uri uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        int columnIndexData;
        ArrayList<ImageItem> listOfAllImages = new ArrayList<>();
        String absolutePathOfImage;
        String[] projection = { MediaStore.MediaColumns.DATA};

        Cursor cursor = context.getContentResolver().query(uri, projection, null,
                null, null);

        if (cursor != null) {
            columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(columnIndexData);
                listOfAllImages.add(new ImageItem(absolutePathOfImage));
            }
            cursor.close();
        }

        return listOfAllImages;
    }
}
