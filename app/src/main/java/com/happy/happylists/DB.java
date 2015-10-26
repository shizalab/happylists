package com.happy.happylists;

import java.sql.Date;
import java.text.SimpleDateFormat;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DB {
	
  static final String TAG = "myLogs";
  
  private SQLiteDatabase database;

  private final Context mCtx;
  
  public DB(Context ctx) {
    mCtx = ctx;
  }
  
   public void open() {
	  if (database==null) {
		  ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(mCtx, "hlbase");
		  database = dbOpenHelper.openDataBase();
	  }
   }
  
   public void close() {
    if (database!=null) {
    	database.close();
    	}
  }

// работа над Списками список

  //Выборка всего списка
	public Cursor getAllSpisok(String DB_TABLE, String DB_SEL,String DB_GR,String DB_OB) {
	    return database.query(DB_TABLE, null, null, null, DB_GR, null, DB_OB);
  	}
 	 // добавить запись в таблицу Список
 	 public void addNewSpisok(String DB_TABLE, int snom,String snam) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		String datetime = sdf.format(new Date(System.currentTimeMillis()));
		ContentValues cv = new ContentValues();
		cv.put("snom", snom);
		cv.put("sname", snam);
		cv.put("sdate", datetime);
		cv.put("pid", "0");
		cv.put("skol", "0");
		cv.put("sprice", "0");
		cv.put("svagno", "0");
		cv.put("skorz", "0");
		cv.put("eid", 0);
		cv.put("vid", 4);
		cv.put("klid", 0);
		database.insert(DB_TABLE, null, cv);
  	}
	// Ищем максимальный номер списка в списке
	public Cursor getMaxSpisok(String DB_TABLE) {
		return database.rawQuery("Select max(snom) as snom, sname, _id  from "+DB_TABLE, null);
	}
	// удалить список из списка
	public void delRec(String DB_TABLE,long id) {
		String txt = "(select s.snom FROM spisok s where s._id ="+id+")";
		database.delete(DB_TABLE, "snom = " + txt, null);
		//database.delete("Nastr", "sn = " + txt, null);
	}

}