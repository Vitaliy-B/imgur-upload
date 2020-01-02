package engidea.imgurupload.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import java.lang.ref.WeakReference;

import engidea.imgurupload.R;
import engidea.imgurupload.model.imgurmodel.ImageResponse;

public class NotificationHelper {
    private final static String TAG = "ei_i";
    private static final String NOTIF_CH_DEF = "NOTIF_CH_DEF";

    private WeakReference<Context> mContext;

    public NotificationHelper(Context context) {
        this.mContext = new WeakReference<>(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notifCh = new NotificationChannel(NOTIF_CH_DEF,
                    context.getString(R.string.notification_ch_def_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            notifCh.setDescription(context.getString(R.string.notification_ch_def_desc));
            NotificationManager notifMngr = context.getSystemService(NotificationManager.class);

            if (notifMngr != null) {
                notifMngr.createNotificationChannel(notifCh);
            }
        }
    }

    public void createUploadingNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext.get(), NOTIF_CH_DEF);
        mBuilder.setSmallIcon(android.R.drawable.ic_menu_upload);
        mBuilder.setContentTitle(mContext.get().getString(R.string.notification_progress));

        mBuilder.setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager) mContext.get().getSystemService(Context.NOTIFICATION_SERVICE);

        if (mNotificationManager == null) {
            aLog.w(TAG, "mNotificationManager == null");
        } else {
            mNotificationManager.notify(mContext.get().getString(R.string.app_name).hashCode(), mBuilder.build());
        }
    }

    public void createUploadedNotification(ImageResponse response) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext.get(), NOTIF_CH_DEF);
        mBuilder.setSmallIcon(android.R.drawable.ic_menu_gallery);
        mBuilder.setContentTitle(mContext.get().getString(R.string.notifaction_success));

        mBuilder.setContentText(response.data.link);

        Intent resultIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(response.data.link));
        PendingIntent intent = PendingIntent.getActivity(mContext.get(), 0, resultIntent, 0);
        mBuilder.setContentIntent(intent);
        mBuilder.setAutoCancel(true);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setDataAndType(Uri.parse(response.data.link), "text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, response.data.link);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        NotificationManager mNotificationManager =
                (NotificationManager) mContext.get().getSystemService(Context.NOTIFICATION_SERVICE);

        if (mNotificationManager == null) {
            aLog.w(TAG, "mNotificationManager == null");
        } else {
            mNotificationManager.notify(mContext.get().getString(R.string.app_name).hashCode(), mBuilder.build());
        }
    }

    public void createFailedUploadNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext.get(), NOTIF_CH_DEF);
        mBuilder.setSmallIcon(android.R.drawable.ic_dialog_alert);
        mBuilder.setContentTitle(mContext.get().getString(R.string.notification_fail));

        mBuilder.setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager) mContext.get().getSystemService(Context.NOTIFICATION_SERVICE);

        if (mNotificationManager == null) {
            aLog.w(TAG, "mNotificationManager == null");
        } else {
            mNotificationManager.notify(mContext.get().getString(R.string.app_name).hashCode(), mBuilder.build());
        }
    }
}
