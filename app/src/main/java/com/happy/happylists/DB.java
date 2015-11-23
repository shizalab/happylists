package com.happy.happylists;

import java.sql.Date;
import java.text.SimpleDateFormat;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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
	public Cursor getUsdat() {
		return database.rawQuery("select date_in from Huser", null);
	}
	//Выборка всех настроек пользователя
	public Cursor getMailSettings() {
		return database.rawQuery("select * from mainset where _id = (select setid from Huser)", null);
	}
	//обновить главные настройки для приложения
	public void UpDateMainSet(String DB_TABLE, int ekr, int tem) {
		SimpleDateFormat sdtf = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
		String datetime = sdtf.format(new Date(System.currentTimeMillis()));
		ContentValues cv = new ContentValues();
		cv.put("ekr", ekr);
		cv.put("tema", tem);
		cv.put("date_in", datetime);
		cv.put("sinxr", 0);
		database.update(DB_TABLE, cv, "_id = 1", null);
	}

// работа над над всей базой

	//Обновление поля date_in по всей базе
	public void updDateIn() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
		String datetime = sdf.format(new Date(System.currentTimeMillis()));
		ContentValues cv = new ContentValues();
		cv.put("date_in", datetime);
		database.update("edin", cv, "_id in (select _id from edin) ", null);
		database.update("huser", cv, "_id in (select _id from huser) ", null);
		database.update("kategor", cv, "_id in (select _id from kategor) ", null);
		database.update("pprice", cv, "_id in (select _id from pprice) ", null);
		database.update("products", cv, "_id in (select _id from products) ", null);
		database.update("mainset", cv, "_id in (select _id from mainset) ", null);
		database.update("popis", cv, "_id in (select _id from popis) ", null);
	}

// работа с Синхронизацией: поле sinxr, где 0 - изменено и не передано, 1 - передано.


// работа над Списками список

  //Выборка всего списка
	public Cursor getAllSpisok(String DB_TABLE,String DB_OB) {
	    return database.query(DB_TABLE, null, null, null, null, null, DB_OB);
  	}
 	 // добавить запись в таблицу Список
 	 public void addNewSpisok(String DB_TABLE, int snom,String snam, String sopis,int stype,int usid ) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		String date = sdf.format(new Date(System.currentTimeMillis()));
		SimpleDateFormat sdtf = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
		String datetime = sdtf.format(new Date(System.currentTimeMillis()));
		ContentValues cv = new ContentValues();
		cv.put("sn", snom);
		cv.put("sname", snam);
    	cv.put("sopis", sopis);
		cv.put("stype", stype);
		cv.put("sdate", date);
		cv.put("vid", 4);
		cv.put("usid", usid);
		cv.put("date_in", datetime);
		cv.put("sinxr", 0);
		database.insert(DB_TABLE, null, cv);
  	}
	// добавить запись в таблицу Список
	public void updateSpisok(String DB_TABLE, int snom,String snam, String sopis,int usid ) {
		SimpleDateFormat sdtf = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
		String datetime = sdtf.format(new Date(System.currentTimeMillis()));
		ContentValues cv = new ContentValues();
		cv.put("sname", snam);
		cv.put("sopis", sopis);
		cv.put("usid", usid);
		cv.put("date_in", datetime);
		cv.put("sinxr", 0);
		database.update(DB_TABLE, cv, "sn = " + snom, null);
	}
	// добавить запись в таблицу Список
	public void updateAnyInSpisok(String DB_TABLE, int snom) {
		SimpleDateFormat sdtf = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
		String datetime = sdtf.format(new Date(System.currentTimeMillis()));
		ContentValues cv = new ContentValues();
		cv.put("date_in", datetime);
		cv.put("sinxr", 0);
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
		database.delete("Nastr", "sn = " + txt, null);
	}
	// Ищем определенный список
	public Cursor getSpisokId(int sid) {
		return database.rawQuery("Select * from spisok where _id ="+sid, null);
	}

