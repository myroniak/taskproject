package com.dadc.taskmanager.activity;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;

import com.dadc.taskmanager.R;
import com.dadc.taskmanager.enumstate.ButtonType;
import com.dadc.taskmanager.helper.ImageHelper;
import com.dadc.taskmanager.model.Task;
import com.dadc.taskmanager.util.ManagerData;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Created by bomko on 27.05.16.
 */
public class TaskActivity extends AppCompatActivity {

    private static final String ACTION_INTENT_CAMERA = "android.media.action.IMAGE_CAPTURE";
    private static final String KEY_TITLE_ACTIVITY = "title_edit_task";
    private static final String KEY_POSITION_ITEM = "position_item";
    private static final String KEY_SUBMIT_TASK = "submit_task";
    private static final String KEY_EDIT_ITEM = "edit_item";
    private static final String KEY_URI_CROP = "cropped";
    private static final int REQUEST_CODE_DESCRIPTION = 6;
    private static final int REQUEST_CODE_TITLE = 5;

    private String mTitle, mDescription;
    private String pathImage;
    private int mPosition, mDefaultTaskColor;
    private long mMaxTimeTask;

    private TextInputLayout inputLayoutTitle, inputLayoutDesc;
    private EditText mEditTextTitle, mEditTextDescription;
    private ImageView mImageViewAvatar;
    private ImageHelper mImageHelper;
    private Button mButtonSetTime;
    private ActionBar mActionBar;
    private Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_task_activity);

        initToolBar();
        initView();


        Intent intent = getIntent();
        ManagerData mManagerData = ManagerData.getInstance(this);
       // ControlDataTask controlDataTask = new ControlDataTask(this);
        mDefaultTaskColor = mManagerData.getDateDefaultColor();
        mMaxTimeTask = mManagerData.defaultTime();
        mImageHelper = new ImageHelper(this);

        //Get data from intent for edit item content
        if (intent.hasExtra(KEY_EDIT_ITEM)) {

            Task mTaskEdit = getIntent().getParcelableExtra(KEY_EDIT_ITEM);
            String mTitleActivity = getIntent().getStringExtra(KEY_TITLE_ACTIVITY);

            if (mActionBar != null) {
                mActionBar.setTitle(mTitleActivity);
            }

            mMaxTimeTask = mTaskEdit.getMaxTime();
            mEditTextTitle.setText(mTaskEdit.getTitle());
            mEditTextDescription.setText(mTaskEdit.getDescription());
            mPosition = getIntent().getIntExtra(KEY_POSITION_ITEM, -1);
            Bitmap bmp = BitmapFactory.decodeFile(mTaskEdit.getUrl());
           if(bmp!=null) {
               mImageViewAvatar.setImageBitmap(bmp);
           }else{
               mImageViewAvatar.setImageResource(R.drawable.no_avatar);

           }
        }

        mButtonSetTime.setText(DateFormat.getTimeFormat(TaskActivity.this).format(new Date(mMaxTimeTask)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_done:
                submitResult();
                return true;

            case android.R.id.home:
                finish();
                return true;

            default:
                return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case REQUEST_CODE_TITLE:
                    mEditTextTitle.setText(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
                    break;

                case REQUEST_CODE_DESCRIPTION:

                    mEditTextDescription.setText(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
                    break;
                case 7:
                    File file = new File(Environment.getExternalStorageDirectory() + File.separator + "image.jpg");
                    mUri = cropImage(Uri.fromFile(file));

                    break;
                case 8:
                    mUri = cropImage(data.getData());
                case Crop.REQUEST_CROP:
                    String fileName = UUID.randomUUID().toString() + ".jpg";

                    int widthImage = mImageViewAvatar.getWidth();
                    int heightImage = mImageViewAvatar.getHeight();

                    mImageHelper
                            .setHeightWidth(heightImage, widthImage)
                            .setUri(mUri)
                            .setFileName(fileName)
                            .save();

                    Bitmap bitmap = mImageHelper
                            .setFileName(fileName)
                            .load();

                    mImageViewAvatar.setImageBitmap(bitmap);
                    pathImage = mImageHelper.getFileDir();

                    break;
                default:
                    break;
            }
        }
    }

    public Uri cropImage(Uri uri) {

        String uuid = UUID.randomUUID().toString();
        Uri destination = Uri.fromFile(new File(getCacheDir(), KEY_URI_CROP + uuid));
        Crop.of(uri, destination).asSquare().start(this);
        return destination;
    }

    private void submitResult() {
        Intent intent = new Intent();

        if (validateTitle() && validateDescription()) {
            Task mTask = new Task(UUID.randomUUID().toString(), mTitle, mDescription, mDefaultTaskColor, 0, 0, mMaxTimeTask, ButtonType.PLAY.name(), pathImage);
            Log.d("myLog", "mid: " + getUUID());

            intent.putExtra(KEY_POSITION_ITEM, mPosition);
            intent.putExtra(KEY_SUBMIT_TASK, mTask);

            setResult(RESULT_OK, intent);
            finish();

        }
    }

    private boolean validateTitle() {
        mTitle = mEditTextTitle.getText().toString();

        inputLayoutTitle.setError(null);

        if (mTitle.length() <= 4) {
            inputLayoutTitle.setErrorEnabled(true);
            requestFocus(mEditTextTitle);
            inputLayoutTitle.setError(getResources().getString(R.string.error_title));

            return false;

        } else {
            inputLayoutTitle.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateDescription() {
        mDescription = mEditTextDescription.getText().toString();

        inputLayoutDesc.setError(null);

        if (mDescription.length() == 0) {
            requestFocus(mEditTextDescription);
            inputLayoutDesc.setErrorEnabled(true);
            inputLayoutDesc.setError(getResources().getString(R.string.error_description));
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

    private String getUUID() {
        return UUID.randomUUID().toString();
    }

    public void timePicker() {

        final Calendar currentTime = Calendar.getInstance();

        currentTime.setTimeInMillis(mMaxTimeTask);

        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);

        final TimePickerDialog picker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                currentTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                currentTime.set(Calendar.MINUTE, selectedMinute);

                mMaxTimeTask = currentTime.getTimeInMillis();
                mButtonSetTime.setText(DateFormat.getTimeFormat(TaskActivity.this).format(new Date(mMaxTimeTask)));

            }
        }, hour, minute, true);

        picker.show();
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);
        }
    }

    private void initView() {

        mEditTextDescription = (EditText) findViewById(R.id.descriptionEditText);
        mEditTextTitle = (EditText) findViewById(R.id.titleEditText);
        inputLayoutTitle = (TextInputLayout) findViewById(R.id.input_layout_title);
        inputLayoutDesc = (TextInputLayout) findViewById(R.id.input_layout_desc);
        mButtonSetTime = (Button) findViewById(R.id.buttonTime);

        mImageViewAvatar = (ImageView) findViewById(R.id.imageViewAvatar);

        mEditTextTitle.addTextChangedListener(new CustomTextWatcher(mEditTextTitle));
        mEditTextDescription.addTextChangedListener(new CustomTextWatcher(mEditTextDescription));

        mTitle = mEditTextTitle.getText().toString();
        mDescription = mEditTextDescription.getText().toString();

        mImageViewAvatar.setImageResource(R.drawable.no_avatar);

        mImageViewAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder getImageFrom = new AlertDialog.Builder(TaskActivity.this);
                getImageFrom.setTitle(getResources().getString(R.string.title_chooser));
                final CharSequence[] opsChars = getResources().getStringArray(R.array.array);
                getImageFrom.setItems(opsChars, new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (which == 0) {
                            Intent intent = new Intent(ACTION_INTENT_CAMERA);
                            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "image.jpg");
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                            startActivityForResult(intent, 7);

                        } else if (which == 1) {
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto, 8);
                        }
                        dialog.dismiss();
                    }
                });
                getImageFrom.show();

            }
        });
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


        mButtonSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker();
            }
        });
    }


    private class CustomTextWatcher implements TextWatcher {

        private View mView;

        private CustomTextWatcher(View mView) {
            this.mView = mView;
        }

        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        }

        public void afterTextChanged(Editable editable) {
            switch (mView.getId()) {

                case R.id.titleEditText:
                    validateTitle();
                    break;

                case R.id.descriptionEditText:
                    validateDescription();
                    break;
            }
        }
    }


}