package com.quasol.geoaseo;

import org.json.JSONArray;
import org.json.JSONException;

import com.quasol.recursos.WebService;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class A_LogIn extends Activity {
	
	private ProgressDialog progress;
	private WebService conection;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a__log_in);
		
		this.progress = new ProgressDialog(this); 
		this.conection = new WebService();
	}
	
	public void logIn(View v){
		TextView txtUser = (TextView) findViewById(R.id.txtUser);
		TextView txtPassword = (TextView) findViewById(R.id.txtPassword);
		
		String user = txtUser.getText().toString();
		String password = txtPassword.getText().toString();
		
		if(user.equals("") || password.equals("")){
			Toast.makeText(this, "Hay campos incompletos", Toast.LENGTH_SHORT).show();
		}
		else{
			new Login().execute(user,password);
		}
	}
	
	public class Login extends AsyncTask<String, Void, Boolean> {
		
		private JSONArray answer;
		
		@Override
		protected void onPreExecute() {
			progress.setTitle("Iniciando sesión");
			progress.setMessage("Por favor espere un momento...");
			progress.setCancelable(true);
			progress.show();
			super.onPreExecute();
		}
		
		@Override
		protected Boolean doInBackground(String... params) {
			String [] parameters = {"login",params[0],params[1]};
			conection.setUrl("http://pruebasgeoaseo.tk/controller/Fachada.php");
			this.answer = conection.conectar(parameters);
			
			return false;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			progress.dismiss();
			if(!result){
				Toast.makeText(getApplicationContext(), "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
			}
			else{
				Intent intent = new Intent(getApplicationContext(), B_MenuPrincipal.class);
				startActivity(intent);
			}
		}
	}
	
}
