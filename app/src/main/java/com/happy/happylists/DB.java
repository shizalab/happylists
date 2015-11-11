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

// работа над Пользователем

	//получить имя списка из таблицы DB_TABLE
	public Cursor getUserID() {
		return database.rawQuery("select _id from Huser ", null);
	}

// работа над Списками список

  //Выборка всего списка
	public Cursor getAllSpisok(String DB_TABLE,String DB_OB) {
	    return database.query(DB_TABLE, null, null, null, null, null, DB_OB);
  	}
 	 // добавить запись в таблицу Список
 	 public void addNewSpisok(String DB_TABLE, int snom,String snam, String sopis,int stype,int usid ) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		String datetime = sdf.format(new Date(System.currentTimeMillis()));
		ContentValues cv = new ContentValues();
		cv.put("sn", snom);
		cv.put("sname", snam);
    	cv.put("sopis", sopis);
		cv.put("stype", stype);
		cv.put("sdate", datetime);
		cv.put("vid", 4);
		cv.put("usid", usid);
		database.insert(DB_TABLE, null, cv);
  	}
	// добавить запись в таблицу Список
	public void updateSpisok(String DB_TABLE, int snom,String snam, String sopis,int usid ) {
		ContentValues cv = new ContentValues();
		cv.put("sname", snam);
		cv.put("sopis", sopis);
		cv.put("usid", usid);
		database.update(DB_TABLE, cv, "sn = " + snom, null);
	}

	// Ищем максимальный номер списка в списке
	public Cursor getMaxSpisok(String DB_TABLE) {
		return database.rawQuery("Select max(sn) as sn, _id  from "+DB_TABLE, null);
	}
	// удалить список из списка
	public void delRec(String DB_TABLE,long id) {
		String txt = "(select s.sn FROM spisok s where s._id ="+id+")";
		database.delete(DB_TABLE, "sn = " + txt, null);
		//database.delete("Nastr", "sn = " + txt, null);
	}
	// Ищем определенный список
	public Cursor getSpisokId(int sid) {
		return database.rawQuery("Select * from spisok where _id ="+sid, null);
	}

// работа с Настройками

	// Добавить новую настройку для нового списка
	public void addNewNastr(String DB_TABLE, int snom, int kg, int kl,int pr,int vl,int ui,int si,int np) {
		ContentValues cv = new ContentValues();
		cv.put("sn", snom);
		cv.put("kateg", kg);
		cv.put("kolvo", kl);
		cv.put("price", pr);
		cv.put("valuta", vl);
		cv.put("ekr", "0");
		cv.put("usid", ui);
		cv.put("sinch", si);
		cv.put("napom", np);
		database.insert(DB_TABLE, null, cv);
	}
	//открыть настройку для указанного списка
	public Cursor getNastr(int sn) {
		return database.rawQuery("select * from Nastr where sn= "+sn, null);
	}
	//обновить настройку для списка
	public void UpDateNastr(String DB_TABLE, int sn, int kl,int pr,int vl,int ui,int si,int np) {
		ContentValues cv = new ContentValues();
		cv.put("kolvo", kl);
		cv.put("price", pr);
		cv.put("valuta", vl);
		cv.put("usid", ui);
		cv.put("sinch", si);
		cv.put("napom", np);
		database.update(DB_TABLE, cv, "sn = "+ sn, null);
	}

// работа с Продуктами
	//получить все данные из таблицы DB_TABLE
	public Cursor getProdName(String partialValue) {
		if (partialValue != "")
			return database.rawQuery("SELECT _ID, PNAME,eid FROM Products WHERE PNAME LIKE '%"+partialValue+"%'", null);
		else
			return database.rawQuery("SELECT _ID, PNAME,eid FROM Products", null);
	}

// работа с Единицами
	//выбрать определенную единицу
	public Cursor getProdED(long pid) {
		return database.rawQuery("select ename from edin where _id= (SELECT eid FROM Products WHERE _id="+pid+")", null);
	}
	//получить все данные из таблицы DB_TABLE
	public Cursor getEdinName(String partialValue) {
		if (partialValue != "")
			return database.rawQuery("SELECT _ID, ENAME FROM Edin WHERE ENAME LIKE '%"+partialValue+"%'", null);
		else
			return database.rawQuery("SELECT _ID, ENAME FROM Edin order by ENAME", null);
	}

// работа с Ценами
	public Cursor getProdPrice(long pid) {
		return database.rawQuery("select _id, prices from pprice where pid = "+pid, null);
	}

// работа со списком покупок
	// получить все данные из таблицы Pokypka
	public Cursor getSpisok(int sn) {
		return database.rawQuery("Select s._id as _id, s.sn,p.pname,e.ename,s.skol,s.sprice,s.svagno,s.skorz,(select kcolor from kategor where _id=p.kid ) as kc,(select abv from valuta where _id=sp.vid) as abv from Pokypka s, products p, Edin e , Spisok sp where s.pid=p._id and s.eid=e._id and s.sn = "+sn+" and s.sn=sp.sn order by skorz,p.kid", null);
}
	//получить имя списка из таблицы DB_TABLE
	public Cursor getSkorSpisok(long id) {
		return database.rawQuery("select _id, skorz,skol,sprice from Pokypka where _id = "+id, null);
	}
	//обновить запись в/из корзины
	public void UpDateKSp(String DB_TABLE,int txt,long id) {
		ContentValues cv = new ContentValues();
		cv.put("skorz", txt);
		database.update(DB_TABLE, cv, "_id = "+ id, null);
	}
	//сумма всех купленных продуктов
	public Cursor getSumInKor(int sn) {
		return database.rawQuery("select (skol*sprice) as sm from Pokypka where skorz=1 and snom="+sn, null);
	}

}