package my.com.engpeng.engpengsalesorder.fragment.dialog;


import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Set;

import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.adapter.BluetoothAdapter;
import my.com.engpeng.engpengsalesorder.bluetooth.BluetoothConnection;
import my.com.engpeng.engpengsalesorder.utilities.UiUtils;

import static my.com.engpeng.engpengsalesorder.Global.I_KEY_PRINTOUT;

public class BluetoothDialogFragment extends DialogFragment {

    public static final String tag = "BLUETOOTH_DIALOG_FRAGMENT";

    private RecyclerView rv;
    private BluetoothAdapter adapter;

    private String printout;

    private boolean isBluetoothPaired = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bluetooth, container, false);

        rv = rootView.findViewById(R.id.bt_rv);

        getDialog().setTitle("Tap to Print");
        getDialog().setCanceledOnTouchOutside(true);

        setupBundle();
        setupRecycleView();

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void setupBundle() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            printout = bundle.getString(I_KEY_PRINTOUT);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(600, 600);
        if (isBluetoothPaired) {
            refreshRecycleView();
        }
    }

    private void setupRecycleView() {
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        Set<BluetoothDevice> bluetoothDevices = BluetoothConnection.getPairedBluetoothDevicesList(getContext());

        try {
            bluetoothDevices.toString();
            isBluetoothPaired = true;
        } catch (NullPointerException e) {
            isBluetoothPaired = false;
            UiUtils.showAlertDialog(getFragmentManager(), "No Bluetooth Printer Paired", "Please pair bluetooth printer before print.");
        }

        if (isBluetoothPaired) {
            adapter = new BluetoothAdapter(getContext(), bluetoothDevices, printout);
            rv.setAdapter(adapter);
        }
    }

    private void refreshRecycleView() {
        adapter.swapSet(BluetoothConnection.getPairedBluetoothDevicesList(getContext()));
    }
}
