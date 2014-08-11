package com.quasol.geoaseo;

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
import android.widget.ImageButton;

public class B_MenuPrincipal extends Activity {

	private SharedPreferences sharedpreferences;
	private ImageButton teamOfWork, starDay, createRoute, closeRoute;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.b__menu_principal);
		this.sharedpreferences = getSharedPreferences("MyPreferences",
				Context.MODE_PRIVATE);
		initialize_visual_controls();
	}

	/**
	 * Method to control the back button
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		moveTaskToBack(true);
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Method to close the session
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
						Intent intent = new Intent(getApplicationContext(),
								A_LogIn.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
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

	/**
	 * Method to display the select operators interface
	 * @param v
	 */
	public void setOperators(View v) {
		Intent intent = new Intent(this, C_GrupoTrabajo.class);
		startActivity(intent);
	}

	/**
	 * Method to start day of work, if the hour meter today is empty, the system
	 * displays the truck information form interface. Else displays the cycle
	 * menu interface.
	 * 
	 * @param v
	 */
	public void startDay(View v) {
		int newOrometer;
		try {
			JSONArray truckInformation = new JSONArray(
					(String) sharedpreferences.getString("TRUCK_INFO", ""));
			JSONObject truckObject = truckInformation.getJSONObject(0);
			newOrometer = truckObject.getInt("nuevo_horometro");
		} catch (Exception e) {
			newOrometer = 0;
		}
		if (newOrometer != 0) {
			// Interfaz para continuar con el siclo
			Intent intent = new Intent(this, E_MenuCiclo.class);
			startActivity(intent);
		} else {
			Intent intent = new Intent(this, D_RegistrarDatosVehiculo.class);
			startActivity(intent);
		}
	}

	/**
	 * Method to display the create route interface
	 * 
	 * @param v
	 */
	public void createRoute(View v) {
		Intent intent = new Intent(this, I_CrearRuta.class);
		startActivity(intent);
	}

	/**
	 * Method to display the close route interface
	 * 
	 * @param v
	 */
	public void closeRoute(View v) {
		Intent intent = new Intent(this, J_CerrarRuta.class);
		startActivity(intent);
	}

	/**
	 * Method to display the ticket gasoline register interface
	 * 
	 * @param v
	 */
	public void registerGas(View v) {
		Intent intent = new Intent(this, K_RegistrarCombustible.class);
		startActivity(intent);
	}

	/**
	 * Method to display the ticket toll register interface
	 * 
	 * @param v
	 */
	public void registerToll(View v) {
		Intent intent = new Intent(this, L_RegistrarPeaje.class);
		startActivity(intent);
	}

	/**
	 * Method to initialize and find the visual controls
	 */
	public void initialize_visual_controls() {
		this.teamOfWork = (ImageButton) findViewById(R.id.btn_team_of_work);
		this.starDay = (ImageButton) findViewById(R.id.btn_start_day);
		this.createRoute = (ImageButton) findViewById(R.id.btn_create_route);
		this.closeRoute = (ImageButton) findViewById(R.id.btn_close_route);
	}

}
