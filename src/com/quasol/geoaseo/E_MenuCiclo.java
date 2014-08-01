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
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class E_MenuCiclo extends Activity {

	private SharedPreferences sharedpreferences;
	private AlertDialog.Builder adb;
	private ImageButton btn_base_exit,btn_start_collection,btn_compactation,btn_collection_finish,btn_arrive_final_disposition,btn_come_back_to_base,btn_special_service,btn_inoperability;
	private Drawable d_base_exit,d_base_exit_two,d_start_collection,d_start_collection_two,d_compactation,d_compactation_two,d_collection_finish,d_collection_finish_two,d_arrive_final_disposition,d_arrive_final_disposition_two,d_come_back_to_base,d_come_back_to_base_two,d_special_service,d_special_service_two,d_inoperability,d_inoperability_two;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.e__menu_ciclo);
		this.sharedpreferences = getSharedPreferences("MyPreferences",Context.MODE_PRIVATE);
		this.initialize_components();
		int current_state = sharedpreferences.getInt("CURRENT_STATE", 0);
		if(current_state != 0){
			if(current_state==1){
				buttons_exit_base();
			}
			else if(current_state==2){
				buttons_start_collection();
			}
		}
	}
	
	@Override
	protected void onResume() {
		this.adb.setTitle("DEBE AGREGAR UN GRUPO DE OPERARIOS");
		this.adb.setPositiveButton(
				"ACEPTAR",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						start_team_of_work();
						finish();
						dialog.dismiss();
					}
				});
		String selectedOperators = sharedpreferences.getString("SELECTED_OPERATORS", null);
		if(selectedOperators == null || selectedOperators.length()<=2){
			this.adb.show();
		}
		super.onResume();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 10){
			if (resultCode == 2) {
				buttons_start_collection();
			}
		}
	}
	
	
	public void start_team_of_work(){
		Intent intent = new Intent(this, C_GrupoTrabajo.class);
		startActivity(intent);
	}
	
	public void start_collection(View v){
		Intent intent = new Intent(this, F_SeleccionarRuta.class);
		startActivityForResult(intent, 10);
	}
	 
	public void exit_base(View v){
		this.adb.setTitle("ESTA SEGURO QUE VA A SALIR DE LA BASE ");
		this.adb.setPositiveButton(
				"SI",
				new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						buttons_exit_base();
					}
				});
		this.adb.setNegativeButton(
				"NO", 
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		this.adb.show();
	}
	
	
	public void buttons_exit_base(){
		
		SharedPreferences.Editor editor = sharedpreferences.edit();
		int current_state = sharedpreferences.getInt("CURRENT_STATE", 0);
		if(current_state==0){
			editor.putInt("CURRENT_STATE", 1);
			editor.commit();
		}
		this.btn_base_exit.setImageDrawable(this.d_base_exit_two);
		this.btn_base_exit.setEnabled(false);
		
		this.btn_start_collection.setImageDrawable(this.d_start_collection);
		this.btn_start_collection.setEnabled(true);
		
		this.btn_inoperability.setImageDrawable(this.d_inoperability);
		this.btn_inoperability.setEnabled(true);
		
		this.btn_special_service.setImageDrawable(this.d_special_service);
		this.btn_special_service.setEnabled(true);
	}
	
