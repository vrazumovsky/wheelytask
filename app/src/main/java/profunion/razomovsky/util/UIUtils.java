package profunion.razomovsky.util;

import android.support.v7.app.AppCompatActivity;

import profunion.razomovsky.ProgressDialogFragment;

/**
 * Created by vadim on 22/10/16.
 */

public class UIUtils {

    public static void showProgressDialog(AppCompatActivity activity) {
        new ProgressDialogFragment().show(activity.getSupportFragmentManager(), null);
    }
}
