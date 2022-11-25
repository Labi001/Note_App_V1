package com.labinot.note_app_v1;

import static com.labinot.note_app_v1.HelperUtils.colorBackground;
import static com.labinot.note_app_v1.HelperUtils.colorPrimary;
import static com.labinot.note_app_v1.HelperUtils.isDark;
import static com.labinot.note_app_v1.HelperUtils.oppositeColor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.ColorUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class NoteActivity extends AppCompatActivity {

    private static final String EXTRA_NOTE_TITLE = "EXTRA_NOTE_TITLE";
    private EditText title_text, note_text;
    private SharedPreferences sharedPreferences;
    private Toolbar toolbar;
    private String title;
    private String note;
    private AlertDialog.Builder dialog;

    public static Intent getStartIntent(Context context,String title){

        Intent intent = new Intent(context,NoteActivity.class);
        intent.putExtra(EXTRA_NOTE_TITLE,title);
        return intent;
    }

    @Override
    public void onBackPressed() {
        finish();

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu.findItem(R.id.btn_delete).setIcon(HelperUtils.colorIconDrawable(NoteActivity.this,R.drawable.ic_delete,oppositeColor));
        menu.findItem(R.id.btn_undo).setIcon(HelperUtils.colorIconDrawable(NoteActivity.this,R.drawable.ic_undo,oppositeColor));
        menu.findItem(R.id.btn_share).setIcon(HelperUtils.colorIconDrawable(NoteActivity.this,R.drawable.ic_share,oppositeColor));

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_note,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home:
                finish();
                return true;

            case R.id.btn_delete:

                dialog = new AlertDialog.Builder(NoteActivity.this)
                        .setTitle(HelperUtils.returnColorString(getString(R.string.confirm_del)))
                        .setMessage(HelperUtils.returnColorString(getString(R.string.del_message)))

                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (HelperUtils.fileExist(NoteActivity.this, title))
                                    deleteFile(title + HelperUtils.TEXT_FILE_EXTENSION);

                                title = "";
                                note = "";
                                title_text.setText(title);
                                 note_text.setText(note);
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(HelperUtils.colorIconDrawable(NoteActivity.this, R.drawable.ic_delete, oppositeColor));


                AlertDialog alertDialog = dialog.create();
                alertDialog.show();

                if (alertDialog.getWindow() != null)
                    alertDialog.getWindow().getDecorView().setBackgroundColor(colorPrimary);


                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(oppositeColor);
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(oppositeColor);





                return true;

            case R.id.btn_undo:

                note_text.setText(note);
                note_text.setSelection(note_text.getText().length());
                return true;

            case R.id.btn_share:

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,note_text.getText().toString());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent,getString(R.string.shareto)));

                return true;



        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(NoteActivity.this);
        HelperUtils.getSettings(NoteActivity.this,sharedPreferences);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        Drawable drawable = toolbar.getNavigationIcon();

        if(drawable != null)
            drawable.setColorFilter(oppositeColor, PorterDuff.Mode.SRC_ATOP);

        title_text = findViewById(R.id.edit_title);
        note_text = findViewById(R.id.edit_note);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if(Intent.ACTION_SEND.equals(action) && type !=null){

        }else{

            title = intent.getStringExtra(EXTRA_NOTE_TITLE);
            if(title == null || TextUtils.isEmpty(title)){

                title = "";
                note = "";
                 note_text.requestFocus();
                if(getSupportActionBar() != null)
                    getSupportActionBar().setTitle(R.string.new_note);

            }else{

                title_text.setText(title);
                note = HelperUtils.readFiles(NoteActivity.this,title);
                note_text.setText(note);
                if(getSupportActionBar() != null)
                    getSupportActionBar().setTitle(title);
            }

        }

        applySettings();
    }

    private void applySettings(){

        HelperUtils.applyColors(NoteActivity.this,toolbar);
        findViewById(R.id.nested_layout).setBackgroundColor(colorBackground);


        if(isDark(colorBackground)){

            title_text.setTextColor(Color.WHITE);
            title_text.setHintTextColor(ColorUtils.setAlphaComponent(Color.WHITE,120));
            title_text.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            note_text.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));

            note_text.setTextColor(Color.WHITE);
            note_text.setHintTextColor(ColorUtils.setAlphaComponent(Color.WHITE,120));

        }else{

            title_text.setTextColor(Color.BLACK);
            title_text.setHintTextColor(ColorUtils.setAlphaComponent(Color.DKGRAY,120));
            title_text.setBackgroundTintList(ColorStateList.valueOf(Color.DKGRAY));
            note_text.setBackgroundTintList(ColorStateList.valueOf(Color.DKGRAY));

            note_text.setTextColor(Color.BLACK);
            note_text.setHintTextColor(ColorUtils.setAlphaComponent(Color.DKGRAY,120));

        }

    }


    @SuppressLint("SuspiciousIndentation")
    @Override
    protected void onPause() {

        if(!isChangingConfigurations())
        saveFiles();

        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        note = note_text.getText().toString().trim();
        if(getCurrentFocus() != null)
            getCurrentFocus().clearFocus();
    }

    private void saveFiles() {

        String newTitle = title_text.getText().toString().trim().replace("/","");
        String newNote = note_text.getText().toString().trim();

        if(TextUtils.isEmpty(newTitle) && TextUtils.isEmpty(newNote))
            return;

        if(newTitle.equals(title) && newNote.equals(note))
            return;

        if(!title.equals(newTitle) || TextUtils.isEmpty(newTitle)){

            newTitle = newFileName(newTitle);
            title_text.setText(newTitle);
        }

        HelperUtils.writeFiles(NoteActivity.this,newTitle,newNote);

        if(!TextUtils.isEmpty(title) && !newTitle.equals(title))
            deleteFile(title + HelperUtils.TEXT_FILE_EXTENSION);

        title = newTitle;

        Toast.makeText(this, title + " saved", Toast.LENGTH_SHORT).show();
    }

    private String newFileName(String newTitle) {

        if(TextUtils.isEmpty(newTitle)){

            newTitle = getString(R.string.note);
        }

        if(HelperUtils.fileExist(NoteActivity.this,newTitle)){

            int i = 1;

            while (true){

                if(!HelperUtils.fileExist(NoteActivity.this,newTitle+" (" + i + ") ")||title.equals(newTitle+" (" + i + ") ")){

                   newTitle = (newTitle+" (" + i + ") ");
                   break;
                }
                i++;
            }

        }
      return  newTitle;
    }
}