package com.quasol.geoaseo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.quasol.recursos.Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class J_CerrarRuta extends Activity implements OnItemClickListener {

	private SharedPreferences sharedpreferences;
	private TextView lblSelectedRoute,lblRouteSheet;
	private Button btnCerrar;
	private ListView lstRoutes;
	private JSONArray plannedRoutes;
	private JSONArray closeRoutes;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.j__cerrar_ruta);
		this.identifyElements();
		this.lstRoutes.setOnItemClickListener(this);
		this.sharedpreferences = getSharedPreferences("MyPreferences",Context.MODE_PRIVATE);
		showStartRoutes();
	}
	
	
	
	private  void identifyElements(){
		this.lblSelectedRoute = (TextView) findViewById(R.id.lblNameRoute);
		this.lblRouteSheet = (TextView) findViewById(R.id.lblSheetSelect);
		this.lstRoutes = (ListView) findViewById(R.id.listActiveRoutes);
		this.btnCerrar = (Button) findViewById(R.id.btnCloseRoute);
		this.plannedRoutes = new JSONArray();
		this.closeRoutes = new JSONArray();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		 try {
			Toast.makeText(this, this.closeRoutes.getJSONObject(position).getString("nombre"), Toast.LENGTH_LONG).show();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void showStartRoutes(){
		
		try {
			 this.plannedRoutes = new JSONArray(this.sharedpreferences.getString("PLANNED_ROUTES", null));
			 
				for(int i=0; i<this.plannedRoutes.length(); i++){
					if(this.plannedRoutes.getJSONObject(i).get("estado").equals("iniciada")){
						this.closeRoutes.put(this.plannedRoutes.getJSONObject(i));
					}
				}
				this.displayRoutes();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void displayRoutes(){
		String [] listRouteNames = new String [this.closeRoutes.length()];
		
			try {
				for (int i = 0; i < this.closeRoutes.length(); i++) {
					listRouteNames[i]=this.closeRoutes.getJSONObject(i).getString("nombre");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listRouteNames);
		this.lstRoutes.setAdapter(adapter);
	}

	
}
