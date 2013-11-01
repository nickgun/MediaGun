package com.nickgun;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

public class SettingAplication extends Activity {
	TextView tvCancelUpdateSettingApp, tvUpdateSettingApp;
	CheckBox cbOnMusicClick, cbOnMusicInBackGround, cbOnClickEffect;
	int isOnMusicClick, isOnMusicInBackGround;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_aplication);

		tvUpdateSettingApp = (TextView) findViewById(R.id.tvUpdateSettingApp);
		tvCancelUpdateSettingApp = (TextView) findViewById(R.id.tvCancelUpdateSettingApp);
		cbOnMusicClick = (CheckBox) findViewById(R.id.cbOnMusicClick);
		cbOnMusicInBackGround = (CheckBox) findViewById(R.id.cbOnMusicInBackGround);
		cbOnClickEffect = (CheckBox) findViewById(R.id.cbOnClickEffect);

		GlobalStatus.strSettingAppPath = Environment
				.getExternalStorageDirectory().toString()
				+ "/NickGun/CameraGun/settingApp.txt";
		readSettingApp();

		tvUpdateSettingApp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				updateSettingApp();
				finish();
			}
		});

		tvCancelUpdateSettingApp.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void readSettingApp() {
		cbOnMusicClick.setChecked(GlobalStatus.isOnMusicClick);
		cbOnMusicInBackGround.setChecked(GlobalStatus.isOnMusicInBackGround);
		cbOnClickEffect.setChecked(GlobalStatus.isOnClickEffect);
	}

	private void updateSettingApp() {
		GlobalStatus.isOnMusicClick = cbOnMusicClick.isChecked();
		GlobalStatus.isOnMusicInBackGround = cbOnMusicInBackGround.isChecked();
		GlobalStatus.isOnClickEffect = cbOnClickEffect.isChecked();
	}
}
