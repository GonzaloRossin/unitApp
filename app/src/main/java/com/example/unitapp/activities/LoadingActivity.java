package com.example.unitapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unitapp.R;
import com.example.unitapp.model.AppPreferences;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        AppPreferences preferences = new AppPreferences(this.getApplication());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        Intent intent;
        if(preferences.getAuthToken() != null) {
            intent = new Intent(LoadingActivity.this, MainActivity.class);
        }else {
            intent = new Intent(LoadingActivity.this,LoginActivity.class);
        }
        startActivity(intent);

    }
}
