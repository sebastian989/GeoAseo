package com.quasol.geoaseo;

import org.json.JSONArray;
import org.json.JSONException;

import com.quasol.recursos.Utilities;
import com.quasol.recursos.WebService;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

public class A_LogIn extends Activity {
	
	private ProgressDialog progress;
	private WebService conection;
	private SharedPreferences sharedpreferences;
	private String user;
	private TextView txtUser;
	private TextView txtPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a__log_in);
		
		this.progress = new ProgressDialog(this); 
		this.conection = new WebService();
		this.sharedpreferences = getSharedPreferences("MyPreferences",Context.MODE_PRIVATE);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	}
	
	/**
	 * Method to verify if the user have already a session
	 */
	@Override
	protected void onResume() {
		super.onResume();
		
		if(this.sharedpreferences.getString("USER_ID", null) != null && !this.sharedpreferences.getString("USER_ID", null).equals(""))
		{
			Intent intent = new Intent(getApplicationContext(), B_MenuPrincipal.class);
			startActivity(intent);
		}
	}

	/**
	 * This method verify if there are empty fields, else send the user information (user, password)
	 * to start a new session.
	 * @param v
	 */
	public void logIn(View v){
		this.txtUser = (TextView) findViewById(R.id.txtUser);
		this.txtPassword = (TextView) findViewById(R.id.txtPassword);
		
		this.user = this.txtUser.getText().toString();
		String password = txtPassword.getText().toString();
		
		if(this.user.equals("") || password.equals("")){
			Utilities.showAlert(this, getResources().getString(R.string.alertEditTextEmpty));
		}
		else{
			if(hayInternet()){
				new Login().execute(this.user,password);
			}
			else{
				Utilities.showAlert(this, getResources().getString(R.string.toastInternetFail));
			}
		}
	}
	
	/**
	 * Method to verify if the device have an internet connection 
	 * @return
	 */
	private boolean hayInternet() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm.getActiveNetworkInfo() != null
				&& cm.getActiveNetworkInfo().isAvailable()
				&& cm.getActiveNetworkInfo().isConnected()) {
			return true;
		} else {
			return false;
		}
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
	
	public class Login extends AsyncTask<String, Void, Boolean> {
		
		private JSONArray answer;
		
		@Override
		protected void onPreExecute() {
			progress.setTitle(getResources().getString(R.string.titleProgress));
			progress.setMessage(getResources().getString(R.string.messageProgress));
			progress.setCancelable(true);
			progress.show();
			super.onPreExecute();
		}
		
		@Override
		protected Boolean doInBackground(String... params) {
			String [] parameters = {"login",params[0],params[1]};
			conection.setUrl("http://pruebasgeoaseo.tk/controller/Fachada.php");
			this.answer = conection.conectar(parameters);
			if(this.saveInformation()){
				return true;
			}
			return false;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			progress.dismiss();
			if(!result){
				Utilities.showAlert((Activity)getApplicationContext(), getResources().getString(R.string.toastErrorLogin));
			}
			else{
				txtUser.setText("");
				txtPassword.setText("");
				Intent intent = new Intent(getApplicationContext(), B_MenuPrincipal.class);
				startActivity(intent);
			}
		}
		
		/**
		 * This method gets and saves the information that return the server about the necessary data for start the
		 * application
		 * @return
		 */
		private Boolean saveInformation(){
			try {
			String token = this.answer.getJSONObject(0).getString("token");
			JSONArray operators = this.answer.getJSONObject(0).getJSONArray("operarios");
			JSONArray plannedRoutes = this.answer.getJSONObject(0).getJSONArray("rutas_planeadas");
			JSONArray alternateRoutes = this.answer.getJSONObject(0).getJSONArray("rutas_alternas");
			JSONArray truckInformation = this.answer.getJSONObject(0).getJSONArray("informacion_vehiculo");

			for(int i=0; i<plannedRoutes.length(); i++){
					if (plannedRoutes.getJSONObject(i).getString("estado").equals("")) {
						plannedRoutes.getJSONObject(i).put("estado", "inactiva");
						plannedRoutes.getJSONObject(i).put("compactaciones", 0);
						plannedRoutes.getJSONObject(i).put("tipo", "planeada");
						plannedRoutes.getJSONObject(i).put("tickets", new JSONArray());
						plannedRoutes.getJSONObject(i).put("ticket_pendiente", false);
					}
			}

			SharedPreferences.Editor editor = sharedpreferences.edit();
			editor.putString("TOKEN", token);
			editor.putString("USER_ID", user);
			editor.putString("OPERATORS", operators.toString());
			editor.putString("PLANNED_ROUTES", plannedRoutes.toString());
			editor.putString("ALTERNATE_ROUTES", alternateRoutes.toString());
			editor.putString("TRUCK_INFO", truckInformation.toString());
			editor.putBoolean("INOPERABILITY", false); 
			editor.commit();
			return true;
			} catch (JSONException e) {
				return false;
			}
		}
	}	
}
