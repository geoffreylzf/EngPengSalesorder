package my.com.engpeng.engpengsalesorder.fragment.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class ConfirmDialogFragment extends DialogFragment{

    public static final String tag = "CONFIRM_DIALOG_FRAGMENT";

    private static final String B_KEY_TITLE = "B_KEY_TITLE";
    private static final String B_KEY_MESSAGE = "B_KEY_MESSAGE";
    private static final String B_KEY_POSITIVE_TEXT = "B_KEY_POSITIVE_TEXT";
    private ConfirmDialogFragmentListener cdfListener;

    public static ConfirmDialogFragment newInstance(String title, String message, String positiveText, ConfirmDialogFragmentListener cdfListener) {
        ConfirmDialogFragment frag = new ConfirmDialogFragment();
        frag.setListener(cdfListener);

        Bundle args = new Bundle();
        args.putString(B_KEY_TITLE, title);
        args.putString(B_KEY_MESSAGE, message);
        args.putString(B_KEY_POSITIVE_TEXT, positiveText);
        frag.setArguments(args);
        return frag;
    }

    public interface ConfirmDialogFragmentListener{
        void onPositiveButtonClicked();
    }

    public void setListener(ConfirmDialogFragmentListener cdfListener){
        this.cdfListener = cdfListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String title = getArguments().getString(B_KEY_TITLE);
        String message = getArguments().getString(B_KEY_MESSAGE);
        String positiveText = getArguments().getString(B_KEY_POSITIVE_TEXT);

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveText,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                cdfListener.onPositiveButtonClicked();
                            }
                        }
                ).setNegativeButton("CANCEL",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dismiss();
                            }
                        }
                )
                .create();
    }
}