// работа с Настройками

	// Добавить новую настройку для нового списка
	public void addNewNastr(String DB_TABLE, int snom, int kg, int kl,int pr,int vl,int ui,int si) {
		SimpleDateFormat sdtf = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
		String datetime = sdtf.format(new Date(System.currentTimeMillis()));
		ContentValues cv = new ContentValues();
		cv.put("sn", snom);
		cv.put("kateg", kg);
		cv.put("kolvo", kl);
		cv.put("price", pr);
		cv.put("valuta", vl);
		cv.put("usid", ui);
		cv.put("sinch", si);
		cv.put("date_in", datetime);
		cv.put("sinxr", 0);
		database.insert(DB_TABLE, null, cv);
	}
	//открыть настройку для указанного списка
	public Cursor getNastr(int sn) {
		return database.rawQuery("select * from Nastr where sn= "+sn, null);
	}
	//обновить настройку для списка
	public void UpDateNastr(String DB_TABLE, int sn, int kl,int pr,int vl,int ui,int si,int np) {
		SimpleDateFormat sdtf = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
		String datetime = sdtf.format(new Date(System.currentTimeMillis()));
		ContentValues cv = new ContentValues();
		cv.put("kolvo", kl);
		cv.put("price", pr);
		cv.put("valuta", vl);
		cv.put("usid", ui);
		cv.put("sinch", si);
		cv.put("date_in", datetime);
		cv.put("sinxr", 0);
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
	//получить все описания продукта по ID продукта
	public Cursor getOpisProd(String partialValue, int pid) {
		if (partialValue != "")
			return database.rawQuery("SELECT _ID, popis FROM Popis WHERE popis LIKE '%"+partialValue+"%' and pid="+pid, null);
		else
			return database.rawQuery("SELECT _ID, popis FROM Popis WHERE pid="+pid, null);
	}
	//поиск продукта по имени
	public Cursor getProdNM(String pnam) {
		return database.rawQuery("select _id, pname from products where pname = '"+pnam+"'", null);
	}
	//поиск продукта по имени
	public Cursor getOpisProdNM(String opis) {
		return database.rawQuery("select _id, popis from popis where popis = '"+opis+"'", null);
	}
	//добавить новый продукт в справочник
	public void addProd(String DB_TABLE,int kid,String pnam,int eid,int hu) {
		SimpleDateFormat sdtf = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
		String datetime = sdtf.format(new Date(System.currentTimeMillis()));
		ContentValues cv = new ContentValues();
		cv.put("kid", kid);
		cv.put("pname", pnam);
		cv.put("eid", eid);
		cv.put("usid", hu);
		cv.put("date_in", datetime);
		cv.put("sinxr", 0);
		database.insert(DB_TABLE, null, cv);
	}
	//добавить новое описание продукта в справочник
	public void addProdOpis(String DB_TABLE,int pid,String popis,int hu) {
		SimpleDateFormat sdtf = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
		String datetime = sdtf.format(new Date(System.currentTimeMillis()));
		ContentValues cv = new ContentValues();
		cv.put("pid", pid);
		cv.put("popis", popis);
		cv.put("usid", hu);
		cv.put("date_in", datetime);
		cv.put("sinxr", 0);
		database.insert(DB_TABLE, null, cv);
	}

	// работа с Единицами
	//выбрать определенную единицу
	public Cursor getProdED(long pid) {
		return database.rawQuery("select ename from edin where _id= (SELECT eid FROM Products WHERE _id="+pid+")", null);
	}
	//получить единицы
	public Cursor getEdinName(String partialValue) {
		if (partialValue != "")
			return database.rawQuery("SELECT _ID, ENAME FROM Edin WHERE ENAME LIKE '%"+partialValue+"%'", null);
		else
			return database.rawQuery("SELECT _ID, ENAME FROM Edin order by ENAME", null);
	}
	//добавить запись единицы в DB_TABLE
	public void addEdin(String DB_TABLE,String enam,int hu) {
		SimpleDateFormat sdtf = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
		String datetime = sdtf.format(new Date(System.currentTimeMillis()));
		ContentValues cv = new ContentValues();
		cv.put("ename", enam);
		cv.put("usid", hu);
		cv.put("date_in", datetime);
		cv.put("sinxr", 0);
		database.insert(DB_TABLE, null, cv);
	}

// работа с Ценами
	public Cursor getProdPrice(long pid) {
		return database.rawQuery("select _id, prices from pprice where pid = "+pid, null);
	}
	//добавить цену продукта
	public void addPriceProd(String DB_TABLE,int pid,float pp,int hu) {
		SimpleDateFormat sdtf = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
		String datetime = sdtf.format(new Date(System.currentTimeMillis()));
		ContentValues cv = new ContentValues();
		cv.put("pid", pid);
		cv.put("prices", pp);
		cv.put("usid", hu);
		cv.put("date_in", datetime);
		cv.put("sinxr", 0);
		database.insert(DB_TABLE, null, cv);
	}
	//обновить цену продукта
	public void upPriceProd(String DB_TABLE, float pp,int pid) {
		SimpleDateFormat sdtf = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
		String datetime = sdtf.format(new Date(System.currentTimeMillis()));
		ContentValues cv = new ContentValues();
		cv.put("prices", pp);
		cv.put("date_in", datetime);
		cv.put("sinxr", 0);
		database.update(DB_TABLE, cv, "pid = " + pid, null);
	}

// работа со списком покупок
	// получить все данные из таблицы Pokypka
	public Cursor getSpisok(int sn) {
		return database.rawQuery("Select s._id as _id, s.sn,p.pname,e.ename,s.skol,s.sprice,s.svagno,s.skorz,po.popis,(select kcolor from kategor where _id=p.kid ) as kc,(select abv from valuta where _id=sp.vid) as abv from Pokypka s, products p, Edin e , Spisok sp, Popis po where s.pid=p._id and s.eid=e._id and s.sn = "+sn+" and s.sn=sp.sn and s.opid=po._id order by skorz,p.kid", null);
	}
	//получить имя списка из таблицы DB_TABLE
	public Cursor getSkorSpisok(long id) {
		return database.rawQuery("select _id, skorz,skol,sprice from Pokypka where _id = "+id, null);
	}
	//обновить запись в/из корзины
	public void UpDateKSp(String DB_TABLE,int txt,long id) {
		SimpleDateFormat sdtf = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
		String datetime = sdtf.format(new Date(System.currentTimeMillis()));
		ContentValues cv = new ContentValues();
		cv.put("skorz", txt);
		cv.put("date_in", datetime);
		cv.put("sinxr", 0);
		database.update(DB_TABLE, cv, "_id = "+ id, null);
	}
	//сумма всех купленных продуктов
	public Cursor getSumInKor(int sn) {
		return database.rawQuery("select (skol*sprice) as sm from Pokypka where skorz=1 and sn="+sn, null);
	}
	// добавить новый продукт в Список покупок
	public void addProdSpisok(String DB_TABLE,int sn,int pid, int opid,float skol, float sprice, int v, int skr, int eid, int hu) {
		SimpleDateFormat sdtf = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
		String datetime = sdtf.format(new Date(System.currentTimeMillis()));
		ContentValues cv = new ContentValues();
		cv.put("sn", sn);
		cv.put("pid", pid);
		cv.put("opid", opid);
		cv.put("skol", skol);
		cv.put("sprice", sprice);
		cv.put("svagno", v);
		cv.put("skorz", skr);
		cv.put("eid", eid);
		cv.put("usid", hu);
		cv.put("date_in", datetime);
		cv.put("sinxr", 0);
		database.insert(DB_TABLE, null, cv);
	}
	// удалить продукт из Списка покупок
	public void delRecSP(String DB_TABLE,long id) {
		database.delete(DB_TABLE, "_id = " + id, null);
	}
	// получить все данные из таблицы Pokypka
	public Cursor getSpisokID(int spid) {
		return database.rawQuery("Select s._id as _id, s.sn,p.pname,e.ename,s.skol,s.sprice,s.svagno,s.skorz,po.popis,(select kcolor from kategor where _id=p.kid ) as kc,(select abv from valuta where _id=sp.vid) as abv from Pokypka s, products p, Edin e , Spisok sp, Popis po where s.pid=p._id and s.eid=e._id and s._id = "+spid+" and s.sn=sp.sn and s.opid=po._id", null);
	}
	// получить все данные из таблицы Pokypka
	public Cursor getVagnoID(int spid) {
		return database.rawQuery("Select svagno from Pokypka where _id = "+spid, null);
	}
	//обновить продукт из Списка покупок
	public void upProdSpisok(String DB_TABLE,int pid, int opid, float skol, float sprice, int v, int eid, int _id,int hu) {
		SimpleDateFormat sdtf = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
		String datetime = sdtf.format(new Date(System.currentTimeMillis()));
		ContentValues cv = new ContentValues();
		cv.put("pid", pid);
		cv.put("opid", opid);
		cv.put("skol", skol);
		cv.put("sprice", sprice);
		cv.put("svagno", v);
		cv.put("eid", eid);
		cv.put("usid", hu);
		cv.put("date_in", datetime);
		cv.put("sinxr", 0);
		database.update(DB_TABLE,cv, "_id = "+ _id, null);
	}

}