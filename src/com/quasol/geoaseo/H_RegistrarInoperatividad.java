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

public class H_RegistrarInoperatividad extends Activity {
	
	private SharedPreferences sharedpreferences;
	private SharedPreferences privatePreferences;
	private RadioGroup radioGroup;
	private EditText txtDetail;
	private Button btnStart;
	private Button btnFinish;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.h__registrar_inoperatividad);
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		this.identifyElements();
		this.sharedpreferences = getSharedPreferences("MyPreferences",Context.MODE_PRIVATE);
		this.privatePreferences = getSharedPreferences("InoperabilityPreferences",Context.MODE_PRIVATE);
		if(this.sharedpreferences.getBoolean("INOPERABILITY", false)){
			btnFinish.setEnabled(true);
			this.blockElements();
			String detail = this.privatePreferences.getString("DETAIL", null);
			int checked = this.privatePreferences.getInt("CHECKED", -1);
			this.radioGroup.check(checked);
			this.txtDetail.setText(detail);
		}
	}
	
	/**
	 * Method called when the user pressed the start button, this method asks the user if really wants start
	 * inoperability, if the answer is "yes" the method sendInformation() is called.
	 * @param v
	 */
	public void start(View v){
		final String detail = this.txtDetail.getText().toString();
		if(detail.equals("")){
			Utilities.showAlert(this, getResources().getString(R.string.alertVoidDetail));
		}
		else{
			final String inoperabilityCase = (String)((Button)findViewById(this.radioGroup.getCheckedRadioButtonId())).getText().toString();
			AlertDialog.Builder adb = new AlertDialog.Builder(this);
			adb.setTitle(getResources().getString(R.string.confirmStartInoperability));
			adb.setPositiveButton(
					getResources().getString(R.string.confirm_button_1),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							btnFinish.setEnabled(true);
							blockElements();
							SharedPreferences.Editor editor = sharedpreferences.edit();
							editor.putBoolean("INOPERABILITY", true);
							editor.commit();
							sendInformation(inoperabilityCase, detail,
									"http://pruebasgeoaseo.tk/controller/Fachada.php", "test", "inoperatividad");
							saveInformation();
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
	 * Method called when the user pressed the finish button, this method asks the user if really wants finish
	 * inoperability, if the answer is "yes" the method sendInformation() is called for send the information to server.
	 * @param v
	 */
	public void finish(View v){
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle(getResources().getString(R.string.confirmFinishInoperability));
		adb.setPositiveButton(
				getResources().getString(R.string.confirm_button_1),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						SharedPreferences.Editor editor = sharedpreferences.edit();
						editor.putBoolean("INOPERABILITY", false);
						editor.commit();
						sendInformation("fin_inoperatividad", "El usuario acaba de terminar la inoperatividad",
								"http://pruebasgeoaseo.tk/controller/Fachada.php", "test", "fin_inoperatividad");
						deleteInformation();
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
	
	/**
	 * This method saves the user, case and detail of the start and the finish of the inoperability 
	 * and sends it to server
	 * @param inoperabilityCase
	 * @param detail
	 * @param url
	 * @param method
	 * @param event
	 */
	private void sendInformation(String inoperabilityCase, String detail, String url, String method, 
			String event)
	{
		String user = this.sharedpreferences.getString("USER_ID", null);
		JSONObject infoInoperability = new JSONObject();
		try {
			infoInoperability.put("usuario_reportante", user);
			infoInoperability.put("motivo", inoperabilityCase);
			infoInoperability.put("observacion", detail);
			
			JSONArray data = new JSONArray();
			data.put(infoInoperability);//Information of user and inoperability
			int posCurrentRout = this.sharedpreferences.getInt("POS_CURRENT_ROUTE", -1);
			if(posCurrentRout>=0){//Information current rout
				JSONArray plannedRoutes = new JSONArray(this.sharedpreferences.getString("PLANNED_ROUTES", null));
				data.put(plannedRoutes.getJSONObject(posCurrentRout));
			}
			else{
				data.put(new String("Sin ruta"));
			}
			new SaveInformation(this).execute(url, method, event, data.toString());
		} catch (JSONException e) {
		}	
	}
	
	/**
	 * Method to recover the activity view state when the inoperability start. 
	 */
	private void saveInformation(){
		SharedPreferences.Editor editor = this.privatePreferences.edit();
		editor.putString("DETAIL", this.txtDetail.getText().toString());
		editor.putInt("CHECKED", this.radioGroup.getCheckedRadioButtonId());
		editor.commit();
	}
	
	/**
	 * Method to remove of preferences previously saved information. 
	 */
	private void deleteInformation(){
		Editor editor = this.privatePreferences.edit();
		editor.clear();
		editor.commit();
	}
	
	/**
	 * Method for load all necessary elements in the view
	 */
	private void identifyElements(){
		this.radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
		this.txtDetail = (EditText) findViewById(R.id.txtDetail);
		this.btnStart = (Button) findViewById(R.id.btnStart);
		this.btnFinish = (Button) findViewById(R.id.btnFinish);
	}
	
	private void blockElements(){
		this.btnStart.setEnabled(false);
		this.txtDetail.setEnabled(false);
		this.radioGroup.setEnabled(false);
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
}