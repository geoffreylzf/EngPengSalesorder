package my.com.engpeng.engpengsalesorder.utilities;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.fragment.AlertDialogFragment;
import my.com.engpeng.engpengsalesorder.fragment.ConfirmDialogFragment;

import static my.com.engpeng.engpengsalesorder.Global.ALERT_DIALOG_TAG;
import static my.com.engpeng.engpengsalesorder.Global.CONFIRM_DIALOG_TAG;
import static my.com.engpeng.engpengsalesorder.Global.RC_CONFIRM_DIALOG_ID;

public class UIUtils {

    public static Dialog getProgressDialog(Context context) {
        Dialog progressDialog = new Dialog(context, android.R.style.Theme_Black);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.getWindow().setBackgroundDrawableResource(R.color.colorTranslucent);
        progressDialog.setContentView(view);
        progressDialog.setCancelable(false);
        return progressDialog;
    }

    public static void showConfirmDialog(FragmentManager fragmentManager, Fragment fragment, String title, String msg, String positiveText) {
        ConfirmDialogFragment confirmDialogFragment = ConfirmDialogFragment.newInstance(title, msg, positiveText);
        confirmDialogFragment.setTargetFragment(fragment, RC_CONFIRM_DIALOG_ID);
        confirmDialogFragment.show(fragmentManager, CONFIRM_DIALOG_TAG);
    }

    public static void showAlertDialog(FragmentManager fragmentManager, String title, String msg) {
        AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(title, msg);
        alertDialogFragment.show(fragmentManager, ALERT_DIALOG_TAG);
    }

    public static void showToastMessage(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
