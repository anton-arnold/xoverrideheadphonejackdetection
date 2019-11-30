/*
This Xposed module allows you to manually override the headphone jack detection of an Android device.
Copyright (C) 2019  Anton Arnold

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package de.antonarnold.android.xoverrideheadphonejackdetection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import de.robv.android.xposed.XposedBridge;
import static de.robv.android.xposed.XposedHelpers.callMethod;

public class ConfigReceiver extends BroadcastReceiver {
    private static final String CONFIG_FILE = "/data/system/xoverrideheadphonejackdetection.cfg.bin";

    private static final boolean defaultOverrideEnable = true;
    private static final boolean defaultOverrideHybridModeEnable = false;
    private static final int defaultOverrideValue = 0;
    private static final int defaultOverrideMask = 255;


    private boolean isRegistered;
    private boolean overrideEnable;
    private boolean overrideHybridModeEnable;
    private int overrideValue;
    private int overrideMask;
    private Object callbackClass;

    public ConfigReceiver()
    {
        isRegistered = false;
        callbackClass = null;

        overrideEnable = defaultOverrideEnable;
        overrideHybridModeEnable = defaultOverrideHybridModeEnable;
        overrideValue = defaultOverrideValue;
        overrideMask =  defaultOverrideMask;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        XposedBridge.log("ConfigReceiver.onReceive(...) called!");

        //todo get data
        if(intent != null) {
            Bundle extras = intent.getExtras();
            if(extras != null) {
                int overrideControl = extras.getInt("overrideEnable", ( (overrideEnable ? 1 : 0) | (overrideHybridModeEnable ? 2 : 0)));
                overrideEnable = (overrideControl & 1) != 0;
                overrideHybridModeEnable = (overrideControl & 2) != 0;
                overrideValue = extras.getInt("overrideValue", overrideValue);
                overrideMask = extras.getInt("overrideMask", overrideMask);

                writeConfig();
            }
        }

        //enforce update on callback class
        if((callbackClass != null) && overrideEnable && !overrideHybridModeEnable)
        {
            try {
                callMethod(callbackClass, "notifyWiredAccessoryChanged", 0L, overrideValue, overrideMask);
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

    public boolean getHybridModeEnable()
    {
        return overrideHybridModeEnable;
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

    public void readConfig()
    {
        boolean backupOverrideEnable = overrideEnable;
        boolean backupOverrideHybridModeEnable = overrideHybridModeEnable;
        int backupOverrideValue = overrideValue;
        int backupOverrideMask = overrideMask;

        try {
            File file = new File(CONFIG_FILE);
            FileInputStream fileInputStream = new FileInputStream(file);
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);
            overrideEnable = dataInputStream.readBoolean();
            overrideValue = dataInputStream.readInt();
            overrideMask = dataInputStream.readInt();
            if(dataInputStream.available() > 0) {
                overrideHybridModeEnable = dataInputStream.readBoolean();
            }
            dataInputStream.close();
        }
        catch(Exception e)
        {
            XposedBridge.log("readConfig() failed with exception: " + e.getMessage());

            overrideEnable = backupOverrideEnable;
            overrideValue = backupOverrideValue;
            overrideMask = backupOverrideMask;
            overrideHybridModeEnable = backupOverrideHybridModeEnable;
        }
    }

    private void writeConfig()
    {
        try {
            File file = new File(CONFIG_FILE);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);
            dataOutputStream.writeBoolean(overrideEnable);
            dataOutputStream.writeInt(overrideValue);
            dataOutputStream.writeInt(overrideMask);
            dataOutputStream.writeBoolean(overrideHybridModeEnable);
            dataOutputStream.close();
        }
        catch(Exception e)
        {
            XposedBridge.log("writeConfig() failed with exception: " + e.getMessage());
        }
    }
}
