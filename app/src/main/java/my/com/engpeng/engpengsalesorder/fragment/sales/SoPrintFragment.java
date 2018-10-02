package my.com.engpeng.engpengsalesorder.fragment.sales;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.parceler.Parcels;

import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.database.AppDatabase;
import my.com.engpeng.engpengsalesorder.database.branch.BranchEntry;
import my.com.engpeng.engpengsalesorder.database.salesorder.SalesorderEntry;
import my.com.engpeng.engpengsalesorder.executor.AppExecutors;
import my.com.engpeng.engpengsalesorder.utilities.PrintUtils;

import static my.com.engpeng.engpengsalesorder.Global.I_KEY_SALESORDER_ENTRY;

public class SoPrintFragment extends Fragment {

    public static final String tag = "SO_PRINT_FRAGMENT";

    private TextView tvPrintout;

    private AppDatabase mDb;

    private SalesorderEntry salesorderEntry;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_so_print, container, false);

        tvPrintout = rootView.findViewById(R.id.so_print_tv_printout);

        mDb = AppDatabase.getInstance(getActivity().getApplicationContext());

        setupBundle();

        return rootView;
    }

    private void setupBundle() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            salesorderEntry = Parcels.unwrap(bundle.getParcelable(I_KEY_SALESORDER_ENTRY));
            constructPrintout();
        }
    }

    private void constructPrintout() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final String printout = PrintUtils.constructSalesorderPrintout(mDb, salesorderEntry);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvPrintout.setText(printout);
                    }
                });
            }
        });
    }
}
