package com.labinot.note_app_v1;

import static com.labinot.note_app_v1.HelperUtils.PREF_SORT_ALPHABETICAL;
import static com.labinot.note_app_v1.HelperUtils.colorBackground;
import static com.labinot.note_app_v1.HelperUtils.colorFont;
import static com.labinot.note_app_v1.HelperUtils.colorPrimary;
import static com.labinot.note_app_v1.HelperUtils.oppositeColor;
import static com.labinot.note_app_v1.HelperUtils.sortAlphabetical;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class NotesListActivity extends AppCompatActivity {

    private TextView txt_empty;
    private RecyclerView note_recyclerView;
    private FloatingActionButton fab_btn;
    private Toolbar toolbar,search_toolbar;
    private FrameLayout frameLayout;
    private Menu search_menu;
    private MenuItem item_search;
    private AlertDialog alertDialog;
    private NoteListAdapter noteListAdapter;

    private SharedPreferences sharedPreferences;

    @Override
    public void onBackPressed() {

        if (item_search != null && item_search.isActionViewExpanded()) {
            item_search.collapseActionView();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu.findItem(R.id.btn_settings).setIcon(HelperUtils.colorIconDrawable(NotesListActivity.this,R.drawable.ic_settings,oppositeColor));
        menu.findItem(R.id.btn_search).setIcon(HelperUtils.colorIconDrawable(NotesListActivity.this,R.drawable.ic_search,oppositeColor));
        menu.findItem(R.id.btn_sort).setIcon(HelperUtils.colorIconDrawable(NotesListActivity.this,R.drawable.numeric_to_alphabetical,oppositeColor));

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_note_list,menu);

        if(sortAlphabetical)
            menu.findItem(R.id.btn_sort).setIcon(R.drawable.alphabetical_to_numerical);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.btn_settings:
                startActivity(new Intent(NotesListActivity.this,SettingsActivity.class));
                break;

            case R.id.btn_sort:

                if(sortAlphabetical){

                    item.setIcon(R.drawable.alphabetical_to_numerical);
                    sortAlphabetical = false;

                }else{

                    item.setIcon(R.drawable.numeric_to_alphabetical);
                    sortAlphabetical = true;

                }

                noteListAdapter.sortList(sortAlphabetical);

                Drawable drawable = item.getIcon();
                drawable.setColorFilter(oppositeColor,PorterDuff.Mode.SRC_ATOP);

                if(drawable instanceof Animatable)
                    ((Animatable) drawable).start();

                return true;

            case R.id.btn_search:
                circleReveal(true,true);
                 item_search.expandActionView();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Note_App_V1);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);

         sharedPreferences = PreferenceManager.getDefaultSharedPreferences(NotesListActivity.this);
         HelperUtils.getSettings(NotesListActivity.this,sharedPreferences);

        txt_empty = findViewById(R.id.txt_empty);
        note_recyclerView = findViewById(R.id.recyclerView);
        frameLayout = findViewById(R.id.frame_layout);
        fab_btn = findViewById(R.id.fab_btn);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setSearchToolbar();

        note_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteListAdapter = new NoteListAdapter(colorFont,colorBackground);
        note_recyclerView.setAdapter(noteListAdapter);
        note_recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(newState == RecyclerView.SCROLL_STATE_IDLE)
                    fab_btn.show();
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                if(dy > 0 || dx < 0 && fab_btn.isShown())
                    fab_btn.hide();

                super.onScrolled(recyclerView, dx, dy);

            }
        });

        setItemTouchHelper(note_recyclerView);
        applySettings();
    }

    private void setItemTouchHelper(RecyclerView recyclerView) {

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onChildDraw(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

                    View itemView = viewHolder.itemView;

                    Paint p = new Paint();

                    p.setColor(ContextCompat.getColor(NotesListActivity.this,R.color.colorDelete));
                    Bitmap icon = HelperUtils.convertVector(NotesListActivity.this,R.drawable.ic_delete,oppositeColor);

                    try {
                        if (dX > 0) {

                            canvas.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom(), p);

                            canvas.drawBitmap(icon,
                                    (float) itemView.getLeft() + Math.round(16 * (getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT)),
                                    (float) itemView.getTop() + ((float) itemView.getBottom() - (float) itemView.getTop() - (float) icon.getHeight()) / 2, p);

                        } else {

                            canvas.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom(), p);

                            canvas.drawBitmap(icon,
                                    (float) itemView.getRight() - Math.round(16 * (getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT)) - icon.getWidth(),
                                    (float) itemView.getTop() + ((float) itemView.getBottom() - (float) itemView.getTop() - (float) icon.getHeight()) / 2, p);

                        }
                    } catch (NullPointerException e) {

                        Log.e("ErrorLog", e.toString());

                    }

                }

                super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                alertDialog = new AlertDialog.Builder(NotesListActivity.this)
                        .setTitle(HelperUtils.returnColorString(getString(R.string.confirm_del)))
                        .setMessage(HelperUtils.returnColorString(getString(R.string.del_message)))

               .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                             noteListAdapter.deleteFile(viewHolder.getLayoutPosition());
                             showEmptyListMessage();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        noteListAdapter.cancelDelete(viewHolder.getLayoutPosition());
                    }
                })

                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {


                    }
                })
                        .setIcon(HelperUtils.colorIconDrawable(NotesListActivity.this,R.drawable.ic_delete,oppositeColor))
                .show();
                if (alertDialog.getWindow() != null)
                   alertDialog.getWindow().getDecorView().setBackgroundColor(colorPrimary);


                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(oppositeColor);
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(oppositeColor);

            }
        };

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);

    }

    private void setSearchToolbar() {
       frameLayout.setBackgroundColor(colorPrimary);

        search_toolbar = findViewById(R.id.search_toolbar);

        if(search_toolbar != null){

            search_toolbar.inflateMenu(R.menu.menu_search);
            search_menu = search_toolbar.getMenu();
            item_search = search_menu.findItem(R.id.action_filter_search);

            search_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    circleReveal(true,false);
                }
            });

            item_search.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    toolbar.setVisibility(View.GONE);
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                   circleReveal(true,false);
                    return true;
                }
            });

            initSearchView();
        }
    }

    private void initSearchView() {

        int tempWidgetColor;

        if(oppositeColor == Color.WHITE)
            tempWidgetColor = Color.BLACK;
        else
            tempWidgetColor = Color.WHITE;

        search_toolbar.setBackgroundColor(oppositeColor);
        search_toolbar.setTitleTextColor(colorFont);
        search_toolbar.setCollapseIcon(R.drawable.ic_back);

        Drawable backIcon = search_toolbar.getCollapseIcon();

        if(backIcon != null){

            backIcon.setColorFilter(tempWidgetColor, PorterDuff.Mode.SRC_ATOP);
        }



        MenuItem search = search_menu.findItem(R.id.action_filter_search);
        search.setIcon(HelperUtils.colorIconDrawable(NotesListActivity.this,R.drawable.ic_search,oppositeColor));
        SearchView searchView = (SearchView) search.getActionView();

        EditText text_search = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        text_search.setHint("Search...");
        text_search.setHintTextColor(ColorUtils.setAlphaComponent(tempWidgetColor,120));
        text_search.setTextColor(tempWidgetColor);

        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

        ImageView searchCloseIcon = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        searchCloseIcon.setColorFilter(tempWidgetColor);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                callSearch(query);
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                callSearch(newText);

                return true;
            }

            public void callSearch(String query){

                noteListAdapter.filterList(query.toLowerCase());

            }
        });

    }

    public void circleReveal(final boolean containsOverFlow,final boolean isShow){

        final int startAnimFrom = 2;

        search_toolbar.post(new Runnable() {
            @SuppressLint("ResourceType")
            @Override
            public void run() {

                int width = search_toolbar.getWidth();

                width -= (startAnimFrom * getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material)) - (getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) / 2);

                if(containsOverFlow)
                    width -= getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material);

                int cx = width;
                int cy = search_toolbar.getHeight() / 2;

                Animator anim;

                if (isShow)
                    anim = ViewAnimationUtils.createCircularReveal(search_toolbar, cx, cy, 0, (float) width);
                else
                    anim = ViewAnimationUtils.createCircularReveal(search_toolbar, cx, cy, (float) width, 0);

                // make the view invisible when the animation is done
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (!isShow) {
                            search_toolbar.setVisibility(View.GONE);
                            toolbar.setVisibility(View.VISIBLE);
                            super.onAnimationEnd(animation);
                        }
                    }
                });

                anim.setDuration(220);

                if (isShow)
                    search_toolbar.setVisibility(View.VISIBLE);
                // start the animation
                anim.start();

            }
        });

    }

    @SuppressLint("ResourceAsColor")
    private void applySettings(){

        HelperUtils.applyColors(NotesListActivity.this,toolbar);
        findViewById(R.id.main_layout_coordinate).setBackgroundColor(colorBackground);
        fab_btn.setBackgroundTintList(ColorStateList.valueOf(colorPrimary));
         fab_btn.setColorFilter(oppositeColor);

         if(HelperUtils.isDark(colorBackground))
             txt_empty.setTextColor(Color.WHITE);
         else
             txt_empty.setTextColor(Color.BLACK);




    }

    private void showEmptyListMessage(){

        if(noteListAdapter.getItemCount() == 0){
            txt_empty.setVisibility(View.VISIBLE);
            note_recyclerView.setVisibility(View.GONE);
        }else{

            txt_empty.setVisibility(View.GONE);
            note_recyclerView.setVisibility(View.VISIBLE);

        }



    }

    @Override
    protected void onResume() {
        super.onResume();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (item_search != null && item_search.isActionViewExpanded())
            item_search.collapseActionView();

        noteListAdapter.updateList(HelperUtils.getFiles(NotesListActivity.this),sortAlphabetical);
        showEmptyListMessage();

        findViewById(R.id.main_layout_coordinate).clearFocus();
    }

    @SuppressLint("SuspiciousIndentation")
    @Override
    protected void onPause() {

        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean(PREF_SORT_ALPHABETICAL,sortAlphabetical);
        edit.apply();

        if(alertDialog != null && alertDialog.isShowing())
            alertDialog.dismiss();
            alertDialog = null;


        super.onPause();
    }

    public void newNote(View view) {

        startActivity(NoteActivity.getStartIntent(NotesListActivity.this,""));
    }
}