package com.example.apptestecoderockr;

import android.app.Activity;
import android.os.Bundle;

public class ProdutoActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			// During initial setup, plug in the details fragment.
			ProdutoDetails details = new ProdutoDetails();
			details.setArguments(getIntent().getExtras());
			getFragmentManager().beginTransaction().add(android.R.id.content, details).commit();
		}
	}

}
