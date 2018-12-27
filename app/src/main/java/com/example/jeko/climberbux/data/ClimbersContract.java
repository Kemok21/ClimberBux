package com.example.jeko.climberbux.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class ClimbersContract {

    private ClimbersContract() {}

    public static class ClimbersEntry implements BaseColumns {

        //Table name
        public static final String TABLE_NAME = "climbers";

        public static final String CONTENT_AUTHORITY = "com.example.jeko.climberbux";
        public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
        public static final String PATH_CLIMBERS = "climbers";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CLIMBERS);
        //Columns
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "climber_name";
        public static final String COLUMN_GENDER = "gender";
        public static final String COLUMN_AGE = "age";
        public static final String COLUMN_RANK = "rank";
        public static final String COLUMN_TYPE_PAYMENT = "type_payment";
        public static final String COLUMN_PAYED = "payed";
        public static final String COLUMN_VISITS = "visits";
        public static final String COLUMN_PHOTO = "photo";

        //Constant for gender
        public static final int GENDER_UNKNOWN = 0;
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;

        //Constant for rank
        public static final int RANK_BR = 0;
        public static final int RANK_THREE = 1;
        public static final int RANK_TWO = 2;
        public static final int RANK_ONE = 3;
        public static final int RANK_KMS = 4;
        public static final int RANK_MS = 5;


        //Constant for payment type
        public static final int TYPE_PAYMENT_SINGLE = 0;
        public static final int TYPE_PAYMENT_SUBSCRIPTION = 1;
        public static final int TYPE_PAYMENT_CERTIFICATE = 2;
        public static final int TYPE_PAYMENT_SPECIAL = 3;

        /**
         * The MIME type of {@link #CONTENT_URI} for a list of climbers.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CLIMBERS;

        /**
         * The MIME type of {@link #CONTENT_URI} for a single climber.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CLIMBERS;

        /**
         * Returns whether or not the given gender is {@link #GENDER_UNKNOWN}, {@link #GENDER_MALE}, {@link #GENDER_FEMALE}.
         */
        public static boolean isValidGender(int gender) {
            if (gender == GENDER_UNKNOWN || gender == GENDER_MALE || gender == GENDER_FEMALE) {
                return true;
            }
            return false;
        }

        /**
         * Returns whether or not the given payment type is {@link #TYPE_PAYMENT_SINGLE}, {@link #TYPE_PAYMENT_SUBSCRIPTION},
         * {@link #TYPE_PAYMENT_CERTIFICATE}, {@link #TYPE_PAYMENT_SPECIAL}.
         */
        public static boolean isValidPayment(int payment) {
            if (payment == TYPE_PAYMENT_SINGLE || payment == TYPE_PAYMENT_SUBSCRIPTION ||
                    payment == TYPE_PAYMENT_CERTIFICATE || payment == TYPE_PAYMENT_SPECIAL) {
                return true;
            }
            return false;
        }

        /**
         * Returns whether or not the given rank is {@link #RANK_BR}, {@link #RANK_THREE},
         * {@link #RANK_TWO}, {@link #RANK_ONE}, {@link #RANK_KMS}, {@link #RANK_MS},
         */
        public static boolean isValidRank(int rank) {
            if (rank == RANK_BR || rank == RANK_THREE || rank == RANK_TWO ||
                    rank == RANK_ONE || rank == RANK_KMS || rank == RANK_MS) {
                return true;
            }
            return false;
        }
    }

    public static class PaymentsEntry implements BaseColumns {

        //Table name
        public static final String TABLE_NAME = "payments";

        public static final String CONTENT_AUTHORITY = "com.example.jeko.climberbux";
        public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
        public static final String PATH_PAYMENTS = "payments";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PAYMENTS);
        //Columns
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_CLIMBER_ID = "climber_id";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_PAYED = "payed";

        /**
         * The MIME type of {@link #CONTENT_URI} for a list of payments.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PAYMENTS;

        /**
         * The MIME type of {@link #CONTENT_URI} for a single payment.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PAYMENTS;
    }
}
