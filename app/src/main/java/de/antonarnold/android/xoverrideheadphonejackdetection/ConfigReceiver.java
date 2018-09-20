package de.antonarnold.android.xoverrideheadphonejackdetection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import de.robv.android.xposed.XposedBridge;

public class ConfigReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        XposedBridge.log("ConfigReceiver.onReceive(...) called!");
    }
}
