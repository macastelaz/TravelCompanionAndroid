package com.castelcode.travelcompanion.log_entry_add_activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.castelcode.travelcompanion.R;
import com.castelcode.travelcompanion.utils.DateStringUtil;
import com.castelcode.travelcompanion.utils.ScalingUtilities;
import com.castelcode.travelcompanion.utils.TimeStringUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class AddLogEntry extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "ADD_LOG_ENTRY";

    private static final int SELECT_PICTURE = 1;
    private static final int TAKE_PICTURE = 0;

    private EditText logTextEditText;
    private FloatingActionButton confirmButton;
    FloatingActionButton deleteButton;
    private Button dateSelectButton;
    private Button timeSelectButton;
    ArrayList<ImageButton> imageButtonArrayList;
    private static ArrayList<Bitmap> bitmaps;
    private static ArrayList<LogEntryImage> images;
    ImageButton imageButton;

    LogEntry entryIn;

    Uri currentUri;

    AppCompatActivity activity;
    String mCurrentPhotoPath;
    String mCurrentPhotoName;
    int currentImageIndex = -1;
    boolean replaceImageFlow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_log_entry);
        imageButtonArrayList = new ArrayList<>();
        images = new ArrayList<>();
        bitmaps = new ArrayList<>();

        activity = this;

        logTextEditText = findViewById(R.id.edit_log_text);
        logTextEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        logTextEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);
        confirmButton = findViewById(R.id.confirm_button);
        deleteButton = findViewById(R.id.delete_button);
        dateSelectButton = findViewById(R.id.DateSelectButton);
        timeSelectButton = findViewById(R.id.TimeSelectButton);

        dateSelectButton.setOnClickListener(this);
        timeSelectButton.setOnClickListener(this);
        confirmButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);

        imageButton = findViewById(R.id.image_button);
        imageButton.setOnClickListener(this);
        imageButtonArrayList.add(imageButton);

        Intent intent = getIntent();
        LogEntry entry = (LogEntry) intent.getSerializableExtra(
                getResources().getString(R.string.log_entry));
        if(entry != null){
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(this);
            logTextEditText.setText(entry.getTextEntry());
            dateSelectButton.setText(entry.getDateString());
            timeSelectButton.setText(entry.getTimeString());
            images = entry.getImages();
            ContextWrapper cw = new ContextWrapper(this);
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            for (int i = 0; i < images.size(); i++) {
                // We have one image button on create.
                currentImageIndex = i;
                if (i != 0) {
                    maybeAddNewImagePicker();
                }
                File myPath = new File(directory, images.get(i).getPhotoName());
                Bitmap bitmap;
                try {
                    bitmap = BitmapFactory.decodeStream(new FileInputStream(myPath));
                }
                catch (Exception ex){
                    bitmap = BitmapFactory.decodeResource(getResources(),
                            android.R.drawable.ic_menu_camera);
                }
                if (bitmap != null) {
                    bitmaps.add(i, bitmap);
                    Drawable drawableToUse = new BitmapDrawable(getResources(), bitmap);
                    Bitmap icon = BitmapFactory.decodeResource(getResources(),
                            android.R.drawable.ic_menu_camera);
                    if (bitmap.sameAs(icon)) {
                        imageButton.setImageDrawable(drawableToUse);
                    } else {
                        handleDrawing(drawableToUse);
                    }
                    if (i == images.size() - 1) {
                        maybeAddNewImagePicker();
                    }
                }
            }
        }
        else{
            currentImageIndex = 0;
            deleteButton.setVisibility(View.INVISIBLE);
            deleteButton.setOnClickListener(null);
        }
        entryIn = entry;
    }

    private void galleryAddPic() {
        try {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File(mCurrentPhotoPath);
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);
        } catch (Exception ex) {
            System.out.println("Failed to add picture to gallery with ex: " + ex.toString());
        }
    }

    private static void saveBitmapsToInternalStorage(Context context){
        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir

        for (int i = 0; i < bitmaps.size(); i++) {
            File mypath = new File(directory, images.get(i).getPhotoName());

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(mypath);

                Bitmap scaledBitmap = ScalingUtilities.createScaledBitmap(bitmaps.get(i),
                        ScalingUtilities.ScalingLogic.FIT);
                bitmaps.get(i).recycle();
                // "RECREATE" THE NEW BITMAP

                // Use the compress method on the BitMap object to write image to the OutputStream
                scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    assert fos != null;
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        }
    }

    private static boolean processImages(ProcessImageParamContainer params){
        String logText = params.getEditText().getText().toString();
        String date = params.getDateSelectButton().getText().toString();
        String time = params.getTimeSelectButton().getText().toString();
        if(!logText.equals(""))
        {
            LogEntry entry = new LogEntry(logText, date, time, images);

            saveBitmapsToInternalStorage(params.getContext());

            Intent resultIntent = new Intent();
            resultIntent.putExtra(params.getActivity().getResources().getString(R.string.log_entry),
                    entry);
            params.getActivity().setResult(Activity.RESULT_OK, resultIntent);
            params.getActivity().finish();
            return true;
        }
        else {
            return false;
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e(TAG, ex.toString());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.castelcode.travelcompanion.myfileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, TAKE_PICTURE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v == timeSelectButton) {
            Calendar c = Calendar.getInstance();
            createTimePickerDialog(timeSelectButton, "Time for Log Entry",
                    "Confirm", "Cancel",
                    TimeStringUtil.createTimeString(c.get(Calendar.HOUR_OF_DAY),
                            c.get(Calendar.MINUTE)));
        }
        else if(v == dateSelectButton) {
            Calendar c = Calendar.getInstance();
            createDatePickerDialog(dateSelectButton, "Date for Log Entry",
                    "Confirm", "Cancel",
                    DateStringUtil.intToDateString(c.get(Calendar.MONTH),
                            c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.YEAR)));
        }
        else if(v == confirmButton){
            confirmButton.setEnabled(false);
            ProcessImageParamContainer params = new ProcessImageParamContainer(activity,
                    getApplicationContext(), logTextEditText, confirmButton, dateSelectButton,
                    timeSelectButton);
            new proccessImageInBackground().execute(params);
        }
        else if(v == deleteButton){
            Log.d("TEST", "DELETE");
            Intent resultIntent = new Intent();
            resultIntent.putExtra(getResources().getString(R.string.delete), true);
            resultIntent.putExtra(getResources().getString(R.string.log_entry), entryIn);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
        else {
            for (int i = 0; i < imageButtonArrayList.size(); i++) {
                if (v == imageButtonArrayList.get(i)) {
                    currentImageIndex = i;
                    if (bitmaps.size() > i) {
                        bitmaps.remove(i);
                        images.remove(i);
                        replaceImageFlow = true;
                    } else {
                        replaceImageFlow = false;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(R.string.image_picker_alert_message)
                            .setPositiveButton(R.string.new_photo,
                                    (DialogInterface dialog, int which) -> {
                                        //Launch Camera Intent
                                        if (shouldAskPermissions()) {
                                            askPermissions();
                                        }
                                        dispatchTakePictureIntent();
                                    })
                            .setNegativeButton(R.string.existing_photo,
                                    (DialogInterface dialog, int which) -> {
                                        //Lauch photo picker intent
                                        Intent intent = new Intent();
                                        intent.setType("image/*");
                                        intent.setAction(Intent.ACTION_GET_CONTENT);
                                        startActivityForResult(Intent.createChooser(intent,
                                                "Select Picture"), SELECT_PICTURE);
                                    });
                    builder.create().show();
                    break;
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void handleDrawing(Drawable drawItem){
        int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            imageButtonArrayList.get(currentImageIndex).setBackgroundDrawable(drawItem);
        } else {
            imageButtonArrayList.get(currentImageIndex).setBackground(drawItem);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                currentUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),
                            currentUri);
                    bitmaps.add(currentImageIndex, bitmap);
                    images.add(currentImageIndex, new LogEntryImage(currentUri.getPath(),
                            getFileName(currentUri)));
                    BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
                    handleDrawing(drawable);
                    maybeAddNewImagePicker();
                } catch (IOException ex) {
                    //......
                    Log.e("TEST", "EXCEPTION " + ex.toString());
                }

            }
            if (requestCode == TAKE_PICTURE) {
                galleryAddPic();
                Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                bitmaps.add(currentImageIndex, bitmap);
                images.add(currentImageIndex,
                        new LogEntryImage(mCurrentPhotoPath, mCurrentPhotoName));
                BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
                handleDrawing(drawable);
                maybeAddNewImagePicker();
            }
        }
    }

    private void maybeAddNewImagePicker() {
        if (!replaceImageFlow) {
            LinearLayout imageContainer = findViewById(R.id.image_container);
            ImageButton imageButton = new ImageButton(AddLogEntry.this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,150,
                            getResources().getDisplayMetrics()),
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,150,
                            getResources().getDisplayMetrics()));
            params.gravity = Gravity.CENTER;
            params.setMargins(10, 10, 10, 10);
            imageButton.setLayoutParams(params);
            imageButton.setContentDescription(
                    getResources().getString(R.string.log_entry_image_description));
            imageButton.setImageResource(android.R.drawable.ic_menu_camera);
            imageButton.setOnClickListener(this);
            imageButtonArrayList.add(imageButton);
            imageContainer.addView(imageButton);
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver()
                    .query(
                            uri,
                            null,
                            null,
                            null,
                            null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).
                format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        mCurrentPhotoName = image.getName();
        return image;
    }

    private static class proccessImageInBackground extends AsyncTask<ProcessImageParamContainer,
            Void, ProcessImageParamContainer> {
        @Override
        protected ProcessImageParamContainer doInBackground(ProcessImageParamContainer... params) {
            ProcessImageParamContainer parameters = params[0];
            if(processImages(parameters)){
                parameters.setSuccessful();
                return parameters;
            }
            else{
                return parameters;
            }
        }
        protected void onPostExecute(ProcessImageParamContainer result) {
            if(!result.getSuccess()){
                Toast.makeText(result.getContext(), "Please enter a text first.",
                        Toast.LENGTH_SHORT).show();
            }
            assert result.getButton() != null;
            result.getButton().setEnabled(true);
        }
    }
    private void createDatePickerDialog(final Button launcher, String title, String positiveMessage,
                                        String negativeMessage, String defaulDate){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        final DatePicker picker = new DatePicker(this);
        if(!defaulDate.equals("") && !defaulDate.equals(getString(R.string.select_date))){
            picker.init(DateStringUtil.getYear(defaulDate),
                    DateStringUtil.getMonth(defaulDate),
                    DateStringUtil.getDay(defaulDate), (DatePicker view, int year, int monthOfYear,
                                                        int dayOfMonth) -> {
                    });
        }
        builder.setTitle(title)
                .setView(picker)
                .setPositiveButton(positiveMessage, (DialogInterface dialog, int which) -> {
                    int day = picker.getDayOfMonth();
                    int month = picker.getMonth() + 1;
                    int year = picker.getYear();
                    launcher.setText(DateStringUtil.intToDateString(month, day, year));
                })
                .setNegativeButton(negativeMessage, null);
        builder.show();
    }

    private void createTimePickerDialog(final Button launcher, String title, String positiveMessage,
                                        String negativeMessage, String defaultTime){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        final TimePicker picker = new TimePicker(this);
        if(!defaultTime.equals("")) {
            int hour = TimeStringUtil.getHour(defaultTime);
            if (TimeStringUtil.getAMorPM(defaultTime) == Calendar.PM) {
                hour += 12;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                picker.setHour(hour);
                picker.setMinute(TimeStringUtil.getMinute(defaultTime));
            }
            else{
                picker.setCurrentHour(hour);
                picker.setCurrentMinute(TimeStringUtil.getMinute(defaultTime));
            }
        }
        builder.setTitle(title)
                .setView(picker)
                .setPositiveButton(positiveMessage, (DialogInterface dialog, int which) -> {
                    int hour, minute;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        hour = picker.getHour();
                        minute = picker.getMinute();
                    }
                    else {
                        hour = picker.getCurrentHour();
                        minute = picker.getCurrentMinute();
                    }
                    launcher.setText(TimeStringUtil.getSummaryString(
                            TimeStringUtil.createTimeString(hour, minute)));
                })
                .setNegativeButton(negativeMessage, null);
        builder.show();
    }

    protected boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    protected void askPermissions() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
    }
}
