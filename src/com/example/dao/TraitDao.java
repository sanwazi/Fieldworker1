package com.example.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.db.DatabaseHelper;
import com.example.domain.Trait;

public class TraitDao {
	private DatabaseHelper dbHelper;

	public TraitDao(Context context) {
		dbHelper = new DatabaseHelper(context);
	}

	public void insert(Trait t) {
		//search by traitid, if finding nothing by id, it can be added to Trait Table
		if (findNameById(t.getTraitID())==null) {
             
		
		SQLiteDatabase sdb = dbHelper.getWritableDatabase();

		String sqlString = "INSERT INTO Trait(traitID,traitName,widgetName,unit) values (?,?,?,?) ";
		Object obj[] = { t.getTraitID(), t.getTraitName(), t.getWidgetName(),
				t.getUnit() };
		sdb.execSQL(sqlString, obj);
		}
	}

	public List<String> findAllTraitNames() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"SELECT traitName FROM Trait WHERE accessible=1", null);
		List<String> traitNames = new ArrayList<String>();
		while (cursor.moveToNext()) {

			String traitName = cursor.getString(0);

			traitNames.add(traitName);

		}
		cursor.close();
		db.close();
		return traitNames;
	}

	public Integer findIdbyName(String name) {
		Integer id;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db
				.rawQuery("SELECT traitID FROM Trait WHERE traitName=" + "'"
						+ name + "'", null);
		cursor.moveToNext();
		id=cursor.getInt(0);
		cursor.close();
		db.close();
		return id;
	}

	public List<Trait> findAll() {
		// TODO Auto-generated method stub
		SQLiteDatabase sdb = dbHelper.getReadableDatabase();
		String sqlString = "SELECT traitID,traitName,widgetName,unit FROM Trait where accessible=1";
		Cursor cursor = sdb.rawQuery(sqlString, null);
		List<Trait> traits = new ArrayList<Trait>();
		while (cursor.moveToNext()) {
			Integer traitID = cursor.getInt(0);
			String traitName = cursor.getString(1);
			String widgetName = cursor.getString(2);
			String unit = cursor.getString(3);
			traits.add(new Trait(traitID, traitName, widgetName, unit));

		}
		cursor.close();
		sdb.close();
		return traits;
	}

	public Trait searchByTraitName(String name) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sqlString = "SELECT traitID,traitName,widgetName,unit FROM Trait"
				+ " WHERE traitName=" + "'" + name + "'";
		Cursor cursor = db.rawQuery(sqlString, null);
		cursor.moveToNext();
		Integer traitID = cursor.getInt(0);
		String traitName = cursor.getString(1);
		String widgetName = cursor.getString(2);
		String unit = cursor.getString(3);
		cursor.close();
		db.close();
		return new Trait(traitID, traitName, widgetName, unit);
	}

	public int findIdByName(String name) {
		int id;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sqlString = "SELECT traitID FROM Trait WHERE traitName=" + "'"
				+ name + "'";
		Cursor cursor = db.rawQuery(sqlString, null);
		if (cursor.moveToNext()) {
			id = cursor.getInt(0);
			return id;
		}
		cursor.close();
		db.close();
		return 0;

	}

	public String findNameById(Integer id) {
		String name;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sqlString = "SELECT traitName FROM Trait WHERE traitID = " + id;
		Cursor cursor = db.rawQuery(sqlString, null);
		if (cursor.moveToNext()) {
			name = cursor.getString(0);
			cursor.close();
			db.close();
			return name;
		} else {
			cursor.close();
			db.close();
			return null;
		}
	}

	public void delete(String traitName) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String sqlString = "UPDATE Trait SET accessible=0 WHERE traitName="
				+ "'" + traitName + "'";
		System.out.println("trait name:" + traitName);
		// int traitID=findIdByName(traitName);
		// String
		// sqlString2="DELETE FROM PredefineVal WHERE traitID="+"'"+traitID+"'";
		db.execSQL(sqlString);
		// db.execSQL(sqlString2);
		db.close();
	}

	public boolean checkTraitName(String traitName) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sqlString = "SELECT * FROM Trait WHERE traitName=" + "'"
				+ traitName + "'";
		Cursor cursor = db.rawQuery(sqlString, null);
		int count = cursor.getCount();
		cursor.close();
		db.close();
		if (count == 0)
			return true;
		else
			return false;

	}

}
