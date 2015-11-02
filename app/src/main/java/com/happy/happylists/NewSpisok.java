package com.happy.happylists;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by lukyanova on 28.10.15.
 *  Класс создания нового списка
 */
public class NewSpisok extends Activity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    DB db;
    MainActivity ma;
    EditText etName,etOpis;
    TextView tvType;
    RadioGroup rg;
    RadioButton rbP,rbZ;
    CheckBox cbPrice, cbValuta, cbKolvo, cbNapom,cbSin;
    Button btnAdd;
    Cursor cursor;

    int sn, snid;
    String btnName;

    static final String TAG = "myLogs";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newspisok);

        btnName = getIntent().getExtras().getString("btn");
        //snid = Integer.parseInt(getIntent().getStringExtra("snid"));
        snid = getIntent().getExtras().getInt("snid");

        Log.d(TAG, "snid="+snid);

        db = new DB(this);
        db.open();

        etName = (EditText) findViewById(R.id.etName);
        etOpis = (EditText) findViewById(R.id.etOpis);
        tvType = (TextView) findViewById(R.id.tvType);
        rg = (RadioGroup) findViewById(R.id.rg);
        rbP = (RadioButton) findViewById(R.id.rbP);
        rbZ = (RadioButton) findViewById(R.id.rbZ);
        cbPrice = (CheckBox) findViewById(R.id.cbPrice);
        cbValuta = (CheckBox) findViewById(R.id.cbValuta);
        cbKolvo = (CheckBox) findViewById(R.id.cbKolvo);
        cbNapom = (CheckBox) findViewById(R.id.cbNapom);
        cbSin = (CheckBox) findViewById(R.id.cbSin);
        btnAdd = (Button) findViewById(R.id.btnAdd);

        GetSN();

        rg.setOnCheckedChangeListener(this);

        btnAdd.setText(btnName);

        btnAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAdd:
                if (etName.length() == 0)
                    Toast.makeText(this, "Введите, пожалуйста, название списка!", Toast.LENGTH_LONG).show();
                else {
                    if (snid == 0) {
                         addNewSpisok();
                        /*Intent intent = new Intent(this, NewSpisok.class);
                        startActivity(intent);*/
                    } else
                        updateSpisok();
                    finish();
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rbP:
                cbPrice.setVisibility(View.VISIBLE);
                cbPrice.setChecked(true);
                cbValuta.setVisibility(View.VISIBLE);
                cbValuta.setChecked(true);
                cbKolvo.setVisibility(View.VISIBLE);
                cbKolvo.setChecked(true);
                cbNapom.setVisibility(View.GONE);
                break;
            case R.id.rbZ:
                cbPrice.setVisibility(View.GONE);
                cbValuta.setVisibility(View.GONE);
                cbKolvo.setVisibility(View.GONE);
                cbNapom.setVisibility(View.VISIBLE);
                cbNapom.setChecked(true);
                break;
        }
    }

    private void GetSN() {
         if (snid==0) {
             //поиск максимального номера
            int max_nom = 0;
            cursor = db.getMaxSpisok("Spisok");
            if (cursor.getCount() > 0) {
                int ssn = cursor.getColumnIndex("sn");
                cursor.moveToFirst();
                do {
                    if (!cursor.isNull(ssn))
                        max_nom = Integer.parseInt(cursor.getString(ssn));
                } while (cursor.moveToNext());
            }
            cursor.close();
            max_nom = max_nom + 1;
            sn = max_nom;
            OpenNew();
        } else
        {
            String sname= "";
            String sopis= "";
            int stype=0;
            cursor = db.getSpisokId(snid);
            if (cursor.getCount() > 0) {
                int ssn = cursor.getColumnIndex("sn");
                cursor.moveToFirst();
                do {
                    if (!cursor.isNull(ssn))
                        sn = Integer.parseInt(cursor.getString(ssn));
                        sname = cursor.getString(cursor.getColumnIndex("sname"));
                        sopis = cursor.getString(cursor.getColumnIndex("sopis"));
                        stype = Integer.parseInt(cursor.getString(cursor.getColumnIndex("stype")));
                } while (cursor.moveToNext());
            }
            cursor.close();
            etName.setText(sname);
            etOpis.setText(sopis);
            OpenSN(stype);
        }
    }

    private void OpenNew() {
        etName.setText("Список " + sn);
        etOpis.setText("");
        rg.setVisibility(View.VISIBLE);
        tvType.setVisibility(View.VISIBLE);
        rbP.setChecked(true);
        rbZ.setChecked(false);
        cbPrice.setVisibility(View.VISIBLE);
        cbPrice.setChecked(true);
        cbValuta.setVisibility(View.VISIBLE);
        cbValuta.setChecked(true);
        cbKolvo.setVisibility(View.VISIBLE);
        cbKolvo.setChecked(true);
        cbSin.setVisibility(View.VISIBLE);
        cbSin.setChecked(true);
        cbNapom.setVisibility(View.GONE);
    }

    private void OpenSN(int stp) {
        int kv=0;
        int pr=0;
        int vl=0;
        int sch=0;
        int np=0;
        rg.setVisibility(View.GONE);
        tvType.setVisibility(View.GONE);
        cursor = db.getNastr(sn);
        if (cursor.getCount() > 0) {
            int sid = cursor.getColumnIndex("_id");
            cursor.moveToFirst();
            do {
                if (!cursor.isNull(sid))
                    kv = Integer.parseInt(cursor.getString(cursor.getColumnIndex("kolvo")));
                    pr = Integer.parseInt(cursor.getString(cursor.getColumnIndex("price")));
                    vl = Integer.parseInt(cursor.getString(cursor.getColumnIndex("valuta")));
                    sch = Integer.parseInt(cursor.getString(cursor.getColumnIndex("sinch")));
                    np = Integer.parseInt(cursor.getString(cursor.getColumnIndex("napom")));
            } while (cursor.moveToNext());
        }
        cursor.close();
        if (stp==1) {
            cbPrice.setVisibility(View.VISIBLE);
            if (pr == 1)
                cbPrice.setChecked(true);
            else
                cbPrice.setChecked(false);
            cbValuta.setVisibility(View.VISIBLE);
            if (vl == 1)
                cbValuta.setChecked(true);
            else
                cbValuta.setChecked(false);
            cbKolvo.setVisibility(View.VISIBLE);
            if (kv == 1)
                cbKolvo.setChecked(true);
            else
                cbKolvo.setChecked(false);
            cbNapom.setVisibility(View.GONE);
        } else {
            cbPrice.setVisibility(View.GONE);
            cbValuta.setVisibility(View.GONE);
            cbKolvo.setVisibility(View.GONE);
            cbNapom.setVisibility(View.VISIBLE);
            if (np == 1)
                cbNapom.setChecked(true);
            else
                cbNapom.setChecked(false);
        }
        cbSin.setVisibility(View.VISIBLE);
        if (sch == 1)
            cbSin.setChecked(true);
        else
            cbSin.setChecked(false);

    }

    private void addNewSpisok() {
        //определяем тип списка
        int stype = 0;
        if (rbP.isChecked())
            stype=1;
        if (rbZ.isChecked())
            stype=2;
        //определяем id пользователя
        int hu = 0;
        cursor = db.getUserID();
        if (cursor.getCount()>0) {
            int hid = cursor.getColumnIndex("_id");
            cursor.moveToFirst();
            do {
                if (!cursor.isNull(hid))
                    hu = Integer.parseInt(cursor.getString(cursor.getColumnIndex("_id")));
            } while (cursor.moveToNext());
        }
        cursor.close();
        //Log.d(TAG, "addNewSpisok: sn="+sn+", name="+etName.getText().toString()+", opis="+etOpis.getText().toString()+", type="+stype+", hu="+hu);
        //добавляем новый список в базу
        db.addNewSpisok("Spisok", sn, etName.getText().toString(),etOpis.getText().toString(),stype,hu);
        // в зависимости от типа списка прописываем категорию
        int kg = 0;
        if (rbP.isChecked())
            kg = 1;
        if (rbZ.isChecked())
            kg = 0;
        int kl;
        if ((cbKolvo.VISIBLE==View.VISIBLE) && (cbKolvo.isChecked()))
            kl = 1;
        else
            kl = 0;
        int pr;
        if ((cbPrice.VISIBLE==View.VISIBLE) && (cbPrice.isChecked()))
            pr = 1;
        else
            pr = 0;
        int vl;
        if ((cbValuta.VISIBLE==View.VISIBLE) && (cbValuta.isChecked()))
            vl = 1;
        else
            vl = 0;
        int si;
        if (cbSin.isChecked())
            si = 1;
        else
            si = 0;
        int np;
        if ((cbNapom.VISIBLE==View.VISIBLE) && (cbNapom.isChecked()))
            np = 1;
        else
            np = 0;
       // Log.d(TAG, "addNewNastr: sn="+sn+", kg="+kg+", kl="+kl+", pr="+pr+", vl="+vl+", hu="+hu+", si="+si+", np="+np);
        //добавляем новую настройку нового списка в базу
        db.addNewNastr("Nastr", sn, kg, kl,pr,vl,hu,si,np);
    }

    private void updateSpisok() {
        //определяем id пользователя
        int hu = 0;
        cursor = db.getUserID();
        if (cursor.getCount()>0) {
            int hid = cursor.getColumnIndex("_id");
            cursor.moveToFirst();
            do {
                if (!cursor.isNull(hid))
                    hu = Integer.parseInt(cursor.getString(cursor.getColumnIndex("_id")));
            } while (cursor.moveToNext());
        }
        cursor.close();
        //Log.d(TAG, "addNewSpisok: sn="+sn+", name="+etName.getText().toString()+", opis="+etOpis.getText().toString()+", type="+stype+", hu="+hu);
        //добавляем новый список в базу
        db.updateSpisok("Spisok", sn, etName.getText().toString(), etOpis.getText().toString(), hu);
        // в зависимости от типа списка прописываем категорию
        int kl;
        if ((cbKolvo.VISIBLE==View.VISIBLE) && (cbKolvo.isChecked()))
            kl = 1;
        else
            kl = 0;
        int pr;
        if ((cbPrice.VISIBLE==View.VISIBLE) && (cbPrice.isChecked()))
            pr = 1;
        else
            pr = 0;
        int vl;
        if ((cbValuta.VISIBLE==View.VISIBLE) && (cbValuta.isChecked()))
            vl = 1;
        else
            vl = 0;
        int si;
        if (cbSin.isChecked())
            si = 1;
        else
            si = 0;
        int np;
        if ((cbNapom.VISIBLE==View.VISIBLE) && (cbNapom.isChecked()))
            np = 1;
        else
            np = 0;
        // Log.d(TAG, "addNewNastr: sn="+sn+", kg="+kg+", kl="+kl+", pr="+pr+", vl="+vl+", hu="+hu+", si="+si+", np="+np);
        //добавляем новую настройку нового списка в базу
        db.UpDateNastr("Nastr", sn, kl, pr, vl, hu, si,np);
    }

    protected void onDestroy() {
        super.onDestroy();
        // закрываем подключение при выходе
        db.close();
    }
}
