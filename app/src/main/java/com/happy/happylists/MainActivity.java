package com.happy.happylists;

import android.app.Dialog;
import android.content.DialogInterface;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        View.OnClickListener {

    static final String TAG = "myLogs";
    final int DIALOG_EXIT = 1;
    final SwipeDetector swipeDetector = new SwipeDetector();

    DB db;
    Button bt_new;
    ListView lvSS;
    SimpleCursorAdapter scAdapter;
    Cursor spisCursor, cursor;

    int prodid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //убрать иконку приложения
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        db = new DB(this);
        db.open();

        bt_new = (Button) findViewById(R.id.bt_new);
        lvSS = (ListView) findViewById(R.id.lvSS);

        bt_new.setOnClickListener(this);

        CreateListSpisok();
        lvSS.setOnTouchListener(swipeDetector);
        lvSS.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (swipeDetector.swipeDetected()) {
                    // do the onSwipe action
                    switch (swipeDetector.getAction().toString()) {
                        case "LR":
                            Log.d(TAG, "ЛУКЬЯНОВ ЗАДРОТ!!!!");
                            break;
                        case "RL":
                            // Log.d(TAG, "сделали свайп справа налево");
                            // свайп влево - удалить
                            prodid = (int) id;
                            showDialog(DIALOG_EXIT);
                            break;
                    }
                } else {
                    // do the onItemClick action
                }
            }
        });
        lvSS.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (swipeDetector.swipeDetected()) {
                    // do the onSwipe action
                } else {
                    // do the onItemLongClick action
                    // Длинный тап - редактировать список.
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_new:
                addNewSpisok();
                break;
        }
    }

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

    DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                // положительная кнопка
                case Dialog.BUTTON_POSITIVE:
                    db.delRec("Spisok", prodid);
                    getSupportLoaderManager().getLoader(0).forceLoad();
                    break;
                // нейтральная кнопка
                case Dialog.BUTTON_NEUTRAL:
                    break;
            }
        }
    };

    //процедура содания Списка списков
    private void CreateListSpisok() {
        String[] from = new String[] { "sname", "sdate" };
        int[] to = new int[] {R.id.tvSS , R.id.tvSSinf};
        scAdapter = new SimpleCursorAdapter(this, R.layout.sspisoklist, null, from, to, 0);
        scAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int column) {
                switch (view.getId()) {
                    case R.id.tvSS:
                        TextView tvSStmp = (TextView) view.findViewById(R.id.tvSS);
                        tvSStmp.setText(cursor.getString(cursor.getColumnIndex("sname")));
                        return true;
                    case R.id.tvSSinf:
                        TextView tvSSinftmp = (TextView) view.findViewById(R.id.tvSSinf);
                        tvSSinftmp.setText(cursor.getString(cursor.getColumnIndex("sdate")));
                        return true;
                }
                return false;
            }
        });
        lvSS.setAdapter(scAdapter);
        // создаем лоадер для чтения данных
        getSupportLoaderManager().initLoader(0, null, this);
    }

    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        // TODO Auto-generated method stub
                return new CursorLoader(this){
                    @Override
                    public Cursor loadInBackground() {
                        spisCursor = db.getAllSpisok("Spisok", null, "snom", "snom");
                        if (spisCursor.getCount() == 0) {
                            db.addNewSpisok("Spisok", 1 ,"Список "+1);
                            spisCursor = db.getAllSpisok("Spisok", null,"snom", "snom");
                        }
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

    protected void onDestroy() {
        super.onDestroy();
        spisCursor.close();
        // закрываем подключение при выходе
        db.close();
    }

    // процедура добавления нового списка
    private void addNewSpisok() {
        cursor = db.getMaxSpisok("Spisok");
        int max_nom = 0;
        int id_nom = cursor.getColumnIndex("snom");
        cursor.moveToFirst();
        do {
            if (!cursor.isNull(id_nom))
                max_nom= Integer.parseInt(cursor.getString(id_nom));
        } while (cursor.moveToNext());
        cursor.close();
        max_nom = max_nom+1;
        //добавляем запись в базу
        db.addNewSpisok("Spisok", max_nom, "Список " + max_nom);
        getSupportLoaderManager().getLoader(0).forceLoad();
    }


}
