package com.quasol.geoaseo;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.quasol.adaptadores.C_ItemSelectedOperator;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class C_GrupoTrabajo extends Activity implements TextWatcher, OnItemClickListener{
	
	private ListView lstAllOperators;
	private ListView lstSelectedOperators;
	private EditText txtSearch;
	private SharedPreferences sharedpreferences;
	private JSONArray jsonSelectedOperators;
	private JSONArray jsonAllOperators;
	private JSONArray displayedListOperators;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.c__grupo_trabajo);
		
		this.jsonSelectedOperators = new JSONArray();
		this.identifyElements();
		this.txtSearch.addTextChangedListener(this);
		this.lstAllOperators.setOnItemClickListener(this);
		this.sharedpreferences = getSharedPreferences("MyPreferences",Context.MODE_PRIVATE);
		String strgOperators = sharedpreferences.getString("OPERATORS", null);
		if(strgOperators != null){
			try {
				this.jsonAllOperators = new JSONArray(strgOperators);
				this.displayedListOperators = new JSONArray(this.jsonAllOperators.toString());
				this.diaplayListAllOperators(this.displayedListOperators);
			} catch (JSONException e) {
			}
		}
	}
	
	private void diaplayListAllOperators(JSONArray jsonOperators){
		List<String> lstNames = new ArrayList<String>();
		for(int i=0; i<jsonOperators.length(); i++){
			try {
				lstNames.add(jsonOperators.getJSONObject(i).getString("nombre"));
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
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		try {
			this.jsonSelectedOperators.put(this.displayedListOperators.getJSONObject(position));
			int i=0;
			while(i<this.jsonAllOperators.length() && 
					this.jsonAllOperators.getJSONObject(i).getInt("cedula") != 
					this.displayedListOperators.getJSONObject(position).getInt("cedula")){
				i++;
			}
			if(i<this.jsonAllOperators.length()){
				this.jsonAllOperators.remove(i);
			}
			this.displayedListOperators.remove(position);
			this.diaplayListAllOperators(this.displayedListOperators);
		} catch (JSONException e) {
		}
		C_ItemSelectedOperator adapter = new C_ItemSelectedOperator(this, this.jsonSelectedOperators);
		this.lstSelectedOperators.setAdapter(adapter);
	}
	
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		this.displayedListOperators = new JSONArray();
		for(int i=0; i<this.jsonAllOperators.length(); i++){
			try {
				if(this.jsonAllOperators.getJSONObject(i).getString("nombre").toUpperCase().startsWith((s.toString().toUpperCase()))){
					this.displayedListOperators.put(this.jsonAllOperators.getJSONObject(i));
				}
			} catch (JSONException e) {
			}
		}
		this.diaplayListAllOperators(this.displayedListOperators);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	@Override
	public void afterTextChanged(Editable s) {}
}
