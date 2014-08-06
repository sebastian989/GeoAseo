package com.quasol.geoaseo;

import org.json.JSONArray;
import org.json.JSONObject;

import com.quasol.recursos.Utilities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class D_RegistrarDatosVehiculo extends Activity {

	private SharedPreferences sharedpreferences;
	private EditText hourmeter, odometer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.d__registrar_datos_vehiculo);
		this.hourmeter = (EditText) findViewById(R.id.editText_hourmeter);
		this.odometer = (EditText) findViewById(R.id.editText_odometer);
		this.sharedpreferences = getSharedPreferences("MyPreferences",
				Context.MODE_PRIVATE);
	}

	public void Save_truck_information(View v) {
		if (this.hourmeter.getText().toString().equals("")
				|| this.odometer.getText().toString().equals("")) {
			Utilities.showAlert(this,
					getResources().getString(R.string.alertEditTextEmpty));
		} else {
			try {
				JSONArray truckInformation = new JSONArray(
						(String) sharedpreferences.getString("TRUCK_INFO", ""));
				if ((int) truckInformation.getJSONObject(0).getInt("horometro") <= Integer
						.parseInt(this.hourmeter.getText().toString())) {
					if ((int) truckInformation.getJSONObject(0).getInt(
							"odometro") <= Integer.parseInt(this.odometer
							.getText().toString())) {

						truckInformation.getJSONObject(0).put(
								"nuevo_horometro",
								Integer.parseInt(this.hourmeter.getText()
										.toString()));
						truckInformation.getJSONObject(0).put(
								"nuevo_odometro",
								Integer.parseInt(this.odometer.getText()
										.toString()));
						SharedPreferences.Editor editor = sharedpreferences
								.edit();
						editor.putString("TRUCK_INFO",
								truckInformation.toString());
						editor.commit();
						Intent intent = new Intent(this, E_MenuCiclo.class);
						startActivity(intent);
						finish();
					} else {
						Utilities.showAlert(
								this,
								getResources()
										.getString(R.string.alertOdometer)
										+ truckInformation.getJSONObject(0)
												.getInt("odometro")
										+ getResources().getString(
												R.string.alertAux));
					}
				} else {
					Utilities.showAlert(
							this,
							getResources().getString(R.string.alertHourMeter)
									+ truckInformation.getJSONObject(0).getInt(
											"horometro")
									+ getResources().getString(
											R.string.alertAux));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		return true;
	}

}
