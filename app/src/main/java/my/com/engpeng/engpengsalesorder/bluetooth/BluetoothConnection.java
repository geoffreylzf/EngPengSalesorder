package my.com.engpeng.engpengsalesorder.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import my.com.engpeng.engpengsalesorder.utilities.UiUtils;

import static my.com.engpeng.engpengsalesorder.Global.RC_BLUETOOTH;

public class BluetoothConnection {

    private static BluetoothSocket btSocket;
    private static OutputStream outputStream;
    private Context context;
    private String printerName, address;

    public static Set<BluetoothDevice> getPairedBluetoothDevicesList(Context context) {
        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                ((Activity) context).startActivityForResult(enableBluetooth, RC_BLUETOOTH);
            }
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

            if (pairedDevices.size() > 0) {
                return pairedDevices;
            } else {
                return null;
            }
        } catch (Exception ex) {
            return null;
        }
    }

    public void initPrint(Context context, String printerName, String address, String printout) {

        this.context = context;
        this.printerName = printerName;
        this.address = address;

        if (initPrinter()) {
            try {
                outputStream = btSocket.getOutputStream();

                printText(printout);

                outputStream.close();
                btSocket.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            UiUtils.showToastMessage(context, "Please try again.");
        }
    }

    private boolean initPrinter() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        try {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                ((Activity) context).startActivityForResult(enableBluetooth, RC_BLUETOOTH);
            }
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

            if (pairedDevices.size() > 0) {
                for (BluetoothDevice bluetoothDevice : pairedDevices) {
                    if (bluetoothDevice.getAddress().equals(address)) {
                        UUID uuid = bluetoothDevice.getUuids()[0].getUuid();
                        btSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
                        btSocket.connect();
                        break;
                    }
                }
            } else {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    private void printText(String msg) {
        try {
            Log.e("printText", msg);
            outputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
            String lines[] = msg.split("\n", -1);
            for (String line : lines) {
                outputStream.write(line.getBytes());
                printNewLine();
            }
            //outputStream.write(msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printByte(byte[] msg) {
        try {
            outputStream.write(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printPhoto(byte[] imgByte) {
        try {
            outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
            printByte(imgByte);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printNewLine() {
        try {
            outputStream.write(PrinterCommands.FEED_LINE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printCustom(String msg, int size, int align) {
        //Print config "mode"
        byte[] cc = new byte[]{0x1B, 0x21, 0x03};  // 0- normal size text
        //byte[] cc1 = new byte[]{0x1B,0x21,0x00};  // 0- normal size text
        byte[] bb = new byte[]{0x1B, 0x21, 0x08};  // 1- only bold text
        byte[] bb2 = new byte[]{0x1B, 0x21, 0x20}; // 2- bold with medium text
        byte[] bb3 = new byte[]{0x1B, 0x21, 0x10}; // 3- bold with large text
        try {
            switch (size) {
                case 0:
                    outputStream.write(cc);
                    break;
                case 1:
                    outputStream.write(bb);
                    break;
                case 2:
                    outputStream.write(bb2);
                    break;
                case 3:
                    outputStream.write(bb3);
                    break;
            }

            switch (align) {
                case 0:
                    //left align
                    outputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
                    break;
                case 1:
                    //center align
                    outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                    break;
                case 2:
                    //right align
                    outputStream.write(PrinterCommands.ESC_ALIGN_RIGHT);
                    break;
            }
            outputStream.write(msg.getBytes());
            outputStream.write(PrinterCommands.LF);
            //outputStream.write(cc);
            //printNewLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void resetPrint() {
        try {
            outputStream.write(PrinterCommands.ESC_FONT_COLOR_DEFAULT);
            outputStream.write(PrinterCommands.FS_FONT_ALIGN);
            outputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
            outputStream.write(PrinterCommands.ESC_CANCEL_BOLD);
            outputStream.write(PrinterCommands.LF);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
