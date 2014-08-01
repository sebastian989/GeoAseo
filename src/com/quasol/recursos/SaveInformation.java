package com.quasol.recursos;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;

public class SaveInformation extends AsyncTask<String, Void, Void> {
	
	private WebService connection;
	private Activity activity;
	
	public SaveInformation(Activity activity){
		this.connection = new WebService();
		this.activity = activity;
	}

	@Override
	protected void onPreExecute() {
	}

	@Override
	protected Void doInBackground(String... params) {
		
		if(this.thereIsInternet()){
			this.connection.setUrl(params[0]);//url
			String [] parameters = {params[1],params[2],params[3]};//method
			JSONArray answer = this.connection.conectar(parameters);
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		
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