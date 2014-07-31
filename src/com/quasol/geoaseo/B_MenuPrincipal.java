package com.quasol.geoaseo;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.DigitalClock;
import android.widget.ImageButton;

public class B_MenuPrincipal extends Activity {

	private SharedPreferences sharedpreferences;
	private DigitalClock dc;
	private ImageButton team_of_work,start_day,create_route,close_route;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.b__menu_principal);
		this.sharedpreferences = getSharedPreferences("MyPreferences",Context.MODE_PRIVATE);
		initialize_visual_controls();
		this.dc=(DigitalClock)findViewById(R.id.clock1);
	}

	/**
	 * 
	 * @param v
	 */
	public void logOut(View v) {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle(getResources().getString(R.string.logout_confirm));
		adb.setPositiveButton(
				getResources().getString(R.string.confirm_button_1),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Editor editor = sharedpreferences.edit();
						editor.clear();
						editor.commit();
						finish();
					}
				});
		adb.setNegativeButton(
				getResources().getString(R.string.confirm_button_2),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
		adb.show();
	}
	
	public void setOperators(View v) {
		Intent intent = new Intent(this, C_GrupoTrabajo.class);
		startActivity(intent);
	}

	public void startDay(View v) {
		int newOrometer;
			try {
				JSONArray truckInformation = new JSONArray((String)sharedpreferences.getString("TRUCK_INFO",""));
				JSONObject truckObject = truckInformation.getJSONObject(0);
				newOrometer = truckObject.getInt("nuevo_horometro");
			} catch (Exception e) {
				newOrometer=0;
			}
			if(newOrometer!=0){
				//Interfaz para continuar con el siclo
				Intent intent = new Intent(this, E_MenuCiclo.class);
				startActivity(intent);
			}
			else{
				Intent intent = new Intent(this, D_RegistrarDatosVehiculo.class);
				startActivity(intent);
			}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		moveTaskToBack(true);
		return super.onKeyDown(keyCode, event);
	}
	
	public void initialize_visual_controls(){
		this.team_of_work=(ImageButton)findViewById(R.id.btn_team_of_work);
		this.start_day=(ImageButton)findViewById(R.id.btn_start_day);
		this.create_route=(ImageButton)findViewById(R.id.btn_create_route);
		this.close_route=(ImageButton)findViewById(R.id.btn_close_route);
	}
}
