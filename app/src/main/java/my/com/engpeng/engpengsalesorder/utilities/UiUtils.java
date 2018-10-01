package my.com.engpeng.engpengsalesorder.utilities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.app.FragmentManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.Calendar;

import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.fragment.dialog.AlertDialogFragment;
import my.com.engpeng.engpengsalesorder.fragment.dialog.ConfirmDialogFragment;
import my.com.engpeng.engpengsalesorder.fragment.dialog.DatePickerDialogFragment;

public class UiUtils {

    public static Dialog getProgressDialog(Context context) {
        Dialog progressDialog = new Dialog(context, android.R.style.Theme_Black);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.getWindow().setBackgroundDrawableResource(R.color.colorTranslucent10);
        progressDialog.setContentView(view);
        progressDialog.setCancelable(false);
        return progressDialog;
    }

    public static void showConfirmDialog(FragmentManager fragmentManager,
                                         String title,
                                         String msg,
                                         String positiveText,
                                         ConfirmDialogFragment.ConfirmDialogFragmentListener cdfListener) {
        ConfirmDialogFragment confirmDialogFragment = ConfirmDialogFragment.newInstance(title, msg, positiveText, cdfListener);
        confirmDialogFragment.show(fragmentManager, ConfirmDialogFragment.tag);
    }

    public static void showAlertDialog(FragmentManager fragmentManager, String title, String msg) {
        AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(title, msg);
        alertDialogFragment.show(fragmentManager, AlertDialogFragment.tag);
    }

    public static void showDatePickerDialog(FragmentManager fragmentManager, Calendar calendar, DatePickerDialog.OnDateSetListener listener){
        DatePickerDialogFragment datePickerDialogFragment  = DatePickerDialogFragment.newInstance(calendar, listener);
        datePickerDialogFragment.show(fragmentManager, DatePickerDialogFragment.tag);
    }

    public static void showToastMessage(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View currentFocusedView = activity.getCurrentFocus();
        if (currentFocusedView != null) {
            inputManager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static int getPrimaryDarkColorId(Context context) {
        TypedValue typedValue = new TypedValue();

        TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[] { R.attr.colorPrimaryDark });
        int color = a.getColor(0, 0);
        a.recycle();

        return color;
    }
}
