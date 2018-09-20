package de.antonarnold.android.xoverrideheadphonejackdetection;

/*
import android.os.Environment;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
*/

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.robv.android.xposed.XposedBridge;

//import de.robv.android.xposed.IXposedHookZygoteInit;

//public class XOverrideHeadphoneJackDetection implements IXposedHookZygoteInit {
public class XOverrideHeadphoneJackDetection implements IXposedHookLoadPackage {
    // 1st try + fix1
    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("android"))
            return;

        XposedBridge.log("preparing shared memory...");


        XposedBridge.log("add hooks in android...");

        // final class WiredAccessoryManager implements WiredAccessoryCallbacks {
        //  public void notifyWiredAccessoryChanged(long whenNanos, int switchValues, int switchMask) {

        findAndHookMethod("com.android.server.WiredAccessoryManager", lpparam.classLoader, "notifyWiredAccessoryChanged", long.class, int.class, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                //XposedBridge.log("<beforeHookedMethod>  in: android.server.WiredAccessoryManager.notifyWiredAccessoryChanged(" + param.args[0] + ", " + param.args[1] + ", " + param.args[2] + ")");


                boolean overrideEnable = true;
                int overrideValue = 0;

                /* well it could have been easy but noooo
                try {
                    File overrideFile = new File(Environment.getExternalStorageDirectory().getPath() + "/.override_headphone_jack_detection");

                    if (overrideFile.exists()) {
                        FileInputStream fi = new FileInputStream(overrideFile);
                        DataInputStream di = new DataInputStream(fi);
                        overrideValue = di.readInt();
                        overrideEnable = true;
                    }
                } catch (IOException e) {
                    XposedBridge.log("IOException: " + e.toString());
                    overrideEnable = false;
                } */

                if(overrideEnable) {
                    XposedBridge.log("override headphone jack detection hook (" + param.args[1] + " -> " + overrideValue + ")");
                    param.args[1] = overrideValue;
                }
                else
                {
                    XposedBridge.log("override headphone jack detection hook disabled");
                }

                //XposedBridge.log("<beforeHookedMethod> out: android.server.WiredAccessoryManager.notifyWiredAccessoryChanged(" + param.args[0] + ", " + param.args[1] + ", " + param.args[2] + ")");
            }
        });
    }

    // 2nd try
    /*
    public void initZygote (IXposedHookZygoteInit.StartupParam startupParam) {
        XposedBridge.log("we are in initZygote...");

        findAndHookMethod("com.android.server.WiredAccessoryManager", null, "notifyWiredAccessoryChanged", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                XposedBridge.log("<beforeHookedMethod> com.android.server.WiredAccessoryManager.notifyWiredAccessoryChanged(...)");
            }
        });
    }*/
}