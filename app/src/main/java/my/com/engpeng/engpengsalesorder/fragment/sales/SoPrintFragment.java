package my.com.engpeng.engpengsalesorder.fragment.sales;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
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
import my.com.engpeng.engpengsalesorder.fragment.dialog.BluetoothDialogFragment;
import my.com.engpeng.engpengsalesorder.fragment.dialog.EnterPriceDialogFragment;
import my.com.engpeng.engpengsalesorder.utilities.PrintUtils;

import static my.com.engpeng.engpengsalesorder.Global.I_KEY_PRINTOUT;
import static my.com.engpeng.engpengsalesorder.Global.I_KEY_SALESORDER_ENTRY;

public class SoPrintFragment extends Fragment {

    public static final String tag = "SO_PRINT_FRAGMENT";

    private TextView tvPrintout;
    private CardView cv;

    private AppDatabase mDb;

    private SalesorderEntry salesorderEntry;
    private String printout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_so_print, container, false);

        tvPrintout = rootView.findViewById(R.id.so_print_tv_printout);
        cv = rootView.findViewById(R.id.so_print_cv);

        mDb = AppDatabase.getInstance(getActivity().getApplicationContext());
        getActivity().setTitle("Print Preview (Touch To Print)");

        setupBundle();
        setupListener();

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
                printout = PrintUtils.constructSalesorderPrintout(mDb, salesorderEntry);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvPrintout.setText(printout);
                    }
                });
            }
        });
    }

    private void setupListener(){
        cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                BluetoothDialogFragment fragment = new BluetoothDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putString(I_KEY_PRINTOUT, printout);
                fragment.setArguments(bundle);
                fragment.show(fm, BluetoothDialogFragment.tag);
            }
        });
    }
}
