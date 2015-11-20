package com.happy.happylists;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import android.widget.Toast;

/**
 * Created by lukyanova on 04.11.15.
 */
public class SpisPokyp extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, AdapterView.OnItemLongClickListener {

    static final String TAG = "myLogs";
    final int DIALOG_EXIT = 1;

    DB db;
    SwipeForSpisPokyp ssp;

    String snName;
    int sn,kv,pr,tmp,vl, prodid;
    float itog;
    AutoCompleteTextView acPN,acPE,acPOpis;
    EditText etCount,etPrice;
    CheckBox chbvagno;
    TextView tvItog, tvItogtxt;
    ListView lvData;
    Cursor spprCursor,prodCursor,edinCursor,cursor,opCursor;
    SimpleCursorAdapter spAdapter,pcAdapter,ecAdapter,opAdapter;
    Button btnAdd,btSave;
    static int ysl;

    private android.support.v7.app.ActionBar actionBar;
    private DrawerLayout myDrawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView nv;

    SwipeForSpisPokyp touchListener;

    protected void onCreate(Bundle savedInstanceState) {
        db = new DB(this);
        db.open();
        getTema();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spispokyp);

        snName = getIntent().getExtras().getString("sname");
        int snid = getIntent().getExtras().getInt("snid");

        //начало процедуры отрисовки левого меню
        actionBar = getSupportActionBar();
        actionBar.setTitle(snName);
        actionBar.setDisplayHomeAsUpEnabled(true);
        myDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        myDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, myDrawerLayout, R.string.open_menu, R.string.close_menu){
            public void onDrawerClosed(View view) {
                actionBar.setTitle(snName);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }
            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle("");
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        myDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        nv = (NavigationView) findViewById(R.id.navigation);
        nv.setNavigationItemSelectedListener(this);
        //конец процедуры отрисовки левого меню

        GetSN(snid);

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
        btSave = (Button) findViewById(R.id.btSave);
        btnAdd.setOnClickListener(this);
        btSave.setOnClickListener(this);
        lvData = (ListView) findViewById(R.id.lvData);
        lvData.setOnItemLongClickListener(this);
        GetNastr();
        CreateSPList(); // постройка ListView
        acPN = (AutoCompleteTextView) findViewById(R.id.acPN);
        acPOpis = (AutoCompleteTextView) findViewById(R.id.acPOpis);
        acPE = (AutoCompleteTextView ) findViewById(R.id.acPE);
        acPOpis.setVisibility(View.GONE);
        acPE.setVisibility(View.GONE);
        ClView();
        CreateListProducts(); //список всех продуктов со справочника
        tvItog.setText(String.format("%.2f", itog));
        SumInKorz();

    }

    //начало процедуры отрисовки левого меню
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        menuItem.setChecked(true);
        myDrawerLayout.closeDrawers();
        switch (menuItem.getItemId())
        {
             case R.id.it01:
                Log.d(TAG, "it01");
                return true;
            case R.id.it02:
                Log.d(TAG, "it02");
                return true;
            case R.id.it03:
                Log.d(TAG, "it03");
                return true;
            case R.id.it04:
                Log.d(TAG, "it04");
                return true;
            case R.id.it05:
                Log.d(TAG, "it05");
                return true;
            case R.id.it06:
                Intent intenms = new Intent(this, MainSettigs.class);
                startActivity(intenms);
                return true;
            case R.id.it07:
                Log.d(TAG, "it07");
                return true;
        }
        return false;
    }
    // создание меню в actionBar
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
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Обработка выбранного элемента меню.
        switch (item.getItemId())
        {
            case R.id.it11:
                Log.d(TAG, "Синхронизировать список");
                return true;
            case R.id.it12:
                Log.d(TAG, "Отправить как СМС");
                return true;
            case R.id.it13:
                Log.d(TAG, "Отправить по E-mail");
                return true;
            case R.id.it14:
                Log.d(TAG, "Настройки списка");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //конец процедуры отрисовки левого меню

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Создание диалога с уточнением о удалении списка
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_EXIT) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            // заголовок
            adb.setTitle(R.string.Titl);
            // сообщение
            adb.setMessage(R.string.delete_prod);
            // иконка
            adb.setIcon(android.R.drawable.ic_dialog_info);
            // кнопка положительного ответа
            adb.setPositiveButton(R.string.yes, myClickListener);
            // кнопка нейтрального ответа
            adb.setNeutralButton(R.string.cancel, myClickListener);
            // создаем диалог
            return adb.create();
        }
        return super.onCreateDialog(id);
    }

    // диалог и его результат
    DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                // положительная кнопка
                case Dialog.BUTTON_POSITIVE:
                    db.delRecSP("Pokypka", prodid);
                    getSupportLoaderManager().getLoader(0).forceLoad();
                    // нейтральная кнопка
                case Dialog.BUTTON_NEUTRAL:
                    break;
            }
            CreateSPList();
        }
    };

    //Чистка
    private void ClView() {
        if (acPN != null && !acPN.isPopupShowing())
            this.acPN.setText("");
        if (etCount != null ) {
            etCount.setText("");
            etCount.setVisibility(View.GONE);
        }
        if (acPOpis != null && !acPOpis.isPopupShowing()) {
            acPOpis.setVisibility(View.GONE);
            acPOpis.setText("");
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
                    if (!cursor.isNull(ssn)) {
                        sn = Integer.parseInt(cursor.getString(ssn));
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        //Log.d(TAG, "GetSN: sn=" + sn);

    }

    // закрытие окна
    protected void onDestroy() {
        super.onDestroy();
        spprCursor.close();
        if (prodCursor != null )
            prodCursor.close();
        if (edinCursor != null )
            edinCursor.close();
        if (opCursor != null )
            opCursor.close();
        // закрываем подключение при выходе
        db.close();
    }

    // возрващение к окну
    @Override
    public void onRestart() {
        super.onRestart();
        CreateSPList();
        Intent intent2 = getIntent();
        finish();
        startActivity(intent2);
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
                acPOpis.setVisibility(View.VISIBLE);
                acPE.setVisibility(View.VISIBLE);
                etPrice.setText("1");
                acPOpis.setText("");
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
                CreateListOpis((int) id);
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

    //список всех описаний для конкретного продукта со справочника, работа с выборкой
    private void CreateListOpis(int ppid) {
        tmp=ppid;
        opCursor = db.getOpisProd("", tmp);
        String[] from = new String[] {"popis"};
        int[] to = new int[] {R.id.text};
        opAdapter = new SimpleCursorAdapter(this,R.layout.opitem, opCursor, from, to);
        opAdapter.setStringConversionColumn(opCursor.getColumnIndexOrThrow("popis"));
        opAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence constraint) {
                String partialValue = null;
                if (constraint != null) {
                    partialValue = constraint.toString();
                }
                return db.getOpisProd(partialValue, tmp);
            }
        });
        acPOpis.setAdapter(opAdapter);
        opAdapter.notifyDataSetChanged();
        acPOpis.showDropDown();
        acPOpis.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
                opAdapter.getFilter().filter(s.toString());
            }
        });
        acPOpis.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if (hasFocus)
                    acPOpis.showDropDown();
            }
        });
        acPOpis.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter, View view, int index, long id) {
                //  Object itemPostion = (Object) adapter.getItemAtPosition(index);
            }
        });
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
        acPE.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter, View view, int index, long id) {
                //  Object itemPostion = (Object) adapter.getItemAtPosition(index);
            }
        });
    }

    //Процедура получения настроек для списка
    private void GetNastr() {
        kv=0;	pr=0;  vl=0;
        cursor = db.getNastr(sn);
        if (cursor.getCount()>0) {
            int nid = cursor.getColumnIndex("_id");
            cursor.moveToFirst();
            do {
                if (!cursor.isNull(nid)) {
                    kv=Integer.parseInt(cursor.getString(cursor.getColumnIndex("kolvo")));
                    pr=Integer.parseInt(cursor.getString(cursor.getColumnIndex("price")));
                    vl=Integer.parseInt(cursor.getString(cursor.getColumnIndex("valuta")));
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    //процедура содания Списка покупков (Listview)
    public void CreateSPList () {
        String[] from = new String[] {"skorz", "pname", "popis","ename", "skol", "abv", "sprice" };
        int[] to = new int[] {R.id.ivKorz,R.id.tvPN,R.id.tvPO,R.id.tvPE,R.id.tvPK,R.id.tvPV,R.id.tvPP};
        spAdapter = new SimpleCursorAdapter(this, R.layout.spispokyplist, spprCursor, from, to,0);
        spAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int column) {
                switch (view.getId()) {
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
                    case R.id.tvPO:
                        int kzo = Integer.parseInt(cursor.getString(cursor.getColumnIndex("skorz")));
                        TextView tPO = (TextView) view.findViewById(R.id.tvPO);
                        if (kzo == 1) {
                            tPO.setPaintFlags(tPO.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            tPO.setTextColor(Color.parseColor("#BEBEBE"));
                        } else {
                            tPO.setPaintFlags(tPO.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                            tPO.setTextColor(Color.parseColor("#000000"));
                        }
                        tPO.setText(cursor.getString(cursor.getColumnIndex("popis")));
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
                        if (vl == 0)
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
        getSupportLoaderManager().initLoader(0, null, this);
        touchListener =
                new SwipeForSpisPokyp(
                        lvData,
                        new SwipeForSpisPokyp.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }
                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                        prodid = (int) spAdapter.getItemId(position);
                                    if (ssp.ysl==0) {
                                        showDialog(DIALOG_EXIT);
                                    } else {
                                        int sk=0;
                                        cursor = db.getSkorSpisok(prodid);
                                        int korzid = cursor.getColumnIndex("_id");
                                        cursor.moveToFirst();
                                        do {
                                            if (!cursor.isNull(korzid)) {
                                                sk = Integer.parseInt(cursor.getString(cursor.getColumnIndex("skorz")));
                                            }
                                        } while (cursor.moveToNext());
                                        cursor.close();
                                        if (sk==0)
                                            db.UpDateKSp("Pokypka",1, prodid);
                                        else
                                            db.UpDateKSp("Pokypka",0, prodid);
                                        CreateSPList();
                                        SumInKorz();
                                    }
                                }
                            }
                        });
        lvData.setOnTouchListener(touchListener);
        lvData.setOnScrollListener(touchListener.makeScrollListener());
    }

    //Расчет итоговой суммы покупки
    private void SumInKorz() {
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
        getSupportLoaderManager().getLoader(0).forceLoad();
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
            Log.v(TAG, "OnLoadFinished: spAdapter is null");
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAdd:
                if (acPN.length() == 0)
                    Toast.makeText(this, R.string.toa_PN, Toast.LENGTH_LONG).show();
                else if (etCount.length() == 0)
                    Toast.makeText(this, R.string.toa_Count, Toast.LENGTH_LONG).show();
                else if (acPE.length() == 0)
                    Toast.makeText(this, R.string.toa_PE, Toast.LENGTH_LONG).show();
                else if (etPrice.length() == 0)
                    Toast.makeText(this, R.string.toa_Price, Toast.LENGTH_LONG).show();
                else {
                    if ((Float.parseFloat(etCount.getText().toString()) <= 0) ||
                            (Float.parseFloat(etPrice.getText().toString()) <= 0) )
                        Toast.makeText(this, R.string.toa_Zero, Toast.LENGTH_LONG).show();
                    else
                    {
                        addProdInSpisok();
                      //  acPN.showDropDown();
                      //  GetNastr(sn);
                     //   CreateSPList();
                       /* etSName.setFocusable(false);
                        etSName.setFocusableInTouchMode(false);*/
                      //  SumInKorz(sn);
                    }
                }
                break;
            case R.id.btSave:
                if (acPN.length() == 0)
                    Toast.makeText(this, R.string.toa_PN, Toast.LENGTH_LONG).show();
                else if (etCount.length() == 0)
                    Toast.makeText(this, R.string.toa_Count, Toast.LENGTH_LONG).show();
                else if (acPE.length() == 0)
                    Toast.makeText(this,  R.string.toa_PE, Toast.LENGTH_LONG).show();
                else if (etPrice.length() == 0)
                    Toast.makeText(this,R.string.toa_Price, Toast.LENGTH_LONG).show();
                else {
                    if ((Float.parseFloat(etCount.getText().toString()) <= 0) ||
                            (Float.parseFloat(etPrice.getText().toString()) <= 0))
                        Toast.makeText(this, R.string.toa_Zero, Toast.LENGTH_LONG).show();
                    else {
                        UpdateProdInSpisok(prodid);
                        getSupportLoaderManager().getLoader(0).forceLoad();
                        ClView();
                        lvData.setClickable(true);
                        etCount.setVisibility(View.GONE);
                        acPE.setVisibility(View.GONE);
                        etPrice.setVisibility(View.GONE);
                        chbvagno.setVisibility(View.GONE);
                        btSave.setVisibility(View.GONE);
                        SumInKorz();
                    }
                }
                break;
        }
    }

    //процедура добавления продукта в Список покупок
    private void addProdInSpisok(){
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
        //поиск ID выбранного продукта
        int p_id=0;
        cursor = db.getProdNM(acPN.getText().toString());
        int id_C = cursor.getCount();
        if (id_C == 0) {
            db.addProd("Products", 20, asUpperCaseFirstChar(acPN.getText().toString()), 11,hu);
            cursor.close();
            cursor = db.getProdNM(asUpperCaseFirstChar(acPN.getText().toString()));
        }
        int pid = cursor.getColumnIndex("_id");
        cursor.moveToFirst();
        do {
            if (!cursor.isNull(pid)) {
                p_id= Integer.parseInt(cursor.getString(pid));
            }
        } while (cursor.moveToNext());
        cursor.close();
        //поиск ID выбранного описания продукта
        int op_id=0;
        cursor = db.getOpisProdNM(acPOpis.getText().toString());
        if (cursor.getCount() == 0) {
            db.addProdOpis("Popis", p_id, asUpperCaseFirstChar(acPOpis.getText().toString()),hu);
            cursor.close();
            cursor = db.getOpisProdNM(asUpperCaseFirstChar(acPOpis.getText().toString()));
        }
        int opid = cursor.getColumnIndex("_id");
        cursor.moveToFirst();
            do {
                if (!cursor.isNull(opid)) {
                    op_id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("_id")));
                }
            } while (cursor.moveToNext());
        cursor.close();
        //важность продукции
        int chv = 0;
        if (chbvagno.isChecked()==true)
            chv=1;
        else
            chv=0;
        //поиск ID единицы
        int e_id=0;
        cursor = db.getEdinName(acPE.getText().toString());
        int id_CE = cursor.getCount();
        if (id_CE == 0) {
            db.addEdin("edin", acPE.getText().toString(),hu);
            cursor.close();
            cursor = db.getEdinName(acPE.getText().toString());
        }
        int eid = cursor.getColumnIndex("_id");
        cursor.moveToFirst();
        do {
            if (!cursor.isNull(eid)) {
                e_id= Integer.parseInt(cursor.getString(eid));
            }
        } while (cursor.moveToNext());
        cursor.close();
        Float ik = Float.parseFloat(etCount.getText().toString().replace(',', '.'));
        Float ip = Float.parseFloat(etPrice.getText().toString().replace(',', '.'));
        //добавление продукта в Список покупок
        db.addProdSpisok("Pokypka", sn, p_id, op_id, ik, ip, chv,0,e_id,hu);
        getSupportLoaderManager().getLoader(0).forceLoad();
        savePriceProd(p_id,ip,hu);
        ClView();
    }

    //процедура Обновления продукта в Список покупок
    private void UpdateProdInSpisok(int txt){
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
        //поиск ID выбранного продукта
        int p_id=0;
        cursor = db.getProdNM(acPN.getText().toString());
        int id_C = cursor.getCount();
        if (id_C == 0) {
            db.addProd("Pokypka", 13, asUpperCaseFirstChar(acPN.getText().toString()), 11, hu);
            cursor.close();
            cursor = db.getProdNM(asUpperCaseFirstChar(acPN.getText().toString()));
        }
        int pid = cursor.getColumnIndex("_id");
        cursor.moveToFirst();
        do {
            if (!cursor.isNull(pid)) {
                p_id= Integer.parseInt(cursor.getString(pid));
            }
        } while (cursor.moveToNext());
        cursor.close();
        //поиск ID выбранного описания продукта
        int op_id=0;
        cursor = db.getOpisProdNM(acPOpis.getText().toString());
        if (cursor.getCount() == 0) {
            db.addProdOpis("Popis", p_id, asUpperCaseFirstChar(acPOpis.getText().toString()), hu);
            cursor.close();
            cursor = db.getOpisProdNM(asUpperCaseFirstChar(acPOpis.getText().toString()));
        }
        int opid = cursor.getColumnIndex("_id");
        cursor.moveToFirst();
        do {
            if (!cursor.isNull(opid)) {
                op_id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("_id")));
            }
        } while (cursor.moveToNext());
        cursor.close();
        //важность продукции
        int chv = 0;
        if (chbvagno.isChecked()==true)
            chv=1;
        else
            chv=0;
        int e_id=0;
        cursor = db.getEdinName(acPE.getText().toString());
        int id_ed = cursor.getCount();
        if (id_ed == 0) {
            db.addEdin("Edin", acPE.getText().toString(),hu);
            cursor.close();
            cursor = db.getEdinName(acPE.getText().toString());
        }
        int eid = cursor.getColumnIndex("_id");
        cursor.moveToFirst();
        do {
            if (!cursor.isNull(eid)) {
                e_id= Integer.parseInt(cursor.getString(eid));
            }
        } while (cursor.moveToNext());
        cursor.close();
        Float ik = Float.parseFloat(etCount.getText().toString().replace(',', '.'));
        Float ip = Float.parseFloat(etPrice.getText().toString().replace(',', '.'));
        db.upProdSpisok("Pokypka", p_id,op_id,ik,ip,chv,e_id,txt,hu);
        getSupportLoaderManager().getLoader(0).forceLoad();
        savePriceProd(p_id,ip,hu);
    }

    //Процедура записи цены
    private void savePriceProd(int p_id, float ip, int hu){
        cursor = db.getProdPrice(p_id);
        int id_CC = cursor.getCount();
        if (id_CC == 0)
            db.addPriceProd("pprice",p_id,ip,hu);
        else
            db.upPriceProd("pprice",ip,p_id);
        cursor.close();
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
        TextView textView1 = (TextView) view.findViewById(R.id.tvPN);
        //работаем с продуктами, которые НЕ в корзине
        if ((textView1.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) == 0)
        {
            prodid = (int) id;
            float tv2 = 0;
            String tv3 = "";
            String tv8 = "";
            float tv4 = 0;
            int tv7 = 0;
            lvData.setClickable(false);
            acPOpis.setVisibility(View.VISIBLE);
            etCount.setVisibility(View.VISIBLE);
            acPE.setVisibility(View.VISIBLE);
            etPrice.setVisibility(View.VISIBLE);
            chbvagno.setVisibility(View.VISIBLE);
            btSave.setVisibility(View.VISIBLE);
            TextView textView5 = (TextView) view.findViewById(R.id.tvPO);
            TextView textView2 = (TextView) view.findViewById(R.id.tvPK);
            TextView textView3 = (TextView) view.findViewById(R.id.tvPE);
            TextView textView4 = (TextView) view.findViewById(R.id.tvPP);
            if ((textView2.getText().toString().length()==0) ||
                    (textView3.getText().toString().length()==0) ||
                    (textView4.getText().toString().length()==0) )
            {
                cursor = db.getSpisokID(prodid);
                if (cursor.getCount() != 0) {
                    cursor.moveToFirst();
                    do {
                        tv2 = Float.parseFloat(cursor.getString(cursor.getColumnIndex("skol")).replace(',', '.'));
                        tv3 = cursor.getString(cursor.getColumnIndex("ename"));
                        tv4 = Float.parseFloat(cursor.getString(cursor.getColumnIndex("sprice")).replace(',', '.'));
                        tv7 = Integer.parseInt(cursor.getString(cursor.getColumnIndex("svagno")));
                        tv8 = cursor.getString(cursor.getColumnIndex("popis"));
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            acPN.setText(textView1.getText().toString());
            if (textView5.getText().toString().length()==0)
                acPOpis.setText(tv8);
            else
                acPOpis.setText(textView5.getText().toString());
            if (textView2.getText().toString().length()==0)
                etCount.setText(Float.toString(tv2));
            else
                etCount.setText(textView2.getText().toString());
            if (textView3.getText().toString().length()==0)
                acPE.setText(tv3);
            else
                acPE.setText(textView3.getText().toString());
            if ((textView2.getText().toString().length()==0) ||
                    (textView4.getText().toString().length()==0) )
                etPrice.setText(Float.toString(tv4));
            else {
                Float tv5 = Float.parseFloat(textView4.getText().toString().replace(',', '.'));
                Float tv6 = Float.parseFloat(textView2.getText().toString().replace(',', '.'));
                Float tv56 =round(tv5/tv6,2);
                etPrice.setText(Float.toString(tv56));
            }
            if (textView4.getVisibility()==View.GONE)
                etPrice.setEnabled(false);
            else
                etPrice.setEnabled(true);
            if ( tv7==1)
                chbvagno.setChecked(true);
            else
                chbvagno.setChecked(false);
            etCount.requestFocus();
        }
        return true;
    }

    //процедура округления
    public static float round(float value, int scale) {
        return (float) (Math.round(value * Math.pow(10, scale)) / Math.pow(10, scale));
    }

}
