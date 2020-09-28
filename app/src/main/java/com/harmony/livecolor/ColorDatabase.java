package com.harmony.livecolor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * TOC (for easier Ctrl+F searching)
 *   - COLOR/PALETTE ADDING/DELETING METHODS
 *   - CURSOR METHODS
 *   - SEARCHING/CHECKING METHODS
 *   - ARRAYLIST HELPER METHODS
 *   - OTHER PALETTE UTILITY METHODS
 */

public class ColorDatabase extends SQLiteOpenHelper {

    private SQLiteDatabase db;
    public static final String DATABASE_NAME = "color.db";
    public static final String COLOR_TABLE_NAME = "colorInfo_table";
    public static final String PALETTE_TABLE_NAME = "paletteInfo_table";

    public static final String COL1 = "ID"; //COLOR ENTRY ID
    public static final String COL2 = "NAME"; //COLOR NAME
    public static final String COL3 = "HEX"; //COLOR HEX value
    public static final String COL4 = "RGB"; //COLOR RGB value
    public static final String COL5 = "HSV"; //COLOR HSV value

    public static final String PAL1 = "ID"; //PALETTE ENTRY ID
    public static final String PAL2 = "NAME"; //PALETTE NAME
    public static final String PAL3 = "REF"; //String containing all IDs of colors in palette, separated by spaces

    public final static int MAX_COLORS_PER_PALETTE = 3;//TODO make this much larger after testing

    final String TAG_COLOR = "ColorDatabase";
    final String TAG_PALETTE = "PaletteDatabase";

    public ColorDatabase(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        Log.d(TAG_COLOR, "onCreate: creating database");
        String createColorInfoTable = "CREATE TABLE " + COLOR_TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " NAME TEXT, HEX TEXT, RGB TEXT, HSV TEXT)";
        String createPaletteInfoTable = "CREATE TABLE " + PALETTE_TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " NAME TEXT, REF TEXT)";
        this.db.execSQL(createColorInfoTable);
        this.db.execSQL(createPaletteInfoTable);
        //creates a saved colors palette
        addNewPalette("Saved Colors","");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        this.db = db;
        this.db.execSQL("DROP TABLE IF EXISTS " + COLOR_TABLE_NAME);
        this.db.execSQL("DROP TABLE IF EXISTS " + PALETTE_TABLE_NAME);
        onCreate(db);
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        final SQLiteDatabase db;
        if(this.db != null){
            db = this.db;
        } else {
            db = super.getWritableDatabase();
        }
        return db;
    }

    /**
     * COLOR/PALETTE ADDING/DELETING METHODS:
     */

    /**
     * CALL FOR ADDING COLOR TO COLOR DATABASE (DONE)
     * adds a new color or retrieves matching existing color from the database. Checks for duplicates by HEX
     * @param name name of the color
     * @param hex hex of the color
     * @param rgb rgb of the color
     * @param hsv hsv of the color
     * @return id of the new color or existing id if the color already exists in the database
     */
    public long addColorInfoData(String name, String hex, String rgb, String hsv) {
        //Check if color already exists (by hex),
        // if does, return existing color's id
        Cursor cursor = getColorInfoByHex(hex);
        if(cursor != null && cursor.getCount()>0){
            long id = cursor.getLong(0);
            Log.d(TAG_COLOR, "addColorInfoData: id of existing color = " + id);
            cursor.close();
            return id;
        }

        db = this.getWritableDatabase();
        //else, add the new color to color database
        ContentValues colorInfoContentValues = new ContentValues();
        colorInfoContentValues.put(COL2, name);
        colorInfoContentValues.put(COL3, hex);
        colorInfoContentValues.put(COL4, rgb);
        colorInfoContentValues.put(COL5, hsv);

        long insertResult = db.insert(COLOR_TABLE_NAME, null, colorInfoContentValues);
        Log.d(TAG_COLOR, "addColorInfoData: id of inserted color = " + insertResult);

        cursor.close();
        return insertResult;
    }

    /**
     * CALL FOR ADDING NEW PALETTE TO DATABASE (DONE)
     * add a new palette to the palette database with its first color
     * @param name of the palette
     * @param id of the color to add to the palette
     * @return true if successful, false otherwise
     */
    public boolean addNewPalette(String name, String id) {
        db = this.getWritableDatabase();
        ContentValues paletteInfoContentValues = new ContentValues();
        paletteInfoContentValues.put(PAL2, name);
        paletteInfoContentValues.put(PAL3, " ");

        Log.d(TAG_PALETTE, "addNewPalette: adding new palette " + name);
        long insertResult = db.insert(PALETTE_TABLE_NAME, null, paletteInfoContentValues);
        Log.d(TAG_PALETTE, "addNewPalette: id of new palette = " + insertResult);

        if (insertResult == -1) {
            return false;
        } else {
            Log.d(TAG_PALETTE, "addNewPalette: insertResult = " + insertResult);
            return addColorToPalette(Long.toString(insertResult),id);
        }
    }

