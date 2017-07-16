package com.ultra.getimages.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.ultra.getimages.App;
import com.ultra.getimages.Utils.O;
import com.ultra.getimages.R;

public class MainActivity extends AppCompatActivity {
	private String token;
	private Button btn;

	private String getToken() {
		String result = App.prefs.getString(O.prefs.TOKEN, null);
		if (result == null)
			App.dBoxApi.getSession().startOAuth2Authentication(MainActivity.this);
		Log.d(O.TAG, "getToken: token=" + result);
		return result;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);

		btn = (Button) findViewById(R.id.btnToImgs);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent jumper = new Intent(MainActivity.this, ImgListActivity.class);
				startActivity(jumper);
			}
		});

		token = getToken();
		if (token != null) {
			App.dBoxApi.getSession().setOAuth2AccessToken(token);
			btn.setEnabled(true);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (App.dBoxApi.getSession().authenticationSuccessful())
			try {
				App.dBoxApi.getSession().finishAuthentication();
				token = App.dBoxApi.getSession().getOAuth2AccessToken();
				App.prefs.edit().putString(O.prefs.TOKEN, token).commit();
				btn.setEnabled(true);
				Log.d(O.TAG, "onResume: token=" + token);
			} catch (IllegalStateException err) {
				Log.e(O.TAG, "onResume: Auth error", err);
			}
	}
}
