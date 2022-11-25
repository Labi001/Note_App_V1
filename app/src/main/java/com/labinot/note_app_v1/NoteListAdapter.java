package com.labinot.note_app_v1;

import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.ViewHolder> {

    private List<File> fullList,fileList;
    int colorText,colorBackground;


    public NoteListAdapter(int colorText,int colorBackground) {

        fullList = new ArrayList<>();
        fileList = new ArrayList<>();
        this.colorText = colorText;
        this.colorBackground = colorBackground;
    }

    @NonNull
    @Override
    public NoteListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteListAdapter.ViewHolder holder, int position) {

        File file = fileList.get(position);
        String fileName = file.getName().substring(0,file.getName().length()-4);
        String fileDate = DateFormat.getDateInstance(DateFormat.MEDIUM).format(file.lastModified());
        String fileTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(file.lastModified());
        holder.setData(fileName,fileDate,fileTime);

    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public void updateList(List<File> files,boolean sortAlphabetical) {

        fileList = files;
        sortList(sortAlphabetical);
        fullList = new ArrayList<>(fileList);

    }

    void sortList(boolean sortAlphabetic) {

        if(sortAlphabetic){

            sortAlphabetical(fileList);
        }else{

            sortData(fileList);
        }

        DiffUtil.calculateDiff(new NoteDiffCallBack(fullList,fileList)).dispatchUpdatesTo(this);
        fullList = new ArrayList<>(fileList);
    }


    private void sortAlphabetical(List<File> list) {

        Collections.sort(list, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return (o1.getName().compareTo(o2.getName()));
            }
        });

    }

    private void sortData(List<File> files) {

        Collections.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return Long.compare (o1.lastModified(),o2.lastModified());
            }
        });

    }

    public void deleteFile(int position) {

        File file = fileList.get(position);
        fullList.remove(file);
        fileList.remove(file);
        notifyItemRemoved(position);
        file.delete();
    }

    public void cancelDelete(int position) {

        notifyItemChanged(position);
    }

    public void filterList(String text) {

        if(TextUtils.isEmpty(text.toLowerCase())){

            DiffUtil.calculateDiff(new NoteDiffCallBack(fileList,fullList)).dispatchUpdatesTo(this);
            fileList = new ArrayList<>(fullList);

        }else{
            fileList.clear();

            for(int i = 0; i < fullList.size();i++){

                final File file = fullList.get(i);
                String fileName = file.getName().substring(0,file.getName().length() - 4).toLowerCase();
                if(fileName.contains(text))
                    fileList.add(fullList.get(i));

                DiffUtil.calculateDiff(new NoteDiffCallBack(fullList,fileList)).dispatchUpdatesTo(this);

            }

        }


    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView noteTitle,noteDate,noteTime;
        ConstraintLayout constraintLayout;
        CardView mCardView;
        private String stringTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            noteTitle = itemView.findViewById(R.id.text_title);
            noteDate = itemView.findViewById(R.id.text_date);
            noteTime = itemView.findViewById(R.id.text_time);

            constraintLayout = itemView.findViewById(R.id.layout_constraint);
            constraintLayout.setBackgroundColor(colorBackground);

            mCardView = itemView.findViewById(R.id.mCardView);

            mCardView.setCardBackgroundColor(Color.WHITE);
            noteTitle.setTextColor(colorText);
            noteDate.setTextColor(colorText);
            noteTime.setTextColor(colorText);

            itemView.setOnClickListener(this);

        }

        void setData(String title,String data,String time){

            stringTitle = title;

            noteTitle.setText(title);
            noteDate.setText(data);
            noteTime.setText(time);


        }

        @Override
        public void onClick(View v) {

            itemView.getContext().startActivity(new Intent(NoteActivity.getStartIntent(itemView.getContext(),stringTitle)));

        }
    }
}
