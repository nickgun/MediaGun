package com.nickgun;

import android.os.Environment;

public class GlobalStatus {
	static String strSettingAppPath = Environment.getExternalStorageDirectory()
			.toString() + "/NickGun/CameraGun/settingApp.txt";
	static boolean isOnMusicInBackGround = true;
	static boolean isOnMusicClick = true;
	static boolean isOnClickEffect = true;
}
