package com.happy.happylists;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by lukyanova on 04.11.15.
 */
public class SpisPokyp extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    static final String TAG = "myLogs";

    DB db;

    String snName;
    int sn,ik,kv,ei,pr;
    float itog;
    AutoCompleteTextView acPN,acPE;
    EditText etCount,etPrice;
    CheckBox chbvagno;
    TextView tvItog, tvItogtxt;
    ListView lvData;
    Cursor spprCursor,prodCursor,edinCursor,cursor,spisCursor2;
    SimpleCursorAdapter spAdapter,pcAdapter,ecAdapter;
    Button btnAdd,btDel,btSave;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spispokyp);

        db = new DB(this);
        db.open();

        snName = getIntent().getExtras().getString("sname");
        int snid = getIntent().getExtras().getInt("snid");
        GetSN(snid);

        Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        mActionBarToolbar.setTitle(snName);
        mActionBarToolbar.setLogo(R.drawable.ic_action_menu);
        setSupportActionBar(mActionBarToolbar);

        itog=(float) 0;
        etCount = (EditText) findViewById(R.id.etCount);
        etPrice = (EditText) findViewById(R.id.etPrice);
        chbvagno = (CheckBox) findViewById(R.id.chbvagno);
        tvItog = (TextView) findViewById(R.id.tvItog);
        tvItogtxt = (TextView) findViewById(R.id.tvItogtxt);
        etCount.setVisibility(View.GONE);
        etPrice.setVisibility(View.GONE);
        chbvagno.setVisibility(View.GONE);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btDel = (Button) findViewById(R.id.btDel);
        btSave = (Button) findViewById(R.id.btSave);
        lvData = (ListView) findViewById(R.id.lvData);
        CreateSPList(); // постройка ListView
        acPN = (AutoCompleteTextView) findViewById(R.id.acPN);
        acPE = (AutoCompleteTextView ) findViewById(R.id.acPE);
        acPE.setVisibility(View.GONE);
        ClView();
        CreateListProducts(); //список всех продуктов со справочника
        tvItog.setText(String.format("%.2f",itog));

    }

    //Чистка
    private void ClView() {
        if (acPN != null && !acPN.isPopupShowing())
            this.acPN.setText("");
        if (etCount != null ) {
            etCount.setText("");
            etCount.setVisibility(View.GONE);
        }
        if (acPE != null && !acPE.isPopupShowing()) {
            acPE.setVisibility(View.GONE);
            acPE.setText("");
        }
        if (etPrice != null ) {
            etPrice.setText("");
            etPrice.setVisibility(View.GONE);
        }
        if (chbvagno != null ) {
            if (chbvagno.isChecked() == true)
                chbvagno.setChecked(false);
            chbvagno.setVisibility(View.GONE);
        }
        if (btDel != null ) {
            btDel.setVisibility(View.GONE);
        }
        if (btSave != null ) {
            btSave.setVisibility(View.GONE);
        }
        if (tvItog != null)
            tvItog.setText(String.format("%.2f",itog));
    }
    //определение sn
    private void GetSN(int snid) {
        cursor = db.getSpisokId(snid);
            if (cursor.getCount() > 0) {
                int ssn = cursor.getColumnIndex("sn");
                cursor.moveToFirst();
                do {
                    if (!cursor.isNull(ssn))
                        sn = Integer.parseInt(cursor.getString(ssn));
                } while (cursor.moveToNext());
            }
            cursor.close();
    }

    // создание меню в toolBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
       /*
        // Проверим доступность сети
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isNetworkAvailable = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
        // Установим видимость кнопке Поделиться
        MenuItem shareMenuItem = menu.findItem(R.id.it02);
        shareMenuItem.setVisible(isNetworkAvailable);*/
        return true;
    }

    // выбор любого пункта с меню actionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Обработка выбранного элемента меню.
        switch (item.getItemId())
        {
            case R.id.it11:

                return true;
            case R.id.it12:

                return true;
            case R.id.it13:

                return true;
            case R.id.it14:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // закрытие окна
    protected void onDestroy() {
        super.onDestroy();
        spprCursor.close();
        if (prodCursor != null )
            prodCursor.close();
        if (edinCursor != null )
            edinCursor.close();
        // закрываем подключение при выходе
        db.close();
    }

    // возрващение к окну
    @Override
    public void onRestart() {
        super.onRestart();
        getSupportLoaderManager().getLoader(0).forceLoad();
    }

    //список всех продуктов со справочника, работа с выборкой
    private void CreateListProducts() {
        prodCursor = db.getProdName("");
        String[] from = new String[] { "pname" };
        int[] to = new int[] {R.id.text1};

        pcAdapter = new SimpleCursorAdapter(this,R.layout.item, prodCursor, from, to,0);
        pcAdapter.setStringConversionColumn(prodCursor.getColumnIndexOrThrow("pname"));
        pcAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence constraint) {
                String partialValue = null;
                if (constraint != null) {
                    partialValue = constraint.toString();
                }
                return db.getProdName(partialValue);
            }
        });
        acPN.setAdapter(pcAdapter);
        pcAdapter.notifyDataSetChanged();
        // acPN.showDropDown();
        acPN.addTextChangedListener(new TextWatcher() {
            //при изменении текста
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etCount.setVisibility(View.VISIBLE);
                etPrice.setVisibility(View.VISIBLE);
                chbvagno.setVisibility(View.VISIBLE);
                acPE.setVisibility(View.VISIBLE);
                etPrice.setText("1");
                acPE.setText("шт.");
            }

            //перед изменением текста
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                if (spAdapter.getCount() == 0)
                    itog = (float) 0;
            }

            //после изменения текста
            public void afterTextChanged(Editable s) {
                pcAdapter.getFilter().filter(asUpperCaseFirstChar(s.toString()));
            }
        });
        acPN.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if (hasFocus)
                    acPN.showDropDown();
                cursor = db.getNastr(sn);
                int prtxt = 0;
                int id_CC = cursor.getCount();
                if (id_CC > 0) {
                    cursor.moveToFirst();
                    do {
                        prtxt = Integer.parseInt(cursor.getString(cursor.getColumnIndex("price")));
                    } while (cursor.moveToNext());
                }
                cursor.close();
                if (prtxt == 0)
                    etPrice.setEnabled(false);
                else
                    etPrice.setEnabled(true);
            }
        });
        acPN.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter, View view, int index, long id) {
                CreateListEdin();
                cursor = db.getProdED(id);
                int id_Col = cursor.getCount();
                String txt = "";
                if (id_Col > 0) {
                    int eid = cursor.getColumnIndex("ename");
                    cursor.moveToFirst();
                    do {
                        txt = cursor.getString(eid);
                    } while (cursor.moveToNext());
                }
                cursor.close();
                acPE.setText(txt);
                cursor = db.getProdPrice(id);
                int id_C = cursor.getCount();
                float pri = (float) 0;
                if (id_C > 0) {
                    cursor.moveToFirst();
                    do {
                        pri = Float.parseFloat(cursor.getString(cursor.getColumnIndex("prices")).replace(',', '.'));
                    } while (cursor.moveToNext());
                }
                cursor.close();
                etPrice.setText(Float.toString(pri));
                etCount.requestFocus();

            }
        });

    }

    //процедура смены заглавной буквы
    public final static String asUpperCaseFirstChar(final String target) {
        if ((target == null) || (target.length() == 0)) {
            return target; // You could omit this check and simply live with an
            // exception if you like
        }
        return Character.toUpperCase(target.charAt(0))
                + (target.length() > 1 ? target.substring(1) : "");
    }

    //список всех единиц со справочника, работа с выборкой
    private void CreateListEdin() {
        edinCursor = db.getEdinName("");
        String[] from = new String[] {"ename"};
        int[] to = new int[] {R.id.text2};
        ecAdapter = new SimpleCursorAdapter(this,R.layout.eitem, edinCursor, from, to);
        ecAdapter.setStringConversionColumn(edinCursor.getColumnIndexOrThrow("ename"));
        ecAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence constraint) {
                String partialValue = null;
                if (constraint != null) {
                    partialValue = constraint.toString();
                }
                return db.getEdinName(partialValue);
            }
        });
        acPE.setAdapter(ecAdapter);
        ecAdapter.notifyDataSetChanged();
        acPE.showDropDown();
        acPE.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }
            public void afterTextChanged(Editable s) {
                ecAdapter.getFilter().filter(s.toString());
            }
        });
        // РЎРѕР±С‹С‚РёРµ РїСЂРё РІС‹Р±РѕСЂРµ СЌР»РµРјРµРЅС‚Р°
        acPE.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> adapter, View view, int index, long id) {
                //  Object itemPostion = (Object) adapter.getItemAtPosition(index);
             }
        });
    }

    //процедура содания Списка покупков (Listview)
    public void CreateSPList () {
        String[] from = new String[] {"kc", "skorz", "pname","ename", "skol", "abv", "sprice" };
        int[] to = new int[] {R.id.ivP,R.id.ivKorz,R.id.tvPN,R.id.tvPE,R.id.tvPK,R.id.tvPV,R.id.tvPP};
        spAdapter = new SimpleCursorAdapter(this, R.layout.spispokyplist, spprCursor, from, to,0);
        spAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int column) {
                switch (view.getId()) {
                    case R.id.ivP:
                        ImageView iv = (ImageView) view.findViewById(R.id.ivP);
                        if (ik == 0) {
                            iv.setVisibility(View.GONE);
                        } else {
                            String kcol = cursor.getString(cursor.getColumnIndex("kc"));
                            iv.setVisibility(View.VISIBLE);
                            iv.setBackgroundColor(Color.parseColor(kcol));
                        }
                        return true;
                    case R.id.ivKorz:
                        int kzz = Integer.parseInt(cursor.getString(cursor.getColumnIndex("skorz")));
                        ImageView ivk = (ImageView) view.findViewById(R.id.ivKorz);
                        if (kzz == 1)
                            ivk.setBackgroundResource(R.drawable.bullchecked);
                        else {
                            int vagn = Integer.parseInt(cursor.getString(cursor.getColumnIndex("svagno")));
                            if (vagn == 1)
                                ivk.setBackgroundResource(R.drawable.bullred);
                            else
                                ivk.setBackgroundResource(R.drawable.bull);
                        }
                        final int idi = Integer.parseInt(cursor.getString(cursor.getColumnIndex("_id")));
                        ivk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                int sk = 0;
                                spisCursor2 = db.getSkorSpisok(idi);
                                int pid = spisCursor2.getColumnIndex("_id");
                                spisCursor2.moveToFirst();
                                do {
                                    if (!spisCursor2.isNull(pid)) {
                                        sk = Integer.parseInt(spisCursor2.getString(spisCursor2.getColumnIndex("skorz")));
                                    }
                                } while (spisCursor2.moveToNext());
                                spisCursor2.close();
                                if (sk == 0)
                                    db.UpDateKSp("Pokypka", 1, idi);
                                else
                                    db.UpDateKSp("Pokypka", 0, idi);
                                SumInKorz(idi);
                            }
                        });
                        return true;
                    case R.id.tvPN:
                        int kz = Integer.parseInt(cursor.getString(cursor.getColumnIndex("skorz")));
                        TextView tPN = (TextView) view.findViewById(R.id.tvPN);
                        if (kz == 1) {
                            tPN.setPaintFlags(tPN.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            tPN.setTextColor(Color.parseColor("#BEBEBE"));
                        } else {
                            tPN.setPaintFlags(tPN.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                            tPN.setTextColor(Color.parseColor("#000000"));
                        }
                        tPN.setText(cursor.getString(cursor.getColumnIndex("pname")));
                        return true;
                    case R.id.tvPK:
                        int kz1 = Integer.parseInt(cursor.getString(cursor.getColumnIndex("skorz")));
                        TextView tvPK = (TextView) view.findViewById(R.id.tvPK);
                        if (kv == 0)
                            tvPK.setVisibility(View.GONE);
                        else {
                            tvPK.setVisibility(View.VISIBLE);
                            tvPK.setText(cursor.getString(cursor.getColumnIndex("skol")));
                            if (kz1 == 1)
                                tvPK.setTextColor(Color.parseColor("#BEBEBE"));
                            else
                                tvPK.setTextColor(Color.parseColor("#000000"));
                            if (pr == 0)
                                tvPK.setTextSize(20);
                            else
                                tvPK.setTextSize(10);

                        }
                        return true;
                    case R.id.tvPE:
                        int kz2 = Integer.parseInt(cursor.getString(cursor.getColumnIndex("skorz")));
                        TextView tvPE = (TextView) view.findViewById(R.id.tvPE);
                        if (kv == 0)
                            tvPE.setVisibility(View.GONE);
                        else {
                            tvPE.setVisibility(View.VISIBLE);
                            tvPE.setText(cursor.getString(cursor.getColumnIndex("ename")));
                            if (kz2 == 1)
                                tvPE.setTextColor(Color.parseColor("#BEBEBE"));
                            else
                                tvPE.setTextColor(Color.parseColor("#000000"));
                            if (pr == 0)
                                tvPE.setTextSize(20);
                            else
                                tvPE.setTextSize(10);
                        }
                        return true;
                    case R.id.tvPV:
                        int kz3 = Integer.parseInt(cursor.getString(cursor.getColumnIndex("skorz")));
                        TextView tvPV = (TextView) view.findViewById(R.id.tvPV);
                        if (ei == 0)
                            tvPV.setVisibility(View.GONE);
                        else {
                            if (pr == 0)
                                tvPV.setVisibility(View.GONE);
                            else {
                                tvPV.setVisibility(View.VISIBLE);
                                tvPV.setText(cursor.getString(cursor.getColumnIndex("abv")));
                                if (kz3 == 1)
                                    tvPV.setTextColor(Color.parseColor("#BEBEBE"));
                                else
                                    tvPV.setTextColor(Color.parseColor("#000000"));
                            }
                        }
                        return true;

                    case R.id.tvPP:
                        int kz4 = Integer.parseInt(cursor.getString(cursor.getColumnIndex("skorz")));
                        TextView tv = (TextView) view.findViewById(R.id.tvPP);
                        if (pr == 0)
                            tv.setVisibility(View.GONE);
                        else {
                            Float sk = Float.parseFloat(cursor.getString(cursor.getColumnIndex("skol")).replace(',', '.'));
                            Float sp = Float.parseFloat(cursor.getString(cursor.getColumnIndex("sprice")).replace(',', '.'));
                            Float it = sp * sk;
                            tv.setVisibility(View.VISIBLE);
                            tv.setText(String.format("%.2f", it));
                            if (kz4 == 1)
                                tv.setTextColor(Color.parseColor("#BEBEBE"));
                            else
                                tv.setTextColor(Color.parseColor("#000000"));
                        }
                        return true;
                }
                return false;
            }
        });
        lvData.setAdapter(spAdapter);
        getSupportLoaderManager().initLoader(1, null, this);
    }

    //Расчет итоговой суммы покупки
    private void SumInKorz(long idsm) {
        float sm=0;
        itog = (float) 0;
        cursor = db.getSumInKor(sn);
        if (cursor.getCount()>0) {
            cursor.moveToFirst();
            do {
                sm= sm + Float.parseFloat(cursor.getString(cursor.getColumnIndex("sm")));
            } while (cursor.moveToNext());
            cursor.close();
        } else
            sm = 0;
        itog = itog +sm;
        tvItog= (TextView) findViewById(R.id.tvItog);
        if (tvItog != null  || itog != 0)
            tvItog.setText(String.format("%.2f",itog));
        getSupportLoaderManager().getLoader(1).forceLoad();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // TODO Auto-generated method stub
        return new CursorLoader(this) {
            @Override
            public Cursor loadInBackground() {
                spprCursor = db.getSpisok(sn);
                return spprCursor;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
        if(spAdapter!=null && arg1!=null)
            spAdapter.swapCursor((android.database.Cursor) arg1);
        else
            Log.v(TAG, "OnLoadFinished: spAdapter is null");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(spAdapter!=null)
            spAdapter.swapCursor(null);
        else
            Log.v(TAG,"OnLoadFinished: spAdapter is null");
    }
}
