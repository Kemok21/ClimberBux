package com.example.jeko.climberbux;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import butterknife.ButterKnife;

public class OnDateCursorAdapter extends CursorAdapter {
    private LayoutInflater mInflate;

    public OnDateCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        mInflate = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }

    static class ViewHolder {

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
