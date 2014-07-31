package com.quasol.geoaseo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
		
		String selectedOperators = sharedpreferences.getString("SELECTED_OPERATORS", null);
		if(selectedOperators == null){
			Intent intent = new Intent(this, C_GrupoTrabajo.class);
			startActivity(intent);
			Toast.makeText(this, "Debe agregar un grupo de trabajo", Toast.LENGTH_LONG).show();	
			finish();
		}
		
		super.onResume();
	}
	
}
