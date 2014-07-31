package com.quasol.geoaseo;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

public class D_RegistrarDatosVehiculo extends Activity {

	private SharedPreferences sharedpreferences;
	private EditText hourmeter,odometer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.d__registrar_datos_vehiculo);
		this.hourmeter=(EditText)findViewById(R.id.editText_hourmeter);
		this.odometer=(EditText)findViewById(R.id.editText_odometer);
		this.sharedpreferences = getSharedPreferences("MyPreferences",Context.MODE_PRIVATE);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	}
	
	public void Save_truck_information(View v){
		
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setPositiveButton(
				"OK",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		if(this.hourmeter.getText().equals("")||this.odometer.getText().equals("") ){
			Toast.makeText(this, "Complete los campos", Toast.LENGTH_LONG).show();
		}
		else{
			try {
				JSONArray truckInformation = new JSONArray((String)sharedpreferences.getString("TRUCK_INFO",""));
				if((int)truckInformation.getJSONObject(0).getInt("horometro")<=Integer.parseInt(this.hourmeter.getText().toString())){
					if((int)truckInformation.getJSONObject(0).getInt("odometro")<=Integer.parseInt(this.odometer.getText().toString())){
						
						truckInformation.getJSONObject(0).put("nuevo_horometro",Integer.parseInt(this.hourmeter.getText().toString()));
						truckInformation.getJSONObject(0).put("nuevo_odometro",Integer.parseInt(this.odometer.getText().toString()));
						SharedPreferences.Editor editor = sharedpreferences.edit();
						editor.putString("TRUCK_INFO", truckInformation.toString());
						editor.commit();
						Intent intent = new Intent(this, E_MenuCiclo.class);
						startActivity(intent);
						finish();
						
					}else{
						adb.setTitle("El SEGUNDO CAMPO ODOMETRO DEBE SER MAYOR O IGUAL A "+truckInformation.getJSONObject(0).getInt("odometro"));
						adb.show();
//						Toast.makeText(this, "El odometro debe ser mayor o igual a "+truckInformation.getJSONObject(0).getInt("odometro"), Toast.LENGTH_LONG).show();
					}
				}
				else{
					adb.setTitle("El PRIMER CAMPO HOROMETRO DEBE SER MAYOR O IGUAL A "+truckInformation.getJSONObject(0).getInt("horometro"));
					adb.show();
//						Toast.makeText(this, "El horometro debe ser mayor o igual a "+truckInformation.getJSONObject(0).getInt("horometro"), Toast.LENGTH_LONG).show();
				}
				
			} catch (Exception e) {
				Toast.makeText(this, "Ocurrio un problema al guardar los datos intentelo de nuevo", Toast.LENGTH_LONG).show();				
			}
			
			
		}
		
		
	}
}
