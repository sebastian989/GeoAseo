package com.quasol.geoaseo;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
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
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(getResources().getString(R.string.logout_confirm));
        adb.setPositiveButton(getResources().getString(R.string.logout_confirm_button_1),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                            int which) {
                    	Editor editor = sharedpreferences.edit();
                		editor.clear();
                		editor.commit();
                		finish();
                        
                    }
                });
        adb.setNegativeButton(getResources().getString(R.string.logout_confirm_button_2),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                            int which) {
                        // TODO Auto-generated method stub
                         dialog.dismiss();
                    }
                });
        adb.show();
	}
	
	public void setOperators(View v){
		Intent intent = new Intent(this, C_GrupoTrabajo.class);
		startActivity(intent);
	}
	
	@Override
	 public boolean onKeyDown(int keyCode, KeyEvent event)  {
		 moveTaskToBack(true);
		 return super.onKeyDown(keyCode, event);
	 }
}
