package kz.narrator.app.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class KeyboardUtils {

    @SuppressLint("Recycle")
    public static void showKeyBoard(final View et, final Context context) {
        (new Handler()).postDelayed(new Runnable() {

            public void run() {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
                et.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0));
                et.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0));

                try {
                    ((EditText) et).setSelection(((EditText) et).getText().toString().length());
                } catch (ClassCastException e) {

                } catch (Exception e) {
                }

            }
        }, 100);
    }

    public static void hideKeyboard(Context context, View view) {
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

}
