package com.example.jeko.climberbux;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jeko.climberbux.data.ClimbersContract.PaymentsEntry;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DateAdapter extends ArrayAdapter<Date> {


    public DateAdapter(@NonNull Activity context, @NonNull ArrayList<Date> objects) {
        super(context, 0, objects);
    }
    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);

    String[] projection = new String[]{
            PaymentsEntry._ID,
            PaymentsEntry.COLUMN_CLIMBER_NAME,
            PaymentsEntry.COLUMN_PAYED_TO_GRAN,
            PaymentsEntry.COLUMN_PAYED_TO_ME
    };

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_on_dates, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Date currentDate = getItem(position);



        holder.dateTextView.setText(currentDate.getTrainingDate());
        holder.countOfClimbersTextView.setText(currentDate.getCountClimber());
        holder.sumOfIncomeTextView.setText(currentDate.getTrainingIncome());
//        if(holder.detailsLinearLayout.getChildCount() > 0) {
//            holder.detailsLinearLayout.removeAllViews();
//        }
//        for(long id : currentDate.getPaymentIdList()) {
//            holder.detailsLinearLayout.addView(addLayoutsOfClimbers(id));
//        }
        return convertView;
    }

//    private View addLayoutsOfClimbers(long id) {
//
//            LinearLayout layout = new LinearLayout(getContext());
//            layout.setOrientation(LinearLayout.HORIZONTAL);
//
//            Uri currentPaymentUri = ContentUris.withAppendedId(PaymentsEntry.CONTENT_URI, id);
//            Cursor cursor = getContext().getContentResolver().query(
//                    currentPaymentUri,
//                    projection,
//                    null,
//                    null,
//                    null
//            );
//            cursor.moveToFirst();
//            int nameColumnIndex = cursor.getColumnIndexOrThrow(PaymentsEntry.COLUMN_CLIMBER_NAME);
//            int payedToGranColumnIndex = cursor.getColumnIndexOrThrow(PaymentsEntry.COLUMN_PAYED_TO_GRAN);
//            int payedToMeColumnIndex = cursor.getColumnIndexOrThrow(PaymentsEntry.COLUMN_PAYED_TO_ME);
//
//            String name = cursor.getString(nameColumnIndex);
//            int payed = cursor.getInt(payedToGranColumnIndex) + cursor.getInt(payedToMeColumnIndex);
//            cursor.close();
//
//            TextView nameTextView = new TextView(getContext());
//            nameTextView.setText(name);
//            nameTextView.setLayoutParams(lp);
//            layout.addView(nameTextView);
//
//            TextView payedTextView = new TextView(getContext());
//            payedTextView.setText(String.valueOf(payed));
//            payedTextView.setLayoutParams(lp);
//            layout.addView(payedTextView);
//
//        return layout;
//    }

    public class ViewHolder {
        @BindView(R.id.date_text_view)
        TextView dateTextView;

        @BindView(R.id.count_of_climbers_text_view)
        TextView countOfClimbersTextView;

        @BindView(R.id.sum_of_income_text_view)
        TextView sumOfIncomeTextView;

//        @BindView(R.id.details_linear_layout)
//        LinearLayout detailsLinearLayout;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
