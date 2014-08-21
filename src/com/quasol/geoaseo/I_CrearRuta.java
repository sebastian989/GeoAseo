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
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;

public class I_CrearRuta extends Activity implements OnItemClickListener, TextWatcher{
	
	private EditText search;
	private ListView lstRoutes;
	private RadioGroup radioGroup;
	private ArrayAdapter<String> adapter;
	private SharedPreferences sharedpreferences;
	private JSONArray allAlternativeRoutes;
	private ArrayList<String> lstRouteNames;
	private int selectedPosition;
	private boolean searching;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.i__crear_ruta);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		this.sharedpreferences = getSharedPreferences("MyPreferences",Context.MODE_PRIVATE);
		this.identifyElements();
		this.displayAllRoutes();
		this.lstRoutes.setOnItemClickListener(this);
		this.search.addTextChangedListener(this);
		this.searching = false;
		this.selectedPosition=-1;
	}
	
	/**
	 * Method for display all alternative routes returned by the server
	 */
	private void displayAllRoutes(){
		String strAllRoutes = this.sharedpreferences.getString("ALTERNATE_ROUTES", null);
		try {
			this.allAlternativeRoutes = new JSONArray(strAllRoutes);
			this.lstRouteNames = new ArrayList<String>();
			for(int i=0; i<this.allAlternativeRoutes.length(); i++){
				this.lstRouteNames.add(this.allAlternativeRoutes.getJSONObject(i).getString("nombre"));
			}
			this.adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, this.lstRouteNames);
			this.lstRoutes.setAdapter(this.adapter);
		} catch (JSONException e) {
		}
	}
	
	/**
	 * This method ask user if really wants to save a new route, if the answer is "yes" then call the next 
	 * method (createRoute).
	 * @param v
	 */
	public void save(View v){
		if(this.selectedPosition>=0){
			AlertDialog.Builder adb = new AlertDialog.Builder(this);
			adb.setTitle(getResources().getString(R.string.confirmSaveSelectedRoute));
			adb.setPositiveButton(
					getResources().getString(R.string.confirm_button_1),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							createRoute();
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
		else{
			Utilities.showAlert(this, getResources().getString(R.string.alertNotSelectedNewRoute));
		}
	}
	
	/**
	 * Method for add information to planned routes json and send this to server
	 */
	private void createRoute(){
		if(searching){
			int i = 0;
			while(i<this.lstRouteNames.size() && 
					!this.adapter.getItem(this.selectedPosition).equals(this.lstRouteNames.get(i))){
				i++;
			}
			if(i<this.lstRouteNames.size()){
				this.searching = false;
				this.selectedPosition = i;
				this.search.setText("");
			}
		}
		this.lstRouteNames.remove(selectedPosition);
		this.adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, this.lstRouteNames);
		this.lstRoutes.setAdapter(adapter);
		this.adapter.notifyDataSetChanged();
		String type = (String)((Button)findViewById(this.radioGroup.getCheckedRadioButtonId())).getText().toString();
		try {
			JSONArray plannedRoutes = new JSONArray(this.sharedpreferences.getString("PLANNED_ROUTES", null));
			JSONObject selectedRoute = this.allAlternativeRoutes.getJSONObject(this.selectedPosition);
			selectedRoute.put("tipo", type);
			selectedRoute.put("estado", "inactiva");
			selectedRoute.put("compactaciones", 0);
			selectedRoute.put("tickets", new JSONArray());
			plannedRoutes.put(selectedRoute);
			this.allAlternativeRoutes = Utilities.delete(this.allAlternativeRoutes, this.selectedPosition);
			SharedPreferences.Editor editor = sharedpreferences.edit();
			editor.putString("ALTERNATE_ROUTES", this.allAlternativeRoutes.toString());
			editor.putString("PLANNED_ROUTES", plannedRoutes.toString());
			editor.commit();
			this.selectedPosition = -1;
			new SaveInformation(this).execute("http://pruebasgeoaseo.tk/controller/Fachada.php",
					"test", "nueva ruta", new JSONArray().put(selectedRoute).toString());
			Utilities.showAlert(this, getResources().getString(R.string.alertSuccessCreateRoute));
		} catch (JSONException e) {
		}
	}
	
	/**
	 * Method for load all necessary elements in the view
	 */
	private void identifyElements(){
		this.search = (EditText) findViewById(R.id.txtSearch);
		this.lstRoutes = (ListView) findViewById(R.id.lstAllRoutes);
		this.radioGroup = (RadioGroup) findViewById(R.id.radioGroupTypeRoute);
	}

	/**
	 * method to identify what route was selected
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		this.selectedPosition = position;
		this.searching = false;
	}

	/**
	 * Method to filter the routes list when the user type something in the text field
	 * and the user can find the route easiest
	 */
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if(s.length()>0){
			this.searching = true;
		}
		else{
			this.searching = false;
		}
		this.adapter.getFilter().filter(s.toString());
		if(searching){
			this.selectedPosition = -1;
		}	
	}
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		this.selectedPosition = -1;
	}

	@Override
	public void afterTextChanged(Editable s) {}
	
	public void desactivePosition(View v){
		this.selectedPosition = -1;
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
