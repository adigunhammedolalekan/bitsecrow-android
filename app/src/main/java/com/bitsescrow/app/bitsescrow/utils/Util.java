package com.bitsescrow.app.bitsescrow.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.bitsescrow.app.bitsescrow.R;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.Random;
import java.util.UUID;


/**
 * Created by Lekan Adigun on 12/21/2017.
 */

public final class Util {

    /*
    * Handy utils
    * */

    public static final String CLOUDPRESET = "w6k1rto6";

    public static String textOf(EditText editText) {

        if(editText == null) return "";

        return editText.getText().toString().trim();
    }


    public static String randString() {
        return String.valueOf("issue-" + UUID.randomUUID().toString());
    }

    public static RequestOptions requestOptions() {
        return new RequestOptions()
                .placeholder(R.color.divider)
                .error(R.color.divider)
                .dontAnimate();


    }

    public static void hideKeyboard(Activity activity) {

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        try {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }catch (Exception e) {
            //if there is NPE
        }
    }

    public static File randomFile() {

        String fileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + File.separator + "Stylish.ly";

        File folder = new File(fileName);
        if (!folder.exists()) {
            boolean result = folder.mkdirs();
            L.fine("Folder created " + result);
        }
        return new File(folder, String.valueOf(System.currentTimeMillis()) + randString() + ".png");
    }
}
