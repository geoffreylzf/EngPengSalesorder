package my.com.engpeng.engpengsalesorder.utilities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import my.com.engpeng.engpengsalesorder.R;

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

    public static void getMessageDialog(Context context, String title, String msg) {
        AlertDialog alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(context, android.R.style.Theme_Material_Light_Dialog_Alert)).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public static void showToastMessage(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
