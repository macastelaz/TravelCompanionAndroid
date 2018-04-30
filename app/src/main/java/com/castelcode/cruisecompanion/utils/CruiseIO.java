package com.castelcode.cruisecompanion.utils;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import com.castelcode.cruisecompanion.Cruise;

public class CruiseIO {
    private static final String CRUISE_KEY = "Cruise";
    private static final String CRUISE_FILE = "cruise";

    private File fileDir;

    public CruiseIO(File dir){
        fileDir = dir;
    }

    public boolean saveCruise(Cruise cruise, String name) {
        File file = new File(fileDir, CRUISE_FILE + name);

        FileOutputStream fos = null;
        ObjectOutputStream oos  = null;
        boolean keep = true;
        boolean successfulSave = true;
        try {
            fos = new FileOutputStream(file);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(cruise);
        }
        catch (Exception e) {
            keep = false;
            Log.e(CRUISE_KEY, "failed to suspend", e);
            successfulSave = false;
        }
        finally {
            try {
                if (oos != null)   oos.close();
                if (fos != null)   fos.close();
                if (!keep)
                    if(!file.delete())
                        Log.i(CRUISE_KEY, "Can't delete file: " + file);
            }
            catch (Exception e) {
                successfulSave = false;
            }
        }
        return successfulSave;
    }
    public Cruise readCruise(String name){
        File file = new File(fileDir, CRUISE_FILE + name);
        FileInputStream fin;
        ObjectInputStream ois=null;
        try {
            fin = new FileInputStream(file);
            ois = new ObjectInputStream(fin);
            Cruise cruise = (Cruise) ois.readObject();

            ois.close();
            Log.d("fileManager", "Records read successfully :\n" + cruise.toString());
            Log.d("fileManager", "drinks_consumed: " +
                    String.valueOf(cruise.getNumDrinksConsumed()));
            Log.d("fileManager", "calendar: " + cruise.getCruiseDateTime().toString());

            return cruise;
        } catch (Exception e) {
            Log.e("fileManager", "Cant read saved records : "+e.getMessage());
            return null;
        }
        finally {
            if (ois != null)
                try {
                    ois.close();
                } catch (Exception e) {
                    Log.e("fileManager", "Error in closing stream while reading records : " + e.getMessage());
                }
        }
    }

    public boolean deleteCruise(String name){
        File file = new File(fileDir, CRUISE_FILE + name);
        return  file.delete();
    }
}
