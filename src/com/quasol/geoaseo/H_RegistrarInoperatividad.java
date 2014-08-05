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
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

public class H_RegistrarInoperatividad extends Activity {
	
	private SharedPreferences sharedpreferences;
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
		if(this.sharedpreferences.getBoolean("INOPERABILITY", false)){
			btnFinish.setEnabled(true);
			this.blockElements();
		}
	}
	
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
}