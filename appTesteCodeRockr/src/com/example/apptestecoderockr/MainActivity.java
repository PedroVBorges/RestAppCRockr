/**
 * Classe principal do projeto
 * 
 * @author  Pedro Vinícius Borges Basseto
 * @version 1.00, 17/03/14
 * 
 */
package com.example.apptestecoderockr;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	
		RetrieveDataAsync syncWeb = new RetrieveDataAsync(this);
		syncWeb.execute();
		
				
		
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
