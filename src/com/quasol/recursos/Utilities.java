package com.quasol.recursos;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.quasol.geoaseo.R;

public class Utilities {
	
	public static JSONArray delete(JSONArray json, int pos){
		JSONArray last = new JSONArray();
		for(int i=0; i<json.length(); i++){
			if(i!=pos){
				try {
					last.put(json.getJSONObject(i));
				} catch (JSONException e) {
				}
			}
		}
		return last;
	}
	
	public static String getDate(){
		Calendar c = Calendar.getInstance(); 
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss a");
		String currentTime = dateFormat.format(c.getTime());
		return currentTime;
	}
	
	public static boolean compareDates(String dateServer , String hourServer){
		
		Calendar c = Calendar.getInstance(); 
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
		String currentTime = dateFormat.format(c.getTime());
		String [] vecInitilize = currentTime.split(" ");
		String [] dateIntern = vecInitilize[0].split("-");
		String [] hourIntern = vecInitilize[1].split(":");
		String [] dateServerVec = dateServer.split("/");
		String [] hourServerVec = hourServer.split(":");
		if(Integer.parseInt(dateIntern[0])==Integer.parseInt(dateServerVec[1])&&
			Integer.parseInt(dateIntern[1])==Integer.parseInt(dateServerVec[0])&& 
			 Integer.parseInt(dateIntern[2])==Integer.parseInt(dateServerVec[2])&&
		      Integer.parseInt(hourIntern[0])==Integer.parseInt(hourServerVec[0])&&
		       Integer.parseInt(hourIntern[1])==Integer.parseInt(hourServerVec[1]))
		{	
			return true;
		}
		else
		{
			return false;
		}
	}
	
	
	public static void showAlert(Activity activity, String message){
		AlertDialog.Builder adb = new AlertDialog.Builder(activity);
		adb.setTitle(message);
		adb.setPositiveButton(
				activity.getResources().getString(R.string.accept_button),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		adb.show();
	}
}
