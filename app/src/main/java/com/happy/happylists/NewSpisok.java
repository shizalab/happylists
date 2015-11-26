package com.happy.happylists;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug;
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
    EditText etName,etOpis,etnds;
    TextView tvType;
    RadioGroup rg;
    RadioButton rbP,rbZ;
    CheckBox cbPrice, cbValuta, cbKolvo, cbNapom,cbSin,cbStoim,cbPricends;
    Button btnAdd;
    Cursor cursor;

    int sn, snid;
    float nds;
    String btnName;

    static final String TAG = "myLogs";

    protected void onCreate(Bundle savedInstanceState) {
        db = new DB(this);
        db.open();
        getTema();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newspisok);

        btnName = getIntent().getExtras().getString("btn");
        snid = getIntent().getExtras().getInt("snid");

        etName = (EditText) findViewById(R.id.etName);
        etOpis = (EditText) findViewById(R.id.etOpis);
        tvType = (TextView) findViewById(R.id.tvType);
        rg = (RadioGroup) findViewById(R.id.rg);
        rbP = (RadioButton) findViewById(R.id.rbP);
        rbZ = (RadioButton) findViewById(R.id.rbZ);
        cbStoim = (CheckBox) findViewById(R.id.cbStoim);
        cbPrice = (CheckBox) findViewById(R.id.cbPrice);
        cbPricends = (CheckBox) findViewById(R.id.cbPricends);
        etnds = (EditText) findViewById(R.id.etnds);
        cbValuta = (CheckBox) findViewById(R.id.cbValuta);
        cbKolvo = (CheckBox) findViewById(R.id.cbKolvo);
        cbNapom = (CheckBox) findViewById(R.id.cbNapom);
        cbSin = (CheckBox) findViewById(R.id.cbSin);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        cbStoim.setOnClickListener(this);
        cbPrice.setOnClickListener(this);

        nds=0;

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
                    Toast.makeText(this, R.string.toa_Name, Toast.LENGTH_LONG).show();
                else if ((cbPricends.isChecked()) &&
                        ((etnds.length() == 0) || (Float.parseFloat(etnds.getText().toString()) <= 0)))
                    Toast.makeText(this, R.string.toa_nds, Toast.LENGTH_LONG).show();
                else {
                    if (snid == 0) {
                        addNewSpisok();
                    } else
                        updateSpisok();
                    finish();
                }
                break;
            case R.id.cbStoim:
                if (cbStoim.isChecked()) {
                    cbStoim.setChecked(true);
                    cbPrice.setVisibility(View.VISIBLE);
                    cbPrice.setChecked(false);
                    cbPricends.setChecked(false);
                    cbPricends.setVisibility(View.GONE);
                    etnds.setVisibility(View.GONE);
                } else {
                    cbStoim.setChecked(false);
                    cbPrice.setChecked(false);
                    cbPrice.setVisibility(View.GONE);
                    cbPricends.setChecked(false);
                    cbPricends.setVisibility(View.GONE);
                    etnds.setVisibility(View.GONE);
                }
                break;
            case R.id.cbPrice:
                if (cbPrice.isChecked()) {
                    cbPrice.setChecked(true);
                    cbPricends.setVisibility(View.VISIBLE);
                    cbPricends.setChecked(false);
                    etnds.setVisibility(View.VISIBLE);
                    if (nds != 0)
                        etnds.setText(Float.toString(nds));
                    else
                        etnds.setText("20");
                } else {
                    cbPrice.setChecked(false);
                    cbPricends.setChecked(false);
                    cbPricends.setVisibility(View.GONE);
                    etnds.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rbP:
                cbStoim.setVisibility(View.VISIBLE);
                cbStoim.setChecked(true);
                cbPrice.setVisibility(View.VISIBLE);
                cbPrice.setChecked(false);
                cbPricends.setVisibility(View.GONE);
                etnds.setVisibility(View.GONE);
                cbValuta.setVisibility(View.VISIBLE);
                cbValuta.setChecked(true);
                cbKolvo.setVisibility(View.VISIBLE);
                cbKolvo.setChecked(true);
                cbNapom.setVisibility(View.GONE);
                break;
            case R.id.rbZ:
                cbStoim.setVisibility(View.GONE);
                cbPrice.setVisibility(View.GONE);
                cbPricends.setVisibility(View.GONE);
                etnds.setVisibility(View.GONE);
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
        etName.setText(getResources().getString(R.string.spisok) +" "+ sn);
        etOpis.setText("");
        rg.setVisibility(View.VISIBLE);
        tvType.setVisibility(View.VISIBLE);
        rbP.setChecked(true);
        rbZ.setChecked(false);
        cbStoim.setVisibility(View.VISIBLE);
        cbStoim.setChecked(true);
        cbPrice.setVisibility(View.VISIBLE);
        cbPrice.setChecked(false);
        cbPricends.setVisibility(View.GONE);
        etnds.setVisibility(View.GONE);
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
        int prnds=0;
        int vl=0;
        int sch=0;
        int np=0;
        int st=0;
        rg.setVisibility(View.GONE);
        tvType.setVisibility(View.GONE);
        cursor = db.getNastr(sn);
        if (cursor.getCount() > 0) {
            int sid = cursor.getColumnIndex("_id");
            cursor.moveToFirst();
            do {
                if (!cursor.isNull(sid))
                    st = Integer.parseInt(cursor.getString(cursor.getColumnIndex("stoim")));
                    pr = Integer.parseInt(cursor.getString(cursor.getColumnIndex("price")));
                    prnds = Integer.parseInt(cursor.getString(cursor.getColumnIndex("pricends")));
                    nds = Float.parseFloat(cursor.getString(cursor.getColumnIndex("nds")));
                    vl = Integer.parseInt(cursor.getString(cursor.getColumnIndex("valuta")));
                    kv = Integer.parseInt(cursor.getString(cursor.getColumnIndex("kolvo")));
                    sch = Integer.parseInt(cursor.getString(cursor.getColumnIndex("sinch")));
            } while (cursor.moveToNext());
        }
        cursor.close();
        if (stp==1) {
            cbStoim.setVisibility(View.VISIBLE);
            if (st == 1) {
                cbStoim.setChecked(true);
                cbPrice.setVisibility(View.VISIBLE);
                cbPricends.setVisibility(View.GONE);
                etnds.setVisibility(View.GONE);
            } else {
                cbStoim.setChecked(false);
                cbPrice.setVisibility(View.GONE);
                cbPricends.setVisibility(View.GONE);
                etnds.setVisibility(View.GONE);
            }
            if (pr == 1) {
                cbPrice.setChecked(true);
                cbPricends.setVisibility(View.VISIBLE);
                cbPricends.setChecked(false);
                etnds.setVisibility(View.VISIBLE);
                if (nds != 0)
                    etnds.setText(Float.toString(nds));
                else
                    etnds.setText("20");
            } else {
                cbPrice.setChecked(false);
            }
            if (prnds == 1)
                cbPricends.setChecked(true);
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
            cbStoim.setVisibility(View.GONE);
            cbPrice.setVisibility(View.GONE);
            cbPricends.setVisibility(View.GONE);
            etnds.setVisibility(View.GONE);
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
            stype=1; //покупки
        if (rbZ.isChecked())
            stype=2; //задачи
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
       // Log.d(TAG, "addNewSpisok: sn=" + sn + ", name=" + etName.getText().toString() + ", opis=" + etOpis.getText().toString() + ", type=" + stype + ", hu=" + hu);
        //добавляем новый список в базу
        db.addNewSpisok("Spisok", sn, etName.getText().toString(),etOpis.getText().toString(),stype,hu);
        // в зависимости от типа списка прописываем категорию
        int st=0;
        if ((cbStoim.VISIBLE==View.VISIBLE) && (cbStoim.isChecked()))
            st = 1;
        else
            st = 0;
        int pr;
        if ((cbPrice.VISIBLE==View.VISIBLE) && (cbPrice.isChecked()))
            pr = 1;
        else
            pr = 0;
        int prnds;
        if ((cbPricends.VISIBLE==View.VISIBLE) && (cbPricends.isChecked()))
            prnds = 1;
        else
            prnds = 0;
        if ((etnds.VISIBLE==View.VISIBLE) && (etnds.getText().toString().length()!=0))
            nds = Float.parseFloat(etnds.getText().toString());
        else
            nds = 0;
        int kl;
        if ((cbKolvo.VISIBLE==View.VISIBLE) && (cbKolvo.isChecked()))
            kl = 1;
        else
            kl = 0;
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
      //  Log.d(TAG, "addNewNastr: sn="+sn+", st="+st+", pr="+pr+", prnds="+prnds+", nds="+nds+", vl="+vl+", kl="+kl+", hu="+hu+", si="+si);
        //добавляем новую настройку нового списка в базу
        db.addNewNastr("Nastr", sn, st, pr, prnds,nds, vl,  kl, hu,si);
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
    //    Log.d(TAG, "addNewSpisok: sn=" + sn + ", name=" + etName.getText().toString() + ", opis=" + etOpis.getText().toString() + ", hu=" + hu);
        //добавляем новый список в базу
        db.updateSpisok("Spisok", sn, etName.getText().toString(), etOpis.getText().toString(), hu);
        // в зависимости от типа списка прописываем категорию
        int st;
        if ((cbStoim.VISIBLE==View.VISIBLE) && (cbStoim.isChecked()))
            st = 1;
        else
            st = 0;
        int pr;
        if ((cbPrice.VISIBLE==View.VISIBLE) && (cbPrice.isChecked()))
            pr = 1;
        else
            pr = 0;
        int prnds;
        if ((cbPricends.VISIBLE==View.VISIBLE) && (cbPricends.isChecked()))
            prnds = 1;
        else
            prnds = 0;
        if ((etnds.VISIBLE==View.VISIBLE) && (etnds.getText().toString().length()!=0))
            nds = Float.parseFloat(etnds.getText().toString());
        else
            nds = 0;
        int kl;
        if ((cbKolvo.VISIBLE==View.VISIBLE) && (cbKolvo.isChecked()))
            kl = 1;
        else
            kl = 0;
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
      //   Log.d(TAG, "addNewNastr: sn="+sn+", st="+st+", pr="+pr+", prnds="+prnds+", nds="+nds+", vl="+vl+", kl="+kl+", hu="+hu+", si="+si);
        //добавляем новую настройку нового списка в базу
        db.UpDateNastr("Nastr", sn, st, pr, prnds,nds, vl,  kl, hu,si);
    }

    protected void onDestroy() {
        super.onDestroy();
        // закрываем подключение при выходе
        db.close();
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
