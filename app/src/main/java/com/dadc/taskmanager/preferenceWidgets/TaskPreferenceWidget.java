package com.dadc.taskmanager.preferenceWidgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;

import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.dadc.taskmanager.R;


public class TaskPreferenceWidget extends Preference {
    int value;

    public TaskPreferenceWidget(Context context, AttributeSet attrs) {
        super(context, attrs);

        setWidgetLayoutResource(R.layout.preference_widget);

    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        // Set our custom views inside the layout
        final View box = view.findViewById(R.id.task_pref_widget);
        if (box != null) {
            box.setBackgroundColor(value);
        }
    }

    @Override
    protected void onClick() {

        ColorChooserDialog.Builder mColorChooser = new ColorChooserDialog.Builder((AppCompatActivity) getContext(), R.string.dialog_color_title)
                .allowUserColorInputAlpha(false)
                .customButton(R.string.dialog_color_custom)
                .cancelButton(R.string.dialog_color_neg)
                .doneButton(R.string.dialog_color_pos)
                .presetsButton(R.string.dialog_color_preset)
                .onPosButton(new ColorChooserDialog.ColorCallback() {
                    @Override
                    public void onColorSelection(@NonNull ColorChooserDialog dialog, @ColorInt int selectedColor) {

                        value = selectedColor;
                        persistInt(value);
                        notifyChanged();

                    }
                });
        mColorChooser.show();

    }

    public void setColorDefault(int value) {
        this.value = value;
        persistInt(value);
        notifyChanged();
    }


    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        // This preference type's value type is Integer, so we read the default value from the attributes as an Integer.
        return a.getInteger(index, 0);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        if (restoreValue) { // Restore state
            value = getPersistedInt(value);
        } else { // Set state
            int value = (Integer) defaultValue;
            this.value = value;
            persistInt(value);
        }
    }

    /*
     * Suppose a client uses this preference type without persisting. We
     * must save the instance state so it is able to, for example, survive
     * orientation changes.
     */
    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (isPersistent())
            return superState; // No need to save instance state since it's persistent

        final SavedState myState = new SavedState(superState);
        myState.value = value;
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!state.getClass().equals(SavedState.class)) {
            // Didn't save state for us in onSaveInstanceState
            super.onRestoreInstanceState(state);
            return;
        }

        // Restore the instance state
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        this.value = myState.value;
        notifyChanged();
    }


    /**
     * SavedState, a subclass of {@link BaseSavedState}, will store the state
     * of MyPreference, a subclass of Preference.
     * <p/>
     * It is important to always call through to super methods.
     */
    private static class SavedState extends BaseSavedState {
        int value;

        public SavedState(Parcel source) {
            super(source);
            value = source.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(value);
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @SuppressWarnings("unused")
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
