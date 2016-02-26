package com.ole.epustakalaya;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

public class AboutUsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       this.setTheme(R.style.myTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        this.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#60B4E5")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
