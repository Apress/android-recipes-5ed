package com.androidrecipes.bluetoothlowenergy;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BleScanningActivity extends AppCompatActivity {

    private static final UUID HEART_RATE_MONITOR
            = UUID.fromString("0000180D-0000-1000-8000-00805F9B34FB");
    private static final UUID BLOOD_PRESSURE_MONITOR
            = UUID.fromString("00001810-0000-1000-8000-00805F9B34FB");
    private static final UUID INDOOR_POSITIONING_MONITOR
            = UUID.fromString("00001821-0000-1000-8000-00805F9B34FB");
    private SortedList<BluetoothDevice> discoveredDevices;
    private LollipopScanCallback lollipopScanCallback;
    private LegacyScanCallback legacyScanCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView list = (RecyclerView) findViewById(R.id.discovered_devices);
        BleDeviceAdapter adapter = new BleDeviceAdapter();
        list.setAdapter(adapter);
        SortedListAdapterCallback<BluetoothDevice> sortedListAdapterCallback = new SortedListAdapterCallback<BluetoothDevice>(adapter) {
            @Override
            public int compare(BluetoothDevice o1, BluetoothDevice o2) {
                return o1.getAddress().compareTo(o2.getAddress());
            }

            @Override
            public boolean areContentsTheSame(BluetoothDevice oldItem, BluetoothDevice newItem) {
                return oldItem.getAddress().equals(newItem.getAddress());
            }

            @Override
            public boolean areItemsTheSame(BluetoothDevice item1, BluetoothDevice item2) {
                return item1.getAddress().equals(item2.getAddress());
            }
        };
        discoveredDevices = new SortedList<>(BluetoothDevice.class, sortedListAdapterCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        discoveredDevices.clear();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //noinspection NewApi
            bleScanningLollipop();
        } else {
            //noinspection deprecation
            bleScanningLegacy();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            stopScanningLegacy();
        } else {
            //noinspection NewApi
            stopScanningLollipop();
        }
    }

    /**
     * @deprecated This method uses the deprecated API, but is required to support API level 18 and 19.
     */
    @Deprecated()
    public void bleScanningLegacy() {
        BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        BluetoothAdapter adapter = bluetoothManager.getAdapter();
        legacyScanCallback = new LegacyScanCallback();
        UUID[] filters = new UUID[]{HEART_RATE_MONITOR};
        adapter.startLeScan(filters, legacyScanCallback);
    }

    public void stopScanningLegacy() {
        BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        BluetoothAdapter adapter = bluetoothManager.getAdapter();
        adapter.stopLeScan(legacyScanCallback);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void bleScanningLollipop() {
        BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        BluetoothAdapter adapter = bluetoothManager.getAdapter();
        BluetoothLeScanner bluetoothLeScanner = adapter.getBluetoothLeScanner();
        List<ScanFilter> filters = new ArrayList<>();
        ScanFilter filter = new ScanFilter.Builder()
                .setServiceUuid(new ParcelUuid(HEART_RATE_MONITOR))
                .build();
        filters.add(filter);
        ScanSettings scanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();
        lollipopScanCallback = new LollipopScanCallback();
        bluetoothLeScanner.startScan(filters, scanSettings, lollipopScanCallback);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void stopScanningLollipop() {
        BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        BluetoothAdapter adapter = bluetoothManager.getAdapter();
        adapter.getBluetoothLeScanner().stopScan(lollipopScanCallback);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private class LollipopScanCallback extends ScanCallback {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            discoveredDevices.add(result.getDevice());
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    }

    private class LegacyScanCallback implements BluetoothAdapter.LeScanCallback {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            discoveredDevices.add(device);
        }
    }

    private class BleDeviceAdapter extends RecyclerView.Adapter<BluetoothDeviceviewHolder> {
        @Override
        public BluetoothDeviceviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(BleScanningActivity.this)
                    .inflate(R.layout.list_item, parent, false);
            return new BluetoothDeviceviewHolder(view);
        }

        @Override
        public void onBindViewHolder(BluetoothDeviceviewHolder holder, int position) {
            BluetoothDevice bluetoothDevice = discoveredDevices.get(position);
            holder.deviceText.setText(String.format("%s (%s)", bluetoothDevice.getName(), bluetoothDevice.getAddress()));
        }

        @Override
        public int getItemCount() {
            return discoveredDevices.size();
        }
    }

    private class BluetoothDeviceviewHolder extends RecyclerView.ViewHolder {
        private final TextView deviceText;

        public BluetoothDeviceviewHolder(View itemView) {
            super(itemView);
            deviceText = (TextView) itemView.findViewById(R.id.device);
        }
    }
}
