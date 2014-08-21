package com.quasol.geoaseo;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.drawable;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.quasol.adaptadores.C_ItemSelectedOperator;
import com.quasol.recursos.SaveInformation;
import com.quasol.recursos.Utilities;

public class C_GrupoTrabajo extends Activity implements TextWatcher, OnItemClickListener{
	
	private ListView lstAllOperators;
	private ListView lstSelectedOperators;
	private EditText txtSearch;
	private ImageButton btnFinishAll,btnInit;
	private SharedPreferences sharedpreferences;
	private JSONArray savedOperators;
	private JSONArray jsonAllOperators;
	private JSONArray displayedListOperators;
	private C_ItemSelectedOperator adapter;
	private Boolean textChange;
	private Drawable dBtnFinishAll,dBtnFinishAllTwo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.c__grupo_trabajo);
		this.textChange = false;
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		this.identifyElements();
		this.txtSearch.addTextChangedListener(this);
		this.lstAllOperators.setOnItemClickListener(this);
		this.adapter = new C_ItemSelectedOperator(this, new JSONArray());
		this.lstSelectedOperators.setAdapter(adapter);
		this.sharedpreferences = getSharedPreferences("MyPreferences",Context.MODE_PRIVATE);
		String selectedOperators = sharedpreferences.getString("SELECTED_OPERATORS", null);
		if(selectedOperators != null){
			try {
				this.btnFinishAll.setEnabled(true);
				this.btnFinishAll.setImageDrawable(this.dBtnFinishAllTwo);
				this.savedOperators = new JSONArray(selectedOperators);
				for(int i=0; i<savedOperators.length(); i++){
					if(savedOperators.getJSONObject(i).getString("hora_fin").equals("")){
						this.adapter.addOperator(savedOperators.getJSONObject(i));
					}
				}
				this.blockElements();
				this.adapter.notifyDataSetChanged();
			} catch (JSONException e) {}
		}
		else{
			String strgOperators = sharedpreferences.getString("OPERATORS", null);
			if(strgOperators != null){
				try {
					this.jsonAllOperators = new JSONArray(strgOperators);
					this.displayedListOperators = new JSONArray(this.jsonAllOperators.toString());
					this.diaplayListAllOperators();
				} catch (JSONException e) {
				}
			}
		}
	}
	
	private void diaplayListAllOperators(){
		List<String> lstNames = new ArrayList<String>();
		for(int i=0; i<this.displayedListOperators.length(); i++){
			try {
				lstNames.add(this.displayedListOperators.getJSONObject(i).getString("nombre"));
			} catch (JSONException e) {
			}
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lstNames);
		this.lstAllOperators.setAdapter(adapter);
	}
	
	/**
	 * Method to change of list the operators or finish the journey of someone
	 * @param operator
	 */
	public void changeOperator(JSONObject operator){
		if(this.savedOperators != null){
			try {
				int i=0;
				while(i<this.savedOperators.length() && 
						!this.savedOperators.getJSONObject(i).getString("cedula").equals(operator.getString("cedula")))
				{
					i++;
				}
				if(i<this.savedOperators.length()){
					int posCurrentRout = this.sharedpreferences.getInt("POS_CURRENT_ROUTE", -1);
					JSONArray plannedRoutes = new JSONArray(this.sharedpreferences.getString("PLANNED_ROUTES", null));
					this.savedOperators = Utilities.delete(this.savedOperators, i);
					operator.put("hora_fin", Utilities.getDate());
					JSONArray data = new JSONArray();
					data.put(plannedRoutes.getJSONObject(posCurrentRout));
					data.put(operator);
					new SaveInformation(this).execute("http://pruebasgeoaseo.tk/controller/Fachada.php",
							"test",
							"fin_jornada",
							data.toString());
				}
				SharedPreferences.Editor editor = sharedpreferences.edit();
				editor.putString("SELECTED_OPERATORS", savedOperators.toString());
				editor.commit();
			} catch (JSONException e) {
			}
		}
		else{
			this.jsonAllOperators.put(operator);
			if(!this.textChange){
				this.displayedListOperators.put(operator);
			}
			this.diaplayListAllOperators();
		}
		this.adapter.notifyDataSetChanged();
	}
	
	/**
	 * Method to ask if really want to finish the journey of all operators in the left list.
	 * if the answer is positive call the next method (removeAllOperators)
	 */
	public void finishAllOperators(View v){
		if (this.savedOperators.length() > 0) {
			final int posCurrentRout = sharedpreferences.getInt("POS_CURRENT_ROUTE", -1);
			if (posCurrentRout >= 0) {
				AlertDialog.Builder adb = new AlertDialog.Builder(this);
				adb.setTitle(getResources().getString(R.string.confirmFinishAllOperators));
				adb.setPositiveButton(
						getResources().getString(R.string.confirm_button_1),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								removeAllOperators(posCurrentRout);
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
				Utilities.showAlert(this,getResources().getString(R.string.alertFinishJourneyWithoutRouteInit));
			}
		}	
		else{
			Utilities.showAlert(this,getResources().getString(R.string.alertFinishJourneyAllWithoutOperators));
		}
	}
	
	/**
	 * Method to finish the journey of all operators
	 * @param posCurrentRout
	 */
	private void removeAllOperators(int posCurrentRout){
		JSONArray data = new JSONArray();
		JSONArray plannedRoutes;
		try {
			plannedRoutes = new JSONArray(this.sharedpreferences.getString("PLANNED_ROUTES", null));
			data.put(plannedRoutes.getJSONObject(posCurrentRout));
			while(this.savedOperators.length()>0){
				JSONObject operator = this.savedOperators.getJSONObject(0);
				operator.put("hora_fin", Utilities.getDate());
				data.put(operator);
				this.savedOperators = Utilities.delete(this.savedOperators, 0);
			}
			new SaveInformation(this).execute("http://pruebasgeoaseo.tk/controller/Fachada.php",
					"test",
					"fin_jornada",
					data.toString());
			SharedPreferences.Editor editor = sharedpreferences.edit();
			editor.putString("SELECTED_OPERATORS", this.savedOperators.toString());
			editor.commit();
		} catch (JSONException e) {
		}	
		this.adapter.removeAll();
		this.adapter.notifyDataSetChanged();
	}
	
	/**
	 * Method to change operators from allOperators list To selectedOperators list
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		try {
			this.adapter.addOperator(this.displayedListOperators.getJSONObject(position));
			int i=0;
			while(i<this.jsonAllOperators.length() && 
					this.jsonAllOperators.getJSONObject(i).getInt("cedula") != 
					this.displayedListOperators.getJSONObject(position).getInt("cedula")){
				i++;
			}
			if(i<this.jsonAllOperators.length()){
				this.jsonAllOperators = Utilities.delete(this.jsonAllOperators, i);
			}
			this.displayedListOperators = Utilities.delete(this.displayedListOperators, position);
			this.diaplayListAllOperators();
		} catch (JSONException e) {
		}
		this.adapter.notifyDataSetChanged();
	}
	
	/**
	 * Method for ask about if really want to save the selected operators, if the answer
	 * is positive call the next method to save the selected operators
	 * @param v
	 */
	public void start(View v){
		if(this.adapter.getCurrentSelected().length()>0){
			AlertDialog.Builder adb = new AlertDialog.Builder(this);
			adb.setTitle(getResources().getString(R.string.confirmSelectedOperators));
			adb.setPositiveButton(
					getResources().getString(R.string.confirm_button_1),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							saveSelectedOperators();
							blockElements();
							btnFinishAll.setEnabled(true);
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
		else{
			Utilities.showAlert(this, getResources().getString(R.string.alertVoidListOperators));
		}
		
	}
	
	/**
	 * Read the description of the previous method
	 */
	private void saveSelectedOperators(){
		String hour = Utilities.getDate();
		for(int i=0; i<this.adapter.getCurrentSelected().length(); i++){
			try {
				this.adapter.getCurrentSelected().getJSONObject(i).put("hora_inicio", hour);
				this.adapter.getCurrentSelected().getJSONObject(i).put("hora_fin", "");
			} catch (JSONException e) {
			}
		}
		
		SharedPreferences.Editor editor = sharedpreferences.edit();
		editor.putString("SELECTED_OPERATORS", this.adapter.getCurrentSelected().toString());
		editor.commit();
	}
	
	/**
	 * Method to filter the list when the user type something in the text field
	 */
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if(s.length()>0){
			this.textChange = true;
		}
		else{
			this.textChange = false;
		}
		this.displayedListOperators = new JSONArray();
		for(int i=0; i<this.jsonAllOperators.length(); i++){
			try {
				if(this.jsonAllOperators.getJSONObject(i).getString("nombre").toUpperCase().contains((s.toString().toUpperCase()))){
					this.displayedListOperators.put(this.jsonAllOperators.getJSONObject(i));
				}
			} catch (JSONException e) {
			}
		}
		this.diaplayListAllOperators();
	}
	
	/**
	 * Method for load all necessary elements in the view
	 */
	private  void identifyElements(){
		this.lstAllOperators = (ListView) findViewById(R.id.lstAllOperators);
		this.lstSelectedOperators = (ListView) findViewById(R.id.lstSelectedOperators);
		this.txtSearch = (EditText) findViewById(R.id.txtSearch);
		this.btnInit = (ImageButton) findViewById(R.id.btnInit);
		this.btnFinishAll = (ImageButton) findViewById(R.id.btnFinishAll);
		this.dBtnFinishAll = this.getResources().getDrawable(R.drawable.btn_finish_all);
		this.dBtnFinishAllTwo = this.getResources().getDrawable(R.drawable.btn_finish_all_two);
	}
	
	private void blockElements(){
		this.btnInit.setBackgroundResource(R.drawable.c_btn_save);
		this.btnInit.setEnabled(false);
		this.lstAllOperators.setEnabled(false);
		this.txtSearch.setEnabled(false);
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

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	@Override
	public void afterTextChanged(Editable s) {}
	
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
