package com.harmony.livecolor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
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
        //creates a saved colors palette
        addPaletteInfoData("Saved Colors","");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PALETTE_TABLE_NAME);
        onCreate(db);
    }

    /**
     * CALL FOR ADDING COLOR TO COLOR DATABASE FOR FIRST TIME (DONE)
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
     * CALL FOR ADDING NEW PALETTE TO DATABASE (DONE)
     * add a new palette to the palette database with a color
     * @param name of the palette
     * @param id of the color to add to the palette
     * @return
     */
    public boolean addPaletteInfoData(String name, String id) {
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
     * CALL FOR ADDING COLOR TO PALETTE (CHECKS IF COLOR ALREADY EXISTS THERE) (DONE)
     * add a color existing in the color database to an existing palette if not already in the palette
     * @param paletteId id of the palette to be added to
     * @param colorId id of the color to add to the palette
     * @return true if update is success, false if update is failure
     */
    public boolean addColorToPalette(String paletteId, String colorId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String colors = getPaletteColors(paletteId);
        Log.d("S4U1", "addPaletteInfoData: id of color added to new palette = " + colorId);
        //check if the color is already in the palette, if not
        if(!doesPaletteHaveColor(paletteId,colorId)) {
            //update the palette ref string to include the new color id
            String paletteColors = getPaletteColors(paletteId);
            //each color should ALWAYS have a space before it for searching
            paletteColors = paletteColors.concat(" " + colorId);
            String updateQuery = "UPDATE " + PALETTE_TABLE_NAME
                    + " SET REF = \'" + paletteColors + "\'"
                    + " WHERE ID = \'" + paletteId + "\'";
            try {
                db.execSQL(updateQuery);
                return true;
            }
            catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    /**
     * Andrew's (DONE)
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
     * FOR CHECKING IF A COLOR ALREADY EXISTS IN COLOR DATABASE (DONE)
     * THIS MUST BE ITS OWN METHOD WITH CURSOR RETURN
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
     * ?
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
     * ?
     * searches the database for a specific color by id
     * @param id of the color to be searched for in the database
     * @return the cursor for the query results
     */
    public Cursor getColorInfoById(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_NAME
                + " WHERE ID = \'" + id + "\'";
        Cursor colorData = db.rawQuery(selectQuery, null);
        return colorData;
    }


    /**
     * Andrew's (DONE)
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
     * FOR SEARCH BAR FEATURE IN PALETTE FRAGMENT (DONE)
     * retrieve a palette by its user given name or hex
     * @param input name or hex of the palette that the user is searching for
     * @return cursor pointing at any matching results
     */
    public Cursor getPaletteInfo(String input) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + PALETTE_TABLE_NAME
                + " WHERE NAME LIKE \'" + input + "\'"
                + " OR HEX LIKE \'" + input + "\'";
        Cursor paletteData = db.rawQuery(selectQuery, null);
        return paletteData;
    }

    /**
     * CHECK IF A PALETTE CONTAINS A COLOR IN ITS REF STRING (DONE)
     * checks if the given color exists in the given palette
     * @param paletteId for accessing the palette
     * @param colorId to be checked for in the palette's ref string
     * @return boolean based on whether the colorId exists in the ref string of the palette
     */
    public boolean doesPaletteHaveColor(String paletteId, String colorId) {
        SQLiteDatabase db = this.getWritableDatabase();
        //TODO: if this method of searching with the where query doesn't work, just parse the string
        String selectQuery = "SELECT * FROM " + PALETTE_TABLE_NAME
                + " WHERE ID = \'" + paletteId + "\'"
                + " AND REF LIKE \' " + colorId + "\'";
        Cursor paletteData = db.rawQuery(selectQuery, null);
        return (paletteData != null);
    }

    /**
     * RENAME A PALETTE (DONE)
     * method for renaming an existing palette that isn't Saved Colors
     * @param id the id of the palette whose name will be changed
     * @param newName the new name to assign the palette
     * @return boolean to indicate success of the executed SQL update query
     */
    public boolean changePaletteName(String id, String newName) {
        SQLiteDatabase db = this.getWritableDatabase();
        //user cannot change the name of Saved Colors
        if(id == "1"){
            return false;
        }
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
     * GET PALETTE'S REF STRING (DONE)
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

    //TODO: helper methods to populate lists?
    public ArrayList<MyColor> getPaletteColorList(String id) {
        ArrayList<MyColor> colorList = new ArrayList<>();
        //get string ref of color ids in given palette
        String colorIds = getPaletteColors(id).trim();
        String[] splitIds = colorIds.split("\\s+");
        //get each color by id
        for(int i = 0; i < splitIds.length; i++) {
            String colorId = splitIds[i];
            Cursor cursor = getColorInfoById(colorId);
            String name = cursor.getString(1);
            String hex = cursor.getString(2);
            String rgb = cursor.getString(3);
            String hsv = cursor.getString(4);
            colorList.add(new MyColor(colorId,name,hex,rgb,hsv));
        }
        return colorList;
    }

    /**
     * get all user made palettes (aka all palettes excluding Saved Colors (id = 1)
     * @return arraylist of palette objects for recycler display
     */
    public ArrayList<MyPalette> getPaletteList() {
        ArrayList<MyPalette> paletteList = new ArrayList<>();
        //get palette data
        Cursor cursor = getPaletteInfoData();
        //skip Saved Colors (id = 1)
        cursor.moveToFirst();
        cursor.moveToNext();
        while(cursor != null){
            String paletteId = cursor.getString(0);
            String paletteName = cursor.getString(1);
            ArrayList<MyColor> colorList = getPaletteColorList(paletteId);
            paletteList.add(new MyPalette(paletteId, paletteName, colorList));
            cursor.moveToNext();
        }
        return paletteList;
    }
}
