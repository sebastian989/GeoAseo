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
import android.widget.RadioGroup;

public class K_RegistrarCombustible extends Activity {
	
	private EditText txtTicket;
	private EditText txtGallons;
	private EditText txtProvider;
	private EditText txtCost;
	private RadioGroup radioGroup;
	private SharedPreferences sharedpreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.k__registrar_combustible);
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		this.identifyElements();
		this.sharedpreferences = getSharedPreferences("MyPreferences",Context.MODE_PRIVATE);
	}
	
	/**
	 * This method validate if the fields are empty, else ask user if really wants to save
	 * the information, if the answer is "yes" call the next method (sendInformation)
	 * @param v
	 */
	public void save(View v){
		final String ticket = this.txtTicket.getText().toString();
		final String gallons = this.txtGallons.getText().toString();
		final String provider = this.txtProvider.getText().toString();
		final String cost = this.txtCost.getText().toString();
		final String gasKind = (String)((Button)findViewById(this.radioGroup.getCheckedRadioButtonId())).getText().toString();
		
		if(ticket.equals("") || gallons.equals("") || cost.equals("")){
			Utilities.showAlert(this, getResources().getString(R.string.alertEditTextEmpty));
		}
		else{
			AlertDialog.Builder adb = new AlertDialog.Builder(this);
			adb.setTitle(getResources().getString(R.string.confirmSaveInformation));
			adb.setPositiveButton(
					getResources().getString(R.string.confirm_button_1),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							sendInformation(ticket, gallons, provider, gasKind, cost);
							finish();
						}
					});
			adb.setNegativeButton(getResources().getString(R.string.confirm_button_2),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			adb.show();
		}
	}
	
	/**
	 * This method save the information of the gas station in a json and send this to server
	 * @param ticket
	 * @param gallons
	 * @param provider
	 * @param gasKind
	 * @param cost
	 */
	private void sendInformation(String ticket, String gallons, String provider, String gasKind, String cost){
		JSONObject information = new JSONObject();
		try {
			information.put("ticket", ticket);
			information.put("galones", gallons);
			information.put("proveedor", provider);
			information.put("tipo_combustible", gasKind);
			information.put("valor", cost);
			new SaveInformation(this).execute("http://pruebasgeoaseo.tk/controller/Fachada.php",
					"test", "combustible", new JSONArray().put(information).toString());
			this.clearFields();
		} catch (JSONException e) {
		}
	}
	
	/**
	 * Method for clear all text fields
	 */
	private void clearFields(){
		this.txtTicket.setText("");
		this.txtGallons.setText("");
		this.txtProvider.setText("");
		this.txtCost.setText("");
	}
	
	/**
	 * Method for load all necessary elements in the view
	 */
	private void identifyElements(){
		this.txtTicket = (EditText) findViewById(R.id.txtTicket);
		this.txtGallons = (EditText) findViewById(R.id.txtGallons);
		this.txtProvider = (EditText) findViewById(R.id.txtProvider);
		this.txtCost = (EditText) findViewById(R.id.txtCost);
		this.radioGroup = (RadioGroup) findViewById(R.id.radioGroupGasKind);
	}
	
	/**
	 * Override method to hide the keyboard when the user touch outside of an element
	 */
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
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
		adb.show();
	}
}
