package de.panko.cdvbackgroundfetchplugin;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @author Sven Panko (sp@intuitiveminds.de)
 */
public class CDVBackgroundFetchService extends IntentService {

    private static final Map<String, Semaphore> semaphores = new HashMap<String, Semaphore>();
    private static final String LOG_TAG = "CDVBackgroundFetchService";


    public CDVBackgroundFetchService() {
        super("CDVBackgroundFetchService");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        try {
            String id = UUID.randomUUID().toString();

            JSONObject o = new JSONObject();
            o.put("type", "BackgroundFetch");
            o.put("id", id);

            Semaphore semaphore = new Semaphore(0);
            semaphores.put(id, semaphore);

            CDVBackgroundFetchPlugin.notifyAsync(o);

            int counter = 0;
            boolean released = false;

            while (!released && counter < 120) {
                released = semaphore.tryAcquire(250, TimeUnit.MILLISECONDS);
                counter++;
            }

            if (!released) {
                Log.e(LOG_TAG, "timeout occurred while waiting for background task to finish");
            }
            else {
                Log.i(LOG_TAG, "received background finish signal");
            }

            semaphores.remove(id);
        }
        catch (Exception e) {
            Log.e(LOG_TAG, "exception while trying to wakeup in service", e);
        }
    }

    public static void executionFinished(String id) {
        Semaphore semaphore = semaphores.get(id);

        if (semaphore != null) {
            Log.i(LOG_TAG, "signalling finish to background task with id " + id);

            semaphore.release(1);
        } else {
            Log.e(LOG_TAG, "no running background task found while signalling execution finished for id " + id);
        }
    }
}