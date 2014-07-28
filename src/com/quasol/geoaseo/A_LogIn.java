package com.quasol.geoaseo;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class A_LogIn extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a__log_in);
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
			
		}
	}
	
	public class Login extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
//			pd2.setMessage("La configuracón inicial tardara algunos segundos");
//			pd2.setTitle("Iniciando sesión");
//			pd2.show();
//			super.onPreExecute();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
}
