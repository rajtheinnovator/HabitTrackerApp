package com.example.android.habittracker;
/**
 * Created by ABHISHEK RAJ on 10/9/2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.habittracker.data.CodeContract.CodeEntry;
import com.example.android.habittracker.data.CodeDbHelper;

/**
 * Displays list of habittracker that were entered and stored in the app.
 */
public class HabbitActivity extends AppCompatActivity {
    private CodeDbHelper mCodeDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HabbitActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        displayDatabaseInfo();
        mCodeDbHelper = new CodeDbHelper(this);
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the habittracker database.
     */
    private void displayDatabaseInfo() {
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        CodeDbHelper mCodeDbHelper = new CodeDbHelper(this);

        // Create and/or open a database to read from it
        SQLiteDatabase db = mCodeDbHelper.getReadableDatabase();
        // Use query method on Database object making use of projection, selection and selection argument
        String[] projection = {CodeEntry._ID, CodeEntry.COLUMN_LANGUAGE,
                CodeEntry.COLUMN_PRACTICE_HOURS, CodeEntry.COLUMN_FEELING, CodeEntry.COLUMN_MODE};
        Cursor cursor = db.query(CodeEntry.TABLE_NAME, projection,
                null, null,
                null, null, null);
        TextView displayView = (TextView) findViewById(R.id.text_view_code_record);
        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // habittracker table in the database).
            displayView.setText("Code database contains: " + cursor.getCount() + " code history \n\n");
            displayView.append(CodeEntry._ID + "-" + CodeEntry.COLUMN_LANGUAGE + "-" + CodeEntry.COLUMN_PRACTICE_HOURS + "-" +
                    CodeEntry.COLUMN_FEELING + "-" + CodeEntry.COLUMN_MODE + "\n");
            int idColumnIndex = cursor.getColumnIndex(CodeEntry._ID);
            int languageNameColumnIndex = cursor.getColumnIndex(CodeEntry.COLUMN_LANGUAGE);
            int practiceHoursColumnIndex = cursor.getColumnIndex(CodeEntry.COLUMN_PRACTICE_HOURS);
            int feelingColumnIndex = cursor.getColumnIndex(CodeEntry.COLUMN_FEELING);
            int modeColumnIndex = cursor.getColumnIndex(CodeEntry.COLUMN_MODE);
            while (cursor.moveToNext()) {
                int currentId = cursor.getInt(idColumnIndex);
                String currentLanguageName = cursor.getString(languageNameColumnIndex);
                String currentPracticeHours = cursor.getString(practiceHoursColumnIndex);
                String currentFeeling = String.valueOf(cursor.getInt(feelingColumnIndex));
                String currentMode = String.valueOf(cursor.getInt(modeColumnIndex));
                displayView.append("\n" + currentId + "-" + currentLanguageName + "-" + currentPracticeHours + "-" +
                        currentFeeling + "-" + currentMode);
            }

        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
            db.close();
        }
    }

    private void insertCode() {
        // Gets the data repository in write mode
        SQLiteDatabase db = mCodeDbHelper.getWritableDatabase();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(CodeEntry.COLUMN_LANGUAGE, "JAVA");
        values.put(CodeEntry.COLUMN_PRACTICE_HOURS, "7");
        values.put(CodeEntry.COLUMN_FEELING, CodeEntry.FEELING_DUBIOUS);
        values.put(CodeEntry.COLUMN_MODE, CodeEntry.MODE_OFFLINE);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(CodeEntry.TABLE_NAME, null, values);
    }

    private void deleteDataBase() {
//        /*If you want to delete Just the table entries but then _ID will not reset*/
//        SQLiteDatabase db = mCodeDbHelper.getWritableDatabase();
//        boolean succeeded = db.delete(CodeEntry.TABLE_NAME, null, null) > 0;
            /*Get the context which is this activity*/
        Context context = HabbitActivity.this;
        /*then delete the database*/
        context.deleteDatabase("code.db");
        mCodeDbHelper = new CodeDbHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertCode();
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteDataBase();
                displayDatabaseInfo();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}