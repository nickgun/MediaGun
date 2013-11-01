package com.nickgun;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.camera_recorder.CameraRecorder;
import com.view_video.ViewVideo;

public class MainActivity extends Activity implements OnClickListener,
		AnimationListener {

	private TextView tvMenu;
	private ImageView ivPreview, ivNext;
	private Animation animBlink, animShortBlink, animNonAnim;
	private int id;
	MediaPlayer mpMusicBackGround, mpClickEffect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// doc file settingApp de thiet lap setting cho app
		readSettingApp();

		// music in background
		mpMusicBackGround = MediaPlayer.create(this, R.raw.burst_the_gravity);
		mpMusicBackGround.setLooping(true); // Set looping
		mpMusicBackGround.setVolume(100, 100);

		mpClickEffect = MediaPlayer.create(this, R.raw.click_effect);
		mpMusicBackGround.setVolume(100, 100);

		tvMenu = (TextView) findViewById(R.id.tvMenu);
		ivPreview = (ImageView) findViewById(R.id.ivPreview);
		ivNext = (ImageView) findViewById(R.id.ivNext);

		animShortBlink = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.anim_shortblink);
		animBlink = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.anim_blink);
		animNonAnim = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.anim_nonanim);

		animShortBlink.setAnimationListener(this);
		animBlink.setAnimationListener(this);
		animNonAnim.setAnimationListener(this);

		tvMenu.setOnClickListener(this);
		ivPreview.setOnClickListener(this);
		ivNext.setOnClickListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mpMusicBackGround.isPlaying()) {
			mpMusicBackGround.stop();
			mpMusicBackGround.release();
		}

		// cap nhat lai setting cho app
		writeSettingApp();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (GlobalStatus.isOnMusicInBackGround) {
			if (!mpMusicBackGround.isPlaying()) {
				mpMusicBackGround = MediaPlayer.create(this,
						R.raw.burst_the_gravity);
				mpMusicBackGround.start();
			}
		} else if (mpMusicBackGround.isPlaying())
			mpMusicBackGround.stop();
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		String str = tvMenu.getText().toString();
		switch (id) {
		case R.id.ivPreview: {
			if (str.equals("WELCOME")) {
				tvMenu.setText("EXIT");
			}
			if (str.equals("GALLERY")) {
				tvMenu.setText("WELCOME");
			}
			if (str.equals("CAMERA")) {
				tvMenu.setText("GALLERY");
			}
			if (str.equals("SETTING")) {
				tvMenu.setText("CAMERA");
			}
			if (str.equals("EXIT")) {
				tvMenu.setText("SETTING");
			}
		}
			break;
		case R.id.ivNext: {
			if (str.equals("WELCOME")) {
				tvMenu.setText("GALLERY");
			}
			if (str.equals("GALLERY")) {
				tvMenu.setText("CAMERA");
			}
			if (str.equals("CAMERA")) {
				tvMenu.setText("SETTING");
			}
			if (str.equals("SETTING")) {
				tvMenu.setText("EXIT");
			}
			if (str.equals("EXIT")) {
				tvMenu.setText("WELCOME");
			}
		}
			break;
		case R.id.tvMenu: {
			if (str.equals("GALLERY")) {
				if (mpMusicBackGround.isPlaying())
					mpMusicBackGround.stop();
				Intent myIntent = new Intent(Intent.ACTION_GET_CONTENT);
				myIntent.setType("file/*");
				startActivityForResult(myIntent, 1);
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_up_out);
			}
			if (str.equals("CAMERA")) {
				if (mpMusicBackGround.isPlaying())
					mpMusicBackGround.stop();
				startActivity(new Intent(this, CameraRecorder.class));
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_up_out);
			}
			if (str.equals("SETTING")) {
				startActivity(new Intent(this, SettingAplication.class));
			}
			if (str.equals("EXIT")) {
				finish();
			}
		}
			break;
		default:
			break;
		}
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
	}

	@Override
	public void onAnimationStart(Animation animation) {
	}

	@Override
	public void onClick(View v) {
		id = v.getId();

		if (GlobalStatus.isOnMusicClick)
			mpClickEffect.start();

		switch (id) {
		case R.id.ivPreview: {
			if (!GlobalStatus.isOnClickEffect)
				ivPreview.startAnimation(animNonAnim);
			else
				ivPreview.startAnimation(animShortBlink);
		}
			break;
		case R.id.ivNext: {
			if (!GlobalStatus.isOnClickEffect)
				ivNext.startAnimation(animNonAnim);
			else
				ivNext.startAnimation(animShortBlink);
		}
			break;
		case R.id.tvMenu: {
			if (!GlobalStatus.isOnClickEffect)
				tvMenu.startAnimation(animNonAnim);
			else
				tvMenu.startAnimation(animBlink);
		}
			break;
		default:
			break;
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == 1) {
				String Path = data.getData().getPath();
				Intent myIntent = new Intent(this, ViewVideo.class);
				myIntent.putExtra("path", Path);
				startActivity(myIntent);
			}
		}
	}

	private void writeSettingApp() {
		try {
			FileWriter fw = new FileWriter(GlobalStatus.strSettingAppPath);
			fw.write("OnMusicClick=" + GlobalStatus.isOnMusicClick + "\n"
					+ "OnMusicInBackGround="
					+ GlobalStatus.isOnMusicInBackGround + "\n"
					+ "OnClickEffect=" + GlobalStatus.isOnClickEffect);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void readSettingApp() {
		try {
			FileReader myFileReader = new FileReader(
					GlobalStatus.strSettingAppPath);
			BufferedReader bf = new BufferedReader(myFileReader);
			String s;
			if ((s = bf.readLine()) != null) {
				if (s.equals("OnMusicClick=true"))
					GlobalStatus.isOnMusicClick = true;
				else
					GlobalStatus.isOnMusicClick = false;
				if (bf.readLine().equals("OnMusicInBackGround=true"))
					GlobalStatus.isOnMusicInBackGround = true;
				else
					GlobalStatus.isOnMusicInBackGround = false;
				if (bf.readLine().equals("OnClickEffect=true"))
					GlobalStatus.isOnClickEffect = true;
				else
					GlobalStatus.isOnClickEffect = false;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			// gap loi k tim duoc file thi nhay vao day tao file moi
			FileWriter fw;
			try {
				fw = new FileWriter(GlobalStatus.strSettingAppPath);
				fw.write("OnMusicClick=true" + "\n"
						+ "OnMusicInBackGround=true" + "\n"
						+ "OnClickEffect=true");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}