package ru.razomovsky.util;

import android.support.v7.app.AppCompatActivity;

import ru.razomovsky.ui.ProgressDialogFragment;

/**
 * Created by vadim on 22/10/16.
 */

public class UIUtils {

    public static ProgressDialogFragment showProgressDialog(AppCompatActivity activity) {
        ProgressDialogFragment progressDialogFragment = new ProgressDialogFragment();
        progressDialogFragment.show(activity.getSupportFragmentManager(), null);
        return progressDialogFragment;
    }
}
