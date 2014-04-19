package com.milone.motoxled;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

/*
 * System level receiver listening to power connected / disconnected
 * When power connected, start the monitor service to listen for when the device goes
 * from 99% to 100%, when unplugged stops that service.
 */
public class PluggedInReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = context.registerReceiver(null, ifilter);
		int chargePlug = batteryStatus.getIntExtra(
				BatteryManager.EXTRA_PLUGGED, 0);

		Intent i = new Intent(context, FullChargeMonitorService.class);

		if (chargePlug != 0) {
			// Plugged in, start service
			context.startService(i);
		} else {
			// unplugged, turn off service
			context.stopService(i);
		}

	}
}
