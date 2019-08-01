package com.example.jeko.climberbux;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.jeko.climberbux.data.ClimbersContract.ClimbersEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ClimbersActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CLIMBER_LOADER = 0;
    ClimberCursorAdapter mCursorAdapter;

    @BindView(R.id.list_view_climber)
    ListView climberListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_climbers);
        ButterKnife.bind(this);

        mCursorAdapter = new ClimberCursorAdapter(this, null);
        climberListView.setAdapter(mCursorAdapter);

        climberListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ClimbersActivity.this, EditorActivity.class);

                Uri currentClimberUri = ContentUris.withAppendedId(ClimbersEntry.CONTENT_URI, id);
                intent.setData(currentClimberUri);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(CLIMBER_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_climbers, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case R.id.action_add:
                Intent intent = new Intent(ClimbersActivity.this, EditorActivity.class);
                startActivity(intent);
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(ClimbersActivity.this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // These are the Contacts rows that we will retrieve
        String[] projection = new String[]{
                ClimbersEntry._ID,
                ClimbersEntry.COLUMN_NAME,
                ClimbersEntry.COLUMN_GENDER,
                ClimbersEntry.COLUMN_AGE
        };
        return new CursorLoader(
                this,
                ClimbersEntry.CONTENT_URI,
                projection,
                null, null, null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
