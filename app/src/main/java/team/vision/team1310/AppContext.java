package team.vision.team1310;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class AppContext {

    // Singleton
    private static AppContext instance = null;

    private AppContext() {

    }

    public synchronized static AppContext getInstance() {
        if (instance == null) {
            instance = new AppContext();
        }
        return instance;
    }

    public Activity mainActivity;

    public double tWidthMin = 0.0;
    public double tWidthMax = 1000;
    public double tHeightMax = 0.0;
    public double tHeightMin = 1000;
    public double tRatioMin = 0.0;
    public double tRatioMax = 0.0;
    public double tPerimeterMin = 0.0;
    public double tAreaMin = 0.0;

    int[] hueValues = {50, 150};
    int[] saturationValues = {25, 100};
    int[] luminanceValues = {10, 120};


    public void loadSettings() {
        String s;
        boolean b;

        SharedPreferences prefs = mainActivity.getSharedPreferences("Prefs", Context.MODE_PRIVATE);

        tWidthMin = Double.parseDouble(prefs.getString("tWidthMin", "0.0"));
        tWidthMax = Double.parseDouble(prefs.getString("tWidthMax", "0.0"));
        tHeightMax = Double.parseDouble(prefs.getString("tHeightMax", "0.0"));
        tHeightMin = Double.parseDouble(prefs.getString("tHeightMin", "0.0"));
        tRatioMin = Double.parseDouble(prefs.getString("tRatioMin", "0.0"));
        tRatioMax = Double.parseDouble(prefs.getString("tRatioMax", "0.0"));
        tPerimeterMin = Double.parseDouble(prefs.getString("tPerimeterMin", "0.0"));
        tAreaMin = Double.parseDouble(prefs.getString("tAreaMin", "0.0"));
        hueValues[0] = prefs.getInt("hueValueStart", 0);
        hueValues[1] = prefs.getInt("hueValueEnd", 100);
        saturationValues[0] = prefs.getInt("saturationValueStart", 0);
        saturationValues[1] = prefs.getInt("saturationValueEnd", 100);
        luminanceValues[0] = prefs.getInt("luminanceValueStart", 0);
        luminanceValues[1] = prefs.getInt("luminanceValueEnd", 100);

        Log.i("AppContext", "settings loaded " + tAreaMin);

    }

    public void saveSettings() {
        SharedPreferences prefs = mainActivity.getSharedPreferences("Prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();

        prefsEditor.putString("tWidthMin", String.valueOf(tWidthMin));
        prefsEditor.putString("tWidthMax", String.valueOf(tWidthMax));
        prefsEditor.putString("tHeightMax", String.valueOf(tHeightMax));
        prefsEditor.putString("tHeightMin", String.valueOf(tHeightMin));
        prefsEditor.putString("tRatioMin", String.valueOf(tRatioMin));
        prefsEditor.putString("tRatioMax", String.valueOf(tRatioMax));
        prefsEditor.putString("tPerimeterMin", String.valueOf(tPerimeterMin));
        prefsEditor.putString("tAreaMin", String.valueOf(tAreaMin));

        prefsEditor.putInt("hueValueStart", hueValues[0]);
        prefsEditor.putInt("hueValueEnd", hueValues[1]);
        prefsEditor.putInt("saturationValueStart", saturationValues[0]);
        prefsEditor.putInt("saturationValueEnd", saturationValues[1]);
        prefsEditor.putInt("luminanceValueStart", luminanceValues[0]);
        prefsEditor.putInt("luminanceValueEnd", luminanceValues[1]);

        prefsEditor.apply();

        Log.i("AppContext", "settings saved");
        Log.i("AppContext", "Min area " + tAreaMin);
    }
}
