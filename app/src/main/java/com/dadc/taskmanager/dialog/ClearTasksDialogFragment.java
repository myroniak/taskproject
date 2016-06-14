package com.dadc.taskmanager.dialog;

/**
 * Created by bomko on 13.06.16.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.dadc.taskmanager.R;

public class ClearTasksDialogFragment extends DialogFragment {
    public interface ClearTask {
        void clearData();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof ClearTask)) {
            throw new ClassCastException(activity.toString() + " must implement DeleteAllItem");
        }
    }


    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.titleAlertDialogClear)
                .setMessage(R.string.messageAlertDialogClear)
                .setPositiveButton(R.string.posBtnAlertDialog,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                ((ClearTask) getActivity()).clearData();
                            }
                        }
                )
                .setNegativeButton(R.string.negBtnAlertDialog, null);
        return adb.create();
    }
}
