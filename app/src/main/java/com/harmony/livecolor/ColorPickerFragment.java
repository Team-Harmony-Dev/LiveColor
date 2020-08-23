package com.harmony.livecolor;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.InputStream;

import static android.content.Context.MODE_PRIVATE;
import static android.graphics.Color.RGBToHSV;
import static android.graphics.Color.blue;
import static android.graphics.Color.green;
import static android.graphics.Color.red;

/**
 * Interface for callback for filling in save button iff
 *   a save actually happened after opening CustomDialog.
 *
 * https://stackoverflow.com/questions/18279302/how-do-i-perform-a-java-callback-between-classes
 */
interface SaveListener{
    void saveHappened();
}

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ColorPickerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ColorPickerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ColorPickerFragment extends Fragment implements SaveListener {

    private boolean isColorSaved = false;
    int colorT;
    String colorNamePass;

    TextView editName, editHex, editRgb, editHsv;
    ColorDatabase colorDB;
    FloatingActionButton add;
    ScaleAnimation scaleAnimation;


    private OnFragmentInteractionListener mListener;
    private static final int CAMERA_OR_GALLERY = 0;
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    //Text displayed as color name if you click on the background
    private static final String BACKGROUND_COLOR_TEXT = "Background";
    private final static double MAX_TEXTVIEW_WIDTH_PERCENT = 0.60;
    //Font size in sp
    private final static float MAX_FONT_SIZE = 30;
    private String imagePath = null;
    private ImageView pickingImage;
    private Uri imageUri;

    //For the save button animation/color fill
    private ImageView saveButtonCB;
    //Can delete this if we want the animation to always happen when tapping save button, rather than only on actual save.
    private View viewCB;

    public ColorPickerFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ColorPickerFragment newInstance() {
        ColorPickerFragment fragment = new ColorPickerFragment();
        //Bundle args = new Bundle();
        //args can be bundled and sent through here if needed
        //fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //if arguments are needed ever, use this to set them to static values in the class
        }
    }

    //Color the save button in if the save occurred (wasn't cancelled)
    public void saveHappened(){
        //Actually this should probably happen on button press, not tied to color. Probably. Maybe. Can add back if we want it here.
//        scaleAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f);
//        scaleAnimation.setDuration(500);
//        BounceInterpolator bounceInterpolator = new BounceInterpolator();
//        scaleAnimation.setInterpolator(bounceInterpolator);
//        viewCB.startAnimation(scaleAnimation);

        saveButtonCB.setImageResource(R.drawable.bookmark_selected);
        saveButtonCB.setColorFilter(colorT);

        final boolean ONLY_SAVE_ONCE_PER_COLOR = false;
        if(ONLY_SAVE_ONCE_PER_COLOR) {
            isColorSaved = true;
        }
        //Log.d("V2S2 bugfix", "callback saveColorB="+saveColorB);

        Log.d("V2S2 bugfix", "Got callback (save happened). isColorSaved="+isColorSaved+" colorT="+colorT);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("Lifecycles", "onCreateView: ColorPickerFragment created");

        //Set title on action bar to match current fragment
        getActivity().setTitle(
                getResources().getText(R.string.app_name) +
                        " - " + getResources().getText(R.string.title_color_picker)
        );



        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_color_picker, container, false);

        colorDB = new ColorDatabase(getActivity());

        editName = rootView.findViewById(R.id.colorName);
        editHex = rootView.findViewById(R.id.plainHex);
        editRgb = rootView.findViewById(R.id.plainRgb);
        editHsv = rootView.findViewById(R.id.plainHsv);

        //"Add" is the button you press for uploading or taking a picture. Refactor?
        add = rootView.findViewById(R.id.addButton);
        add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(getActivity(), view);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.camera:
                                ContentValues values = new ContentValues();
                                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                                values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
                                imageUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_OR_GALLERY);
                                }
                                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
                                return true;
                            case R.id.gallery:
                                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_OR_GALLERY);
                                }
                                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                galleryIntent.setType("image/*");
                                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
                                return true;
                            default:
                                return false;
                        }
                    }

                });
                popup.inflate(R.menu.add_menu);
                popup.show();
            }
        });


        scaleAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f);
        scaleAnimation.setDuration(500);
        BounceInterpolator bounceInterpolator = new BounceInterpolator();
        scaleAnimation.setInterpolator(bounceInterpolator);

        final ImageButton saveColorB = rootView.findViewById(R.id.saveButton);

        Log.d("V2S2 bugfix", "This="+(this).getClass().getName());
        final ColorPickerFragment callbackToHere = this;

        saveColorB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editName.getText().toString();
                String hex = editHex.getText().toString();
                String rgb = editRgb.getText().toString();
                String hsv = editHsv.getText().toString();

                //Animate each time they clicked, even if it was already pressed?
                //If we do, we should probably have a notification saying why nothing is happening.
                //But it's totally reasonable that they'd want to save again to a different place. Should probably allow that.
                view.startAnimation(scaleAnimation);

                if(!isColorSaved){
                    saveButtonCB = saveColorB;
                    CustomDialog pickerDialog = new CustomDialog(getActivity(),name,hex,rgb,hsv);
                    //We'll get a callback if they did save the color (to tell us we need to fill in the save button)
                    pickerDialog.addListener(callbackToHere);
                    pickerDialog.showSaveDialog();
                }
            }
        });

        final ImageButton infoColorB = rootView.findViewById(R.id.infoButton);
        infoColorB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view){
                //updateColorName(getView());
                Intent startCIA = new Intent(getActivity(), ColorInfoActivity.class);
                startActivity(startCIA);
            }
        });

        final ImageButton editColorB = (ImageButton) rootView.findViewById(R.id.editButton);
        editColorB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view){
                //updateColorName(getView());
                Intent startEditColorActivity = new Intent(getActivity(), EditColorActivity.class);
                startActivity(startEditColorActivity);
            }
        });

        //onClickListener for
        pickingImage = rootView.findViewById(R.id.pickingImage);
        if (pickingImage != null) { // loads saved image to fragment using path
            SharedPreferences prefs = getContext().getSharedPreferences("prefs", MODE_PRIVATE);
            imagePath = prefs.getString("image", null);
            pickingImage.setImageURI(imageUri);
            if(imagePath != null) {
                Drawable drawable = Drawable.createFromPath(imagePath);
                pickingImage.setImageDrawable(drawable);
            }
        }
        //Adds a listener to get the x and y coordinates of taps and update the display
        pickingImage.setOnTouchListener(handleTouch);
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == RESULT_LOAD_IMAGE && data != null) {
                imageUri = data.getData(); // only needed for gallery images
            }
            pickingImage.setImageURI(imageUri); // updates the view
            imagePath = getPath(data, this.getActivity());
            if (imagePath != null) {
                // save image path to saved prefs after updating imageview
                SharedPreferences.Editor editor = getContext().getSharedPreferences("prefs", MODE_PRIVATE).edit();
                editor.putString("image", imagePath);
                editor.apply();
            }
        }
    }
    // gets path of image to save to fragment
    public String getPath(Intent data, Context context) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(imageUri, projection, null, null, null);
        if (cursor == null) return null;
        cursor.moveToFirst();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        imagePath = cursor.getString(column_index);
        cursor.close();
        return imagePath;
    }

    /**
     * Used for getting color using coordinates (without annoying math stuff) from the ImageView on ColorPickerFragment
     *
     * @param view The view to get the bitmap from
     * @return A bitmap of what was displayed in the view
     * @author https://stackoverflow.com/a/52905682
     */
    public Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
    /**
     * The background checkered pattern appeared in the bitmap of above. So lets change the background while we grab it.
     * @param view The imageview. TODO yeah this shouldn't accept normal view, probably.
     * @param backgroundColor The background color you want the bitmap to have
     * @param background The background you want it to end up with (checkered, probably)
     * @return
     */
    public Bitmap getBitmapFromViewWithBackground(View view, int backgroundColor, Drawable background) {
        view.setBackgroundColor(backgroundColor);
        Bitmap bitmap = getBitmapFromView(view);
        view.setBackground(background);
        return bitmap;
    }

    /**
     * Change visibility of all three buttons.
     * @param visibility View.INVISIBLE, View.GONE, or View.VISIBLE
     */
    public void changeVisibilityInfoEditSaveButtons(int visibility){
        getActivity().findViewById(R.id.infoButton).setVisibility(visibility);
        getActivity().findViewById(R.id.editButton).setVisibility(visibility);
        getActivity().findViewById(R.id.saveButton).setVisibility(visibility);
    }

    // https://stackoverflow.com/a/39588899
    // For Sprint 2 User Story 2.
    private View.OnTouchListener handleTouch = new View.OnTouchListener() {
        //This is where the color picking happens.
        //User's clicked on the image, we goota take their click coordinates and get the appropriate color, its name, and update info displayed.
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            //Hide the image selection button while dragging.
            if(event.getActionMasked() == MotionEvent.ACTION_MOVE) {
                add.hide();
            }

            com.ortiz.touchview.TouchImageView touchView = getActivity().findViewById(R.id.pickingImage);

            final int BACKGROUND_COLOR = 0;
            //Get color int from said pixel coordinates using the source image. Default to background color.
            int pixel = BACKGROUND_COLOR;
            boolean wasBackgroundPixel = false;
            //We can just get the bitmap of whatever our imageview is displaying, and not need any annoying math.
            //Though since we have the math anyway we might as well use it if we aren't zoomed in? Might be more efficient than making the bitmap.  TODO
            final boolean USE_FILE_BITMAP = true;
            //The || is required because if we zoom in on a rectangular image we might use more of the imageview than was originally valid.
            if(/*wasValidClick ||*/ USE_FILE_BITMAP){
                if(USE_FILE_BITMAP){
                    final Drawable background = ResourcesCompat.getDrawable(getResources(), R.drawable.newtransparent, null);
                    Bitmap view_bitmap = getBitmapFromViewWithBackground(touchView, BACKGROUND_COLOR, background);
                    if(view_bitmap == null){
                        Log.w("DEBUG S2US2 pinchzoom", "Bitmap was null");
                    } else {
                        try {
                            pixel = view_bitmap.getPixel((int) event.getX(), (int) event.getY());
                            //Check if the pixel is a part of the background
                            if(pixel == BACKGROUND_COLOR) {
                                //3 Possible cases for pixels:
                                //1. Pixel is opaque. Always the same no matter the background
                                //2. Pixel is partially transparent. Changes with background, but not exactly equal to the background color.
                                //3. Pixel is fully transparent. Exactly equal to background.
                                //  But the background might be the same color as some pixel actually in the image.
                                //  So by testing with two background colors that the pixel is exactly equal to the background both times, we can tell if this pixel is from the background.
                                //Any not completely transparent color should be fine.
                                final int ARBITRARY_NON_BACKGROUND_COLOR = Color.rgb(100, 100, 100);
                                Bitmap view_bitmap2 = getBitmapFromViewWithBackground(touchView, ARBITRARY_NON_BACKGROUND_COLOR, background);
                                int pixel2 = view_bitmap2.getPixel((int) event.getX(), (int) event.getY());
                                if (pixel2 == ARBITRARY_NON_BACKGROUND_COLOR) {
                                    wasBackgroundPixel = true;
                                }
                            }

                            Log.d("DEBUG S2US2 pinchzoom", "Bitmap was non-null, found bg=" + wasBackgroundPixel + " pixel=" + pixel);
                        } catch (Exception e){
                            //They dragged off the image. I could just check if X and Y are in range, but this should work fine.
                            Log.d("DEBUG S2US2 pinchzoom", "Bitmap was non-null, but had error: " + e);
                        }
                    }
                }
            }

            //TODO this should probably only be set once, or detect something about the image (resolution?) and work based on that when the image is loaded.
            final float MAX_ZOOM_MULT = 100;
            touchView.setMaxZoom(MAX_ZOOM_MULT);

            //TODO clean this up a lot. Make functions for this sort of thing, it will be reused.
            final boolean USE_API_FOR_NAMES = false;

            //Get the color name from an API call
            //It takes a second to load and I don't want to spam the API so we only call it when we release
            if(
                    //It used to be that we wanted to be able to drag around and then update the name once, when we let go, after finding the color we wanted.
                    //event.getActionMasked() == MotionEvent.ACTION_UP
                    //With how it works now, I think we want the updated color when we press down instead of up.
                    //Since we get the updated color on every drag as well, getting it when we let go is a bit redundant.
                    event.getActionMasked() == MotionEvent.ACTION_DOWN
                    //We don't want to spam the API, but local color names are so fast we can just do it live.
                    //If we're zoomed we are panning on drag, so ignore name in that case as well.
                    || (event.getActionMasked() == MotionEvent.ACTION_MOVE && !USE_API_FOR_NAMES && !touchView.isZoomed())) {
                Log.d("S3US5", "Release detected");
                Log.d("DEBUG S2US2 pinchzoom", "action up or drag detected");

                //Update the color info being displayed (the patch of color the user sees, and hex, and save button)
                updateColorValues(view, pixel);
                isColorSaved = false;
                ImageButton saveColorB = (ImageButton) getView().findViewById(R.id.saveButton);
                saveColorB.setImageResource(R.drawable.unsaved);
                saveColorB.setColorFilter(null);
                changeVisibilityInfoEditSaveButtons(View.VISIBLE);

                if(wasBackgroundPixel) {
                    //We don't need a name, we can call it whatever we want to make it clear that it wasn't a real color.
                    TextView viewToUpdateColorName = getActivity().findViewById(R.id.colorName);
                    //Fit the name into the textview
                    ColorNameGetterCSV.setAppropriatelySizedText(viewToUpdateColorName, BACKGROUND_COLOR_TEXT, MAX_TEXTVIEW_WIDTH_PERCENT, MAX_FONT_SIZE);
                    //Remove the buttons
                    changeVisibilityInfoEditSaveButtons(View.INVISIBLE);
                } else if(USE_API_FOR_NAMES) {
                    TextView viewToUpdateColorName = getActivity().findViewById(R.id.colorName);
                    ColorNameGetter.updateViewWithColorName(viewToUpdateColorName, pixel, MAX_TEXTVIEW_WIDTH_PERCENT, MAX_FONT_SIZE);
                } else {
                    //Get the hex, and then name that corresponds to the hex
                    String hex = "#"+colorToHex(pixel);
                    final boolean CHANGE_FONT_SIZE_IF_TOO_LONG = true;
                    if(CHANGE_FONT_SIZE_IF_TOO_LONG) {
                        //Display the name on one line
                        TextView viewToUpdateColorName = getActivity().findViewById(R.id.colorName);
                        ColorNameGetterCSV.getAndFitName(viewToUpdateColorName, hex, MAX_TEXTVIEW_WIDTH_PERCENT, MAX_FONT_SIZE);
                    } else {
                        String colorName = ColorNameGetterCSV.getName(hex);
                        //Display the name
                        TextView viewToUpdateColorName = getActivity().findViewById(R.id.colorName);
                        viewToUpdateColorName.setText(colorName);
                        Log.d("V2S1 colorname", "Hex " + hex + ": " + colorName);
                    }
                }
            } else if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
                //Wipe the color name until we get a new one during drags.
                //((TextView) getActivity().findViewById(R.id.colorName)).setText("");
            }

            //Anything other than dragging should have this reappear afterwards.
            if(event.getActionMasked() != MotionEvent.ACTION_MOVE){
                //I believe this is the button to select an image or take a picture.
                //  It disappears when dragging so it doesn't cover any part of the image.
                //  So after you're done dragging it needs to reappear.
                add.show();
            }
            //The view parameter actually isn't used.
            //Save the new name to storage in case the app closes or fragment switches.
            updateColorName(getView());
            return true;
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //updateColorName(getView()); // saves color name to sharedprefs upon leaving fragment
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.d("Lifecycles", "onViewCreated: View Created for Color Picker Fragment");
        loadColorView();
    }

    public void loadColorView() {
        SharedPreferences prefs = getContext().getSharedPreferences("prefs", MODE_PRIVATE);
        int savedColorInt = prefs.getInt("colorValue", Color.WHITE);
        String savedColorName = prefs.getString("colorName", null);
        // loads saved name, if it exists
        if(savedColorName != null) {
            TextView view = getActivity().findViewById(R.id.colorName);
            if(savedColorName.equals(BACKGROUND_COLOR_TEXT)){
                //Hide the buttons iff it was a background color
                changeVisibilityInfoEditSaveButtons(View.INVISIBLE);
            }
            ColorNameGetterCSV.setAppropriatelySizedText(view, savedColorName, MAX_TEXTVIEW_WIDTH_PERCENT, MAX_FONT_SIZE);
        }
        updateColorValues(getView(), savedColorInt);
    }

    //This function actually saves, not loads
    public void updateColorName(View view){
        SharedPreferences preferences = this.getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        TextView colorNameView = getActivity().findViewById(R.id.colorName);
        String input = colorNameView.getText().toString();
        Log.d("S3US5", "saved colorname input="+input);
        editor.putString("colorName", input);
        editor.apply();
    }

    public void updateColorValues(View view, int colorNew){
        Log.d("DEBUG", "updateColorValues: called");
        Log.d("DEBUG", "updateColorValues: color int = " + colorNew);

        colorT = colorNew;

        //fetch the color components from colorNew
        int RV = Color.red(colorNew);
        int GV = Color.green(colorNew);
        int BV = Color.blue(colorNew);

        //update the RGB value displayed
        String rgb = String.format("(%1$d, %2$d, %3$d)",RV,GV,BV);
        String fullRGB = String.format("RGB: %1$s",rgb);  //add "RGB: " and rgb together
        Log.d("DEBUG", "updateColorValues: fullRGB = " + fullRGB);

        TextView plainRgbDisplay = getActivity().findViewById(R.id.plainRgb);
        TextView rgbDisplay = getActivity().findViewById(R.id.RGBText);//get the textview that displays the RGB value
        plainRgbDisplay.setText(rgb);
        rgbDisplay.setText(fullRGB); //set the textview to the new RGB: rgbvalue

        //update the HEX value
        String hexValue = String.format("#%06X", (0xFFFFFF & colorNew)); //get the hex representation minus the first ff
        String fullHEX = String.format("HEX: %1$s",hexValue);
        Log.d("DEBUG", "updateColorValues: fullHEX = " + fullHEX);
        TextView plainHexDisplay = getActivity().findViewById(R.id.plainHex);
        TextView hexDisplay = getActivity().findViewById(R.id.HEXText);
        plainHexDisplay.setText(hexValue);
        hexDisplay.setText(fullHEX);

        //update the HSV value
        float[] hsvArray = new float[3];
        RGBToHSV(RV,GV,BV,hsvArray);
        int hue = Math.round(hsvArray[0]);
        String plainHSV = String.format("(%1$d, %2$.3f, %3$.3f)",hue,hsvArray[1],hsvArray[2]);
        String fullHSV = String.format("HSV: (%1$d, %2$.3f, %3$.3f)",hue,hsvArray[1],hsvArray[2]);
        TextView plainHsvDisplay = getActivity().findViewById(R.id.plainHsv);
        TextView hsvDisplay = getActivity().findViewById(R.id.HSVText);
        plainHsvDisplay.setText(plainHSV);
        hsvDisplay.setText(fullHSV);


        //Update the color display with the color they've chosen, ignoring transparency.
        ImageView colorDisplay = getActivity().findViewById(R.id.pickedColorDisplayView);
        // https://stackoverflow.com/a/7741300
        final int TRANSPARENT = 0xFF000000;
        colorNew = colorNew | TRANSPARENT;
        colorDisplay.setBackgroundColor(colorNew);

        // save color value (int) to Shared Prefs.
        SharedPreferences.Editor editor = getContext().getSharedPreferences("prefs", MODE_PRIVATE).edit();
        editor.putInt("colorValue", colorNew);
        editor.apply();

        //Put the color in SharedPreferences as a String with key nameKey
        editor.putString("colorString", Integer.toString(colorNew));
        editor.apply();
    }

    //Takes a pixel color, returns the hex
    //Ignores transparency.
    public static String colorToHex(int color){
        return String.format("%06X", (0xFFFFFF & color));
    }
}

