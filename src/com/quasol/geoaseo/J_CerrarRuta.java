package com.quasol.geoaseo;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.quasol.recursos.SaveInformation;
import com.quasol.recursos.Utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
	private ListView lstRoutes;
	private JSONArray plannedRoutes;
	private JSONArray closeRoutes;
	private JSONObject closeRoute;
	private AlertDialog.Builder adb;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.j__cerrar_ruta);
		this.identifyElements();
		this.lstRoutes.setOnItemClickListener(this);
		this.sharedpreferences = getSharedPreferences("MyPreferences",Context.MODE_PRIVATE);
		showStartRoutes();
	}
	
	public void closeRoute(View v){
		
		if(this.closeRoute==null){
			Utilities.showAlert(this, "DEBE SELECCIONAR UNA RUTA PARA PODER FINALIZARLA");
		}
		else{
			try {
				if(this.closeRoute.getJSONArray("tickets").length()>0){
					
					this.adb.setTitle("DESEA CERRAR LA RUTA");
					this.adb.setPositiveButton(getResources().getString(R.string.confirm_button_1),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									changeStateRoute();
									sendInformation();
									dialog.dismiss();
								}
							});
					this.adb.setNegativeButton(getResources().getString(R.string.confirm_button_2),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							});
					this.adb.show();
					
				}
				else{
					Utilities.showAlert(this,"LA RUTA DEBE TENER ALMENOS UN TICKET PARA PODER SER CERRADA");
				}
				
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		
	}
	
	private  void identifyElements(){
		this.lblSelectedRoute = (TextView) findViewById(R.id.lblNameRoute);
		this.lblRouteSheet = (TextView) findViewById(R.id.lblSheetSelect);
		this.lstRoutes = (ListView) findViewById(R.id.listActiveRoutes);
		this.plannedRoutes = new JSONArray();
		this.closeRoutes = new JSONArray();
		this.closeRoute = null;
		this.adb = new AlertDialog.Builder(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		 try {
			 this.closeRoute = this.closeRoutes.getJSONObject(position);
			 this.lblSelectedRoute.setText(this.closeRoute.getString("nombre"));
			 this.lblRouteSheet.setText(this.closeRoute.getString("Hoja"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void showStartRoutes(){
		
		ArrayList<String> listRouteNames = new ArrayList<>();
		
		try {
			 this.plannedRoutes = new JSONArray(this.sharedpreferences.getString("PLANNED_ROUTES", null));
			 
				for(int i=0; i<this.plannedRoutes.length(); i++){
					if(this.plannedRoutes.getJSONObject(i).get("estado").equals("iniciada")){
						this.closeRoutes.put(this.plannedRoutes.getJSONObject(i));
						listRouteNames.add(this.plannedRoutes.getJSONObject(i).getString("nombre"));
					}
				}
				
				this.displayRoutes(listRouteNames);
				
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void changeStateRoute(){
		int i=0;
		try {
				while(i<this.plannedRoutes.length()){
					if(this.plannedRoutes.getJSONObject(i).get("Hoja").equals(this.closeRoute.getString("Hoja"))){
						this.plannedRoutes.getJSONObject(i).put("estado", "terminada");
						this.plannedRoutes.getJSONObject(i).put("fecha_fin", Utilities.getDate());
						this.closeRoute=this.plannedRoutes.getJSONObject(i);
						i=this.plannedRoutes.length();
						SharedPreferences.Editor editor = sharedpreferences.edit();
						editor.putString("PLANNED_ROUTES", this.plannedRoutes.toString());
						editor.commit();
					}
					
					i++;
				}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void displayRoutes(ArrayList<String> closeRoutes){
		if(closeRoutes.size()>0){
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, closeRoutes);
			this.lstRoutes.setAdapter(adapter);
		}
		else{
			Utilities.showAlert(this, "NO TIENE NINGUNA RUTA INICIADA");
		}
	}

	public void sendInformation(){
			new SaveInformation(this).execute("http://pruebasgeoaseo.tk/controller/Fachada.php",
					"test",
					"cerrar_ruta",
					this.closeRoute.toString());
	}
	
}
