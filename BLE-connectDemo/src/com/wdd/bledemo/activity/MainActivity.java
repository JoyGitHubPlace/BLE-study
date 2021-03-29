package com.wdd.bledemo.activity;

import java.util.ArrayList;

import com.wdd.bledemo.R;
import com.wdd.bledemo.R.id;
import com.wdd.bledemo.R.layout;
import com.wdd.bledemo.adapter.DeviceAdapter;
import com.wdd.bledemo.entity.EntityDevice;
import com.wdd.bledemo.service.BLEService;
import com.wdd.bledemo.utils.BluetoothController;
import com.wdd.bledemo.utils.ConstantUtils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.Toast;
/**
 * 
 * author @joy
 *
 */
public class MainActivity extends Activity {
	private Button search;
	private ListView listview;
	private ArrayList<EntityDevice> list = new ArrayList<EntityDevice>();
	private DeviceAdapter adapter;
	private Intent intentService;
	private MsgReceiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acitivity_main);
		

		intentService = new Intent(MainActivity.this,BLEService.class);   
		startService(intentService);

		BluetoothController.getInstance().initBLE();
		listview = (ListView) findViewById(R.id.list_devices);
		adapter = new DeviceAdapter(this, list);
		listview.setAdapter(adapter);
		search = (Button) findViewById(R.id.btn_search);
		search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if(!BluetoothController.getInstance().initBLE()){
					Toast.makeText(MainActivity.this, "不支持蓝牙",
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (!BluetoothController.getInstance().isBleOpen()) {
					Toast.makeText(MainActivity.this, "蓝牙未打开",
							Toast.LENGTH_SHORT).show();
					return;
				}
				new GetDataTask().execute();

			}
		});
		registerReceiver();
		
	}

	private void registerReceiver() {
		receiver=new MsgReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ConstantUtils.ACTION_UPDATE_DEVICE_LIST);
		registerReceiver(receiver, intentFilter);
	}

	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			if(BluetoothController.getInstance().isBleOpen()){
				BluetoothController.getInstance().startScanBLE();
			};
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
		}
	}

	public class MsgReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equalsIgnoreCase(
					ConstantUtils.ACTION_UPDATE_DEVICE_LIST)) {
				String name = intent.getStringExtra("name");
				String address = intent.getStringExtra("address");
				boolean found=false;
				for(EntityDevice device:list){
					if(device.getAddress().equals(address)){
						found=true;
						break;
						}
				}
				if(!found){
					EntityDevice temp = new EntityDevice();
					temp.setName(name);
					temp.setAddress(address);
					list.add(temp);
					adapter.notifyDataSetChanged();
				}
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopService(intentService);
		unregisterReceiver(receiver);
	}

}
