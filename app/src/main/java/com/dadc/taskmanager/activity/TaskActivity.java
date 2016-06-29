package com.dadc.taskmanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.dadc.taskmanager.R;
import com.dadc.taskmanager.model.Task;
import com.dadc.taskmanager.util.ControlDataTask;

/**
 * Created by bomko on 27.05.16.
 */
public class TaskActivity extends AppCompatActivity {

    private static final String KEY_SUBMIT_TASK = "submit_task";
    private static final String KEY_POSITION_ITEM = "position_item";
    private static final String KEY_EDIT_ITEM = "edit_item";
    private static final String KEY_TITLE_ACTIVITY = "title_edit_task";
    int REQUEST_CODE_TITLE = 5;
    int REQUEST_CODE_DESCRIPTION = 6;
    private EditText mEditTextTitle, mEditTextDescription;
    private int mPosition, mDefaultTaskColor;
    TextInputLayout inputLayoutTitle, inputLayoutDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_task_activity);

        // ActionBar mActionBar = getSupportActionBar();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);

        mEditTextTitle = (EditText) findViewById(R.id.titleEditText);
        mEditTextDescription = (EditText) findViewById(R.id.descriptionEditText);

        // get data from intent for edit item content
        Task mTaskEdit = getIntent().getParcelableExtra(KEY_EDIT_ITEM);
        mPosition = getIntent().getIntExtra(KEY_POSITION_ITEM, -1);
        String mTitleActivity = getIntent().getStringExtra(KEY_TITLE_ACTIVITY);


        inputLayoutTitle = (TextInputLayout) findViewById(R.id.input_layout_title);
        inputLayoutDesc = (TextInputLayout) findViewById(R.id.input_layout_desc);

        mEditTextTitle.addTextChangedListener(new MyTextWatcher(mEditTextTitle));
        mEditTextDescription.addTextChangedListener(new MyTextWatcher(mEditTextDescription));

        if (mTaskEdit != null) {
            mEditTextTitle.setText(mTaskEdit.getTitle());
            mEditTextDescription.setText(mTaskEdit.getDescription());
            getSupportActionBar().setTitle(mTitleActivity);
        }

        ControlDataTask mControlDataTask = new ControlDataTask(this);

        mDefaultTaskColor = mControlDataTask.getDateDefaultColor();

        //Input voice Title
        mEditTextTitle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (mEditTextTitle.getRight() - mEditTextTitle.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

                        startActivityForResult(intent, REQUEST_CODE_TITLE);
                        return true;
                    }
                }
                return false;
            }
        });


        //Input voice Description
        mEditTextDescription.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (mEditTextDescription.getRight() - mEditTextDescription.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

                        startActivityForResult(intent, REQUEST_CODE_DESCRIPTION);

                        return true;
                    }
                }
                return false;
            }
        });
    }


    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_new_task, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_TITLE && resultCode == RESULT_OK) {

            mEditTextTitle.setText(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));

        } else if (requestCode == REQUEST_CODE_DESCRIPTION && resultCode == RESULT_OK) {

            mEditTextDescription.setText(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_done:

                submitForm();

                return true;

            case android.R.id.home:

                finish();

                return true;

            default:
                return true;
        }


    }

    private void submitForm() {
        String mTitle = mEditTextTitle.getText().toString();
        String mDescription = mEditTextDescription.getText().toString();

        if (validateTitle() && validateDesc()) {

            Task mTask = new Task(mTitle, mDescription, mDefaultTaskColor, 0, 0);

            Intent intent = new Intent();
            intent.putExtra(KEY_SUBMIT_TASK, mTask);

            if (mPosition >= 0) {
                intent.putExtra(KEY_POSITION_ITEM, mPosition);
            }

            setResult(RESULT_OK, intent);
            finish();

        }
    }

    private boolean validateTitle() {
        String mTitle = mEditTextTitle.getText().toString();
        inputLayoutTitle.setError(null);
        if (mTitle.length() <= 4) {
            inputLayoutTitle.setErrorEnabled(true);
            requestFocus(mEditTextTitle);
            inputLayoutTitle.setError("Поле порожне або немає 5 символів.");

            return false;

        } else {
            inputLayoutTitle.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateDesc() {

        String mDescription = mEditTextDescription.getText().toString();
        inputLayoutDesc.setError(null);

        if (mDescription.length() == 0) {
            requestFocus(mEditTextDescription);
            inputLayoutDesc.setErrorEnabled(true);
            inputLayoutDesc.setError("Це поле не може бути порожнім.");
            return false;

        } else {
            inputLayoutDesc.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {

        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.titleEditText:
                    validateTitle();
                    break;
                case R.id.descriptionEditText:
                    validateDesc();
                    break;

            }
        }
    }
}