package com.bet007.mobile.score.common;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class ToastUtil {
	private static Toast toast = null;
	private static View view = null;
	public static void showMessage(final Context act, final String msg) {
		showMessage(act, msg, Toast.LENGTH_SHORT);
	}
	public static void showMessage_Long(final Context act, final String msg) {
		showMessage(act, msg, Toast.LENGTH_LONG);
	}
	public static void showMessage(final Context act, final String msg, final int len) {
		try {
			if (toast != null && view != null) {
				toast.setView(view);
				toast.setText(msg);
				toast.setDuration(len);
				toast.show();
			} else {
				toast = Toast.makeText(act, msg, len);
				toast.show();
				view = toast.getView();
			}
		} catch (Exception e) {
			Log.d("exp", e.toString());
		}
	}
}