public void buttons_start_collection(){
	
		this.btn_base_exit.setImageDrawable(this.d_base_exit_two);
		this.btn_base_exit.setEnabled(false);
	
		this.btn_start_collection.setImageDrawable(this.d_start_collection_two);
		this.btn_start_collection.setEnabled(false);
	
		this.btn_compactation.setImageDrawable(this.d_compactation);
		this.btn_compactation.setEnabled(true);
		
		this.btn_special_service.setImageDrawable(this.d_special_service_two);
		this.btn_special_service.setEnabled(false);
		
		this.btn_collection_finish.setImageDrawable(this.d_collection_finish);
		this.btn_collection_finish.setEnabled(true);
		
		this.btn_inoperability.setImageDrawable(this.d_inoperability);
		this.btn_inoperability.setEnabled(true);
	}
	
	public void compactation(View v){
		
		this.adb.setTitle("SEGURO QUE DESEA REALIZAR UNA COMPACTACIÓN");
		this.adb.setPositiveButton(
				getResources().getString(R.string.confirm_button_1),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						try {
							
							JSONArray plannedRoutes = new JSONArray((String)sharedpreferences.getString("PLANNED_ROUTES", null)); 
							int position = (int)sharedpreferences.getInt("POS_CURRENT_ROUTE",7000);
							JSONObject select_route = plannedRoutes.getJSONObject(position);
							int compactations = select_route.getInt("compactaciones");
							compactations=compactations+1;
							select_route.put("compactaciones",compactations);
							SharedPreferences.Editor editor = sharedpreferences.edit();
							editor.putString("PLANNED_ROUTES", plannedRoutes.toString());
							editor.commit();
							
							
						} catch (Exception e) {
							// TODO: handle exception
						}
						
						
						
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
	
	/**
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
						finish();
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
	
public void initialize_components(){
		
		//Initialize controls
		this.adb= new AlertDialog.Builder(this);
		this.btn_base_exit=(ImageButton)findViewById(R.id.btn_base_exit);
		this.btn_start_collection=(ImageButton)findViewById(R.id.btn_start_collection);
		this.btn_start_collection.setEnabled(false);
		this.btn_compactation=(ImageButton)findViewById(R.id.btn_compactation);
		this.btn_compactation.setEnabled(false);
		this.btn_collection_finish=(ImageButton)findViewById(R.id.btn_collection_finish);
		this.btn_collection_finish.setEnabled(false);
		this.btn_arrive_final_disposition=(ImageButton)findViewById(R.id.btn_arrive_final_disposition);
		this.btn_arrive_final_disposition.setEnabled(false);
		this.btn_come_back_to_base=(ImageButton)findViewById(R.id.btn_come_back_to_base);
		this.btn_come_back_to_base.setEnabled(false);
		this.btn_special_service=(ImageButton)findViewById(R.id.btn_special_service);
		this.btn_special_service.setEnabled(false);
		this.btn_inoperability=(ImageButton)findViewById(R.id.btn_inoperability);
		this.btn_inoperability.setEnabled(false);
		
		//Inizialize drawable objects
		this.d_base_exit = this.getResources().getDrawable(R.drawable.btn_base_exit);
		this.d_base_exit_two = this.getResources().getDrawable(R.drawable.btn_base_exit_two);
		this.d_start_collection = this.getResources().getDrawable(R.drawable.btn_start_collection);
		this.d_start_collection_two = this.getResources().getDrawable(R.drawable.btn_start_collection_two);
		this.d_compactation = this.getResources().getDrawable(R.drawable.btn_compaction);
		this.d_compactation_two = this.getResources().getDrawable(R.drawable.btn_compaction_two);
		this.d_collection_finish = this.getResources().getDrawable(R.drawable.btn_collection_finish);
		this.d_collection_finish_two = this.getResources().getDrawable(R.drawable.btn_collection_finish_two);
		this.d_arrive_final_disposition = this.getResources().getDrawable(R.drawable.btn_arrive_final_disposition);
		this.d_arrive_final_disposition_two = this.getResources().getDrawable(R.drawable.btn_arrive_final_disposition_two);
		this.d_come_back_to_base = this.getResources().getDrawable(R.drawable.btn_come_back_to_base);
		this.d_come_back_to_base_two = this.getResources().getDrawable(R.drawable.btn_come_back_to_base_two);
		this.d_special_service = this.getResources().getDrawable(R.drawable.btn_special_service);
		this.d_special_service_two = this.getResources().getDrawable(R.drawable.btn_special_service_two);
		this.d_inoperability = this.getResources().getDrawable(R.drawable.btn_inoperability);
		this.d_inoperability_two = this.getResources().getDrawable(R.drawable.btn_inoperability_two);
		
	}
	
}
