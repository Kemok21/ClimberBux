package com.example.jeko.climberbux;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.jeko.climberbux.data.ClimbersContract;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ClimberCursorAdapter extends CursorAdapter {
    private LayoutInflater mInflate;

    public ClimberCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        mInflate = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = mInflate.inflate(R.layout.list_climber, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        // Find the columns of climber attributes that we're interested in
        int idColumnIndex = cursor.getColumnIndexOrThrow(ClimbersContract.ClimbersEntry._ID);
        int nameColumnIndex = cursor.getColumnIndexOrThrow(ClimbersContract.ClimbersEntry.COLUMN_NAME);
        int genderColumnIndex = cursor.getColumnIndexOrThrow(ClimbersContract.ClimbersEntry.COLUMN_GENDER);
        int ageColumnIndex = cursor.getColumnIndexOrThrow(ClimbersContract.ClimbersEntry.COLUMN_AGE);

        // Extract properties from cursor
        final long climberId = cursor.getLong(idColumnIndex);
        final String name = cursor.getString(nameColumnIndex);
        final int gender = cursor.getInt(genderColumnIndex);
        final int age = cursor.getInt(ageColumnIndex);

        holder.climberName.setText(name);
        String[] genders = context.getResources().getStringArray(R.array.array_gender_option);
        holder.climberGender.setText(genders[gender]);
        if (age != 0) holder.climberAge.setText(String.valueOf(age));
        else holder.climberAge.setText("—");
    }

    static class ViewHolder {
        // Find fields to populate in inflated template
        @BindView(R.id.name)
        TextView climberName;
        @BindView(R.id.gender)
        TextView climberGender;
        @BindView(R.id.age)
        TextView climberAge;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}