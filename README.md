# XOverrideHeadphoneJackDetection

## What is this?
This XPosed module allows you to manually override the headphone jack detection of an Android device.

## Why is this useful?
Most smartphones provide a headphone/headset jack that allows using a headset instead the internal speaker/microphone.
To automatically select between these, a mechanical switch is added to detect a plugged in cable.
If this switch breaks and replacing it is not realistic, your phone may become more or less unusable.
With this module you are able to manually control the headphone detection state and continue using your phone.

## How do I use it?
This is a module for the
[**XPosed framework**](https://forum.xda-developers.com/xposed/xposed-installer-versions-changelog-t2714053)
so you need it installed on your phone. For installation and further information refer to their FAQs.
After installing the XOverrideHeadphoneJackDetection apk, it should be added in the list of modules.
Before it will become active you have to enable it in the XPosedInstaller's Module section and reboot your phone.
You can check the Logs section to see if this module was loaded properly.
The **default behavior** is to **override to no headphone detected**.
In the GUI you can change the behavior. Your changes will be stored and reapplied after reboots.
The GUI does not fetch the active configuration upon start! This may be changed in future versions.

## How does it work internally?
This module hooks with the help of the XPosed framework into the android system servers
[**WiredAccessoryManager**] (https://github.com/LineageOS/android_frameworks_base/blob/staging/lineage-15.1/services/core/java/com/android/server/WiredAccessoryManager.java)
class and manipulates the parameters of the mehtod
[**notifyWiredAccessoryChanged**](https://github.com/LineageOS/android_frameworks_base/blob/staging/lineage-15.1/services/core/java/com/android/server/WiredAccessoryManager.java#L122)
before it's called.
The GUI uses android intent broadcasts as IPC to configure the module functionality at runtime.
This necessary since the GUI runs in another context and not able to share a config file with the hooked process without messing around with app permissions.

## Debugging hints
### Configure via adb shell
```
adb shell am broadcast -a de.antonarnold.android.xoverrideheadphonejackdetection.ConfigReceiver --ei overrideEnable 1 --ei overrideValue 4 --ei overrideMask 255
adb shell am broadcast -a de.antonarnold.android.xoverrideheadphonejackdetection.ConfigReceiver --ei overrideEnable 1 --ei overrideValue 20 --ei overrideMask 255
adb shell am broadcast -a de.antonarnold.android.xoverrideheadphonejackdetection.ConfigReceiver --ei overrideEnable 1 --ei overrideValue 0 --ei overrideMask 255
adb shell am broadcast -a de.antonarnold.android.xoverrideheadphonejackdetection.ConfigReceiver --ei overrideEnable 0
```

## License and copyright information
Copyright (C) 2018  Anton Arnold

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