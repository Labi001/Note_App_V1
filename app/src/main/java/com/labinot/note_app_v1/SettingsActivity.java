package com.labinot.note_app_v1;

import static com.labinot.note_app_v1.HelperUtils.PREF_COLOUR_BACKGROUND;
import static com.labinot.note_app_v1.HelperUtils.PREF_COLOUR_FONT;
import static com.labinot.note_app_v1.HelperUtils.PREF_COLOUR_NAVBAR;
import static com.labinot.note_app_v1.HelperUtils.PREF_COLOUR_PRIMARY;
import static com.labinot.note_app_v1.HelperUtils.PREF_OPPOSITE_COLOR;
import static com.labinot.note_app_v1.HelperUtils.colorBackground;
import static com.labinot.note_app_v1.HelperUtils.colorFont;
import static com.labinot.note_app_v1.HelperUtils.colorNavBar;
import static com.labinot.note_app_v1.HelperUtils.colorPrimary;
import static com.labinot.note_app_v1.HelperUtils.isDark;
import static com.labinot.note_app_v1.HelperUtils.oppositeColor;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.core.widget.CompoundButtonCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import yuku.ambilwarna.AmbilWarnaDialog;

public class SettingsActivity extends AppCompatActivity {


    private ImageView primary_imageView,font_imageView,background_imageView;
    private TextView primary_txt,font_txt,background_txt,navBar_txt;
    private CheckBox checkBox;
    private SharedPreferences sharedPreferences;
    private Toolbar toolbar;
    private Button apply_btn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
        HelperUtils.getSettings(SettingsActivity.this,sharedPreferences);


        primary_imageView = findViewById(R.id.primary_imageView);
        font_imageView = findViewById(R.id.font_imageView);
        background_imageView = findViewById(R.id.background_imageView);
        primary_txt = findViewById(R.id.primary_textView);
        font_txt = findViewById(R.id.font_textView);
        background_txt = findViewById(R.id.background_textView);
        apply_btn = findViewById(R.id.btn_apply);
        navBar_txt = findViewById(R.id.nav_textView);
        checkBox = findViewById(R.id.nav_checkBox);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Drawable drawable = toolbar.getNavigationIcon();

        if(drawable != null)
            drawable.setColorFilter(oppositeColor, PorterDuff.Mode.SRC_ATOP);

        primary_imageView.setColorFilter(colorPrimary);
        font_imageView.setColorFilter(colorFont);
        background_imageView.setColorFilter(colorBackground);

         applySettings();


    }

    private void applySettings() {

        HelperUtils.applyColors(SettingsActivity.this,toolbar);

        apply_btn.setBackgroundColor(colorPrimary);
        apply_btn.setTextColor(oppositeColor);

        findViewById(R.id.setting_layout).setBackgroundColor(colorBackground);

        primary_txt.setTextColor(colorFont);
        font_txt.setTextColor(colorFont);
        background_txt.setTextColor(colorFont);
        navBar_txt.setTextColor(colorFont);

        checkBox.setChecked(colorNavBar);
        CompoundButtonCompat.setButtonTintList(checkBox, ColorStateList.valueOf(colorPrimary));

        int tempBorder;

        if(isDark(colorBackground))
            tempBorder = Color.WHITE;
        else
            tempBorder = Color.BLACK;

        HelperUtils.setBorderColor(primary_imageView,tempBorder);
        HelperUtils.setBorderColor(font_imageView,tempBorder);
        HelperUtils.setBorderColor(background_imageView,tempBorder);


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    public void saveSettings(View view) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PREF_COLOUR_PRIMARY, colorPrimary);
        editor.putInt(PREF_OPPOSITE_COLOR, oppositeColor);
        editor.putInt(PREF_COLOUR_FONT, colorFont);
        editor.putInt(PREF_OPPOSITE_COLOR,oppositeColor);
        editor.putInt(PREF_COLOUR_BACKGROUND,colorBackground);
        editor.putBoolean(PREF_COLOUR_NAVBAR, checkBox.isChecked());
        editor.apply();

        startActivity(new Intent(SettingsActivity.this,NotesListActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
          finish();
    }

    public void showPicker1(View view) {

        AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(SettingsActivity.this, colorPrimary, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {

                colorPrimary = color;

              if(isDark(colorPrimary))

                  oppositeColor = Color.WHITE;
              else
                  oppositeColor = Color.BLACK;

                primary_imageView.setColorFilter(colorPrimary);

            }
        });

        ambilWarnaDialog.show();

    }


    public void showPicker2(View view) {

        AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(SettingsActivity.this, colorFont, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {

                colorFont = color;
                font_imageView.setColorFilter(colorFont);


            }
        });

        ambilWarnaDialog.show();

    }

    public void showPicker3(View view) {

        AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(SettingsActivity.this, colorBackground, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {

                colorBackground = color;
                background_imageView.setColorFilter(colorBackground);


            }
        });

        ambilWarnaDialog.show();


    }

    public void toggleCheckBox(View view) {

        checkBox.toggle();

    }



}