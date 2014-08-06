package com.quasol.geoaseo;

import org.json.JSONArray;
import org.json.JSONObject;

import com.quasol.recursos.SaveInformation;
import com.quasol.recursos.Utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class L_RegistrarPeaje extends Activity implements OnClickListener {

	private ImageButton tollA, tollB, tollC;
	private Drawable dTollA, dTollATwo, dTollB, dTollBTwo, dTollC, dTollCTwo;
	private Button payToll;
	private String typeToll;
	private JSONArray sendDataJson;
	private EditText ticket;
	private SharedPreferences sharedpreferences;
	private AlertDialog.Builder adb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.l__registrar_peaje);
		inicializeObjects();
		this.sharedpreferences = getSharedPreferences("MyPreferences",
				Context.MODE_PRIVATE);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	}
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnTollA) {
			this.tollA.setImageDrawable(this.dTollATwo);
			this.tollB.setImageDrawable(this.dTollB);
			this.tollC.setImageDrawable(this.dTollC);
			this.typeToll = "A";
		} else if (v.getId() == R.id.btnTollB) {

			this.tollA.setImageDrawable(this.dTollA);
			this.tollB.setImageDrawable(this.dTollBTwo);
			this.tollC.setImageDrawable(this.dTollC);
			this.typeToll = "B";
		} else if (v.getId() == R.id.btnTollC) {

			this.tollA.setImageDrawable(this.dTollA);
			this.tollB.setImageDrawable(this.dTollB);
			this.tollC.setImageDrawable(this.dTollCTwo);
			this.typeToll = "C";
		}

		else if (v.getId() == R.id.btnPayToll) {
			if (this.ticket.getText().toString().equals("")
					|| this.typeToll.equals("")) {
				Utilities.showAlert(this,
						getResources().getString(R.string.alertEmptyToll));
			} else {
				try {
					this.adb.setTitle(getResources().getString(
							R.string.alertPayToll)
							+ this.typeToll);
					this.adb.setPositiveButton(
							getResources().getString(R.string.confirm_button_1),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									sendInformation();
									finish();
									dialog.dismiss();
								}
							});
					this.adb.setNegativeButton(
							getResources().getString(R.string.confirm_button_2),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});
					this.adb.show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * 
	 */
	public void inicializeObjects() {

		this.tollA = (ImageButton) findViewById(R.id.btnTollA);
		this.tollB = (ImageButton) findViewById(R.id.btnTollB);
		this.tollC = (ImageButton) findViewById(R.id.btnTollC);
		this.payToll = (Button) findViewById(R.id.btnPayToll);
		this.dTollA = this.getResources().getDrawable(R.drawable.btn_toll_a);
		this.dTollATwo = this.getResources().getDrawable(
				R.drawable.btn_toll_a_two);
		this.dTollB = this.getResources().getDrawable(R.drawable.btn_toll_b);
		this.dTollBTwo = this.getResources().getDrawable(
				R.drawable.btn_toll_b_two);
		this.dTollC = this.getResources().getDrawable(R.drawable.btn_toll_c);
		this.dTollCTwo = this.getResources().getDrawable(
				R.drawable.btn_toll_c_two);
		this.tollA.setOnClickListener(this);
		this.tollB.setOnClickListener(this);
		this.tollC.setOnClickListener(this);
		this.payToll.setOnClickListener(this);
		this.typeToll = "";
		this.sendDataJson = new JSONArray();
		this.ticket = (EditText) findViewById(R.id.etTicket);
		this.adb = new AlertDialog.Builder(this);

	}

	public void sendInformation() {
		try {
			JSONObject auxJson = new JSONObject();
			auxJson.put("usuario",
					this.sharedpreferences.getString("USER_ID", ""));
			auxJson.put("ticket", this.ticket.getText().toString());
			auxJson.put("tipo_peaje", this.typeToll);
			this.sendDataJson.put(auxJson);
			JSONArray aux2 = new JSONArray(sharedpreferences.getString(
					"TRUCK_INFO", ""));
			this.sendDataJson.put(aux2.getJSONObject(0));
			new SaveInformation(this).execute(
					"http://pruebasgeoaseo.tk/controller/Fachada.php", "test",
					"pago_peaje", this.sendDataJson.toString());
		} catch (Exception e) {
		}
	}
	
	/**
	 * 
	 * @param v
	 */
	public void logOut(View v) {
		
		this.adb.setTitle(getResources().getString(R.string.logout_confirm));
		this.adb.setPositiveButton(
				getResources().getString(R.string.confirm_button_1),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Editor editor = sharedpreferences.edit();
						editor.clear();
						editor.commit();
						Intent intent = new Intent(getApplicationContext(), A_LogIn.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
					}
				});
		this.adb.setNegativeButton(
				getResources().getString(R.string.confirm_button_2),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
		this.adb.show();
	}
	
	@Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }
}
