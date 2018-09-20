package my.com.engpeng.engpengsalesorder.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;

public class DatePickerDialogFragment extends DialogFragment {

    public static final String tag = "DATE_PICKER_DIALOG_FRAGMENT";
    private DatePickerDialog.OnDateSetListener listener;
    private Calendar calendar;

    public static DatePickerDialogFragment newInstance(Calendar calendar, DatePickerDialog.OnDateSetListener listener){
        DatePickerDialogFragment datePickerDialogFragment = new DatePickerDialogFragment();

        datePickerDialogFragment.setCalendar(calendar);
        datePickerDialogFragment.setListener(listener);

        return datePickerDialogFragment;
    }

    public void setListener(DatePickerDialog.OnDateSetListener listener){
        this.listener = listener;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), listener, year, month, day);
    }
}
