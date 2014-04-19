package com.milone.motoxled;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.execution.CommandCapture;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.IBinder;
import android.util.Log;

/*
 * Service which is started when the device is plugged in and stopped when unplugged.
 * 
 * Registers a receiver to listen for battery level changes and makes the change to
 * use the full configuration when the battery is full. Receiver is only active when plugged in.
 */
public class FullChargeMonitorService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		this.registerReceiver(this.batteryInfoReceiver, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));
		

	}

	@Override
	public void onDestroy() {
		this.unregisterReceiver(this.batteryInfoReceiver);

	}

	private BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
			boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING;
			boolean isFull = status == BatteryManager.BATTERY_STATUS_FULL;
			
			
			String prefs_cmd;
			String prefs_bright;

			if (isFull == true) {
				// Battery is full, read those preferences
				prefs_cmd = "cmd_full";
				prefs_bright = "bright_full";
			} else {
				// Charging but not full, read charging preferences
				prefs_cmd = "cmd";
				prefs_bright = "bright";
			}

			SharedPreferences preferences = context.getSharedPreferences(
					"motoxled", Context.MODE_PRIVATE);
			String cmd = preferences.getString(prefs_cmd, "none");
			int bright = preferences.getInt(prefs_bright, 255);
			//String last_used = preferences.getString("last_used", "");

			// If the last shell command was the same that it currently is,
			// don't do it again
			if (!last_used.equals(cmd + "_" + bright)) {
				if (RootTools.isAccessGiven()) {
					CommandCapture command = new CommandCapture(
							0,
							"echo " + cmd
									+ " >/sys/class/leds/charging/trigger",
							"echo "
									+ bright
									+ " >/sys/class/leds/charging/max_brightness");
					try {
						RootTools.getShell(true).add(command);
						SharedPreferences.Editor prefsEditr = preferences
								.edit();
						prefsEditr.putString("last_used", cmd + "_" + bright);
						prefsEditr.commit();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	};
}
