package engidea.imgurupload.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import engidea.imgurupload.Constants;
import engidea.imgurupload.utils.NotificationHelper;
import engidea.imgurupload.model.imgurmodel.ImageResponse;
import engidea.imgurupload.model.imgurmodel.ImgurAPI;
import engidea.imgurupload.model.imgurmodel.Upload;
import engidea.imgurupload.utils.NetworkUtils;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class UploadService extends Service {
//    public final static String TAG = "ei_i";

    public class LocalBinder extends Binder {
        public UploadService getService() {
            return UploadService.this;
        }
    }

    public UploadService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Nullable @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }

    public void execute(Upload upload, Callback<ImageResponse> callback) {
        new Thread(() -> {
            final Callback<ImageResponse> cb = callback;

            if (!NetworkUtils.isConnected(this)) {
                cb.failure(null);
                return;
            }

            final NotificationHelper notificationHelper = new NotificationHelper(this);
            notificationHelper.createUploadingNotification();

            RestAdapter restAdapter = buildRestAdapter();

            restAdapter.create(ImgurAPI.class).postImage(
                    Constants.getClientAuth(),
                    upload.title, upload.description, upload.albumId, null,
                    new TypedFile("image/*", upload.image),
                    new Callback<ImageResponse>() {
                        @Override
                        public void success(ImageResponse imageResponse, Response response) {
                            if (cb != null) cb.success(imageResponse, response);
                            if (response == null) {
                                notificationHelper.createFailedUploadNotification();
                                return;
                            }
                            if (imageResponse.success) {
                                notificationHelper.createUploadedNotification(imageResponse);
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            if (cb != null) cb.failure(error);
                            notificationHelper.createFailedUploadNotification();
                        }
                    });
        }).start();
    }

    private RestAdapter buildRestAdapter() {
        RestAdapter imgurAdapter = new RestAdapter.Builder()
                .setEndpoint(ImgurAPI.server)
                .build();

        if (Constants.LOGGING)
            imgurAdapter.setLogLevel(RestAdapter.LogLevel.BASIC);
        return imgurAdapter;
    }
}