    /**
     * ADD GIVEN MYPALETTE OBJECT TO DATABASE AS A NEW PALETTE (WIP)
     * add the given MyPalette data to the database as a *new* palette. Currently for undoing palette deletion
     * @param palette MyPalette object of the palette to be added to the database, this palette should not already exist in the database
     * @return true if successful, false otherwise
     */
    public boolean addPreExistingPalette(MyPalette palette) {
        db = this.getWritableDatabase();
        //add empty palette to the database
        ContentValues paletteCVs = new ContentValues();
        paletteCVs.put(PAL1, palette.getId());
        paletteCVs.put(PAL2, palette.getName());
        paletteCVs.put(PAL3, " ");

        Log.d(TAG_PALETTE, "addPreExistingPalette: adding new palette " + palette.getId() + palette.getName());
        long insertResult = db.insert(PALETTE_TABLE_NAME, null, paletteCVs);
        Log.d(TAG_PALETTE, "addPreExistingPalette: id of new palette = " + insertResult);

        //returns the id of the newly added item if successful
        if (insertResult == -1) {
            return false;
        } else {
            Log.d(TAG_PALETTE, "addPreExistingPalette: insertResult = " + insertResult);
            for(MyColor color : palette.getColors()) {
                Long colorId = addColorInfoData(color.getName(),color.getHex(),color.getRgb(),color.getHsv());
                addColorToPalette(Long.toString(insertResult),Long.toString(colorId));
            }
            return true;
        }
    }

    //TODO: Might be used for harmony saving, but could probably be replaced with the above depending on implementation
    public boolean addPalette(MyPalette palette) {
        db = this.getWritableDatabase();
        //add empty palette to the database
        ContentValues paletteCVs = new ContentValues();
        paletteCVs.put(PAL2, palette.getName());
        paletteCVs.put(PAL3, " ");

        Log.d(TAG_PALETTE, "addPalette: adding new palette " + palette.getId() + palette.getName());
        long insertResult = db.insert(PALETTE_TABLE_NAME, null, paletteCVs);
        Log.d(TAG_PALETTE, "addPalette: id of new palette = " + insertResult);

        //returns the id of the newly added item if successful
        if (insertResult == -1) {
            return false;
        } else {
            Log.d(TAG_PALETTE, "addPalette: insertResult = " + insertResult);
            for(MyColor color : palette.getColors()) {
                Long colorId = addColorInfoData(color.getName(),color.getHex(),color.getRgb(),color.getHsv());
                addColorToPalette(Long.toString(insertResult),Long.toString(colorId));
            }
            return true;
        }
    }

    //Based on doesPaletteHaveColor

    /**
     * Simply gets the number of colors in a given palette.
     *
     * @param paletteId
     * @return Number of colors in the palette
     * @author Dustin
     */
    //TODO undo save bookmark if it didn't go? Eh. Minor issue. Could just make a toast? Current toast claims color already exists, fix that.
    public int numColorsInPalette(String paletteId){
        db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + PALETTE_TABLE_NAME
                + " WHERE ID = \'" + paletteId + "\'";
        Cursor paletteData = db.rawQuery(selectQuery, null);
        Log.d(TAG_PALETTE,"numColorsInPalette: Results = " + paletteData + " " + (paletteData.moveToFirst()));
        int numColors = 0;
        if(paletteData != null){
            //TODO is this anywhere else? Or can I do it by column name?
            final int COLOR_ID_INDEX = 2;
            String colorIDs = paletteData.getString(COLOR_ID_INDEX);
            //Split on whitespace
            //https://stackoverflow.com/questions/7899525/how-to-split-a-string-by-space
            numColors = colorIDs.trim().split("\\s+").length;
            Log.d("I102", "pd had "+paletteData.getString(COLOR_ID_INDEX));
        }
        Log.d("I102", "palette had "+numColors+" colors");
        return numColors;
    }

