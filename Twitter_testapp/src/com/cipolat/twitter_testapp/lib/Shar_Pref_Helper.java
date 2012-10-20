package com.cipolat.twitter_testapp.lib;
/*Creado por SEBASTIAN CIPOLAT
 * 2012 Buenos Aires Argentina
 */
import android.content.Context;
import android.content.SharedPreferences;

import com.cipolat.twitter_testapp.Constants_Settings;

public class Shar_Pref_Helper {

	SharedPreferences settings;
	Constants_Settings constants;

	public Shar_Pref_Helper(String Shared_pref_name, Context context) {
		// creamos referencia al shared preferences
		settings = context.getSharedPreferences(Shared_pref_name,
				Context.MODE_PRIVATE);

	}

	// Retorna T o F si existe el valor
	public boolean isExist(String val) {
		//si no existe retorna null
		String value = settings.getString(val, null);

		if (value == null) {

			return false;
		} else {
			return true;
		}
	}

	// Retorna un String con el conetenido del parametro val sacado del Shared
	// Preferences
	public String Get_stringfrom_shprf(String val) {

		String valor = settings.getString(val, null);
		return valor;

	}

	//Escribimos un valor string
	public void Write_String(String clave, String valor) {
		SharedPreferences.Editor editor = settings.edit();

		editor.putString(clave, valor);
		editor.commit();

	}

	//borramos un valor
	public void Remove_Value(String clave) {
		SharedPreferences.Editor editor = settings.edit();
		editor.remove(clave);
		editor.commit();
	}

}
