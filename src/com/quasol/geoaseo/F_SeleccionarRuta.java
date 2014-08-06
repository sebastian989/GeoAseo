package com.quasol.geoaseo;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.quasol.recursos.SaveInformation;
import com.quasol.recursos.Utilities;

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
	private String method;

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
		ArrayList<String> listRouteNames = new ArrayList<>();
		for(int i=0; i<routes.length(); i++){
			try {
				if(routes.getJSONObject(i).getString("estado").equals("inactiva")||routes.getJSONObject(i).getString("estado").equals("iniciada")){
					listRouteNames.add(routes.getJSONObject(i).getString("nombre"));
				}
			} catch (JSONException e) {
			}
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listRouteNames);
		this.lstRoutes.setAdapter(adapter);
	}
	
	public void startRoute(View v){
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		if(this.selectRoute != null){
			try {
				SharedPreferences.Editor editor = this.sharedpreferences.edit();
				if(this.selectRoute.getString("estado").equals("inactiva")){
					this.selectRoute.put("estado", "iniciada");
					this.selectRoute.put("fecha_inicio", Utilities.getDate());
					this.plannedRoutes.put(this.routePosition, this.selectRoute);
					editor.putString("PLANNED_ROUTES", this.plannedRoutes.toString());
					editor.commit();
					adb.setTitle("DESEA INICIAR LA RUTA "+ this.selectRoute.getString("nombre"));
					this.method = "iniciar_porte";
				}
				else{
					adb.setTitle("DESEA CONTINUAR LA RUTA "+ this.selectRoute.getString("nombre"));
					this.method = "continuar_porte";
				}
				editor.putInt("POS_CURRENT_ROUTE", this.routePosition);
				editor.putInt("CURRENT_STATE", 2);
				editor.commit();
				adb.setPositiveButton(
						getResources().getString(R.string.confirm_button_1),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
								Intent intent = new Intent();
								setResult(2, intent);
								sendInformation();
								finish();
							}
						});
				adb.setNegativeButton(
						getResources().getString(R.string.confirm_button_2),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						});
				adb.show();
			} catch (JSONException e) {
			}
		}
		else{
			Utilities.showAlert(this, getResources().getString(R.string.alertNotSelectedRoute));
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
	
	public void sendInformation(){
		JSONArray data = new JSONArray();
		JSONArray auxjson = new JSONArray();
		JSONObject auxobject = new JSONObject();
		try {
			data.put(this.plannedRoutes.getJSONObject(this.routePosition));
			auxjson = new JSONArray(this.sharedpreferences.getString("SELECTED_OPERATORS", null));
			auxobject.put("operators_select", auxjson);
			data.put(auxobject);
			auxjson =  new JSONArray(this.sharedpreferences.getString("TRUCK_INFO",null));
			data.put(auxjson.get(0));

			new SaveInformation(this).execute("http://pruebasgeoaseo.tk/controller/Fachada.php",
					"test",
					this.method,
					data.toString());
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private  void identifyElements(){
		this.lblSelectedRoute = (TextView) findViewById(R.id.lblSelectedRoute);
		this.lblRouteSheet = (TextView) findViewById(R.id.lblSelectedSheet);
		this.lblRouteState = (TextView) findViewById(R.id.lblRouteState);
		this.lstRoutes = (ListView) findViewById(R.id.lstRoutes);
		this.btnIniciar = (Button) findViewById(R.id.btnIniciar);
	}
	
	@Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }
	
}
