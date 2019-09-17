package com.example.jeko.climberbux;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IncomeAdapter extends ArrayAdapter<PayedByMonth> {

    public IncomeAdapter(Activity context, ArrayList<PayedByMonth> object) {
        super(context, 0, object);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_income, parent, false);
            holder = new IncomeAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (IncomeAdapter.ViewHolder) convertView.getTag();
        }

        final PayedByMonth currentPayedByMonth = getItem(position);

        holder.dateTextView.setText(currentPayedByMonth.getMonth());
        holder.toGranValueTextView.setText(String.valueOf(currentPayedByMonth.getIncomeToGran()));
        holder.toMeValueTextView.setText(String.valueOf(currentPayedByMonth.getIncomeToMe()));

        return convertView;
    }

    public class ViewHolder {
        @BindView(R.id.date_text_view)
        TextView dateTextView;

        @BindView(R.id.to_gran_value)
        TextView toGranValueTextView;

        @BindView(R.id.to_me_value)
        TextView toMeValueTextView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
