package com.shumadlads.hallamhelper.hallamhelper.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DBOpenHelper extends SQLiteAssetHelper {
   private static final String DATABASE_NAME = "MyExternalDatabase.db";
   private static final int DATABASE_VERSION= 1;

   public DBOpenHelper(Context context){
       super(context,DATABASE_NAME,null,DATABASE_VERSION);
   }
}
