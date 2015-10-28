package com.happy.happylists;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * Created by lukyanova on 28.10.15.
 *  Класс создания нового списка
 */
public class NewSpisok extends Activity implements View.OnClickListener {

    DB db;
    EditText etName,etOpis;
    TextView tvType;
    RadioButton rbD,rbP,rbZ;
    CheckBox cbPrice, cbValuta, cbKolvo, cbNapom,cbSin;
    Button btnAdd;
    Cursor spisCursor;

    static final String TAG = "myLogs";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newspisok);

        db = new DB(this);
        db.open();

        etName = (EditText) findViewById(R.id.etName);
        etOpis = (EditText) findViewById(R.id.etOpis);
        tvType = (TextView) findViewById(R.id.tvType);
        rbD = (RadioButton) findViewById(R.id.rbD);
        rbP = (RadioButton) findViewById(R.id.rbP);
        rbZ = (RadioButton) findViewById(R.id.rbZ);
        cbPrice = (CheckBox) findViewById(R.id.cbPrice);
        cbValuta = (CheckBox) findViewById(R.id.cbValuta);
        cbKolvo = (CheckBox) findViewById(R.id.cbKolvo);
        cbNapom = (CheckBox) findViewById(R.id.cbNapom);
        cbSin = (CheckBox) findViewById(R.id.cbSin);
        btnAdd = (Button) findViewById(R.id.btnAdd);

        rbD.setChecked(false);
        rbP.setChecked(true);
        rbP.setChecked(false);

        cbPrice.setChecked(true);
        cbValuta.setChecked(true);
        cbKolvo.setChecked(true);
        cbSin.setChecked(true);
        cbNapom.setVisibility(View.GONE);

        btnAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAdd:

                /*Intent intent = new Intent(this, NewSpisok.class);
                startActivity(intent);*/
              //  spisCursor.close();
                break;
        }
    }

}
