package com.cipolat.twitter_testapp;

import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cipolat.twitter_testapp.lib.TweetHelper;

public class MainActivity extends Activity {

	private TextView tweetTextView;
	private RequestToken mRequestToken = null;
	private Button tweetbutton, logffbtn;
	private EditText tweet_txt;
	private ImageView imgv;
	private TweetHelper tweet_hlp;
	private int TWITTER_AUTH=1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		tweetbutton = (Button) findViewById(R.id.tweetbtn);
		logffbtn = (Button) findViewById(R.id.logffbtn);

		tweet_txt = (EditText) findViewById(R.id.twtfield);

		imgv = (ImageView) findViewById(R.id.tw_status_img);

		// Creo instancia a clase para interactuar con twitter.
		tweet_hlp = new TweetHelper(this);

		//Verificamos que halla alguna key guardada en el telefono si no 
		//llamamos a un webview para obtenerlas 
	validar_login();
		
		// LOG OFF
		logffbtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//"Deslogueo"
				tweet_hlp.Logoff();

			}
		});

		// ENVIO DE TWEET
		tweetbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					//validamos login de nuevo
					validar_login();
					// obtenemos texto de componente de texto
					String texto = tweet_txt.getText().toString();
					// enviamos tweet y obtenemos estado de envio
					boolean twt_snd_status = tweet_hlp.Send_Tweet(texto);

					if (twt_snd_status)// OK enviado
					{ // cambiamos imagen de estad a twitter_OK
						tweet_imgstatus(1);
						Show_Toast("Tu tweet ha sido enviado");

					} else {
						// cambiamos imagen de estad a twitter_fial
						tweet_imgstatus(0);
						Show_Toast("Hubo un Error no se pudo eviar tu tweet.");

					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	public void validar_login(){
		//Verificar si existen claves alamacenadas en el telefono si no llamar al webview.
				if(!tweet_hlp.verify_logindata())
				{
					Show_Toast("No se han encontrado claves en el telefono las descargaremos.");
					//creamos intent y pasamos get_AuthenticationURL
					Intent i = new Intent(MainActivity.this, TwitterWebActivity.class);
					i.putExtra("URL", tweet_hlp.get_AuthenticationURL());
					startActivityForResult(i, TWITTER_AUTH);
					
				}
	}
	
	public void tweet_imgstatus(int val) {
		if (val == 1)
			imgv.setImageResource(R.drawable.twitter_ok);
		else
			imgv.setImageResource(R.drawable.twitter_fail);
	}

	//muestra un toast
	public void Show_Toast(String txt){
		
		Toast.makeText(this,txt, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//si el codigo obtenido es ok
		if (resultCode == Activity.RESULT_OK) {
			// Obtenemos oauth_verifier pasado por el webview
			String oauthVerifier = (String) data.getExtras().get("oauth_verifier");
			Log.e("oauthVerifier ->", oauthVerifier);

			// Grabamos el valor de oauthVerifier en el shared preferences
			tweet_hlp.Store_OAuth_verifier(oauthVerifier);

		}

	}

}
