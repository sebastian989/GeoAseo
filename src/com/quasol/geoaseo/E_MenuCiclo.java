package com.quasol.geoaseo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

public class E_MenuCiclo extends Activity {

	private SharedPreferences sharedpreferences;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.e__menu_ciclo);
		this.sharedpreferences = getSharedPreferences("MyPreferences",Context.MODE_PRIVATE);
	}
	
	@Override
	protected void onResume() {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle("DEBE AGREGAR UN GRUPO DE OPERARIOS");
		
		adb.setPositiveButton(
				"OK",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						start_team_of_work();
						finish();
						dialog.dismiss();
					}
				});
		String selectedOperators = sharedpreferences.getString("SELECTED_OPERATORS", null);
		if(selectedOperators == null){
			adb.show();
//			Toast.makeText(this, "Debe agregar un grupo de trabajo", Toast.LENGTH_LONG).show();	
		}
		
		super.onResume();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RESULT_OK){
			if (resultCode == 2) {
				
				
			}
		}
	}
	
	
	public void start_team_of_work(){
		Intent intent = new Intent(this, C_GrupoTrabajo.class);
		startActivity(intent);
	}
	
	public void start_collection(View v){
		Intent intent = new Intent(this, F_SeleccionarRuta.class);
		startActivityForResult(intent, RESULT_OK);
	}
	 
	public void exit_base(View v){
		
		
		
	}
	
}
