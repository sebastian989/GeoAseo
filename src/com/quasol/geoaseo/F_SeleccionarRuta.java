package com.quasol.geoaseo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.quasol.recursos.Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class F_SeleccionarRuta extends Activity implements OnItemClickListener {
	
	private SharedPreferences sharedpreferences;
	private TextView lblSelectedRoute;
	private TextView lblRouteSheet;
	private TextView lblRouteState;
	private Button btnIniciar;
	private ListView lstRoutes;
	private JSONArray plannedRoutes;
	private JSONObject selectRoute;
	private int routePosition;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.f__seleccionar_ruta);
		
		this.identifyElements();
		this.lstRoutes.setOnItemClickListener(this);
		this.sharedpreferences = getSharedPreferences("MyPreferences",Context.MODE_PRIVATE);
		try {
			this.plannedRoutes = new JSONArray(this.sharedpreferences.getString("PLANNED_ROUTES", null));
			this.displayRoutes(plannedRoutes);
		} catch (JSONException e) {
			Toast.makeText(this, getResources().getString(R.string.toastLoadRoutes), Toast.LENGTH_SHORT).show();
		}
	}
	
	private void displayRoutes(JSONArray routes){
		String [] listRouteNames = new String [routes.length()] ;
		for(int i=0; i<routes.length(); i++){
			try {
				listRouteNames[i] = routes.getJSONObject(i).getString("nombre");
			} catch (JSONException e) {
			}
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listRouteNames);
		this.lstRoutes.setAdapter(adapter);
	}
	
	public void startRoute(View v){
		if(this.selectRoute != null){
			try {
				if(this.selectRoute.getString("estado").equals("inactiva")){
					this.selectRoute.put("estado", "iniciada");
					this.selectRoute.put("fecha", Utilities.getDate());
					this.plannedRoutes.put(this.routePosition, this.selectRoute);
					SharedPreferences.Editor editor = this.sharedpreferences.edit();
					editor.putString("PLANNED_ROUTES", this.plannedRoutes.toString());
					editor.putString("POS_CURRENT_ROUTE", String.valueOf(this.routePosition));
					editor.commit();
					Toast.makeText(this, getResources().getString(R.string.toastRouteStarted), Toast.LENGTH_SHORT).show();
					this.finish();
				}
			} catch (JSONException e) {
			}
		}
		else{
			Toast.makeText(this, getResources().getString(R.string.toastNotSelectedRoute), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	
		try {
			this.routePosition = position;
			this.selectRoute = this.plannedRoutes.getJSONObject(position);
			this.lblSelectedRoute.setText(this.selectRoute.getString("nombre"));
			this.lblRouteSheet.setText(this.selectRoute.getString("Hoja"));
			this.lblRouteState.setText(this.selectRoute.getString("estado"));
			if(this.selectRoute.getString("estado").equals("terminada")){
				this.btnIniciar.setEnabled(false);
			}
			else if(this.selectRoute.getString("estado").equals("iniciada")){
				this.btnIniciar.setEnabled(true);
				this.btnIniciar.setText(getResources().getString(R.string.btnContinueRoute));
			}
			else{
				this.btnIniciar.setEnabled(true);
				this.btnIniciar.setText(getResources().getString(R.string.btnStartRoute));
			}
		} catch (JSONException e) {
		}	
	}
	
	private  void identifyElements(){
		this.lblSelectedRoute = (TextView) findViewById(R.id.lblSelectedRoute);
		this.lblRouteSheet = (TextView) findViewById(R.id.lblSelectedSheet);
		this.lblRouteState = (TextView) findViewById(R.id.lblRouteState);
		this.lstRoutes = (ListView) findViewById(R.id.lstRoutes);
		this.btnIniciar = (Button) findViewById(R.id.btnIniciar);
	}
}