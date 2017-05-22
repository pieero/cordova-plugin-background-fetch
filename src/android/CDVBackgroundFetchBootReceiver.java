package de.panko.cdvbackgroundfetchplugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.DateFormat;
import java.util.Date;

public class CDVBackgroundFetchBootReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = "CDVBackgroundFetchBootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        DateFormat sdf = DateFormat.getDateTimeInstance();
        Log.d(LOG_TAG, "wakeup boot receiver fired at " + sdf.format(new Date()));
    }
}
