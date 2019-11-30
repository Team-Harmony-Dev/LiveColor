package com.harmony.livecolor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ColorDatabase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "color.db";
    public static final String TABLE_NAME = "colorInfo_table";
    public static final String PALETTE_TABLE_NAME = "PaletteInfo_table";
    public static final String COL1 = "ID";
    public static final String COL2 = "NAME";
    public static final String COL3 = "HEX";
    public static final String COL4 = "RGB";
    public static final String COL5 = "HSV";
    public static final String PAL1 = "ID";
    public static final String PAL2 = "NAME";
    public static final String PAL3 = "ref";

    public ColorDatabase(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createColorInfoTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " NAME TEXT, HEX TEXT, RGB TEXT, HSV TEXT)";
        String createPaletteInfoTable = "CREATE TABLE " + PALETTE_TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " NAME TEXT, REF TEXT, FOREIGN KEY(REF) REFERENCES TABLE_NAME(ID))";
        db.execSQL(createColorInfoTable);
        db.execSQL(createPaletteInfoTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PALETTE_TABLE_NAME);
        onCreate(db);
    }

    public boolean addColorInfoData(String name, String hex, String rgb, String hsv) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues colorInfoContentValues = new ContentValues();
        colorInfoContentValues.put(COL2, name);
        colorInfoContentValues.put(COL3, hex);
        colorInfoContentValues.put(COL4, rgb);
        colorInfoContentValues.put(COL5, hsv);

        long insertResult = db.insert(TABLE_NAME, null, colorInfoContentValues);

        if (insertResult == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean addPaletteInfoData(String name, String id) { // new palette
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues paletteInfoContentValues = new ContentValues();
        paletteInfoContentValues.put(PAL2, name);
        paletteInfoContentValues.put(PAL3, id);

        long insertResult = db.insert(PALETTE_TABLE_NAME, null, paletteInfoContentValues);

        if (insertResult == -1) {
            return false;
        } else {
            return true;
        }
    }

    //might need new method for adding to an existing palette

    public Cursor getColorInfoData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectAllQuery = "SELECT * FROM " + TABLE_NAME;
        Cursor colorData = db.rawQuery(selectAllQuery, null);
        return colorData;
    }

    public Cursor getPaletteInfoData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectAllQuery = "SELECT * FROM " + PALETTE_TABLE_NAME;
        Cursor paletteData = db.rawQuery(selectAllQuery, null);
        return paletteData;
    }


}
