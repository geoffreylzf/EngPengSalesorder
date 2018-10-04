package my.com.engpeng.engpengsalesorder.fragment.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;


public class AlertDialogFragment extends DialogFragment {

    public static final String tag = "ALERT_DIALOG_FRAGMENT";
    private static final String B_KEY_TITLE = "B_KEY_TITLE";
    private static final String B_KEY_MESSAGE = "B_KEY_MESSAGE";

    public static AlertDialogFragment newInstance(String title, String message) {
        AlertDialogFragment frag = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putString(B_KEY_TITLE, title);
        args.putString(B_KEY_MESSAGE, message);
        frag.setArguments(args);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String title = getArguments().getString(B_KEY_TITLE);
        String message = getArguments().getString(B_KEY_MESSAGE);

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dismiss();
                            }
                        }
                )
                .create();
    }
}
