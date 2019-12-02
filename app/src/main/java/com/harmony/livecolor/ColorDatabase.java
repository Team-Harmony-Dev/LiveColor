package com.harmony.livecolor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.StringTokenizer;

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
    public static final String PAL3 = "REF";

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
        addPaletteInfoData("Saved Colors",""); //creates a saved colors palette
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PALETTE_TABLE_NAME);
        onCreate(db);
    }

    /**
     * adds a new color or retrieves matching existing color from the database
     * @param name name of the new color
     * @param hex hex of the new color
     * @param rgb rgb of the new color
     * @param hsv hsv of the new color
     * @return id of the new color or existing id if the color already exists in the database
     */
    public long addColorInfoData(String name, String hex, String rgb, String hsv) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        //Check if color already exists (by hex),
        // if does, return existing color's id
        Cursor cursor = getColorInfoByHex(hex);
        if(cursor != null){
            long id = cursor.getLong(0);
            Log.d("S4U1", "addColorInfoData: id of existing color = " + id);
            return id;
        }

        //else, add the new color to color database
        ContentValues colorInfoContentValues = new ContentValues();
        colorInfoContentValues.put(COL2, name);
        colorInfoContentValues.put(COL3, hex);
        colorInfoContentValues.put(COL4, rgb);
        colorInfoContentValues.put(COL5, hsv);

        long insertResult = db.insert(TABLE_NAME, null, colorInfoContentValues);
        Log.d("S4U1", "addColorInfoData: id of inserted color = " + insertResult);

        return insertResult;
    }

    /**
     * add a new palette to the palette database with a color
     * @param name of the palette
     * @param id of the color to add to the palette
     * @return
     */
    public boolean addPaletteInfoData(String name, String id) { // new palette
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues paletteInfoContentValues = new ContentValues();
        paletteInfoContentValues.put(PAL2, name);
        paletteInfoContentValues.put(PAL3, "");

        long insertResult = db.insert(PALETTE_TABLE_NAME, null, paletteInfoContentValues);
        Log.d("S4U1", "addPaletteInfoData: id of new palette = " + insertResult);

        if (insertResult == -1) {
            return false;
        } else {
            return addColorToPalette(Long.toString(insertResult),id);
        }
    }

    /**
     * add a color existing in the color database to an existing palette
     * @param paletteId id of the palette to be added to
     * @param colorId id of the color to add to the palette
     * @return
     */
    public boolean addColorToPalette(String paletteId, String colorId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String colors = getPaletteColors(paletteId);
        Log.d("S4U1", "addPaletteInfoData: id of color added to new palette = " + colorId);
        //TODO: SPECIFIC COLORS IN REF CAN BE SEARCHED USING "WHERE REF LIKE \' " + id + "\'"

        //TODO: concat color id if it doesnt already exist in the palette
        //TODO: each color should ALWAYS have a space before it for searching
        return false;
    }

    /**
     * allows access to cursor for entire color database access
     * @return cursor at top of color database
     */
    public Cursor getColorInfoData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectAllQuery = "SELECT * FROM " + TABLE_NAME;
        Cursor colorData = db.rawQuery(selectAllQuery, null);
        return colorData;
    }

    /**
     * searches the color database for all colors with the given HEX within a palette
     * @param hex the Hex to be searched for in the database
     * @return the cursor for the query results
     */
    public Cursor getColorInfoByHex(String hex) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_NAME
                + " WHERE HEX = \'" + hex + "\'";
        Cursor colorData = db.rawQuery(selectQuery, null);
        return colorData;
    }

    /**
     * searches a palette for all colors with the given HEX within a palette
     * @param id the ID of the palette to search
     * @param hex the Hex to be searched for in the palette
     * @return the cursor for the query results
     */
    public Cursor getColorInfoByHex(String id, String hex) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_NAME
                + " WHERE HEX = \'" + hex + "\'";
        Cursor colorData = db.rawQuery(selectQuery, null);
        return colorData;
    }

    /**
     * searches the database for all colors with the given name
     * @param name the color name to be searched for in the database
     * @return the cursor for the query results
     */
    public Cursor getColorInfoByName(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_NAME
                + " WHERE NAME = \'" + name + "\'";
        Cursor colorData = db.rawQuery(selectQuery, null);
        return colorData;
    }

    /**
     * searches the database for all colors with the given name within a palette
     * @param id the ID of the palette to search
     * @param name the color name to be searched for in the palette
     * @return the cursor for the query results
     */
    public Cursor getColorInfoByName(String id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_NAME
                + " WHERE NAME = " + name;
        Cursor colorData = db.rawQuery(selectQuery, null);
        return colorData;
    }

    /**
     * allows access to cursor for entire palette database access
     * @return cursor at the top of palette database
     */
    public Cursor getPaletteInfoData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectAllQuery = "SELECT * FROM " + PALETTE_TABLE_NAME;
        Cursor paletteData = db.rawQuery(selectAllQuery, null);
        return paletteData;
    }

    /**
     * retrieve a palette by its user given name
     * @param name of the palette that the user is searching for
     * @return cursor pointing at any matching results
     */
    public Cursor getPaletteInfoByName(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + PALETTE_TABLE_NAME
                + " WHERE NAME = \'" + name + "\'";
        Cursor paletteData = db.rawQuery(selectQuery, null);
        return paletteData;
    }

    //TODO: SPECIFIC COLORS IN REF CAN BE SEARCHED USING "WHERE REF LIKE \' " + id + "\'"
    public Cursor checkPaletteForColor(String paletteId, String colorId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + PALETTE_TABLE_NAME
                + " WHERE ID = \'" + paletteId + "\'"
                + " AND REF LIKE \' " + colorId + "\'";
        Cursor paletteData = db.rawQuery(selectQuery, null);
        return paletteData;
    }

    /**
     * method for renaming an existing palette
     * @param id the id of the palette whose name will be changed
     * @param newName the new name to assign the palette
     * @return boolean to indicate success of the executed SQL update query
     */
    public boolean changePaletteName(String id, String newName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String updateQuery = "UPDATE " + PALETTE_TABLE_NAME
                + " SET NAME = \'" + newName + "\'"
                + " WHERE ID = \'" + id + "\'";

        try {
            db.execSQL(updateQuery);
            return true;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * gets the a string of all colors within the palette
     * @param id of the palette to get the data from
     * @return the string of all ids of the colors in the palette
     */
    public String getPaletteColors(String id) {
        String result = ""; //will hold the result with the color ids if any
        SQLiteDatabase db = this.getWritableDatabase();
        //query for ref string only for specific palette by id
        String selectQuery = "SELECT REF FROM" + PALETTE_TABLE_NAME
                + " WHERE ID = \'" + id + "\'";

        //get the cursor from the query
        Cursor paletteData = db.rawQuery(selectQuery, null);
        if(paletteData != null){
            //get the string result from the query
            result = paletteData.getString(0);
        }

        return result;
    }
}
