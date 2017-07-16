package com.ultra.getimages;

import android.app.Application;
import android.content.SharedPreferences;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.ultra.getimages.Utils.O;

/**
 * <p></p>
 * <p><sub>(15.07.2017)</sub></p>
 *
 * @author CC-Ultra
 */

public class App extends Application
	{
	public static SharedPreferences prefs;
	public static DropboxAPI<AndroidAuthSession> dBoxApi;

	@Override
	public void onCreate()
		{
		super.onCreate();

		AppKeyPair appKeys = new AppKeyPair(O.APP_KEY,O.APP_SECRET);
		AndroidAuthSession session = new AndroidAuthSession(appKeys);
		dBoxApi= new DropboxAPI<>(session);

		prefs= getSharedPreferences(O.prefs.FILENAME,MODE_PRIVATE);
		}
	}
