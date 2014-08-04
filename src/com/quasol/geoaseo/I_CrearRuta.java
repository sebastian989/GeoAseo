package com.quasol.geoaseo;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;

public class I_CrearRuta extends Activity {
	
	private EditText search;
	private ListView lstRoutes;
	private RadioGroup radioGroup;
	private SharedPreferences sharedpreferences;
	private JSONArray allAlternativeRoutes;
	private String [] lstRouteNames;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.i__crear_ruta);
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		this.sharedpreferences = getSharedPreferences("MyPreferences",Context.MODE_PRIVATE);
		this.identifyElements();
		this.displayAllRoutes();
	}
	
	private void displayAllRoutes(){
		String strAllRoutes = this.sharedpreferences.getString("ALTERNATE_ROUTES", null);
		try {
			this.allAlternativeRoutes = new JSONArray(strAllRoutes);
			this.lstRouteNames = new String [this.allAlternativeRoutes.length()];
			for(int i=0; i<this.allAlternativeRoutes.length(); i++){
				this.lstRouteNames[i] = this.allAlternativeRoutes.getJSONObject(i).getString("nombre");
			}
			final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, this.lstRouteNames);
			this.lstRoutes.setAdapter(adapter);
		} catch (JSONException e) {
		}
	}
	
	private void save(View v){
		
	}
	
	private void identifyElements(){
		this.search = (EditText) findViewById(R.id.txtSearch);
		this.lstRoutes = (ListView) findViewById(R.id.lstAllRoutes);
		this.radioGroup = (RadioGroup) findViewById(R.id.radioGroupTypeRoute);
	}
}