    /**
     * CALL FOR ADDING COLOR TO PALETTE (CHECKS IF COLOR ALREADY EXISTS IN PALETTE) (DONE)
     * add a color existing in the color database to an existing palette if not already in the palette
     * @param paletteId id of the palette to be added to
     * @param colorId id of the color to add to the palette
     * @return true if update is success, false if update is failure
     */
    public boolean addColorToPalette(String paletteId, String colorId) {
        db = this.getWritableDatabase();
        Log.d(TAG_PALETTE, "addPaletteInfoData: id of color adding to new palette = " + colorId);
        //Check if the color is already in the palette or if the palette is full
        if(doesPaletteHaveColor(paletteId,colorId)){
            Log.d(TAG_PALETTE, "addPaletteInfoData: color already existed");
            return false;
        } else if (numColorsInPalette(paletteId) >= MAX_COLORS_PER_PALETTE){
            Log.d(TAG_PALETTE, "addPaletteInfoData: palette full");
            return false;
        }
        //update the palette ref string to include the new color id
        String paletteColors = getPaletteColors(paletteId);
        Log.d(TAG_PALETTE, "addColorToPalette: paletteColors before = " + paletteColors);
        //each color should ALWAYS have a space before it for searching
        String newPaletteColors = paletteColors.concat(colorId + " ");
        String updateQuery = "UPDATE " + PALETTE_TABLE_NAME
                + " SET REF = \'" + newPaletteColors + "\'"
                + " WHERE ID = \'" + paletteId + "\'";
        try {
            db.execSQL(updateQuery);
            Log.d(TAG_PALETTE, "addColorToPalette: paletteColors after = " + paletteColors);
            return true;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * FOR DELETING A PALETTE FROM THE DATABASE (WIP)
     * deletes a palette from the database given the palette's unique id
     * @param paletteId the id of the palette to be deleted from the database
     * @return true if deletion was a success, false otherwise
     */
    public boolean deletePalette(String paletteId) {
        db = this.getWritableDatabase();

        String deleteQuery = "DELETE FROM " + PALETTE_TABLE_NAME
                + " WHERE ID = \'" + paletteId + "\'";
        try {
            db.execSQL(deleteQuery);
            Log.d(TAG_PALETTE, "deletePalette: palette id " + paletteId + " has been deleted");
            return true;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * CURSOR METHODS:
     */

    /**
     * POINT CURSOR TO TOP OF COLOR DATABASE (DONE)
     * allows access to cursor for entire color database access
     * REMEMBER TO CLOSE THE CURSOR IF YOU USE
     * @return cursor at top of color database
     */
    public Cursor getColorDatabaseCursor() {
        db = this.getWritableDatabase();
        String selectAllQuery = "SELECT * FROM " + COLOR_TABLE_NAME;
        Cursor colorData = db.rawQuery(selectAllQuery, null);
        colorData.moveToFirst();
        return colorData;
    }

    /**
     * POINT CURSOR TO TOP OF PALETTE DATABASE (DONE)
     * allows access to cursor for entire palette database access
     * REMEMBER TO CLOSE THE CURSOR IF YOU USE
     * @return cursor at top of palette database
     */
    public Cursor getPaletteDatabaseCursor() {
        db = this.getWritableDatabase();
        String selectAllQuery = "SELECT * FROM " + PALETTE_TABLE_NAME;
        Cursor paletteData = db.rawQuery(selectAllQuery, null);
        paletteData.moveToFirst();
        return paletteData;
    }

    /**
     * RETRIEVE CURSOR FOR A SPECIFIC COLOR BY IT'S UNIQUE DATABASE ID (DONE)
     * searches the database for a specific color by id
     * REMEMBER TO CLOSE THE CURSOR IF YOU USE
     * @param id of the color to be searched for in the database
     * @return the cursor for the query results
     */
    public Cursor getColorInfoById(String id) {
        db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + COLOR_TABLE_NAME
                + " WHERE ID = \'" + id + "\'";
        Cursor colorData = db.rawQuery(selectQuery, null);
        colorData.moveToFirst();
        return colorData;
    }

    /**
     * RETRIEVE CURSOR FOR A SPECIFIC PALETTE BY IT'S UNIQUE DATABASE ID (DONE)
     * searches the database for a specific palette by id
     * REMEMBER TO CLOSE THE CURSOR IF YOU USE
     * @param id of the palette to be searched for in the database
     * @return the cursor for the query results
     */
    public Cursor getPaletteInfoById(String id) {
        db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + PALETTE_TABLE_NAME
                + " WHERE ID = \'" + id + "\'";
        Cursor paletteData = db.rawQuery(selectQuery, null);
        paletteData.moveToFirst();
        return paletteData;
    }

    /**
     * SEARCHING/CHECKING METHODS:
     */

    /**
     * FOR CHECKING IF A COLOR ALREADY EXISTS IN COLOR DATABASE (DONE)
     * THIS MUST BE ITS OWN METHOD WITH CURSOR RETURN
     * searches the color database for all colors with the given HEX within a palette
     * REMEMBER TO CLOSE THE CURSOR IF YOU USE
     * @param hex the Hex to be searched for in the database
     * @return the cursor for the query results
     */
    public Cursor getColorInfoByHex(String hex) {
        db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + COLOR_TABLE_NAME
                + " WHERE HEX = \'" + hex + "\'";
        Cursor colorData = db.rawQuery(selectQuery, null);
        colorData.moveToFirst();
        return colorData;
    }

    /**
     * CHECK IF A PALETTE CONTAINS A COLOR IN ITS REF STRING (DONE)
     * checks if the given color exists in the given palette
     * @param paletteId for accessing the palette
     * @param colorId to be checked for in the palette's ref string
     * @return boolean based on whether the colorId exists in the ref string of the palette
     */
    public boolean doesPaletteHaveColor(String paletteId, String colorId) {
        db = this.getWritableDatabase();
        Log.d(TAG_PALETTE,"doesPaletteHaveColor: Searching palette " + paletteId + " for " + colorId);
        String selectQuery = "SELECT * FROM " + PALETTE_TABLE_NAME
                + " WHERE ID = \'" + paletteId + "\'"
                + " AND REF LIKE \'% " + colorId + " %\'";
        Cursor paletteData = db.rawQuery(selectQuery, null);
        Log.d(TAG_PALETTE,"doesPaletteHaveColor: Results = " + paletteData + " " + (paletteData.moveToFirst()));
        return (paletteData.moveToFirst());
    }

    /**
     * FOR SEARCH BAR FEATURE IN PALETTE FRAGMENT (DONE)
     * retrieve a palette by its user-given name or hex
     * REMEMBER TO CLOSE THE CURSOR IF YOU USE
     * @param input query for name of the palette that the user is searching for
     * @return cursor pointing at any matching results
     */
    public Cursor searchPalettesByName(String input) {
        db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + PALETTE_TABLE_NAME
                + " WHERE NAME LIKE \'%" + input + "%\'";
        Cursor paletteData = db.rawQuery(selectQuery, null);
        paletteData.moveToFirst();
        return paletteData;
    }

    /**
     * FOR SEARCH BAR FEATURE IN PALETTE FRAGMENT (DONE)
     * REMEMBER TO CLOSE THE CURSOR IF YOU USE
     * @param input hex query for name of the palette that the user is searching for
     * @return cursor pointing at any matching results
     */
    public Cursor searchPalettesByHex(String input) {
        db = this.getWritableDatabase();
        //Search color database to see if HEX exists anywhere
        String selectQuery = "SELECT * FROM " + COLOR_TABLE_NAME
                + " WHERE HEX LIKE \'" + input + "%\'";
        Cursor colorData = db.rawQuery(selectQuery, null);
        Log.d(TAG_PALETTE, "searchPalettesByHex: colorData count = " + colorData.getCount());
        //Get id if any, otherwise return empty cursor
        if(colorData.moveToFirst()) {
            String colorId = colorData.getString(0);
            selectQuery = "SELECT * FROM " + PALETTE_TABLE_NAME
                    + " WHERE REF LIKE \'% " + colorId + " %\' ";
            while(colorData.moveToNext()) {
                colorId = colorData.getString(0);
                selectQuery = selectQuery.concat("OR REF LIKE \'% " + colorId + " %\'");
            }
            Log.d(TAG_PALETTE, "searchPalettesByHex: final query = " + selectQuery);
            Cursor paletteData = db.rawQuery(selectQuery, null);
            paletteData.moveToFirst();
            colorData.close();
            return paletteData;
        } else {
            return colorData;
        }
    }

    /**
     * ARRAYLIST HELPER METHODS:
     */

    /**
     * GET AN ARRAYLIST OF MYCOLOR OBJECTS FROM A PALETTE (DONE)
     * retrieves a usable arraylist of colors from a given palette
     * @param id of the palette to get the color list from
     * @param isReversed determines whether to retrieve the list in reverse or not
     * @return ArrayList of MyColor objects
     */
    public ArrayList<MyColor> getColorList(String id, boolean isReversed) {
        ArrayList<MyColor> colorList = new ArrayList<>();
        //get string ref of color ids in given palette
        String colorIds = getPaletteColors(id).trim();
        //return empty list if palette is empty
        if(colorIds.equals("")) {
            return colorList;
        }
        String[] splitIds = colorIds.split("\\s+");
        //get each color by id, add as MyColor object to arraylist
        Log.d(TAG_COLOR, "getColorList: colorIds = " + colorIds + ", # of colors = " + splitIds.length);
        if(isReversed) {
            for(int i = splitIds.length - 1; i >= 0; i--) {
                String colorId = splitIds[i];
                Cursor cursor = getColorInfoById(colorId);
                String name = cursor.getString(1);
                String hex = cursor.getString(2);
                String rgb = cursor.getString(3);
                String hsv = cursor.getString(4);
                colorList.add(new MyColor(colorId,name,hex,rgb,hsv));
                cursor.close();
            }
        } else {
            for(int i = 0; i < splitIds.length; i++) {
                String colorId = splitIds[i];
                Cursor cursor = getColorInfoById(colorId);
                String name = cursor.getString(1);
                String hex = cursor.getString(2);
                String rgb = cursor.getString(3);
                String hsv = cursor.getString(4);
                colorList.add(new MyColor(colorId,name,hex,rgb,hsv));
                cursor.close();
            }
        }

        return colorList;
    }

    /**
     * GET ARRAYLIST OF PALETTES AS MYPALETTE OBJECTS (DONE)
     * get all user made palettes (aka all palettes excluding Saved Colors (id = 1)
     * @param cursor pointing to the set of palettes to be put into the arraylist
     * @return arraylist of palette objects for recycler display
     */
    public ArrayList<MyPalette> getPaletteList(Cursor cursor) {
        ArrayList<MyPalette> paletteList = new ArrayList<>();
        String paletteId;
        String paletteName;
        ArrayList<MyColor> colorList;

        if(cursor.moveToFirst()){ //check that cursor isn't empty
            paletteId = cursor.getString(0);
            if(!paletteId.equals("1")) { //do not count Saved Colors as a palette
                paletteName = cursor.getString(1);
                colorList = getColorList(paletteId, false);
                paletteList.add(new MyPalette(paletteId, paletteName, colorList));
            }
            while(cursor.moveToNext()) {
                paletteId = cursor.getString(0);
                if(!paletteId.equals("1")) { //do not count Saved Colors as a palette
                    paletteName = cursor.getString(1);
                    colorList = getColorList(paletteId, false);
                    paletteList.add(new MyPalette(paletteId, paletteName, colorList));
                }
            }
        }
        cursor.close();
        return paletteList;
    }

    /**
     * UPDATE PALETTE DATABASE AND EXISTING COLORS IN PALETTE USING MYCOLORS LIST (DONE)
     * updates the database ref string of the given MyColors list of a palette
     * 1used for deletion from/reinsertion into palette
     * @param id the id of the palette to be updated
     * @param colors the MyColors arraylist of the palette that needs to be updated in the database
     * @param isReversed determines whether the ref string should be updated in reverse order or not
     * @return boolean of whether the database update was successful
     */
    public boolean updateRefString(String id, ArrayList<MyColor> colors, boolean isReversed){
        db = this.getWritableDatabase();
        //create new ref string
        String newRef = getRefString(colors, isReversed);
        //update database with new ref string
        String updateQuery = "UPDATE " + PALETTE_TABLE_NAME
                + " SET REF = \'" + newRef + "\'"
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
     * OTHER PALETTE UTILITY METHODS:
     */

    /**
     * RENAME A PALETTE (DONE)
     * method for renaming an existing palette that isn't Saved Colors
     * @param id the id of the palette whose name will be changed
     * @param newName the new name to assign the palette
     * @return boolean to indicate success of the executed SQL update query
     */
    public boolean changePaletteName(String id, String newName) {
        db = this.getWritableDatabase();
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
        db = this.getWritableDatabase();
        //query for ref string only for specific palette by id
        String selectQuery = "SELECT REF FROM " + PALETTE_TABLE_NAME
                + " WHERE ID = \'" + id + "\'";

        //get the cursor from the query
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()){
            //get the string result from the query
            result = cursor.getString(0);
        }

        cursor.close();
        return result;
    }

    /**
     * GETS REF STRING OF A PALETTE FROM MYCOLORS LIST (DONE)
     * makes the ref string for the palette based on the given arraylist of MyColor objects
     * @param colors arraylist of MyColor objects
     * @param isReversed determines whether the the arraylist should be processed in reverse or not
     * @return the ref string for the given palette
     */
    public String getRefString(ArrayList<MyColor> colors, boolean isReversed) {
        String ref = " ";

        if(isReversed){
            for (int i = colors.size() - 1; i >= 0; i--) {
                ref = ref.concat(colors.get(i).getId() + " ");
            }
        } else {
            for (MyColor color : colors) {
                ref = ref.concat(color.getId() + " ");
            }
        }

        return ref;
    }
}
