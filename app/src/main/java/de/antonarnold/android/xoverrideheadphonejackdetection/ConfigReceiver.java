package de.antonarnold.android.xoverrideheadphonejackdetection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import de.robv.android.xposed.XposedBridge;
import static de.robv.android.xposed.XposedHelpers.callMethod;

public class ConfigReceiver extends BroadcastReceiver {
    private boolean isRegistered;
    private boolean overrideEnable;
    private int overrideValue;
    private int overrideMask;
    private Object callbackClass;

    public ConfigReceiver()
    {
        isRegistered = false;
        overrideEnable = true;
        overrideValue = 0;
        overrideMask =  20;
        callbackClass = null;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        XposedBridge.log("ConfigReceiver.onReceive(...) called!");

        //todo get data
        if(intent != null) {
            Bundle extras = intent.getExtras();
            if(extras != null) {
                overrideEnable = extras.getInt("overrideEnable", 1) != 0;
                overrideValue = extras.getInt("overrideValue", 0);
                overrideMask = extras.getInt("overrideMask", 20);
            }
        }

        //enforce update on callback class
        if(callbackClass != null)
        {
            try {
                callMethod(callbackClass, "notifyWiredAccessoryChanged", new Long(0), new Integer(overrideValue), new Integer(overrideMask));
            }
            catch(Exception e)
            {
                XposedBridge.log("enforce update on callback class failed with exception: " + e.getMessage());
            }
        }
    }

    public boolean getOverrideEnable()
    {
        return overrideEnable;
    }

    public int getOverrideValue()
    {
        return overrideValue;
    }

    public int getOverrideMask()
    {
        return overrideMask;
    }

    public boolean getIsRegistered()
    {
        return isRegistered;
    }

    public void setIsRegistered(boolean val)
    {
        isRegistered = val;
    }

    public void setCallbackClass(Object val)
    {
        callbackClass = val;
    }
}
