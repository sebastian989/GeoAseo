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
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class G_TicketRelleno extends Activity {
	
	private SharedPreferences sharedpreferences;
	private EditText numberTicket,weightTicket;
	private Spinner kindOfRecidue;
	private AlertDialog.Builder adb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.g__ticket_relleno);
		this.sharedpreferences = getSharedPreferences("MyPreferences",Context.MODE_PRIVATE);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		inicializeComponents();
	}
	
	public void setTiket(View v){
		
//		String numberTiket = this.numberTicket.getText().toString();
		
			if(!this.numberTicket.getText().toString().equals("") && !this.weightTicket.getText().toString().equals("")){
				
				try {
					JSONArray plannedRoutes = new JSONArray(sharedpreferences.getString("PLANNED_ROUTES",""));
					JSONArray tikets=new JSONArray();
					JSONObject auxRoute;
					JSONObject auxTiket=new JSONObject();
					auxTiket.put("tiket", this.numberTicket.getText());
					auxTiket.put("peso_ticket", this.weightTicket.getText());
					auxTiket.put("tipo_reciduo", this.kindOfRecidue.getSelectedItem());
					auxTiket.put("fecha_hora_entrada", sharedpreferences.getString("TIME_START_IN_FILLER",""));
					auxTiket.put("fecha_hora_dalida", Utilities.getDate());
					for (int i = 0; i < plannedRoutes.length(); i++) {
						auxRoute = (JSONObject)plannedRoutes.get(i);
						if(auxRoute.getString("estado").equals("iniciada")){
							tikets=auxRoute.getJSONArray("tickets");
							tikets.put(auxTiket);
						}
						else if(auxRoute.getString("estado").equals("terminada")&& auxRoute.getBoolean("ticket_pendiente")){
							tikets=auxRoute.getJSONArray("tickets");
							tikets.put(auxTiket);
							auxRoute.put("ticket_pendiente", false);
						}
					}
					
					SharedPreferences.Editor editor = sharedpreferences.edit();
					editor.putString("PLANNED_ROUTES",plannedRoutes.toString());
					editor.putBoolean("IN_FILLER", false);
					editor.putInt("COMPACTIONS", 0);
					editor.commit();
					sendInformation();
					this.adb.setTitle("TIKET ALMACENADO CON EXITO");
					this.adb.setPositiveButton(getResources().getString(R.string.accept_button),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									SharedPreferences.Editor editor = sharedpreferences.edit();
									editor.putInt("CURRENT_STATE", 5);
									editor.commit();
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
				Utilities.showAlert(this,getResources().getString(R.string.alertTicketEmpty));
			}
	}
	
	public void inicializeComponents(){
	
		this.adb = new AlertDialog.Builder(this);
		this.numberTicket = (EditText)findViewById(R.id.tiketNumber);
		this.weightTicket = (EditText)findViewById(R.id.weightTiket);
		this.kindOfRecidue = (Spinner) findViewById(R.id.kindOfResidue);
		
	}
	
	public void sendInformation() {
		try {
			JSONArray auxJson = new JSONArray(this.sharedpreferences.getString("PLANNED_ROUTES", ""));
			
			new SaveInformation(this).execute(
					"http://pruebasgeoaseo.tk/controller/Fachada.php", "test",
					"ticket_relleno", auxJson.toString());
		} catch (Exception e) {
		}
	}
	
	@Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }
	
	/**
	 * Method to close the session
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
	
}
