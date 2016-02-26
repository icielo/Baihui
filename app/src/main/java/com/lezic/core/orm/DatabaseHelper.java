package com.lezic.core.orm;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.lezic.app.product.vo.Product;
import com.lezic.core.exception.CustomException;
import com.lezic.core.orm.util.SQLUtil;


public class DatabaseHelper extends SQLiteOpenHelper {

	private static DatabaseHelper databaseHelper;

	private static String name = "lezic.db";

	private static CursorFactory factory;

	private static int version = 3;

	public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	public static DatabaseHelper getInstance(Context context) {
		if (databaseHelper == null) {
			databaseHelper = new DatabaseHelper(context, name, factory, version);
		}
		return databaseHelper;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			db.execSQL(SQLUtil.getCreateTableSQL(Product.class));
		} catch (SQLException e) {
			Log.e("出错了", e.toString());
		} catch (CustomException e) {
			Log.e("出错了", e.toString());
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion == 1 && newVersion == 2 || oldVersion == 2 && newVersion == 3) {
			try {
				db.execSQL(SQLUtil.getDropTableSQL(Product.class));
				db.execSQL(SQLUtil.getCreateTableSQL(Product.class));
			} catch (SQLException e) {
				Log.e("出错了", e.toString());
			} catch (CustomException e) {
				Log.e("出错了", e.toString());
			}
		}
	}

}
