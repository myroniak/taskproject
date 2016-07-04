package com.dadc.taskmanager.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageHelper {

    private Context mContext;
    private Uri mUri;
    private int mHeight, mWidth;
    private String mName;

    public ImageHelper(Context context) {
        mContext = context;
    }

    public ImageHelper setUri(Uri uri) {
        mUri = uri;
        return this;
    }
    public ImageHelper setFileName (String name) {
        this.mName = name;
        return this;
    }
    public ImageHelper setHeightWidth(int height, int width) {
        mHeight = height;
        mWidth = width;
        return this;
    }

    public void save() {
        FileOutputStream fileOutputStream = null;
        try {

            Bitmap photo = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), mUri);
            photo = Bitmap.createScaledBitmap(photo, mWidth, mHeight, false);
            fileOutputStream = new FileOutputStream(getFileDir());
            photo.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @NonNull
    public String getFileDir() {
        File directory = new File(mContext.getFilesDir() + "/avatar/");
        return directory + mName;
    }

    public Bitmap load() {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(getFileDir());
            return BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
