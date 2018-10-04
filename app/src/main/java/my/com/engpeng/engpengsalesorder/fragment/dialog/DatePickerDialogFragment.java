package my.com.engpeng.engpengsalesorder.fragment.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerDialogFragment extends DialogFragment {

    public static final String tag = "DATE_PICKER_DIALOG_FRAGMENT";
    private DatePickerDialog.OnDateSetListener listener;
    private Calendar calendar;
    private long minDateTimestamp, maxDateTimestamp;

    public static DatePickerDialogFragment newInstance(Calendar calendar,
                                                       DatePickerDialog.OnDateSetListener listener,
                                                       long minDateTimestamp, long maxDateTimestamp){
        DatePickerDialogFragment datePickerDialogFragment = new DatePickerDialogFragment();

        datePickerDialogFragment.setCalendar(calendar);
        datePickerDialogFragment.setListener(listener);
        datePickerDialogFragment.setMinDateTimestamp(minDateTimestamp);
        datePickerDialogFragment.setMaxDateTimestamp(maxDateTimestamp);

        return datePickerDialogFragment;
    }

    public void setListener(DatePickerDialog.OnDateSetListener listener){
        this.listener = listener;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public void setMinDateTimestamp(long minDateTimestamp) {
        this.minDateTimestamp = minDateTimestamp;
    }

    public void setMaxDateTimestamp(long maxDateTimestamp) {
        this.maxDateTimestamp = maxDateTimestamp;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), listener, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(minDateTimestamp);
        datePickerDialog.getDatePicker().setMaxDate(maxDateTimestamp);
        return datePickerDialog;
    }
}
