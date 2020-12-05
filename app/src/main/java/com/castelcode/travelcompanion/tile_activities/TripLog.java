package com.castelcode.travelcompanion.tile_activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.castelcode.travelcompanion.R;
import com.castelcode.travelcompanion.adapters.LogEntryItemsBaseAdapter;
import com.castelcode.travelcompanion.log_entry_add_activity.AddLogEntry;
import com.castelcode.travelcompanion.log_entry_add_activity.CreateEmailParamContainer;
import com.castelcode.travelcompanion.log_entry_add_activity.LogEntry;
import com.castelcode.travelcompanion.log_entry_add_activity.LogEntryImage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class TripLog extends AppCompatActivity implements View.OnClickListener {
    private final static int ALL_FILES = -1;
    private final static int LOG_ENTRY_ADD = 1;
    private final static int EMAIL_SEND = 2;

    private final static String TAG = "TRIP_LOG";


    FloatingActionButton addLogItemButton;

    Button shareButton;

    private ArrayList<LogEntry> logEntryItems = new ArrayList<>();

    private ListView logEntryItemsView;
    private LogEntryItemsBaseAdapter adapter;

    private static ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_log);

        addLogItemButton = findViewById(R.id.add_entry_button);

        addLogItemButton.setOnClickListener(this);

        progress = new ProgressDialog(this);
        progress.setTitle("Creating Document to Share");
        progress.setMessage("Please wait while we create your document");
        progress.setCancelable(false);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Type listOfLogEntries = new TypeToken<ArrayList<LogEntry>>(){}.getType();
        Gson gson = new Gson();
        String json = sharedPref.getString(getString(R.string.log_entry), "");
        ArrayList<LogEntry> readItems = gson.fromJson(json, listOfLogEntries);
        if(readItems != null && readItems.size() > 0){
            logEntryItems.clear();
            logEntryItems.addAll(readItems);
        }

        logEntryItemsView = findViewById(R.id.log_entry_list_view);

        adapter = new LogEntryItemsBaseAdapter(this, logEntryItems);

        logEntryItemsView.setAdapter(adapter);

        logEntryItemsView.setOnItemClickListener((AdapterView<?> parent, View view, int position,
                long id) -> {
            Object o = logEntryItemsView.getItemAtPosition(position);
            LogEntry entry = (LogEntry) o;
            Intent editLogEntryItemIntent = new Intent(parent.getContext(),
                    AddLogEntry.class);
            editLogEntryItemIntent.putExtra(getResources().getString(R.string.log_entry),
                    entry);
            startActivityForResult(editLogEntryItemIntent, LOG_ENTRY_ADD);
        });
        final AppCompatActivity activity = this;
        final Context context = getApplicationContext();
        logEntryItemsView.setOnItemLongClickListener((AdapterView<?> parent, View view,
                final int position, long id) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
            Object o = logEntryItemsView.getItemAtPosition(position);
            LogEntry entry = (LogEntry) o;

            builder.setTitle("Share entry")
                .setMessage("Share the entry with a log entry that starts with: " +
                    entry.getTextPreview())
                .setPositiveButton(R.string.share, (DialogInterface dialog, int which) -> {
                    progress.show();
                    CreateEmailParamContainer params = new CreateEmailParamContainer(
                            activity, context, position, shareButton, logEntryItems,
                            activity.getFilesDir());
                    new createEmailInBackground().execute(params);
                })
                .setNegativeButton(R.string.cancel, (DialogInterface dialog, int which) -> {
                });
            builder.create().show();
            return true;
        });
        shareButton = findViewById(R.id.share);
        shareButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == addLogItemButton){
            Intent addIntent = new Intent(this, AddLogEntry.class);
            startActivityForResult(addIntent, LOG_ENTRY_ADD);
        }
        else if(v == shareButton){
            shareButton.setEnabled(false);
            progress.show();
            CreateEmailParamContainer params = new CreateEmailParamContainer(this,
                    getApplicationContext(), ALL_FILES, shareButton, logEntryItems, getFilesDir());
            new createEmailInBackground().execute(params);
        }
    }

    private static void createEmail(CreateEmailParamContainer params){
        File file = createDocument(params);
        if (file != null) {
            Uri sharedFileUri = FileProvider.getUriForFile(params.getContext(),
                    "com.castelcode.travelcompanion.myfileprovider", file);
            sendMail(sharedFileUri, params.getActivity());
        }
        else{
            Log.e(TAG, "ISSUE CREATING DOCUMENT");
        }
    }
    private static void sendMail(Uri URI, AppCompatActivity activity) {
        try {
            String subject = "Trip log for my cruise";

            String message = "Check out the trip log I've created for my cruise";

            final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.setType("application/pdf");
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,subject);
            emailIntent.putExtra(Intent.EXTRA_TEXT, message);

            if (URI != null) {
                emailIntent.putExtra(Intent.EXTRA_STREAM, URI);
            }

            emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivityForResult(Intent.createChooser(emailIntent,"Send file via:"),
                    EMAIL_SEND);

        } catch (Throwable t) {
            Toast.makeText(activity,
                "Request failed try again: " + t.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private static File createDocument(CreateEmailParamContainer params){
        if(params.getEntries().size() == 0)
            return null;
        File file = null;
        File dir;
        Document doc = new Document();

        try {
            dir = new File(params.getFileDir(), "shared");
            boolean status = true;
            if(!dir.exists())
                status = dir.mkdirs();
            if(!status){
                return null;
            }
            file = new File(dir, "CruiseDoc.pdf");

            FileOutputStream fOut = new FileOutputStream(file);

            PdfWriter.getInstance(doc, fOut);

            //open the document
            doc.open();
            if(params.getItemPosition() != -1){
                LogEntry entry = params.getEntries().get(params.getItemPosition());
                Paragraph p = new Paragraph(entry.getDateTimeAsString() + " - " +
                        entry.getTextEntry());
                Font paraFont = new Font(Font.FontFamily.COURIER);
                p.setAlignment(Paragraph.ALIGN_CENTER);
                p.setFont(paraFont);
                doc.add(p);
                try{
                    File f = new File(entry.getImagePath(), entry.getFileName() + ".png");
                    Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    Image myImg = Image.getInstance(stream.toByteArray());
                    myImg.setAlignment(Image.MIDDLE | Image.TEXTWRAP);
                    doc.add(myImg);
                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                catch (NullPointerException e){
                    Log.i(TAG, "No image");
                }
            }
            else{
                for (LogEntry entry: params.getEntries()) {
                    Paragraph p = new Paragraph(entry.getDateTimeAsString() + " - " +
                            entry.getTextEntry());
                    Font paraFont= new Font(Font.FontFamily.COURIER);
                    p.setAlignment(Paragraph.ALIGN_CENTER);
                    p.setFont(paraFont);
                    doc.add(p);
                    ArrayList<LogEntryImage> images = entry.getImages();
                    ContextWrapper cw = new ContextWrapper(params.getContext());
                    File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                    for (int i = 0; i < images.size(); i++) {
                        try {
                            File f = new File(directory, images.get(i).getPhotoName());
                            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100 , stream);
                            Image myImg = Image.getInstance(stream.toByteArray());
                            myImg.setAlignment(Image.MIDDLE | Image.TEXTWRAP);
                            doc.add(myImg);
                        }
                        catch (FileNotFoundException e)
                        {
                            e.printStackTrace();
                        }
                        catch (NullPointerException e){
                            Log.i(TAG, "No image");
                        }
                    }
                }
            }
        } catch (DocumentException de) {
            Log.e(TAG, "DocumentException:" + de);
        } catch (IOException e) {
            Log.e(TAG, "ioException:" + e);
        }
        finally {
            doc.close();
        }
        return file;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == LOG_ENTRY_ADD) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                LogEntry entry = (LogEntry) data.getSerializableExtra(getResources().getString(
                        R.string.log_entry));
                if(data.getBooleanExtra(getResources().getString(R.string.delete), false)){
                    if(logEntryItems.contains(entry)){
                        logEntryItems.remove(entry);
                    }
                }
                else{
                    if(!logEntryItems.contains(entry)){
                        logEntryItems.add(entry);
                    }
                    else{
                        logEntryItems.remove(entry); //based on name and conf number
                        logEntryItems.add(entry); // other fields might have changed.
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }
        else if(requestCode == EMAIL_SEND && resultCode == RESULT_CANCELED){
            shareButton.setEnabled(true);
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        progress.dismiss();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        Type listOfLogEntry = new TypeToken<ArrayList<LogEntry>>(){}.getType();
        Gson gson = new Gson();
        String json = gson.toJson(logEntryItems, listOfLogEntry);
        editor.putString(getString(R.string.log_entry), json);
        editor.apply();
    }

    private static class createEmailInBackground extends AsyncTask<CreateEmailParamContainer,
            Void, CreateEmailParamContainer> {

        @Override
        protected CreateEmailParamContainer doInBackground(CreateEmailParamContainer... params) {
            createEmail(params[0]);
            return params[0];
        }

        @Override
        protected void onPostExecute(CreateEmailParamContainer result) {
            result.getShareButton().setEnabled(true);
            progress.dismiss();
        }
    }

}
