package com.example.apptestecoderockr;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;

public class ProdutoActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		
		//Re-orienta a posição da activity
		if ((getResources().getConfiguration().screenLayout &      Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {     
			setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} 
		else if ((getResources().getConfiguration().screenLayout &      Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {     
			setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		
		

		if (savedInstanceState == null) {
			// During initial setup, plug in the details fragment.
			ProdutoDetails details = new ProdutoDetails();
			details.setArguments(getIntent().getExtras());
			getFragmentManager().beginTransaction().add(android.R.id.content, details).commit();
		}
	}

}
