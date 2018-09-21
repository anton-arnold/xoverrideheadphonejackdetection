package de.antonarnold.android.xoverrideheadphonejackdetection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;

public class MainActivity extends Activity implements View.OnClickListener {
    private static final String CONFIG_ACTION = "de.antonarnold.android.xoverrideheadphonejackdetection.ConfigReceiver";

    private CheckBox cbEnabledState;
    private CheckBox cbHeadphoneState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cbEnabledState = new CheckBox(this);
        cbEnabledState.setText("Override enabled");
        cbEnabledState.setChecked(true);

        cbHeadphoneState = new CheckBox(this);
        cbHeadphoneState.setText("Headphone connected");
        cbEnabledState.setChecked(false);

        Button btnUpdate = new Button(this);
        btnUpdate.setText("Update Settings");
        btnUpdate.setOnClickListener(this);

        GridLayout layout = new GridLayout(this);
        layout.setColumnCount(1);
        layout.addView(cbEnabledState);
        layout.addView(cbHeadphoneState);
        layout.addView(btnUpdate);
        setContentView(layout);
    }

    public void onClick(View v)
    {
        Intent intent = new Intent(CONFIG_ACTION);

        intent.putExtra("overrideEnable", (cbEnabledState.isChecked() ? 1 : 0));
        intent.putExtra("overrideValue", (cbHeadphoneState.isChecked() ? 4 : 0));
        intent.putExtra("overrideMask", 255);

        Context ctx = getApplicationContext();
        ctx.sendBroadcast(intent);
    }
}
