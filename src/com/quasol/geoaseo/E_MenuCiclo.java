package com.quasol.geoaseo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.quasol.recursos.SaveInformation;
import com.quasol.recursos.Utilities;

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
import android.widget.TextView;

public class E_MenuCiclo extends Activity {

	private SharedPreferences sharedpreferences;
	private AlertDialog.Builder adb;
	private ImageButton btn_base_exit, btn_start_collection, btn_compactation,
			btn_collection_finish, btn_arrive_final_disposition,
			btn_come_back_to_base, btn_special_service, btn_inoperability;
	private Drawable d_base_exit, d_base_exit_two, d_start_collection,
			d_start_collection_two, d_compactation, d_compactation_two,
			d_collection_finish, d_collection_finish_two,
			d_arrive_final_disposition, d_arrive_final_disposition_two,
			d_come_back_to_base, d_come_back_to_base_two, d_special_service,
			d_special_service_two, d_inoperability, d_inoperability_two;
	private JSONArray send_data_json;
	private String method;
	private TextView numberOfCompatations,nameRoute;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.e__menu_ciclo);
		this.sharedpreferences = getSharedPreferences("MyPreferences",Context.MODE_PRIVATE);
		this.initializeComponents();
		currentState();
	}

	public void baseOut(View v) {

		this.adb.setTitle("DEBE AGREGAR UN GRUPO DE OPERARIOS");
		this.adb.setPositiveButton("ACEPTAR",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						startTeamOfWork();
						finish();
						dialog.dismiss();
					}
				});
		String selectedOperators = sharedpreferences.getString("SELECTED_OPERATORS", null);
		if (selectedOperators == null || selectedOperators.length() <= 2) {
			this.adb.show();
		} else {
			this.adb.setTitle("ESTA SEGURO QUE VA A SALIR DE LA BASE ");
			this.adb.setPositiveButton("SI",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							buttonsOutBase();
							method="salida_base";
							sendInformation();
						}
					});
			this.adb.setNegativeButton("NO",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			this.adb.show();
		}
	}

	public void startCollection(View v) {
		Intent intent = new Intent(this, F_SeleccionarRuta.class);
		startActivityForResult(intent, 10);
	}

	public void compactation(View v) {

		this.adb.setTitle("SEGURO QUE DESEA REALIZAR UNA COMPACTACIÓN");
		this.adb.setPositiveButton(
				getResources().getString(R.string.confirm_button_1),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							JSONArray plannedRoutes = new JSONArray(
									(String) sharedpreferences.getString(
											"PLANNED_ROUTES", null));
							int position = (int) sharedpreferences.getInt(
									"POS_CURRENT_ROUTE", 7000);
							JSONObject select_route = plannedRoutes
									.getJSONObject(position);
							int compactations = select_route
									.getInt("compactaciones");
							compactations = compactations + 1;
							select_route.put("compactaciones", compactations);
							SharedPreferences.Editor editor = sharedpreferences
									.edit();
							editor.putString("PLANNED_ROUTES",
									plannedRoutes.toString());
							editor.commit();
							setCompactationsAndSheet();
						} catch (Exception e) {
							// TODO: handle exception
						}

					}
				});
		this.adb.setNegativeButton(
				getResources().getString(R.string.confirm_button_2),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		this.adb.show();
	}
	
	public void finishCollection(View v) {

		this.adb.setTitle("DESEA FINALIZAR EL PORTE");
		this.adb.setPositiveButton(
				getResources().getString(R.string.confirm_button_1),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						SharedPreferences.Editor editor = sharedpreferences.edit();
						editor.putInt("CURRENT_STATE", 4);
						editor.commit();
						try {
							JSONArray auxRutes = new JSONArray(sharedpreferences.getString("PLANNED_ROUTES", null));
							int position = sharedpreferences.getInt("POS_CURRENT_ROUTE", 0);
							send_data_json = new JSONArray();
							send_data_json.put((JSONObject)auxRutes.getJSONObject(position));
							method="fin_porte";
							sendInformation();
						} catch (Exception e) {
						}
						buttonsFinishCollectionOrFinishFiller(1);
					}
				});
		this.adb.setNegativeButton(
				getResources().getString(R.string.confirm_button_2),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		this.adb.show();
	}

	public void arriveFinalDisposition(View v){
		Intent intent = new Intent(this, G_formulario_Relleno.class);
		startActivity(intent);
	}
	
	public void comeBackToBase(View v){
		
			this.adb.setTitle("ESTA SEGURO QUE LLEGO A LA BASE ");
			this.adb.setPositiveButton("SI",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							buttonsInBase();
							JSONArray auxRutes;
							try {
								send_data_json = new JSONArray(sharedpreferences.getString("PLANNED_ROUTES", null));
								method="regreso_base";
								sendInformation();
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
			this.adb.setNegativeButton("NO",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			this.adb.show();
		
		
	}
	
	public void specialService(View v){
		
		
		
	}
	
	public void inoperability(View v){
		Intent intent = new Intent(this, H_RegistrarInoperatividad.class);
		startActivity(intent);
	}
	
	//Configurations methods
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 10) {
			if (resultCode == 2) {
				buttonsStartCollection();
			}
		}
	}
	
	@Override
	protected void onResume() {
		setCompactationsAndSheet();
		if (sharedpreferences.getBoolean("INOPERABILITY", false)) {
			buttonsInoperabilityOrFiller(1);
		} 
		else if (sharedpreferences.getBoolean("IN_FILLER",false))
		{
			buttonsInoperabilityOrFiller(2);
		}
		else{
			currentState();
		}
		super.onResume();
	}
	
	public void sendInformation(){
		try {
			new SaveInformation(this).execute("http://pruebasgeoaseo.tk/controller/Fachada.php",
					"test",
					this.method,
					this.send_data_json.toString());
		} catch (Exception e) {
		}
	}
	
	public void startTeamOfWork() {
		Intent intent = new Intent(this, C_GrupoTrabajo.class);
		startActivity(intent);
	}
	
	public void buttonsOutBase() {
		
		SharedPreferences.Editor editor = sharedpreferences.edit();
		int current_state = sharedpreferences.getInt("CURRENT_STATE", 0);
		
		if (current_state == 0) {
			editor.putInt("CURRENT_STATE", 1);
			editor.commit();
		}
		
		this.btn_base_exit.setImageDrawable(this.d_base_exit_two);
		this.btn_base_exit.setEnabled(false);
		
		this.btn_start_collection.setImageDrawable(this.d_start_collection);
		this.btn_start_collection.setEnabled(true);
		
		this.btn_compactation.setImageDrawable(this.d_compactation_two);
		this.btn_compactation.setEnabled(false);
		
		this.btn_collection_finish.setImageDrawable(this.d_collection_finish_two);
		this.btn_collection_finish.setEnabled(false);
		
		this.btn_arrive_final_disposition.setImageDrawable(this.d_arrive_final_disposition_two);
		this.btn_arrive_final_disposition.setEnabled(false);
		
		this.btn_come_back_to_base.setImageDrawable(this.d_come_back_to_base_two);
		this.btn_come_back_to_base.setEnabled(false);
	
		this.btn_special_service.setImageDrawable(this.d_special_service);
		this.btn_special_service.setEnabled(true);
		
		this.btn_inoperability.setImageDrawable(this.d_inoperability);
		this.btn_inoperability.setEnabled(true);
	}
	
	public void buttonsInBase() {
		SharedPreferences.Editor editor = sharedpreferences.edit();
		editor.putInt("CURRENT_STATE", 1);
		editor.commit();
		buttonsOutBase();
	}
	

	/**
	 * @category Configuration
	 *  
	 */
	public void buttonsStartCollection() {
		
		this.btn_base_exit.setImageDrawable(this.d_base_exit_two);
		this.btn_base_exit.setEnabled(false);
		
		this.btn_start_collection.setImageDrawable(this.d_start_collection_two);
		this.btn_start_collection.setEnabled(false);
		
		this.btn_compactation.setImageDrawable(this.d_compactation);
		this.btn_compactation.setEnabled(true);
		
		this.btn_collection_finish.setImageDrawable(this.d_collection_finish);
		this.btn_collection_finish.setEnabled(true);
		
		this.btn_arrive_final_disposition.setImageDrawable(this.d_arrive_final_disposition_two);
		this.btn_arrive_final_disposition.setEnabled(false);
		
		this.btn_come_back_to_base.setImageDrawable(this.d_come_back_to_base_two);
		this.btn_come_back_to_base.setEnabled(false);
	
		this.btn_special_service.setImageDrawable(this.d_special_service_two);
		this.btn_special_service.setEnabled(false);
		
		this.btn_inoperability.setImageDrawable(this.d_inoperability);
		this.btn_inoperability.setEnabled(true);
		
	}

	/**
	 * 
	 * @param type
	 */
	public void buttonsFinishCollectionOrFinishFiller(int type) {

		this.btn_base_exit.setImageDrawable(this.d_base_exit_two);
		this.btn_base_exit.setEnabled(false);
		
		this.btn_start_collection.setImageDrawable(this.d_start_collection);
		this.btn_start_collection.setEnabled(true);
		
		this.btn_compactation.setImageDrawable(this.d_compactation_two);
		this.btn_compactation.setEnabled(false);
		
		this.btn_collection_finish.setImageDrawable(this.d_collection_finish_two);
		this.btn_collection_finish.setEnabled(false);
		
		if (type==1){
			
			this.btn_arrive_final_disposition.setImageDrawable(this.d_arrive_final_disposition);
			this.btn_arrive_final_disposition.setEnabled(true);
			
		}
		else if (type == 2){
			
			this.btn_arrive_final_disposition.setImageDrawable(this.d_arrive_final_disposition_two);
			this.btn_arrive_final_disposition.setEnabled(false);
			
		}
		
		this.btn_come_back_to_base.setImageDrawable(this.d_come_back_to_base);
		this.btn_come_back_to_base.setEnabled(true);
		
		this.btn_special_service.setImageDrawable(this.d_special_service_two);
		this.btn_special_service.setEnabled(false);
		
		this.btn_inoperability.setImageDrawable(this.d_inoperability);
		this.btn_inoperability.setEnabled(true);
	}
	
	/**
	 * 
	 * @param type
	 */
	public void buttonsInoperabilityOrFiller(int type){
		this.btn_base_exit.setImageDrawable(this.d_base_exit_two);
		this.btn_base_exit.setEnabled(false);
		
		this.btn_start_collection.setEnabled(false);
		this.btn_start_collection.setImageDrawable(this.d_base_exit_two);
		
		this.btn_compactation.setImageDrawable(this.d_compactation_two);
		this.btn_compactation.setEnabled(false);
		
		this.btn_collection_finish.setImageDrawable(this.d_collection_finish_two);
		this.btn_collection_finish.setEnabled(false);
		
		this.btn_come_back_to_base.setImageDrawable(this.d_come_back_to_base_two);
		this.btn_come_back_to_base.setEnabled(false);
		
		this.btn_special_service.setImageDrawable(this.d_special_service_two);
		this.btn_special_service.setEnabled(false);
		
		if(type == 1)
		{
		
			this.btn_inoperability.setImageDrawable(this.d_inoperability);
			this.btn_inoperability.setEnabled(true);
			
			this.btn_arrive_final_disposition.setImageDrawable(this.d_arrive_final_disposition_two);
			this.btn_arrive_final_disposition.setEnabled(false);
		
		}
		else if (type == 2)
		{
			
			this.btn_arrive_final_disposition.setImageDrawable(this.d_arrive_final_disposition);
			this.btn_arrive_final_disposition.setEnabled(true);
			
			this.btn_inoperability.setImageDrawable(this.d_inoperability_two);
			this.btn_inoperability.setEnabled(false);
		
		}
		
	}
	
//	public void buttonsExitFiller(){
//		
//		this.btn_base_exit.setImageDrawable(this.d_base_exit_two);
//		this.btn_base_exit.setEnabled(false);
//		
//		this.btn_start_collection.setImageDrawable(this.d_start_collection);
//		this.btn_start_collection.setEnabled(true);
//		
//		this.btn_compactation.setImageDrawable(this.d_compactation_two);
//		this.btn_compactation.setEnabled(false);
//		
//		this.btn_collection_finish.setImageDrawable(this.d_collection_finish_two);
//		this.btn_collection_finish.setEnabled(false);
//		
//		this.btn_arrive_final_disposition.setImageDrawable(this.d_arrive_final_disposition);
//		this.btn_arrive_final_disposition.setEnabled(true);
//		
//		this.btn_come_back_to_base.setImageDrawable(this.d_come_back_to_base);
//		this.btn_come_back_to_base.setEnabled(true);
//		
//		this.btn_special_service.setImageDrawable(this.d_special_service_two);
//		this.btn_special_service.setEnabled(false);
//		
//		this.btn_inoperability.setImageDrawable(this.d_inoperability);
//		this.btn_inoperability.setEnabled(true);
//	}


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
						Intent intent = new Intent(getApplicationContext(), A_LogIn.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
					}
				});
		adb.setNegativeButton(
				getResources().getString(R.string.confirm_button_2),
				new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
		adb.show();
	}

	public void initializeComponents() {

		// Initialize controls
		this.adb = new AlertDialog.Builder(this);
		this.btn_base_exit = (ImageButton) findViewById(R.id.btn_base_exit);
		this.btn_start_collection = (ImageButton) findViewById(R.id.btn_start_collection);
		this.btn_start_collection.setEnabled(false);
		this.btn_compactation = (ImageButton) findViewById(R.id.btn_compactation);
		this.btn_compactation.setEnabled(false);
		this.btn_collection_finish = (ImageButton) findViewById(R.id.btn_collection_finish);
		this.btn_collection_finish.setEnabled(false);
		this.btn_arrive_final_disposition = (ImageButton) findViewById(R.id.btn_arrive_final_disposition);
		this.btn_arrive_final_disposition.setEnabled(false);
		this.btn_come_back_to_base = (ImageButton) findViewById(R.id.btn_come_back_to_base);
		this.btn_come_back_to_base.setEnabled(false);
		this.btn_special_service = (ImageButton) findViewById(R.id.btn_special_service);
		this.btn_special_service.setEnabled(false);
		this.btn_inoperability = (ImageButton) findViewById(R.id.btn_inoperability);
		this.btn_inoperability.setEnabled(false);

		// Inizialize drawable objects
		this.d_base_exit = this.getResources().getDrawable(
				R.drawable.btn_base_exit);
		this.d_base_exit_two = this.getResources().getDrawable(
				R.drawable.btn_base_exit_two);
		this.d_start_collection = this.getResources().getDrawable(
				R.drawable.btn_start_collection);
		this.d_start_collection_two = this.getResources().getDrawable(
				R.drawable.btn_start_collection_two);
		this.d_compactation = this.getResources().getDrawable(
				R.drawable.btn_compaction);
		this.d_compactation_two = this.getResources().getDrawable(
				R.drawable.btn_compaction_two);
		this.d_collection_finish = this.getResources().getDrawable(
				R.drawable.btn_collection_finish);
		this.d_collection_finish_two = this.getResources().getDrawable(
				R.drawable.btn_collection_finish_two);
		this.d_arrive_final_disposition = this.getResources().getDrawable(
				R.drawable.btn_arrive_final_disposition);
		this.d_arrive_final_disposition_two = this.getResources().getDrawable(
				R.drawable.btn_arrive_final_disposition_two);
		this.d_come_back_to_base = this.getResources().getDrawable(
				R.drawable.btn_come_back_to_base);
		this.d_come_back_to_base_two = this.getResources().getDrawable(
				R.drawable.btn_come_back_to_base_two);
		this.d_special_service = this.getResources().getDrawable(
				R.drawable.btn_special_service);
		this.d_special_service_two = this.getResources().getDrawable(
				R.drawable.btn_special_service_two);
		this.d_inoperability = this.getResources().getDrawable(
				R.drawable.btn_inoperability);
		this.d_inoperability_two = this.getResources().getDrawable(
				R.drawable.btn_inoperability_two);
		this.send_data_json= new JSONArray();
		this.numberOfCompatations = (TextView) findViewById(R.id.numberOfCompatations); 
		this.nameRoute=(TextView) findViewById(R.id.nameRoute); 
		setCompactationsAndSheet();
	}
	
	public void currentState(){
		
		int current_state = sharedpreferences.getInt("CURRENT_STATE", 0);

		if (current_state != 0) {

			if (sharedpreferences.getBoolean("INOPERABILITY", false)) {
				buttonsInoperabilityOrFiller(1);
			} else if (sharedpreferences.getBoolean("IN_FILLER", false)) {
				buttonsInoperabilityOrFiller(2);
			} else if (current_state == 1) {
				buttonsOutBase();
			} else if (current_state == 2) {
				buttonsStartCollection();
			} else if (current_state == 4) {
				buttonsFinishCollectionOrFinishFiller(1);
			}
		}
		
	}
	
	public void setCompactationsAndSheet(){
		
		int position = sharedpreferences.getInt("POS_CURRENT_ROUTE", -1);
		if(position!=-1){
			try {
				JSONArray auxRoutes = new JSONArray(sharedpreferences.getString("PLANNED_ROUTES", null)); 
				JSONObject auxRoute = auxRoutes.getJSONObject(position);
				this.numberOfCompatations.setText(auxRoute.getString("compactaciones"));
				this.nameRoute.setText(auxRoute.getString("Hoja"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

}
