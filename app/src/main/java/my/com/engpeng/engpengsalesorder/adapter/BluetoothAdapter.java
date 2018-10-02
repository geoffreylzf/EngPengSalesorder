package my.com.engpeng.engpengsalesorder.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.bluetooth.BluetoothConnection;

public class BluetoothAdapter extends RecyclerView.Adapter<BluetoothAdapter.PrinterViewHolder>{

    private Context context;
    private Set<BluetoothDevice> bluetoothDevices;
    private String printout;

    public BluetoothAdapter(Context context, Set<BluetoothDevice> bluetoothDevices, String printout) {
        this.context = context;
        this.bluetoothDevices = bluetoothDevices;
        this.printout = printout;
    }

    @NonNull
    @Override
    public PrinterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater li = LayoutInflater.from(context);
        View view = li.inflate(R.layout.list_item_bluetooth, viewGroup, false);
        return new PrinterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PrinterViewHolder printerViewHolder, int i) {
        int order = 0;
        for (BluetoothDevice device : bluetoothDevices) {
            if (order == i) {
                final String name = device.getName();
                final String address = device.getAddress();

                printerViewHolder.tvName.setText(name);
                printerViewHolder.tvAddress.setText(address);

                printerViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BluetoothConnection conn = new BluetoothConnection();
                        conn.initPrint(context, name, address, printout);
                    }
                });
            }
            order++;
        }
    }

    @Override
    public int getItemCount() {
        return bluetoothDevices.size();
    }

    public void swapSet(Set<BluetoothDevice> bluetoothDevices) {
        //this.bluetoothDevices = bluetoothDevices;
        if (bluetoothDevices != null) {
            //this.notifyDataSetChanged();
        }
    }

    class PrinterViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvAddress;

        private PrinterViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.li_bt_name);
            tvAddress = itemView.findViewById(R.id.li_bt_address);
        }
    }
}
