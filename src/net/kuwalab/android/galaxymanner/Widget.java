package net.kuwalab.android.galaxymanner;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.widget.RemoteViews;

public class Widget extends AppWidgetProvider {
	private static final String ACTION = "net.kuwalab.android.galaxymanner.CLICK";

	protected static final String PREFERENCES_NAME = "ORIGINAL_DATA";
	protected static final String PREFERENCES_KEY_SOUND = "music_volume";

	public void onUpdate(Context context, AppWidgetManager am, int[] ids) {
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
				R.layout.widget_main);

		for (int i = 0; i < ids.length; i++) {
			Intent intent = new Intent(context, GalaxyMannerActivity.class);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, ids[i]);
			PendingIntent pi = PendingIntent.getActivity(context, ids[i],
					intent, 0);
			remoteViews.setOnClickPendingIntent(R.id.widgetBody, pi);

			intent = new Intent(ACTION);
			pi = PendingIntent.getBroadcast(context, ids[i], intent, 0);
			remoteViews.setOnClickPendingIntent(R.id.mannerButton, pi);

			am.updateAppWidget(ids[i], remoteViews);
		}
		refresh(context);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		String action = intent.getAction();
		if (action.equals(ACTION)) {
			onClickToggleButton(context);
		}
	}

	/**
	 * ウィジェットのボタンが押された時の処理。<br>
	 * 無音と通常の状態を切り買える。
	 * 
	 * @param context
	 */
	private void onClickToggleButton(Context context) {
		AudioManager am = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		if (isAllStop(context)) {
			am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			SharedPreferences pref = context.getSharedPreferences(
					PREFERENCES_NAME, Context.MODE_PRIVATE);
			int volume = pref.getInt(PREFERENCES_KEY_SOUND, 5);
			am.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
		} else {
			am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
			am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
		}

		refresh(context);
	}

	/**
	 * ウィジェット全ての状態を最新の状態に更新する。
	 * 
	 * @param context
	 */
	private void refresh(Context context) {
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
				R.layout.widget_main);
		if (isAllStop(context)) {
			remoteViews.setTextViewText(R.id.mannerButton,
					context.getString(R.string.status_normal));
			remoteViews.setImageViewResource(R.id.statusIcon,
					android.R.drawable.ic_lock_silent_mode);
		} else {
			remoteViews.setTextViewText(R.id.mannerButton,
					context.getString(R.string.status_manner));
			remoteViews.setImageViewResource(R.id.statusIcon,
					android.R.drawable.ic_lock_silent_mode_off);
		}
		AppWidgetManager awm = AppWidgetManager.getInstance(context);
		int[] ids = awm.getAppWidgetIds(new ComponentName(context, this
				.getClass()));
		awm.updateAppWidget(ids, remoteViews);
	}

	/**
	 * ringerModeがVIBRATEかつ、musicのボリュームが0ならtrue
	 * 
	 * @return ringerModeがVIBRATEかつ、musicのボリュームが0ならtrue
	 */
	private boolean isAllStop(Context context) {
		AudioManager am = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		int volume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
		if (am.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
			if (volume == 0) {
				return true;
			}
		}
		// VIBRATEでないときの音楽のボリュームを保管する。
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		Editor edit = pref.edit();
		edit.putInt(PREFERENCES_KEY_SOUND, volume);
		edit.commit();

		return false;
	}
}
