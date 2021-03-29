package com.wdd.bledemo.utils;


import com.wdd.bledemo.App;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 
 * author @Joy
 *
 */
public class BluetoothController {
	
	private   BluetoothAdapter bleAdapter;
	private  Handler serviceHandler;
	
	/**
	 * 
	 */
	private static BluetoothController instance=null;
	private   BluetoothController(){
	}
	public static BluetoothController getInstance(){
		if(instance==null) instance=new BluetoothController();
		return instance;
	}
	/**
	 * 
	 * @return
	 */
	public  boolean initBLE(){
		
		if (!App.app.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			return false;
		}
		final BluetoothManager bluetoothManager = (BluetoothManager) App.app.getSystemService(Context.BLUETOOTH_SERVICE);
		bleAdapter = bluetoothManager.getAdapter();
		
		if (bleAdapter == null) return false;
		else return true;
	} 
	
	/**
	 * 
	 * @return
	 */
	public void setServiceHandler(Handler handler){
		
		serviceHandler=handler;
	}
	/**
	 * 
	 */
	BluetoothAdapter.LeScanCallback bleScanCallback =new BluetoothAdapter.LeScanCallback() {
		@Override
		public void onLeScan(BluetoothDevice device, int arg1, byte[] arg2) {
			
			String name=device.getName();
			if(name==null) return;
			if(BluetoothController.this.serviceHandler!=null&&!name.isEmpty()){
				Message msg=new Message();
				msg.what=ConstantUtils.WM_UPDATE_BLE_LIST;
				msg.obj=device;
				BluetoothController.this.serviceHandler.sendMessage(msg);
			}
		}
	};
	/**
	 * 
	 */
	public  void startScanBLE(){
		bleAdapter.startLeScan(bleScanCallback);
		if(serviceHandler!=null)
			serviceHandler.sendEmptyMessageDelayed(ConstantUtils.WM_STOP_SCAN_BLE, 5000);
	    }
	/**
	 * ͣ
	 */
	public  void stopScanBLE(){
		bleAdapter.stopLeScan(bleScanCallback);
	}
	/**
	 * 
	 * @return
	 */
	public  boolean isBleOpen(){
		return  bleAdapter.isEnabled();
	}

}
