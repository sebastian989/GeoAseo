package com.quasol.geoaseo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.quasol.recursos.Utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class G_TicketRelleno extends Activity {
	
	private SharedPreferences sharedpreferences;
	private EditText numberTiket,weightTiket;
	private Spinner kindOfRecidue;
	private AlertDialog.Builder adb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.g__ticket_relleno);
		this.sharedpreferences = getSharedPreferences("MyPreferences",Context.MODE_PRIVATE);
		inicializeComponents();
	}
	
	public void setTiket(View v){
		
		String numberTiket = this.numberTiket.getText().toString();
		
			if(!this.numberTiket.getText().toString().equals("") && !this.weightTiket.getText().toString().equals("")){
				
				try {
					JSONArray plannedRoutes = new JSONArray(sharedpreferences.getString("PLANNED_ROUTES",""));
					JSONArray tikets=new JSONArray();
					JSONObject auxRoute;
					JSONObject auxTiket=new JSONObject();
					auxTiket.put("tiket", this.numberTiket.getText());
					auxTiket.put("peso_tiket", this.weightTiket.getText());
					auxTiket.put("tipo_reciduo", this.kindOfRecidue.getSelectedItem());
					auxTiket.put("fecha_hora_entrada", sharedpreferences.getString("TIME_START_IN_FILLER",""));
					auxTiket.put("fecha_hora_dalida", Utilities.getDate());
					for (int i = 0; i < plannedRoutes.length(); i++) {
						auxRoute = (JSONObject)plannedRoutes.get(i);
						if(auxRoute.getString("estado").equals("iniciada")){
							tikets=auxRoute.getJSONArray("tickets");
							tikets.put(auxTiket);
						}
					}
					
					SharedPreferences.Editor editor = sharedpreferences.edit();
					editor.putString("PLANNED_ROUTES",plannedRoutes.toString());
					editor.putBoolean("IN_FILLER", false);
					editor.putInt("CURRENT_STATE", 5);
					editor.commit();
					this.adb.setTitle("TIKET ALMACENADO CON EXITO");
					this.adb.setPositiveButton(getResources().getString(R.string.accept_button),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									
									Intent intent = new Intent();
									setResult(2, intent);
									finish();
									
								}
							});
					this.adb.setCancelable(false);
					this.adb.show();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else{
				Utilities.showAlert(this,"DEBE COMPLETAR LOS CAMPOS NUMERO Y PESO DEL TIKET");
			}
		
	}
	
	
	
	
	public void inicializeComponents(){
	
		this.adb = new AlertDialog.Builder(this);
		this.numberTiket = (EditText)findViewById(R.id.tiketNumber);
		this.weightTiket = (EditText)findViewById(R.id.weightTiket);
		this.kindOfRecidue = (Spinner) findViewById(R.id.kindOfResidue);
		
	}
	
	
	
}
