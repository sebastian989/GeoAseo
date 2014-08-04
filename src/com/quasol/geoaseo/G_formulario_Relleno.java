package com.quasol.geoaseo;

import com.quasol.recursos.Utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;

public class G_formulario_Relleno extends Activity {

	private ImageButton in_filler, out_filler;
	private Drawable d_in_filler, d_in_filler_two, d_out_filler,d_out_filler_two;
	private SharedPreferences sharedpreferences;
	private AlertDialog.Builder adb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.g__formulario_relleno);
		this.sharedpreferences = getSharedPreferences("MyPreferences",
				Context.MODE_PRIVATE);
		initializeComponents();
		if(sharedpreferences.getBoolean("IN_FILLER",false)){
			buttonsInFiller();
		}

	}

	public void inFiller(View v) {

		this.adb.setTitle("ESTA SEGURO DE QUE ESTA EN EL RELLENO SANITARIO");
		this.adb.setPositiveButton(getResources().getString(R.string.confirm_button_1),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						SharedPreferences.Editor editor = sharedpreferences.edit();
						editor.putString("TIME_START_IN_FILLER",Utilities.getDate());
						editor.putBoolean("IN_FILLER", true);
						editor.commit();
						buttonsInFiller();
					}
				});
		this.adb.setNegativeButton(getResources().getString(R.string.confirm_button_2),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		
		

	}

	public void outFiller(View v) {
		Intent intent = new Intent(this, G_TicketRelleno.class);
		startActivityForResult(intent, 10);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 10) {
			if (resultCode == 2) {
				
			}
		}
	}
	
	public void buttonsInFiller(){
		this.in_filler.setEnabled(false);
		this.in_filler.setImageDrawable(this.d_in_filler_two);
		this.out_filler.setEnabled(true);
		this.out_filler.setImageDrawable(this.d_out_filler);
	}
	
	public void initializeComponents() {
		this.in_filler = (ImageButton) findViewById(R.id.in_filler);
		this.out_filler = (ImageButton) findViewById(R.id.out_filler);
		this.d_in_filler = this.getResources().getDrawable(
				R.drawable.btn_in_filler);
		this.d_in_filler_two = this.getResources().getDrawable(
				R.drawable.btn_in_filler_two);
		this.d_out_filler = this.getResources().getDrawable(
				R.drawable.btn_out_filler);
		this.d_out_filler_two = this.getResources().getDrawable(
				R.drawable.btn_out_filler_two);
		this.out_filler.setEnabled(false);
	}
}
