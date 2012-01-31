package net.kuwalab.android.galaxymanner;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class GalaxyMannerActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		SharedPreferences pref = getSharedPreferences(Widget.PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		int volume = pref.getInt(Widget.PREFERENCES_KEY_SOUND, 5);
		TextView textView = (TextView) findViewById(R.id.sound);
		textView.setText("音楽のボリューム設定: " + volume);

		textView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}