package com.labinot.note_app_v1;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import java.io.File;
import java.util.List;

public class NoteDiffCallBack extends DiffUtil.Callback {

    private final List<File> newList;
    private final List<File> oldList;


    public NoteDiffCallBack(List<File> oldList, List<File> newList) {

        this.oldList = oldList;
        this.newList= newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {

        return oldList.get(oldItemPosition).getName().equals(newList.get(newItemPosition).getName());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).lastModified() < (System.currentTimeMillis() - 5000);
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
