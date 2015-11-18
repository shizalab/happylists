package com.happy.happylists;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * Created by lukyanova on 16.11.15.
 */
public class MainSettigs extends Activity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    DB db;

    static final String TAG = "myLogs";

    RadioGroup rgColor;
    RadioButton rbColorN,rbColorD;
    Button btnSavNast, btnCancel;
    CheckBox cbEkr;
    Cursor cursor;

    protected void onCreate(Bundle savedInstanceState) {
        db = new DB(this);
        db.open();
        getTema();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainsettings);

        rgColor = (RadioGroup) findViewById(R.id.rgColor);
        rbColorN = (RadioButton) findViewById(R.id.rbColorN);
        rbColorD = (RadioButton) findViewById(R.id.rbColorD);
        rgColor.setOnCheckedChangeListener(this);
        rbColorD.setChecked(true);
        rbColorN.setChecked(false);
        cbEkr = (CheckBox) findViewById(R.id.cbEkr);
        cbEkr.setOnClickListener(this);

        btnSavNast = (Button) findViewById(R.id.btnSavNast);
        btnSavNast.setOnClickListener(this);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);

        GetNastr();

    }

    private void GetNastr() {
        int tmp_ekr = 0;
        int tmp_tema = 0;
        cursor = db.getMailSettings();
        if (cursor.getCount() > 0) {
            int sid = cursor.getColumnIndex("_id");
            cursor.moveToFirst();
            do {
                if (!cursor.isNull(sid))
                    tmp_ekr = Integer.parseInt(cursor.getString(cursor.getColumnIndex("ekr")));
                    tmp_tema = Integer.parseInt(cursor.getString(cursor.getColumnIndex("tema")));
             } while (cursor.moveToNext());
        }
        cursor.close();
        if (tmp_ekr==0)
            cbEkr.setChecked(false);
        else
            cbEkr.setChecked(true);
        if (tmp_tema==0) //0-день, 1-ночь
            rbColorD.setChecked(true);
        else
            rbColorN.setChecked(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSavNast:
                updateNastr();
                finish();
                break;
            case R.id.btnCancel:
                finish();
                break;
            case R.id.cbEkr:
                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, TAG);
                if (cbEkr.isChecked()) {
                     cbEkr.setChecked(true);
                    getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    if (wl.isHeld())
                        wl.release();
                } else {
                    cbEkr.setChecked(false);
                    getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    wl.acquire();
                }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rbColorN:
                break;
            case R.id.rbColorD:
                break;
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        // закрываем подключение при выходе
        db.close();
    }

    private void updateNastr() {
        int tm_tema = 0;
        int tm_ekr = 0;
        if (rbColorD.isChecked())
            tm_tema=0;  //светлая тема
        if (rbColorN.isChecked())
            tm_tema=1;  //темная тема
        if  (cbEkr.isChecked())
            tm_ekr = 1;
        else
            tm_ekr = 0;
        db.UpDateMainSet("mainset", tm_ekr, tm_tema);
    }

    //определение темы (светлое/темное) активити
    public void getTema() {
        int tmp_t=0;
        Cursor cursor = db.getMailSettings();
        if (cursor.getCount() > 0) {
            int sid = cursor.getColumnIndex("_id");
            cursor.moveToFirst();
            do {
                if (!cursor.isNull(sid))
                    tmp_t = Integer.parseInt(cursor.getString(cursor.getColumnIndex("tema")));
            } while (cursor.moveToNext());
        }
        cursor.close();
        if (tmp_t==0)
            setTheme(R.style.AppThemeLight);
        else
            setTheme(R.style.AppTheme);
    }

}