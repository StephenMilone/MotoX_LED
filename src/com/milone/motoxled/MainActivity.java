package com.milone.motoxled;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.exceptions.RootDeniedException;
import com.stericson.RootTools.execution.CommandCapture;

import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
/*
 * Main Activity & UI 
 * Radio group and buttons to save shared preferences that the receivers will execute
 */
public class MainActivity extends Activity {

	RadioGroup choose;
	RadioButton radio0;
	RadioButton radio1;
	RadioButton radio2;
	RadioButton radio3;
	RadioButton radio4;
	RadioButton radio5;
	
	Button butF;
	Button butC;
	
	NumberPicker np;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//UI
		setContentView(R.layout.activity_main);
		choose = (RadioGroup) findViewById(R.id.radioGroup1);
		radio0 = (RadioButton) findViewById(R.id.radio0); // off
		radio1 = (RadioButton) findViewById(R.id.radio1); // hearbeat
		radio2 = (RadioButton) findViewById(R.id.radio2); // charging-full
		radio3 = (RadioButton) findViewById(R.id.radio3); // charging
		radio4 = (RadioButton) findViewById(R.id.radio4); // full
		radio5 = (RadioButton) findViewById(R.id.radio5); // mmc0
		butF = (Button) findViewById(R.id.buttonF);
		butC = (Button) findViewById(R.id.buttonC);
		np = (NumberPicker) findViewById(R.id.np);
		
		//Load saved values and update radio group and number picker
		SharedPreferences preferences = getSharedPreferences("motoxled",
				Context.MODE_PRIVATE);

		np.setMaxValue(255);
		np.setMinValue(0);
		np.setValue(preferences.getInt("bright", 255));
			
		//Keyboard was showing up when setting the number picker value? so just force it to hide
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		int savedId = preferences.getInt("choice", 0);
		if (savedId == radio1.getId())
			radio1.setChecked(true);
		else if (savedId == radio2.getId())
			radio2.setChecked(true);
		else if (savedId == radio3.getId())
			radio3.setChecked(true);
		else if (savedId == radio4.getId())
			radio4.setChecked(true);
		else if (savedId == radio5.getId())
			radio5.setChecked(true);
		else
			radio0.setChecked(true);
		
		//Update button text to show user what is saved as well
		String cmd = preferences.getString("cmd", "none");
		int bright = preferences.getInt("bright", 255);
		butC.setText("Set for Charging : " + cmd + " " + bright);
		String cmd_f = preferences.getString("cmd_full", "none");
		int bright_f = preferences.getInt("bright_full", 255);
		butF.setText("Set for Full : " + cmd_f + " " + bright_f);

	}
	
	//Save buttons
	//Write correct selections to shared preferences
	public void save(View v){
		
		
		int checkedId = choose.getCheckedRadioButtonId();
		String cmd = "none";
		int bright = np.getValue();

		if (checkedId == radio1.getId())
			cmd = "heartbeat";
		else if (checkedId == radio2.getId())
			cmd = "battery-charging-or-full";
		else if (checkedId == radio3.getId())
			cmd = "battery-charging";
		else if (checkedId == radio4.getId())
			cmd = "battery-full";
		else if (checkedId == radio5.getId())
			cmd = "mmc0";


		String option = "Battery Charging";
		String pref_cmd = "cmd";
		String pref_bright = "bright";
		
		if(v == butF){
			option = "Battery Full";
			pref_cmd = "cmd_full";
			pref_bright = "bright_full";
			butF.setText("Set for Full : " + cmd + " " + bright);
		} else{
			butC.setText("Set for Charging : " + cmd + " " + bright);
		}
		
		SharedPreferences preferences = getSharedPreferences("motoxled",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor prefsEditr = preferences.edit();
		prefsEditr.putInt("choice", checkedId);
		prefsEditr.putInt(pref_bright, bright);
		prefsEditr.putString(pref_cmd, cmd);
		prefsEditr.commit();
		
		Toast.makeText(getApplicationContext(),
				option + " settings saved!\nScroll down to execute settings",
				Toast.LENGTH_LONG).show();
		
	}

	//Execute button
	public void run(View v) {
		if (RootTools.isAccessGiven()) {
			
			IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
			Intent batteryStatus = getApplicationContext().registerReceiver(null, ifilter);

			int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
			boolean isFull = status == BatteryManager.BATTERY_STATUS_FULL;

			String prefs_cmd;
	        String prefs_bright;
	        //Figure out if we're full already 
	        if(isFull == true){
	        	prefs_cmd = "cmd_full";
	        	prefs_bright = "bright_full";
	        } else {
	            prefs_cmd = "cmd";
		        prefs_bright = "bright";
	        } 

			SharedPreferences preferences = getSharedPreferences("motoxled",
					Context.MODE_PRIVATE);
			SharedPreferences.Editor prefsEditr = preferences.edit();

			
			String cmd = preferences.getString(prefs_cmd, "none");
			int bright = preferences.getInt(prefs_bright, 255);

			CommandCapture command = new CommandCapture(0, 
					"echo " + cmd + " >/sys/class/leds/charging/trigger", 
					"echo " + bright + " >/sys/class/leds/charging/max_brightness");
			try {
				RootTools.getShell(true).add(command);
				prefsEditr.putString("last_used", cmd + "_" + bright);
				prefsEditr.commit();
			} catch (IOException e) {
				Toast.makeText(getApplicationContext(), "IO Error - try again",
						Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			} catch (TimeoutException e) {
				Toast.makeText(getApplicationContext(),
						"Timeout Error - try again", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			} catch (RootDeniedException e) {
				Toast.makeText(getApplicationContext(),
						"You need root to use this!", Toast.LENGTH_SHORT)
						.show();
				e.printStackTrace();
			}
			Toast.makeText(getApplicationContext(),
					"Success! It could take a minute to turn on and device must be plugged in!",
					Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(getApplicationContext(),
					"You need root to use this!", Toast.LENGTH_SHORT).show();
		}

	}
	
	//Product placement =)
	public void market(View v) {
		final Intent marketIntent = new Intent(
				Intent.ACTION_VIEW,
				Uri.parse("https://play.google.com/store/apps/developer?id=Stephen+Milone"));
		startActivity(marketIntent);
	}
	

}
