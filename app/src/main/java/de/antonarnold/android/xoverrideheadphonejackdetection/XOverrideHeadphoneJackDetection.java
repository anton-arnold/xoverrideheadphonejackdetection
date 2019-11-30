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


import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.robv.android.xposed.XposedBridge;

@SuppressWarnings("RedundantThrows")
public class XOverrideHeadphoneJackDetection implements IXposedHookLoadPackage {
    private static final String CONFIG_ACTION = "de.antonarnold.android.xoverrideheadphonejackdetection.ConfigReceiver";

    private static boolean initializedOnce = false;

    private static ConfigReceiver configReceiverInstance;

    private static ConfigReceiver getConfigReceiver() {
        if (configReceiverInstance == null) {
            configReceiverInstance = new ConfigReceiver();
        }
        return configReceiverInstance;
    }

    @SuppressLint("PrivateApi")
    private static Application getApplicationUsingReflection() throws Exception {
        return (Application) Class.forName("android.app.ActivityThread")
                .getMethod("currentApplication").invoke(null, (Object[]) null);
    }

    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("android"))
            return;


        XposedBridge.log("add hooks in android...");

        findAndHookMethod("com.android.server.WiredAccessoryManager", lpparam.classLoader, "notifyWiredAccessoryChanged", long.class, int.class, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                boolean overrideEnable = true;
                boolean hybridModeEnable = true;
                int overrideValue = 0;
                int overrideMask = 255;

                ConfigReceiver cr = getConfigReceiver();

                if(!initializedOnce)
                {
                    initializedOnce = true;

                    XposedBridge.log("preparing shared memory...");

                    try {
                        Application app = getApplicationUsingReflection();

                        if (app != null) {
                            Context ctx = app.getApplicationContext();
                            if (ctx != null) {
                                if (cr != null) {
                                    cr.setCallbackClass(param.thisObject);

                                    IntentFilter intentFilter = new IntentFilter(CONFIG_ACTION);
                                    if (intentFilter != null) {
                                        XposedBridge.log("registering config receiver intent...");
                                        ctx.registerReceiver(cr, intentFilter);
                                        cr.readConfig();
                                        cr.setIsRegistered(true);
                                        XposedBridge.log("successfully registered...");
                                    } else {
                                        XposedBridge.log("cannot create IntentFilter...");
                                    }
                                } else {
                                    XposedBridge.log("getConfigReceiver() failed...");
                                }
                            } else {
                                XposedBridge.log("getApplicationContext() failed...");
                            }
                        } else {
                            XposedBridge.log("getApplicationUsingReflection() failed...");
                        }
                    } catch (Exception e) {
                        XposedBridge.log("something went wrong with an exception: " + e.getMessage());
                    }
                }

                if((cr != null) && (cr.getIsRegistered()))
                {
                    overrideEnable = cr.getOverrideEnable();
                    hybridModeEnable = cr.getHybridModeEnable();
                    overrideValue = cr.getOverrideValue();
                    overrideMask = cr.getOverrideMask();
                }


                if(overrideEnable) {
                    if(hybridModeEnable)
                    {
                        if((int)param.args[1] != 0)
                        {
                            XposedBridge.log("override headphone jack detection hook (value: " + param.args[1] + " -> " + overrideValue + " | mask: " + param.args[2] + " -> " + overrideMask + ")");
                            param.args[1] = overrideValue;
                            param.args[2] = overrideMask;
                        }
                        else
                        {
                            XposedBridge.log("override headphone jack detection hook hybrid mode allows transition to connection state 'Nothing'");
                        }
                    }
                    else
                    {
                        XposedBridge.log("override headphone jack detection hook (value: " + param.args[1] + " -> " + overrideValue + " | mask: " + param.args[2] + " -> " + overrideMask + ")");
                        param.args[1] = overrideValue;
                        param.args[2] = overrideMask;
                    }
                }
            }
        });
    }
}