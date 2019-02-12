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

public class TrainingAdapter extends ArrayAdapter<Climber> {

    public TrainingAdapter(Activity context, ArrayList<Climber> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;
//        View listItemView = convertView;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_training, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Climber currentClimber = getItem(position);
        // Сумма PaymentGran и PaymentMe
        int payment = Integer.parseInt(currentClimber.getPaymentGran()) + Integer.parseInt(currentClimber.getPaymentMe());

        holder.nameClimberTextView.setText(currentClimber.getName());
        holder.typePaymentTextView.setText(currentClimber.getTypePayment());
        holder.paymentTextView.setText(String.valueOf(payment));

        return convertView;
    }

    public class ViewHolder {
        @BindView(R.id.name)
        TextView nameClimberTextView;

        @BindView(R.id.type_payment)
        TextView typePaymentTextView;

        @BindView(R.id.payment)
        TextView paymentTextView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
