package com.quasol.recursos;

import org.json.JSONArray;
import org.json.JSONException;

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

}
