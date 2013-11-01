package com.camera_recorder;

import java.io.File;
import java.io.IOException;

import com.nickgun.R;
import com.nickgun.Utils;
import com.nickgun.R.id;
import com.nickgun.R.layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.media.MediaRecorder.VideoEncoder;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CameraRecorder extends Activity implements SurfaceHolder.Callback {
	private SurfaceView prSurfaceView;
	private Button btnStartRecorder;
	private Button btnSettings;
	private Button btnAddNote;

	private boolean prRecordInProcess;
	private SurfaceHolder prSurfaceHolder;
	private Camera prCamera;
	private String myVideoFilePath;
	private String videoFileFullPath;
	private String videoFormat;

	private Context prContext;
	private long timeStartRecorder;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		prContext = this.getApplicationContext();
		setContentView(R.layout.camera_recorder);

		String myStorage = Environment.getExternalStorageDirectory().toString();
		myVideoFilePath = myStorage + "/NickGun/CameraGun/";

		Utils.createDirIfNotExist(myVideoFilePath);
		prSurfaceView = (SurfaceView) findViewById(R.id.surface_camera);
		btnStartRecorder = (Button) findViewById(R.id.main_btn1);
		btnSettings = (Button) findViewById(R.id.main_btn2);
		btnAddNote = (Button) findViewById(R.id.btnAddNote);

		prRecordInProcess = false;

		btnStartRecorder.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (prRecordInProcess == false) {
					timeStartRecorder = System.currentTimeMillis();
					startRecording();
				} else {
					stopRecording();
					showDialog();
				}
			}
		});
		btnSettings.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent();
				myIntent.setClass(prContext, SettingsDialog.class);
				startActivityForResult(myIntent, REQUEST_DECODING_OPTIONS);
			}
		});
		btnAddNote.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent();
				Bundle extras = new Bundle();
				extras.putString("TIME_START_RECORDER",
						String.valueOf(timeStartRecorder));
				myIntent.putExtras(extras);
				myIntent.setClass(prContext, AddNote.class);
				startActivityForResult(myIntent, REQUEST_ADD_NOTE);
			}
		});
		prSurfaceHolder = prSurfaceView.getHolder();
		prSurfaceHolder.addCallback(this);
		prMediaRecorder = new MediaRecorder();
	}

	// @Override
	public void surfaceChanged(SurfaceHolder _holder, int _format, int _width,
			int _height) {
		Camera.Parameters lParam = prCamera.getParameters();
		prCamera.setParameters(lParam);
		try {
			prCamera.setPreviewDisplay(_holder);
			prCamera.startPreview();
			// prPreviewRunning = true;
		} catch (IOException _le) {
			_le.printStackTrace();
		}
	}

	// @Override
	public void surfaceCreated(SurfaceHolder arg0) {
		prCamera = Camera.open();
		if (prCamera == null) {
			Toast.makeText(this.getApplicationContext(),
					"Camera is not available!", Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	// @Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		if (prRecordInProcess) {
			stopRecording();
		} else {
			prCamera.stopPreview();
		}
		if (prMediaRecorder != null) {
			prMediaRecorder.release();
			prMediaRecorder = null;
		}
		if (prCamera != null) {
			prCamera.release();
			prCamera = null;
		}
	}

	private MediaRecorder prMediaRecorder;
	private final long cMaxFileSizeInBytes = 5000000;
	private final int cFrameRate = 25;
	private File prRecordedFile;

	private void updateEncodingOptions() {
		if (prRecordInProcess) {
			stopRecording();
			startRecording();
			Toast.makeText(prContext, "Recording restarted with new options!",
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(prContext, "Recording options updated!",
					Toast.LENGTH_SHORT).show();
		}
	}

	private boolean startRecording() {
		prCamera.stopPreview();
		try {
			prCamera.unlock();
			prMediaRecorder.setCamera(prCamera);
			// set audio source as Microphone, video source as camera
			// state: Initial=>Initialized
			// prMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			prMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			// set the file output format: 3gp or mp4
			// state: Initialized=>DataSourceConfigured

			String lDisplayMsg = "Current container format: ";

			if (Utils.puContainerFormat == SettingsDialog.cpu3GP) {
				lDisplayMsg += "3GP\n";
				videoFormat = ".3gp";
				prMediaRecorder
						.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			} else if (Utils.puContainerFormat == SettingsDialog.cpuMP4) {
				lDisplayMsg += "MP4\n";
				videoFormat = ".mp4";
				prMediaRecorder
						.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			} else {
				lDisplayMsg += "3GP\n";
				videoFormat = ".3gp";
				prMediaRecorder
						.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			}

			lDisplayMsg += "Current encoding format: ";
			if (Utils.puEncodingFormat == SettingsDialog.cpuH263) {
				lDisplayMsg += "H263\n";
				prMediaRecorder.setVideoEncoder(VideoEncoder.H263);
			} else if (Utils.puEncodingFormat == SettingsDialog.cpuMP4_SP) {
				lDisplayMsg += "MPEG4-SP\n";
				prMediaRecorder.setVideoEncoder(VideoEncoder.MPEG_4_SP);
			} else if (Utils.puEncodingFormat == SettingsDialog.cpuH264) {
				lDisplayMsg += "H264\n";
				prMediaRecorder.setVideoEncoder(VideoEncoder.H264);
			} else {
				lDisplayMsg += "H263\n";
				prMediaRecorder.setVideoEncoder(VideoEncoder.H263);
			}
			videoFileFullPath = myVideoFilePath
					+ String.valueOf(System.currentTimeMillis()) + videoFormat;
			prRecordedFile = new File(videoFileFullPath);
			prMediaRecorder.setOutputFile(prRecordedFile.getPath());
			if (Utils.puResolutionChoice == SettingsDialog.cpuRes176) {
				prMediaRecorder.setVideoSize(176, 144);
			} else if (Utils.puResolutionChoice == SettingsDialog.cpuRes320) {
				prMediaRecorder.setVideoSize(320, 240);
			} else if (Utils.puResolutionChoice == SettingsDialog.cpuRes720) {
				prMediaRecorder.setVideoSize(720, 480);
			}
			Toast.makeText(prContext, lDisplayMsg, Toast.LENGTH_LONG).show();
			prMediaRecorder.setVideoFrameRate(cFrameRate);
			prMediaRecorder.setPreviewDisplay(prSurfaceHolder.getSurface());
			prMediaRecorder.setMaxFileSize(cMaxFileSizeInBytes);
			// prepare for capturing
			// state: DataSourceConfigured => prepared
			prMediaRecorder.prepare();
			// start recording
			// state: prepared => recording
			prMediaRecorder.start();
			btnStartRecorder.setText("Stop");
			prRecordInProcess = true;
			return true;
		} catch (IOException _le) {
			_le.printStackTrace();
			return false;
		}
	}

	private void stopRecording() {
		prMediaRecorder.stop();
		prMediaRecorder.reset();
		try {
			prCamera.reconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		btnStartRecorder.setText("Start");
		prRecordInProcess = false;
		prCamera.startPreview();
	}

	private static final int REQUEST_DECODING_OPTIONS = 0;
	private static final int REQUEST_ADD_NOTE = 1;

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		switch (requestCode) {
		case REQUEST_DECODING_OPTIONS:
			if (resultCode == RESULT_OK) {
				updateEncodingOptions();
			}
			break;
		case REQUEST_ADD_NOTE:

			break;
		}
	}

	// Dat ten Video va luu vao thu muc NickGun/CameraGun/
	private void showDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				CameraRecorder.this);
		builder.setTitle("Name Video");

		LayoutInflater layoutInf = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = layoutInf.inflate(R.layout.edit_name_video, null);

		final EditText dataEdit = (EditText) view
				.findViewById(R.id.dataEditText);
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String name = dataEdit.getText().toString();
				String cmd = "mkdir " + myVideoFilePath + name;
				String cmd1 = "mv " + videoFileFullPath + " " + myVideoFilePath
						+ "/" + name + "/" + name + videoFormat;
				String cmd2 = "mv " + myVideoFilePath + "/note.txt" + " "
						+ myVideoFilePath + "/" + name + "/" + name + ".txt";
				try {
					Process process = Runtime.getRuntime().exec(cmd);
					process.waitFor();
					process = Runtime.getRuntime().exec(cmd1);
					process.waitFor();
					process = Runtime.getRuntime().exec(cmd2);
					process.waitFor();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				/*
				 * // retart lai intent Intent intent = getIntent(); finish();
				 * startActivity(intent);
				 */
			}
		});
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});

		builder.setView(view);
		builder.create();
		builder.show();
	}
}
