package com.lzq.lianliankan2_3_3_v1_0;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class LianLianKanActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lian_lian_kan);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.lian_lian_kan, menu);
		return true;
	}

}
