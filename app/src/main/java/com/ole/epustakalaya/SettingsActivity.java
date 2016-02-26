package com.ole.epustakalaya;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ole.epustakalaya.helper.Preference;
import com.ole.epustakalaya.models.PustakalayApp;


public class SettingsActivity extends ActionBarActivity {

    private RadioButton rbPustakalayaServer, rbSchoolServer, rbCustomServer;
    private EditText etIPCustomServer;
    Preference preference;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setTheme(R.style.myTheme);

        super.onCreate(savedInstanceState);
        Tracker t = ((PustakalayApp) getApplication()).getTracker(
                PustakalayApp.TrackerName.APP_TRACKER);

        // Set screen name.
        t.setScreenName("settings");

        // Send a screen view.
        t.send(new HitBuilders.ScreenViewBuilder().build());

        setContentView(R.layout.activity_settings);
        this.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#60B4E5")));
        context = getApplicationContext();


        preference = new Preference(context);

        rbPustakalayaServer = (RadioButton) findViewById(R.id.rbPustakalayaServer);
        rbSchoolServer = (RadioButton) findViewById(R.id.rbSchoolServer);
        rbCustomServer = (RadioButton) findViewById(R.id.rbCustomServer);
        etIPCustomServer  = (EditText) findViewById(R.id.etIPCustomServer);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadCurrentState();

        rbPustakalayaServer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //save the state on preference and toggle the activeness

                if (rbPustakalayaServer.isChecked()) {
                    preference.setServer("remote");
                    rbSchoolServer.setChecked(false);
                    rbCustomServer.setChecked(false);
                    etIPCustomServer.setEnabled(false);

                    Tracker t = ((PustakalayApp) getApplication()).getTracker(
                            PustakalayApp.TrackerName.APP_TRACKER);

                    t.send(new HitBuilders.EventBuilder()
                            .setCategory("Clicks")  // category i.e. Player Buttons
                            .setAction("settings")    // action i.e.  Play
                            .setLabel("pustakalaya server selected")    // label i.e.  any meta-data
                            .build());
                }

            }
        });

        rbSchoolServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (rbSchoolServer.isChecked()) {
                    preference.setServer("local");

                    rbPustakalayaServer.setChecked(false);
                    rbCustomServer.setChecked(false);
                    etIPCustomServer.setEnabled(false);
                    Tracker t = ((PustakalayApp) getApplication()).getTracker(
                            PustakalayApp.TrackerName.APP_TRACKER);

                    t.send(new HitBuilders.EventBuilder()
                            .setCategory("Clicks")  // category i.e. Player Buttons
                            .setAction("settings")    // action i.e.  Play
                            .setLabel("skoolserver server selected")    // label i.e.  any meta-data
                            .build());
                }
            }
        });


        rbCustomServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (rbCustomServer.isChecked()) {
                    preference.setServer("custom", etIPCustomServer.getText().toString());
                    rbPustakalayaServer.setChecked(false);
                    rbSchoolServer.setChecked(false);
                    etIPCustomServer.setEnabled(true);
                    etIPCustomServer.setText(preference.getCustomServer());

                    Tracker t = ((PustakalayApp) getApplication()).getTracker(
                            PustakalayApp.TrackerName.APP_TRACKER);

                    t.send(new HitBuilders.EventBuilder()
                            .setCategory("Clicks")  // category i.e. Player Buttons
                            .setAction("settings")    // action i.e.  Play
                            .setLabel("custom0 server selected "+etIPCustomServer.getText())    // label i.e.  any meta-data
                            .build());
                }
            }
        });


        etIPCustomServer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                preference.setServer("custom",etIPCustomServer.getText().toString());
            }
        });

    }

    void loadCurrentState(){
        String serverType = preference.getServerType();
        String serverIP = preference.getCustomServer();

        if(!serverIP.equalsIgnoreCase("")){
            etIPCustomServer.setText(serverIP);
        }
        etIPCustomServer.setEnabled(false);

        if(serverType.equalsIgnoreCase("remote")){
            rbPustakalayaServer.setChecked(true);
        } else if(serverType.equalsIgnoreCase("local")){
            rbSchoolServer.setChecked(true);
        } else if(serverType.equalsIgnoreCase("custom")){
            rbCustomServer.setChecked(true);
            etIPCustomServer.setEnabled(true);
        } else {
            rbPustakalayaServer.setChecked(true);
            Log.i("TEST","Why this");
        }

        if(rbCustomServer.isChecked() && etIPCustomServer.getText().toString().trim().length()==0){
            Toast.makeText(context,"Please provide the IP of the server where pustakalaya is hosted.",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
