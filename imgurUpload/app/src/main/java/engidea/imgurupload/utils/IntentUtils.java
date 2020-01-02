package engidea.imgurupload.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class IntentUtils {
    private final static String TAG = "ei_i";
    private final static List<ComponentName> browserComponents  = new ArrayList<ComponentName>() {{
        add(new ComponentName("com.google.android.browser", "com.google.android.browser.BrowserActivity"));
        add(new ComponentName("com.android.browser", "com.android.browser.BrowserActivity"));
        add(new ComponentName("com.android.chrome", "com.google.android.apps.chrome.IntentDispatcher"));
    }};

    public static void openInNativeBrowser(Context context, String url) {
        if (context == null || TextUtils.isEmpty(url)) {
            return;
        }

        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(url));

        for (ComponentName cn : browserComponents) {
            intent.setComponent(cn);
            ActivityInfo ai = intent.resolveActivityInfo(pm, 0);
            if (ai != null) {
                aLog.w(TAG, "browser:  " + ai);
                context.startActivity(intent);
                return;
            }
        }

        // no native browser
        intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        List<ResolveInfo> list = pm.queryIntentActivities(intent, 0);
        if (list.size() > 0) {
//            for (ResolveInfo ri : list) {
//                aLog.w(TAG, ri + " : " + ri.isDefault);
//            }
            context.startActivity(intent);
        } else {
            aLog.w(TAG, "no browser apps");
        }
    }
}
