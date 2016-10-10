package com.example.android.habittracker;
/**
 * Created by ABHISHEK RAJ on 10/9/2016.
 */
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.habittracker.data.CodeContract.CodeEntry;
import com.example.android.habittracker.data.CodeDbHelper;

public class EditorActivity extends AppCompatActivity {
    private CodeDbHelper mCodeDbHelper;
    /**
     * EditText field to enter the Programming Language Name
     */
    private EditText mLanguageNameEditText;
    /**
     * EditText field to enter the Programming Practice Hours
     */
    private EditText mCodePracticeHoursEditText;
    /**
     * Spinner to enter the mode of programming practice
     */
    private Spinner mModeSpinner;
    /**
     * Spinner to enter the feeling of the user
     */
    private Spinner mFeelingSpinner;

    /**
     * Feeling of the user. The possible values are:
     * 0 for dubious feeling, 1 for good feeling, 2 for feeling confident.
     */
    private int mFeeling = CodeEntry.FEELING_DUBIOUS;

    private int mMode = CodeEntry.MODE_OFFLINE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from
        mLanguageNameEditText = (EditText) findViewById(R.id.edit_code_language_name);
        mCodePracticeHoursEditText = (EditText) findViewById(R.id.edit_code_practice_hours);
        mModeSpinner = (Spinner) findViewById(R.id.spinner_mode);
        mFeelingSpinner = (Spinner) findViewById(R.id.spinner_feeling);
        setupFeelingSpinner();
        setupModeSpinner();
    }

    /**
     * Setup the dropdown spinner that allows to select the feeling of the user.
     */
    private void setupFeelingSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter feelingSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_feeling_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        feelingSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mFeelingSpinner.setAdapter(feelingSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mFeelingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.feeling_dubious))) {
                        mFeeling = CodeEntry.FEELING_DUBIOUS; // dubious
                    } else if (selection.equals(getString(R.string.feeling_good))) {
                        mFeeling = CodeEntry.FEELING_GOOD; // good
                    } else {
                        mFeeling = CodeEntry.FEELING_CONFIDENT; // confident
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mFeeling = CodeEntry.FEELING_DUBIOUS; // dubious
            }
        });
    }

    /**
     * Setup the dropdown spinner that allows to select the mode of study of the user.
     */
    private void setupModeSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter modeSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_mode_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        modeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mModeSpinner.setAdapter(modeSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.mode_offline))) {
                        mMode = CodeEntry.MODE_OFFLINE; // offline
                    } else {
                        mMode = CodeEntry.MODE_ONLINE; // online
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mMode = CodeEntry.MODE_OFFLINE; // Unknown
            }
        });
    }

    //Gets user input from editor and saves them into the database
    private void insertCode() {
        String languageNameString = mLanguageNameEditText.getText().toString().trim();
        int mCodePracticeHoursString = Integer.parseInt(mCodePracticeHoursEditText.getText().toString().trim());
        // Gets the data repository in write mode
        // Create database helper
        CodeDbHelper mCodeDbHelper = new CodeDbHelper(this);
        SQLiteDatabase db = mCodeDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(CodeEntry.COLUMN_LANGUAGE, languageNameString);
        values.put(CodeEntry.COLUMN_PRACTICE_HOURS, mCodePracticeHoursString);
        values.put(CodeEntry.COLUMN_FEELING, mFeeling);
        values.put(CodeEntry.COLUMN_MODE, mMode);

        // Insert the new row, returning the primary key value of the new row
        db.insert(CodeEntry.TABLE_NAME, null, values);
        Toast.makeText(this, "one column added", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                insertCode();
                //Exit Activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}