package engidea.imgurupload.utils;

import android.util.Log;

import engidea.imgurupload.Constants;

/**
 * Basic logger bound to a flag in Constants.java
 */
public class aLog {
    public static void w (String TAG, String msg){
        if(Constants.LOGGING) {
            if (TAG != null && msg != null)
                Log.w(TAG, msg);
        }
    }

}
