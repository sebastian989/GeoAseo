package com.quasol.recursos;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;

public class SaveInformation extends AsyncTask<String, Void, Void> {
	
	private WebService connection;
	private Activity activity;
	private SharedPreferences sharedpreferences;
	private BackUpDataSource dataBase;
	
	public SaveInformation(Activity activity){
		this.connection = new WebService();
		this.activity = activity;
		this.sharedpreferences = activity.getSharedPreferences("MyPreferences",Context.MODE_PRIVATE);
		this.dataBase = new BackUpDataSource(activity);
		this.dataBase.open();
	}

	@Override
	protected void onPreExecute() {
	}

	@Override
	protected Void doInBackground(String... params) {
		JSONArray answer;
		String token = this.sharedpreferences.getString("TOKEN", null);
		if(this.thereIsInternet()){
			//Send old information
			JSONArray stored = this.dataBase.getAllRoutes();
			if(stored.length()>0){
				this.connection.setUrl("http://pruebasgeoaseo.tk/controller/Fachada.php");//url
				String [] parameters = {"testOld",stored.toString()};//method,oldJSON
				answer = this.connection.conectar(parameters);
				try {
					if(answer.getJSONObject(0).getInt("respuesta")==1){
						this.dataBase.deleteRoutes();
					}
				} catch (JSONException e) {}
			}
			
			//Send current event
			this.connection.setUrl(params[0]);//url
			String [] parameters = {params[1], token, Utilities.getDate(), params[2], params[3]};//method,token,date,event,json
			answer = this.connection.conectar(parameters);
			try {
				if(answer.getJSONObject(0).getInt("respuesta")==0){
					this.saveInDataBase(token, Utilities.getDate(), params[2], params[3]);
				}
			} catch (JSONException e) {}
		}
		else{
			this.saveInDataBase(token, Utilities.getDate(), params[2], params[3]);
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		
	}
	
	private void saveInDataBase(String token, String date, String event, String json){
		this.dataBase.CreateRoute(token, date, event, json);
	}
	
	private boolean thereIsInternet() {
		ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm.getActiveNetworkInfo() != null
				&& cm.getActiveNetworkInfo().isAvailable()
				&& cm.getActiveNetworkInfo().isConnected()) {
			return true;
		} else {
			return false;
		}
	}
}