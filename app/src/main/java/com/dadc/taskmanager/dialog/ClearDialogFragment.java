package com.dadc.taskmanager.dialog;

/**
 * Created by bomko on 11.06.16.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.dadc.taskmanager.R;

public class ClearDialogFragment extends DialogFragment {
    public interface ClearTasks {
        void clearTaskList();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof ClearTasks)) {
            throw new ClassCastException(activity.toString());
        }
    }


    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.titleAlertDialogClear)
                .setMessage(R.string.messageAlertDialogClear)
                .setPositiveButton(R.string.posBtnAlertDialog,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                ((ClearTasks) getActivity()).clearTaskList();
                            }
                        }
                )
                .setNegativeButton(R.string.negBtnAlertDialog, null);
        return adb.create();
    }
}
