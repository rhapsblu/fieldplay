package com.jmie.fieldplay.route;

import com.jmie.fieldplay.R;
import com.jmie.fieldplay.zxing.integration.android.IntentIntegrator;
import com.jmie.fieldplay.zxing.integration.android.IntentResult;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class RouteAddActivity extends Activity {

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_add);
	}


	public void zxinghandler(View view) {
		IntentIntegrator scanIntegrator = new IntentIntegrator(this);
		scanIntegrator.initiateScan();
	}
	public void fpstorehandler(View view){
		AlertDialog.Builder alert = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
		alert.setTitle("Coming Soon!");
		alert.setMessage("The FieldPlay store is not yet available.  Please check back later.");
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});
		alert.show();
	}
	public void urlhandler(View view){
		AlertDialog.Builder alert = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
		alert.setTitle("Route URL");
		alert.setMessage("Please enter the url of the route to download");
		
		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_URI);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				returnLocation(false, value);
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
		  }
		});

		alert.show();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if(intent == null) return;
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(
				requestCode, resultCode, intent);
		if (scanningResult != null) {
			String scanContent = scanningResult.getContents();
			String scanFormat = scanningResult.getFormatName();
			returnLocation(false, scanContent);
			
			Log.d("Route Add", scanContent);

		} else {
			Toast toast = Toast.makeText(getApplicationContext(),
					"No scan data received!", Toast.LENGTH_SHORT);
			toast.show();

		}
	}
	private void returnLocation(boolean isLocal, String location){
		if(Patterns.WEB_URL.matcher(location).matches()){
			Intent intent = new Intent();
			intent.putExtra("com.jmie.fieldplay.local", isLocal);
			intent.putExtra("com.jmie.fieldplay.location", location);
			setResult(RESULT_OK, intent);
			finish();
		}
		else{
			AlertDialog.Builder alert = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
			alert.setTitle("Invalid address");
			alert.setMessage(location + "\nIs not a valid URL");
			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

				}
			});
			alert.show();
		}
	}
}
