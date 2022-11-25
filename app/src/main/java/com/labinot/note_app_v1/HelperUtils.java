package com.labinot.note_app_v1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HelperUtils {

    public static final String TEXT_FILE_EXTENSION = ".txt";
    public static String PREF_COLOUR_PRIMARY = "colourPrimary";
    public static String PREF_OPPOSITE_COLOR = "oppositeColor";
    public static String PREF_COLOUR_FONT = "colourFont";
    public static String PREF_COLOUR_BACKGROUND = "colourBackground";
    public static String PREF_COLOUR_NAVBAR = "colourNavbar";
    public static String PREF_SORT_ALPHABETICAL = "sortAlphabetical";

    public static boolean colorNavBar, sortAlphabetical;

    @ColorInt
    public static int colorPrimary, oppositeColor, colorBackground, colorFont;

    @SuppressLint("SuspiciousIndentation")
    public static void applyColors(AppCompatActivity activity, Toolbar toolbar) {

        Window window = activity.getWindow();

        if (colorNavBar)
            window.setNavigationBarColor(colorPrimary);


        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(darkenColor(colorPrimary, 0.2));

        toolbar.setBackgroundColor(colorPrimary);
        toolbar.setTitleTextColor(oppositeColor);

        if (isDark(colorPrimary)) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                View decor = window.getDecorView();
                decor.setSystemUiVisibility(0);

            }

        } else {


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                View decor = window.getDecorView();


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);

                }else{

                    decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }

            }

        }


    }

    public static boolean isDark(int color) {

        return ColorUtils.calculateLuminance(color) < 0.5;
    }

    public static void getSettings(Context context, SharedPreferences sharedPreferences) {

        colorPrimary = sharedPreferences.getInt(HelperUtils.PREF_COLOUR_PRIMARY, ContextCompat.getColor(context, R.color.primary));
        colorBackground = sharedPreferences.getInt(HelperUtils.PREF_COLOUR_BACKGROUND, Color.WHITE);
        colorFont = sharedPreferences.getInt(HelperUtils.PREF_COLOUR_FONT, Color.DKGRAY);
        oppositeColor = sharedPreferences.getInt(HelperUtils.PREF_OPPOSITE_COLOR, Color.BLACK);
        colorNavBar = sharedPreferences.getBoolean(HelperUtils.PREF_COLOUR_NAVBAR, false);
        sortAlphabetical = sharedPreferences.getBoolean(HelperUtils.PREF_SORT_ALPHABETICAL,false);
    }

    public static int darkenColor(int color, double fraction) {

        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        red = darken(red, fraction);
        green = darken(green, fraction);
        blue = darken(blue, fraction);

        int alfa = Color.alpha(color);

        return Color.argb(alfa, red, green, blue);

    }

    private static int darken(int color, double fraction) {

        return (int) Math.max(color - (color * fraction), 0);
    }

    public static void setBorderColor(ImageView borderImage,int borderColor) {

        GradientDrawable drawable = (GradientDrawable) borderImage.getBackground();
        drawable.setStroke(5,borderColor);
    }

    public static Drawable colorIconDrawable(Context context, int icon, int color) {

        Drawable colorDrawable;
        colorDrawable = VectorDrawableCompat.create(context.getResources(),icon,null);

        if(colorDrawable != null){

            colorDrawable = DrawableCompat.wrap(colorDrawable);
            DrawableCompat.setTint(colorDrawable,color);

        }

        return colorDrawable;
    }

    public static boolean fileExist(Context context,String fileName) {

        File file = context.getFileStreamPath(fileName + TEXT_FILE_EXTENSION);
        return file.exists();

    }

    public static Bitmap convertVector(Context context, int drawID, int yourColor) {
        Drawable drawable = HelperUtils.colorIconDrawable(context,drawID, yourColor);

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawableCompat || drawable instanceof VectorDrawable) {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            throw new IllegalArgumentException("Unsuported Drawble Type");
        }

    }

    public static void writeFiles(Context context,String fileName,String fileContent) {


        try {
            OutputStreamWriter out = new OutputStreamWriter(context.openFileOutput(fileName + TEXT_FILE_EXTENSION,0));
            out.write(fileContent);
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Exception: " +e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }

    public static String readFiles(Context context,String fileName) {

        String content = "";

        if(fileExist(context,fileName)){


            try {
                InputStream  in = context.openFileInput(fileName + TEXT_FILE_EXTENSION);
                if(in != null){

                    InputStreamReader temp = new InputStreamReader(in);
                    BufferedReader reader = new BufferedReader(temp);
                    String str;

                    StringBuilder buf = new StringBuilder();
                    while ((str = reader.readLine())!= null){

                        buf.append(str).append("\n");
                    }
                    in.close();
                    content = buf.toString();


                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "Exception: " +e.getMessage(), Toast.LENGTH_SHORT).show();
            }



        }

        return content.trim();
    }

    public static List<File> getFiles(Context context) {

        File[] files = context.getFilesDir().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {

                return name.toLowerCase().endsWith(TEXT_FILE_EXTENSION);
            }
        });

        return new ArrayList<>(Arrays.asList(files));
    }

    public static Spanned returnColorString(String text) {
        return Html.fromHtml("<font color='" + oppositeColor + "'>" + text + "</font>");
    }
}
