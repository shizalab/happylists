package com.happy.happylists;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    static final String TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
      //  Log.d(TAG, "Запустились!");

        //убрать иконку приложения
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }
}
