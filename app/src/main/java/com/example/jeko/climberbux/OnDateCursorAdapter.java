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

public class OnDateCursorAdapter extends CursorAdapter {
    //    private ArrayList<String> dates = new ArrayList<>();
    private LayoutInflater mInflate;
//    private int countOfClimbers = 0;
//    private int countOfPayed = 0;

    public OnDateCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        mInflate = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = mInflate.inflate(R.layout.list_on_dates, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();

        // Find the columns of payments attributes that we're interested in
        int dateColumnIndex = cursor.getColumnIndexOrThrow(ClimbersContract.PaymentsEntry.COLUMN_DATE);
        int climberNameColumnIndex = cursor.getColumnIndexOrThrow(ClimbersContract.PaymentsEntry.COLUMN_CLIMBER_NAME);
        int payedGranColumnIndex = cursor.getColumnIndexOrThrow(ClimbersContract.PaymentsEntry.COLUMN_PAYED_TO_GRAN);
        int payedMeColumnIndex = cursor.getColumnIndexOrThrow(ClimbersContract.PaymentsEntry.COLUMN_PAYED_TO_ME);

        // Extract properties from cursor
        String date = cursor.getString(dateColumnIndex);
        String climberName = cursor.getString(climberNameColumnIndex);
        int payedGarn = cursor.getInt(payedGranColumnIndex);
        int payedMy = cursor.getInt(payedMeColumnIndex);

//        if (dates.size() == 0 || dates.contains(date)) {
//            countOfClimbers += 1;
//            countOfPayed += payed;
//            dates.add(date);
//        } else {
//            holder.totalRelativeLayout.setVisibility(RelativeLayout.VISIBLE);
//            holder.trainingDateTotal.setText(dates.get(dates.size()-1));
//            holder.numberOfClimbers.setText(String.valueOf(countOfClimbers));
//            holder.incomeForTraining.setText(String.valueOf(countOfPayed));
//
//            countOfClimbers = 1;
//            countOfPayed = payed;
//            dates.add(date);
//        }
        holder.trainingDate.setText(date);
        holder.nameOfClimber.setText(String.valueOf(climberName));
        holder.payedFromClimber.setText(String.valueOf(payedGarn + payedMy));
    }

    static class ViewHolder {
        @BindView(R.id.date_text_view)
        TextView trainingDate;
        @BindView(R.id.income_text_view)
        TextView nameOfClimber;
        @BindView(R.id.climbers_text_view)
        TextView payedFromClimber;

//        @BindView(R.id.total_relative_layout)
//        RelativeLayout totalRelativeLayout;
//        @BindView(R.id.date_text_view_total)
//        TextView trainingDateTotal;
//        @BindView(R.id.number_of_climber_text_view)
//        TextView numberOfClimbers;
//        @BindView(R.id.income_text_view)
//        TextView incomeForTraining;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
