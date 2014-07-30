package com.quasol.adaptadores;

import org.json.JSONArray;
import org.json.JSONException;

import com.quasol.geoaseo.C_GrupoTrabajo;
import com.quasol.geoaseo.R;
import com.quasol.recursos.Utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class C_ItemSelectedOperator extends BaseAdapter {
	
	private final Activity activity;
	private JSONArray jsonSelected;
	
	public C_ItemSelectedOperator(Activity activity, JSONArray jsonSelected) {
		super();
		
		this.activity = activity;
		this.jsonSelected = jsonSelected;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.jsonSelected.length();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final int pos = position;
		LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.c__item_selected_operator, null,true);
        
        TextView lblName = (TextView) view.findViewById(R.id.lblOperatorName);
        Button btnDelete = (Button) view.findViewById(R.id.btnDelete);
        
        try {
			lblName.setText(this.jsonSelected.getJSONObject(position).getString("nombre"));
			btnDelete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					try {
						if(jsonSelected.getJSONObject(pos).getString("hora_fin").equals("")){
							AlertDialog.Builder adb = new AlertDialog.Builder(activity);
							adb.setTitle(activity.getResources().getString(R.string.confirmEndJourney));
							adb.setPositiveButton(
									activity.getResources().getString(R.string.confirm_button_1),
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											
											try {
												((C_GrupoTrabajo)activity).changeOperator(jsonSelected.getJSONObject(pos),pos);
												jsonSelected = Utilities.delete(jsonSelected, pos);
											} catch (JSONException e) {
											}
											
										}
									});
							adb.setNegativeButton(
									activity.getResources().getString(R.string.confirm_button_2),
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											dialog.dismiss();
										}
									});
							adb.show();
						}
						
					} catch (JSONException e) {
						try {
							((C_GrupoTrabajo)activity).changeOperator(jsonSelected.getJSONObject(pos),pos);
							jsonSelected = Utilities.delete(jsonSelected, pos);
						} catch (JSONException e1) {
						}
					}
					
				}
			});
		} catch (JSONException e) {
		}
        
        return view;
	}

	
}
