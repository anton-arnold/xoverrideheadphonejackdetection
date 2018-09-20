package de.antonarnold.android.xoverrideheadphonejackdetection;


import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.robv.android.xposed.XposedBridge;



public class XOverrideHeadphoneJackDetection implements IXposedHookLoadPackage {
    private static final String CONFIG_ACTION = "de.antonarnold.android.xoverrideheadphonejackdetection.ConfigReceiver";

    private static boolean initializedOnce = false;

    private static boolean overrideEnable = true;
    private static int overrideValue = 0;

    private static ConfigReceiver configReceiverInstance;

    public static ConfigReceiver getConfigReceiver() {
        if (configReceiverInstance == null) {
            configReceiverInstance = new ConfigReceiver();
        }
        return configReceiverInstance;
    }

    public static Application getApplicationUsingReflection() throws Exception {
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
                if(initializedOnce == false)
                {
                    initializedOnce = true;

                    XposedBridge.log("preparing shared memory...");
                    Application app = getApplicationUsingReflection();

                    if(app != null)
                    {
                        Context ctx = app.getApplicationContext();
                        if(ctx != null)
                        {
                            ConfigReceiver cr = getConfigReceiver();
                            if(cr != null)
                            {
                                IntentFilter intentFilter = new IntentFilter(CONFIG_ACTION);
                                if(intentFilter != null) {
                                    XposedBridge.log("registering config receiver intent...");
                                    try {
                                        ctx.registerReceiver(cr, intentFilter);
                                        XposedBridge.log("successfully registered...");
                                    }
                                    catch (Exception e)
                                    {
                                        XposedBridge.log("registration failed with exception: " + e.getMessage());
                                    }
                                }
                                else {
                                    XposedBridge.log("cannot create IntentFilter...");
                                }
                            }
                            else
                            {
                                XposedBridge.log("getConfigReceiver() failed...");
                            }
                        }
                        else
                        {
                            XposedBridge.log("getApplicationContext() failed...");
                        }
                    }
                    else
                    {
                        XposedBridge.log("getApplicationUsingReflection() failed...");
                    }
                }

                //todo


                if(overrideEnable) {
                    XposedBridge.log("override headphone jack detection hook (" + param.args[1] + " -> " + overrideValue + ")");
                    param.args[1] = overrideValue;
                }
                else
                {
                    XposedBridge.log("override headphone jack detection hook disabled");
                }
            }
        });
    }
}