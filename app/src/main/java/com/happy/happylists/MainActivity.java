package com.happy.happylists;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        View.OnClickListener, AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {

    static final String TAG = "myLogs";
    final int DIALOG_EXIT = 1;

    DB db;
    Button bt_new;
    ListView lvSS;
    SimpleCursorAdapter scAdapter;
    Cursor spisCursor;
    SwipeDismissListViewTouchListener touchListener;

    private String[] viewsNames;
    private DrawerLayout myDrawerLayout;
    private ListView myDrawerList;
    private ActionBarDrawerToggle myDrawerToggle;
    // navigation drawer title
    private CharSequence myDrawerTitle;
    // used to store app title
    private CharSequence myTitle;

    int prodid, sn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //начало процедуры отрисовки левого меню
        getSupportActionBar().setTitle(R.string.Spiski);
        myDrawerTitle = getResources().getString(R.string.menu);
        viewsNames = getResources().getStringArray(R.array.views_array);
        myDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        myDrawerList = (ListView) findViewById(R.id.left_drawer);
        myDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, viewsNames));
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        myDrawerToggle = new ActionBarDrawerToggle(this, myDrawerLayout,
                R.string.open_menu,
                R.string.close_menu
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(R.string.Spiski);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(myDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        myDrawerLayout.setDrawerListener(myDrawerToggle);
        myDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        //конец процедуры отрисовки левого меню

        db = new DB(this);
        db.open();

        bt_new = (Button) findViewById(R.id.bt_new);
        lvSS = (ListView) findViewById(R.id.lvSS);

        bt_new.setOnClickListener(this);

        CreateListSpisok();

        lvSS.setOnItemLongClickListener(this);
        lvSS.setOnItemClickListener(this);

    }

    //начало процедуры отрисовки левого меню
    //нажатие на пункт левого меню
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(
                AdapterView<?> parent, View view, int position, long id
        ) {
            // display view for selected nav drawer item
            selectItem(position);
        }
    }
    //выполнение нажатого пункта слева
    private void selectItem(int position) {
        // update the main content by replacing fragments
        switch (position) {
            case 0:
                Log.d(TAG, "Синхронизировать");
                break;
            case 1:
                Log.d(TAG, "Горячая синхронизация");
                break;
            case 2:
                Log.d(TAG, "Оценить приложение");
                break;
            case 3:
                Log.d(TAG, "Аккаунт");
                break;
            case 4:
                Log.d(TAG, "Инфо");
                break;
            case 5:
                Log.d(TAG, "Настройка");
                break;
            case 6:
                Log.d(TAG, "Справочники");
                break;
            default:
                break;
        }
        // update selected item and title, then close the drawer
        myDrawerList.setItemChecked(position, true);
        myDrawerList.setSelection(position);
        setTitle(viewsNames[position]);
        myDrawerLayout.closeDrawer(myDrawerList);
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
        if (myDrawerToggle.onOptionsItemSelected(item)) {
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
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if navigation drawer is opened, hide the action items
        boolean drawerOpen = myDrawerLayout.isDrawerOpen(myDrawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        myDrawerToggle.syncState();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        myDrawerToggle.onConfigurationChanged(newConfig);
    }
    //конец процедуры отрисовки левого меню

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
        Intent intentns = new Intent(this, NewSpisok.class);
        intentns.putExtra("snid", snid);
        intentns.putExtra("btn", btn);
        startActivity(intentns);
        return true;
    }

    // просто нажатие на пункт в listview
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int snid = (int) id;
        TextView textView1 = (TextView) view.findViewById(R.id.tvSS);
        Intent intentsp = new Intent(this, SpisPokyp.class);
        intentsp.putExtra("snid", snid);
        intentsp.putExtra("sname", textView1.getText().toString());
        startActivity(intentsp);
    }
}
