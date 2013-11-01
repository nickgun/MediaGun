package com.camera_recorder;

import java.io.FileWriter;
import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nickgun.R;

public class AddNote extends Activity implements OnClickListener {
	private Button btnSaveNote, btnCancelNote;
	private EditText etNote;

	private String noteFilePath;
	private long timeStartRecorder;
	private long timeCreatNote;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_note);

		btnSaveNote = (Button) findViewById(R.id.btnSaveNote);
		btnCancelNote = (Button) findViewById(R.id.btnCancelNote);
		etNote = (EditText) findViewById(R.id.etNote);

		btnSaveNote.setOnClickListener(this);
		btnCancelNote.setOnClickListener(this);

		Bundle extras = getIntent().getExtras();
		timeStartRecorder = Long.parseLong(extras
				.getString("TIME_START_RECORDER"));
		timeCreatNote = System.currentTimeMillis();
	}

	@Override
	public void onClick(View v) {
		int i = v.getId();
		switch (i) {
		case R.id.btnSaveNote: {
			Toast.makeText(getBaseContext(), "Save Note", Toast.LENGTH_SHORT);
			saveNote((timeCreatNote - timeStartRecorder), etNote
					.getText().toString());
			finish();

		}
			break;
		case R.id.btnCancelNote:
			finish();
			break;
		default:
			break;
		}
	}

	private void saveNote(long time, String str) {
		try {
			noteFilePath = Environment.getExternalStorageDirectory().toString()
					+ "/NickGun/CameraGun/note.txt";
			FileWriter fw = new FileWriter(noteFilePath, true);
			fw.write(time + "\n" + str + "\n");
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
