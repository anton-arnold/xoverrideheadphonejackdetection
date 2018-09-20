package de.antonarnold.android.xoverrideheadphonejackdetection;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.robv.android.xposed.XposedBridge;

public class XOverrideHeadphoneJackDetection implements IXposedHookLoadPackage {
    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("android"))
            return;

        XposedBridge.log("preparing shared memory...");

        //todo

        XposedBridge.log("add hooks in android...");

        findAndHookMethod("com.android.server.WiredAccessoryManager", lpparam.classLoader, "notifyWiredAccessoryChanged", long.class, int.class, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                boolean overrideEnable = true;
                int overrideValue = 0;

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