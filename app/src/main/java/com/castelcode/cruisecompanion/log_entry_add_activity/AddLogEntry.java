package com.castelcode.cruisecompanion.log_entry_add_activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.ContextWrapper;
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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.castelcode.cruisecompanion.R;
import com.castelcode.cruisecompanion.utils.DateStringUtil;
import com.castelcode.cruisecompanion.utils.ScalingUtilities;
import com.castelcode.cruisecompanion.utils.TimeStringUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddLogEntry extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "ADD_LOG_ENTRY";

    private static final int SELECT_PICTURE = 1;
    private static final int TAKE_PICTURE = 0;

    private EditText logTextEditText;
    private FloatingActionButton confirmButton;
    FloatingActionButton deleteButton;
    private Button dateSelectButton;
    private Button timeSelectButton;
    ImageButton imageButton;

    LogEntry entryIn;

    Uri currentUri;
    static Bitmap currentBitmap;
    String mCurrentPhotoPath;
    String mCurrentPhotoName;

    AppCompatActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_log_entry);

        activity = this;

        logTextEditText = (EditText) findViewById(R.id.edit_log_text);
        logTextEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        logTextEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);
        confirmButton = (FloatingActionButton) findViewById(R.id.confirm_button);
        deleteButton = (FloatingActionButton) findViewById(R.id.delete_button);
        dateSelectButton = (Button) findViewById(R.id.DateSelectButton);
        timeSelectButton = (Button) findViewById(R.id.TimeSelectButton);

        dateSelectButton.setOnClickListener(this);
        timeSelectButton.setOnClickListener(this);
        confirmButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);

        imageButton = (ImageButton) findViewById(R.id.image_button);

        imageButton.setOnClickListener(this);

        Intent intent = getIntent();
        LogEntry entry = (LogEntry) intent.getSerializableExtra(
                getResources().getString(R.string.log_entry));

        if(entry != null){
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(this);
            logTextEditText.setText(entry.getTextEntry());
            dateSelectButton.setText(entry.getDateString());
            timeSelectButton.setText(entry.getTimeString());
            File mypath = new File(entry.getImagePath(), entry.getFileName() + ".png");
            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeStream(new FileInputStream(mypath));
                currentBitmap = bitmap;
            }
            catch (Exception ex){
                bitmap = BitmapFactory.decodeResource(getResources(),
                        android.R.drawable.ic_menu_camera);
            }
            Drawable drawableToUse = new BitmapDrawable(getResources(), bitmap);
            Bitmap icon = BitmapFactory.decodeResource(getResources(),
                    android.R.drawable.ic_menu_camera);
            if(bitmap == icon) {
                imageButton.setImageDrawable(drawableToUse);
            }
            else{
                handleDrawing(drawableToUse);
            }
        }
        else{
            deleteButton.setVisibility(View.INVISIBLE);
            deleteButton.setOnClickListener(null);
        }
        entryIn = entry;
    }

    private static void saveBitmapToInternalStorage(LogEntry entry, Context context){
        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir

        File mypath = new File(directory, entry.getFileName() + ".png");
        //entry.setImagePath(mypath.getPath());

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);

            Bitmap scaledBitmap = ScalingUtilities.createScaledBitmap(currentBitmap,
                    ScalingUtilities.ScalingLogic.FIT);
            currentBitmap.recycle();
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
        entry.setImagePath(directory.getAbsolutePath());
    }

    private static boolean processImage(ProcessImageParamContainer params){
        String logText = params.getEditText().getText().toString();
        String date = params.getDateSelectButton().getText().toString();
        String time = params.getTimeSelectButton().getText().toString();
        if(!logText.equals(""))
        {
            LogEntry entry = new LogEntry(logText, date, time);

            saveBitmapToInternalStorage(entry, params.getContext());

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
                        "com.castelcode.cruisecompanion.myfileprovider",
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
        else if(v == imageButton){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.image_picker_alert_message)
                    .setPositiveButton(R.string.new_photo,
                            (DialogInterface dialog, int which) -> {
                                   //Launch Camera Intent
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
        }
        else if(v == deleteButton){
            Log.d("TEST", "DELETE");
            Intent resultIntent = new Intent();
            resultIntent.putExtra(getResources().getString(R.string.delete), true);
            resultIntent.putExtra(getResources().getString(R.string.log_entry), entryIn);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void handleDrawing(Drawable drawItem){
        int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            imageButton.setBackgroundDrawable(drawItem);
        } else {
            imageButton.setBackground(drawItem);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                currentUri = data.getData();
                try {
                    currentBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),
                            currentUri);
                    BitmapDrawable drawable = new BitmapDrawable(getResources(), currentBitmap);
                    handleDrawing(drawable);
                } catch (IOException ex) {
                    //......
                    Log.e("TEST", "EXCEPTION " + ex.toString());
                }

            }
            if (requestCode == TAKE_PICTURE) {
                galleryAddPic();

                currentBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);

                BitmapDrawable drawable = new BitmapDrawable(getResources(), currentBitmap);
                handleDrawing(drawable);
            }
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).
                format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        mCurrentPhotoName = imageFileName;
        return image;
    }

    private static class proccessImageInBackground extends AsyncTask<ProcessImageParamContainer,
            Void, ProcessImageParamContainer> {
        @Override
        protected ProcessImageParamContainer doInBackground(ProcessImageParamContainer... params) {
            ProcessImageParamContainer parameters = params[0];
            if(processImage(parameters)){
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
}
