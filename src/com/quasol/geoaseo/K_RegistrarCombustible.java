package com.quasol.geoaseo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.quasol.recursos.SaveInformation;
import com.quasol.recursos.Utilities;

import android.app.Activity;
import android.content.Context;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.k__registrar_combustible);
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		this.identifyElements();
	}
	
	@Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }
	
	public void save(View v){
		String ticket = this.txtTicket.getText().toString();
		String gallons = this.txtGallons.getText().toString();
		String provider = this.txtProvider.getText().toString();
		String cost = this.txtCost.getText().toString();
		String gasKind = (String)((Button)findViewById(this.radioGroup.getCheckedRadioButtonId())).getText().toString();
		
		if(ticket.equals("") || gallons.equals("") || cost.equals("")){
			Utilities.showAlert(this, getResources().getString(R.string.alertEditTextEmpty));
		}
		else{
			JSONObject information = new JSONObject();
			try {
				information.put("ticket", ticket);
				information.put("galones", gallons);
				information.put("proveedor", provider);
				information.put("tipo", gasKind);
				information.put("valor", cost);
				new SaveInformation(this).execute("http://pruebasgeoaseo.tk/controller/Fachada.php",
						"test", "combustible", new JSONArray().put(information).toString());
			} catch (JSONException e) {
			}
		}
	}
	
	private void identifyElements(){
		this.txtTicket = (EditText) findViewById(R.id.txtTicket);
		this.txtGallons = (EditText) findViewById(R.id.txtGallons);
		this.txtProvider = (EditText) findViewById(R.id.txtProvider);
		this.txtCost = (EditText) findViewById(R.id.txtCost);
		this.radioGroup = (RadioGroup) findViewById(R.id.radioGroupGasKind);
	}
}
