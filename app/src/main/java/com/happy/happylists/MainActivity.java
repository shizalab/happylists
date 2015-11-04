package com.happy.happylists;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        View.OnClickListener, AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {

    static final String TAG = "myLogs";
    final int DIALOG_EXIT = 1;

    DB db;
    Button bt_new;
    ListView lvSS;
    SimpleCursorAdapter scAdapter;
    Cursor spisCursor;
    SwipeDismissListViewTouchListener touchListener;

    int prodid, sn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.Spiski);
        //стрелочка
       // actionBar.setDisplayHomeAsUpEnabled(true);

        db = new DB(this);
        db.open();

        bt_new = (Button) findViewById(R.id.bt_new);
        lvSS = (ListView) findViewById(R.id.lvSS);

        bt_new.setOnClickListener(this);

        CreateListSpisok();

        lvSS.setOnItemLongClickListener(this);
        lvSS.setOnItemClickListener(this);

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
        // Обработка выбранного элемента меню.
        switch (item.getItemId())
        {
            case R.id.it01:

                return true;
            case R.id.it02:

                return true;
            case R.id.it03:

                return true;
            case R.id.it04:

                return true;
            case R.id.it05:

                return true;
            case R.id.it06:

                return true;
            case R.id.it07:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // нажатие на кнопку Создать
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_new:
                String btn = "Создать";
                Intent intent = new Intent(this, NewSpisok.class);
                intent.putExtra("snid", "0");
                intent.putExtra("btn", btn);
                startActivity(intent);
                break;
        }
    }

    // Создание диалога с уточнением о удалении списка
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_EXIT) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            // заголовок
            adb.setTitle(R.string.Titl);
            // сообщение
            adb.setMessage(R.string.delete_record);
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
                    db.delRec("Spisok", prodid);
                    getSupportLoaderManager().getLoader(0).forceLoad();
                // нейтральная кнопка
                case Dialog.BUTTON_NEUTRAL:
                    break;
            }
            CreateListSpisok();

        }
    };

    //процедура содания Списка списков
    private void CreateListSpisok() {
        String[] from = new String[] { "sname", "sdate", "sopis" };
        int[] to = new int[] {R.id.tvSS , R.id.tvDat, R.id.tvOpis};
        scAdapter = new SimpleCursorAdapter(this, R.layout.sspisoklist, null, from, to, 0);
        scAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int column) {
                switch (view.getId()) {
                    case R.id.tvSS:
                        TextView tvSStmp = (TextView) view.findViewById(R.id.tvSS);
                        tvSStmp.setText(cursor.getString(cursor.getColumnIndex("sname")));
                        return true;
                    case R.id.tvDat:
                        TextView tvDat = (TextView) view.findViewById(R.id.tvDat);
                        tvDat.setText(cursor.getString(cursor.getColumnIndex("sdate")));
                        return true;
                    case R.id.tvOpis:
                        TextView tvOpis = (TextView) view.findViewById(R.id.tvOpis);
                        tvOpis.setText(cursor.getString(cursor.getColumnIndex("sopis")));
                        return true;
                }
                return false;
            }
        });
        lvSS.setAdapter(scAdapter);
        // создаем лоадер для чтения данных
        getSupportLoaderManager().initLoader(0, null, this);
        touchListener =
                new SwipeDismissListViewTouchListener(
                        lvSS,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    prodid = (int) scAdapter.getItemId(position);
                                    showDialog(DIALOG_EXIT);
                                }
                            }
                        });
        lvSS.setOnTouchListener(touchListener);
        lvSS.setOnScrollListener(touchListener.makeScrollListener());
    }

    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        // TODO Auto-generated method stub
                return new CursorLoader(this){
                    @Override
                    public Cursor loadInBackground() {
                        spisCursor = db.getAllSpisok("Spisok", "sn");
                        return spisCursor;
                    }
                };
    }

    public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
        // TODO Auto-generated method stub
        if(scAdapter!=null && arg1!=null)
            scAdapter.swapCursor((android.database.Cursor) arg1);
        else
            Log.v(TAG,"OnLoadFinished: scAdapter is null");
     }

    public void onLoaderReset(Loader<Cursor> arg0) {
        // TODO Auto-generated method stub
        if(scAdapter!=null)
             scAdapter.swapCursor(null);
        else
            Log.v(TAG,"OnLoadFinished: scAdapter is null");
    }

    // закрытие окна
    protected void onDestroy() {
        super.onDestroy();
        spisCursor.close();
        // закрываем подключение при выходе
        db.close();
    }

    // возрващение к окну
    @Override
    public void onRestart() {
        super.onRestart();
        getSupportLoaderManager().getLoader(0).forceLoad();
    }

    // долгое нажатие на пункт в listview
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        String btn = "Сохранить";
        int snid = (int) id;
        Intent intent = new Intent(this, NewSpisok.class);
        intent.putExtra("snid", snid);
        intent.putExtra("btn", btn);
        startActivity(intent);
        return false;
    }

    // просто нажатие на пункт в listview
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int snid = (int) id;
        TextView textView1 = (TextView) view.findViewById(R.id.tvSS);
        Intent intent = new Intent(this, SpisPokyp.class);
        intent.putExtra("snid", snid);
        intent.putExtra("sname", textView1.getText().toString());
        startActivity(intent);
    }
}
