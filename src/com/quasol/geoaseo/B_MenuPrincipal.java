package com.quasol.geoaseo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;

public class B_MenuPrincipal extends Activity {
	
	private SharedPreferences sharedpreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.b__menu_principal);
		this.sharedpreferences = getSharedPreferences("MyPreferences",Context.MODE_PRIVATE);
	}
	
	public void logOut(View v){
		Editor editor = sharedpreferences.edit();
		editor.clear();
		editor.commit();
		finish();
	}
}
