package com.quasol.geoaseo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;

public class G_formulario_Relleno extends Activity {

	private ImageButton in_filler, out_filler;
	private Drawable d_in_filler, d_in_filler_two, d_out_filler,
			d_out_filler_two;
	private SharedPreferences sharedpreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.g__formulario_relleno);
		this.sharedpreferences = getSharedPreferences("MyPreferences",
				Context.MODE_PRIVATE);
		initialize_components();

	}

	public void in_filler(View v) {

		this.in_filler.setEnabled(false);
		this.in_filler.setImageDrawable(this.d_in_filler_two);

		this.out_filler.setEnabled(true);
		this.out_filler.setImageDrawable(this.d_out_filler);
		

	}

	public void out_filler(View v) {

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}

	public void initialize_components() {
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
