package com.cipolat.twitter_testapp.lib;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.content.Context;
import android.util.Log;

import com.cipolat.twitter_testapp.Constants_Settings;

public class TweetHelper {
	
	//Clase para acceder a twitter de la lib T4J.
	private Twitter twitter;
	
	private RequestToken mRequestToken = null;

	Constants_Settings constants = new Constants_Settings();

	// twitter
	public String OAUTH_CONSUMER_KEY = constants.OAUTH_CONSUMER_KEY;
	public String OAUTH_CONSUMER_SECRET = constants.OAUTH_CONSUMER_SECRET;
	public String CALLBACKURL = constants.TWITTER_CALLBACK;

	// Registros a guardar en Shared preferences
	public String TW_ACCTOKEN = constants.TW_ACCTOKEN;
	public String TW_ACCTOKEN_SECRET = constants.TW_ACCTOKEN_SECRET;
	
	//instancia a clase para manejar el acceso al Shared preferences
	Shar_Pref_Helper shrpref;

	//Constructor recibe como parametro el contexto 
	public TweetHelper(Context context) {
		// creo referencia para manejar shared prefrences
		shrpref = new Shar_Pref_Helper(constants.SHARED_PREF_NAME, context);

		// Empezamos a crear objeto para interactuar con twitter
		twitter = new TwitterFactory().getInstance();
		mRequestToken = null;
		
		//Seteamos claves para la autenticacion usando OAuth
		twitter.setOAuthConsumer(OAUTH_CONSUMER_KEY, OAUTH_CONSUMER_SECRET);

		String callbackURL = CALLBACKURL;

		try {
			//Tomamos request token en base a callback URL
			//es utilizado para en el web view para obtener las claves
			mRequestToken = twitter.getOAuthRequestToken(callbackURL);
		} catch (TwitterException e) {
			e.printStackTrace();
		}

	}

	// Grabamos nuevo OAuth en el shared preferences
	public void Store_OAuth_verifier(String OAuth) {

		AccessToken at = null;

		// Pair up our request with the response
		try {
			at = twitter.getOAuthAccessToken(mRequestToken, OAuth);

			shrpref.Write_String(TW_ACCTOKEN, at.getToken());
			shrpref.Write_String(TW_ACCTOKEN_SECRET, at.getTokenSecret());

		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// Devolvemos autentication URL
	public String get_AuthenticationURL() {
		return mRequestToken.getAuthenticationURL();
	}



	// Borramos del shared preferences valores TW_ACCTOKEN y TW_ACCTOKEN_SECRET
	public void Logoff() {
		shrpref.Remove_Value(TW_ACCTOKEN);
		shrpref.Remove_Value(TW_ACCTOKEN_SECRET);
	}

	/*Verifica si en el shared preferences estan guardados 
	TW_ACCTOKEN
	TW_ACCTOKEN_SECRET*/
	public boolean verify_logindata(){
		
		
		if (shrpref.isExist(TW_ACCTOKEN))
			{
			Log.e("shrpref.isExist(TW_ACCTOKEN)->","true");
			if (shrpref.isExist(TW_ACCTOKEN_SECRET)){				
				
				//ok estan estos datos guardados
				Log.e("shrpref.isExist(TW_ACCTOKEN_SECRET)->","true");
				return true;
			}else{
				Log.e("shrpref.isExist(TW_ACCTOKEN_SECRET)->","false");

				return false;
			}
			}else{
				Log.e("shrpref.isExist(TW_ACCTOKEN)->","false");

				//tiene que estar si o si los dos 
				return false;
			}
		
	}
	
	// Enviamos Tweet
	public boolean Send_Tweet(String tweet_text) {

		Log.e("Tweet send_tweet", "started");
		
		//Cargamos keys del shared preferences 
		String accessToken = shrpref.Get_stringfrom_shprf(TW_ACCTOKEN);
		String accessTokenSecret = shrpref.Get_stringfrom_shprf(TW_ACCTOKEN_SECRET);
		
		Log.e("accessToken= "+accessToken,"accessTokenSecret "+accessTokenSecret);
		
		//Validamos clave cargadas del shared preferences
		if ((accessToken != null) && (accessTokenSecret != null)) {
			/* Luego creamos el objeto configuracion de T4j
			 * pasamos como parametros las claves
			 * consumer key and consumer Secret 
			 * y los accessToken y accessTokensecret  para la autenticacion OAuth
			 */
			Configuration conf = new ConfigurationBuilder()
					.setOAuthConsumerKey(OAUTH_CONSUMER_KEY)
					.setOAuthConsumerSecret(OAUTH_CONSUMER_SECRET)
					//datos obtenidos del shared pref
					.setOAuthAccessToken(accessToken)
					.setOAuthAccessTokenSecret(accessTokenSecret).build();
			
            //usamos lo seteado anteriormente para obtener una instancia para autenticacion OAuth.
			//creamos objeto para acceder a twitter.
			Twitter t = new TwitterFactory(conf).getInstance();

			try {
                //Actualizamos estado, envamos el twwet.
				t.updateStatus(tweet_text);

			} catch (TwitterException e) {//error

				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("Error Tweet NO Enviado!!", "FAIL");

				return false;

			}

			Log.e("Tweet Enviado!!", "ok");
			return true;

		} else {
			Log.e("Error Tweet NO Enviado!!", "FAIL");
			return false;
		}

	}

}

