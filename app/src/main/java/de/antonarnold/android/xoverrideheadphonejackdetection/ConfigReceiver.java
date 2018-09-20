package de.antonarnold.android.xoverrideheadphonejackdetection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import de.robv.android.xposed.XposedBridge;
import static de.robv.android.xposed.XposedHelpers.callMethod;

public class ConfigReceiver extends BroadcastReceiver {
    private static final int SW_HEADPHONE_INSERT = 0x02;
    private static final int SW_MICROPHONE_INSERT = 0x04;

    private static final int SW_HEADPHONE_INSERT_BIT = 1 << SW_HEADPHONE_INSERT;
    private static final int SW_MICROPHONE_INSERT_BIT = 1 << SW_MICROPHONE_INSERT;

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
        overrideMask =  SW_HEADPHONE_INSERT_BIT | SW_MICROPHONE_INSERT_BIT;
        callbackClass = null;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        XposedBridge.log("ConfigReceiver.onReceive(...) called!");

        //todo get data

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
