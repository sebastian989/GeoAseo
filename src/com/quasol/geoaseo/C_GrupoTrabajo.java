package com.quasol.geoaseo;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.quasol.adaptadores.C_ItemSelectedOperator;
import com.quasol.recursos.Utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import android.widget.Toast;

public class C_GrupoTrabajo extends Activity implements TextWatcher, OnItemClickListener{
	
	private ListView lstAllOperators;
	private ListView lstSelectedOperators;
	private EditText txtSearch;
	private Button btnInit;
	private SharedPreferences sharedpreferences;
	private JSONArray savedOperators;
	private JSONArray jsonAllOperators;
	private JSONArray displayedListOperators;
	private C_ItemSelectedOperator adapter;
	private Boolean textChange;

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
	
	private  void identifyElements(){
		this.lstAllOperators = (ListView) findViewById(R.id.lstAllOperators);
		this.lstSelectedOperators = (ListView) findViewById(R.id.lstSelectedOperators);
		this.txtSearch = (EditText) findViewById(R.id.txtSearch);
		this.btnInit = (Button) findViewById(R.id.btnInit);
	}
	
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
					savedOperators.getJSONObject(i).put("hora_fin", Utilities.getDate());
					Toast.makeText(this, savedOperators.getJSONObject(i).toString(), Toast.LENGTH_LONG).show();
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
	
	private void blockElements(){
		this.btnInit.setBackgroundResource(R.drawable.c_btn_save);
		this.btnInit.setEnabled(false);
		this.lstAllOperators.setEnabled(false);
		this.txtSearch.setEnabled(false);
	}
	
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
				if(this.jsonAllOperators.getJSONObject(i).getString("nombre").toUpperCase().startsWith((s.toString().toUpperCase()))){
					this.displayedListOperators.put(this.jsonAllOperators.getJSONObject(i));
				}
			} catch (JSONException e) {
			}
		}
		this.diaplayListAllOperators();
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	@Override
	public void afterTextChanged(Editable s) {}
	
}
