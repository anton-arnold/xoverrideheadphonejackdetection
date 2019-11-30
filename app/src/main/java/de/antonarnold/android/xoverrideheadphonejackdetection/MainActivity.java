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
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener {
    private static final String CONFIG_ACTION = "de.antonarnold.android.xoverrideheadphonejackdetection.ConfigReceiver";

    private CheckBox cbEnabledState;
    private CheckBox cbHybridState;
    private RadioGroup rgConnectionState;
    private RadioButton rbNothingState;
    private RadioButton rbHeadphoneState;
    private RadioButton rbHeadsetState;
    private RadioButton rbLineoutState;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cbEnabledState = new CheckBox(this);
        cbEnabledState.setText("Override enabled");
        cbEnabledState.setChecked(true);

        cbHybridState = new CheckBox(this);
        cbHybridState.setText("Hybrid mode");
        cbHybridState.setChecked(false);

        rbNothingState = new RadioButton(this);
        rbNothingState.setText("Nothing");

        rbHeadphoneState = new RadioButton(this);
        rbHeadphoneState.setText("Headphone");

        rbHeadsetState = new RadioButton(this);
        rbHeadsetState.setText("Headset");

        rbLineoutState = new RadioButton(this);
        rbLineoutState.setText("Lineout");

        Button btnUpdate = new Button(this);
        btnUpdate.setText("Update Settings");
        btnUpdate.setOnClickListener(this);

        TextView tvConnectionState = new TextView(this);
        tvConnectionState.setText("Connection state:");

        rgConnectionState = new RadioGroup(this);
        rgConnectionState.addView(rbNothingState);
        rgConnectionState.addView(rbHeadphoneState);
        rgConnectionState.addView(rbHeadsetState);
        rgConnectionState.addView(rbLineoutState);
        rgConnectionState.check(rgConnectionState.getChildAt(0).getId());

        TextView tvTransitionNote = new TextView(this);
        tvTransitionNote.setText("Avoid direct changes between connection states. Override to 'Nothing' and make sure the transition is performed before selecting the next one to prevent misbehavior of the internal state machine. Hybrid Mode overrides to the selected state only if a plugged in cable is detected. If you enable this state you may need to plug in and out your cable before it takes effect.");
        tvTransitionNote.setTextSize(9.0f);

        GridLayout layout = new GridLayout(this);
        layout.setColumnCount(1);
        layout.addView(cbEnabledState);
        layout.addView(cbHybridState);
        layout.addView(tvConnectionState);
        layout.addView(rgConnectionState);
        layout.addView(btnUpdate);
        layout.addView(tvTransitionNote);
        setContentView(layout);
    }

    public void onClick(View v)
    {
        Intent intent = new Intent(CONFIG_ACTION);

        intent.putExtra("overrideEnable", ( (cbEnabledState.isChecked() ? 1 : 0) | (cbHybridState.isChecked() ? 2 : 0) ));

        int overrideValue;

        if(rbHeadphoneState.isChecked())
        {
            overrideValue = 4;
        }
        else if(rbHeadsetState.isChecked())
        {
            overrideValue = 4 + 16;
        }
        else if(rbLineoutState.isChecked())
        {
            overrideValue = 64;
        }
        else
        {
            overrideValue = 0;
        }

        intent.putExtra("overrideValue", overrideValue);
        intent.putExtra("overrideMask", 255);

        Context ctx = getApplicationContext();
        ctx.sendBroadcast(intent);
    }
}
