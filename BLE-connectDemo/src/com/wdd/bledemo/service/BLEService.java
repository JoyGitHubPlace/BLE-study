package com.wdd.bledemo.service;

import com.wdd.bledemo.utils.BluetoothController;
import com.wdd.bledemo.utils.ConstantUtils;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

public class BLEService extends Service {
	BluetoothController bleCtrl;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			
			case ConstantUtils.WM_STOP_SCAN_BLE:
				bleCtrl.stopScanBLE();
				break;
			case ConstantUtils.WM_UPDATE_BLE_LIST:
				Intent intent=new Intent(ConstantUtils.ACTION_UPDATE_DEVICE_LIST);
				BluetoothDevice device=(BluetoothDevice)msg.obj;
				intent.putExtra("name",device.getName());
				intent.putExtra("address", device.getAddress());
				sendBroadcast(new Intent(intent));
				break;
			
			}
		}
	};
	
	public void onStart(Intent intent, int startId) {
		bleCtrl=BluetoothController.getInstance();
		bleCtrl.setServiceHandler(handler);
	};

}
