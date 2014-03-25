/**
 * Classe principal do projeto
 * 
 * @author  Pedro Vinícius Borges Basseto
 * @version 1.00, 17/03/14
 * 
 */
package com.example.apptestecoderockr;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	
		if ((getResources().getConfiguration().screenLayout &      Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {     
			setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} 
		else if ((getResources().getConfiguration().screenLayout &      Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {     
			setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		
		
		// Chama a classe principal para atualizar o database
		RetrieveDataAsync syncWeb = new RetrieveDataAsync(this);
		syncWeb.execute();
		
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		super.onDestroy();
	}
	
	
	

}
