package kz.narrator.app.utils;

import android.util.Log;

import kz.narrator.app.BuildConfig;

public class Logger {

    private static final boolean LOGGING_ENABLED = BuildConfig.IS_DEBUG;
//    private static final boolean LOGGING_ENABLED = false;

    private static final String TAG = "MINIGO";
    private static final String LOGGER_FORMAT = "%s/%s";

    private static String getTag(Class clazz) {
        return String.format(LOGGER_FORMAT, TAG, clazz.getSimpleName());
    }

    public static void i(Class clazz, String message) {
        if (LOGGING_ENABLED) {
            Log.i(getTag(clazz), message);
        }
    }

    public static void d(Class clazz, String message) {
        if (LOGGING_ENABLED) {
            Log.d(getTag(clazz), message);
        }
    }

    public static void d(String message) {
        if (LOGGING_ENABLED) {
            Log.d(TAG, message);
        }
    }

    public static void e(String message) {
        if (LOGGING_ENABLED) {
            Log.e(TAG, message);
        }
    }

    public static void e(Class clazz, String message) {
        if (LOGGING_ENABLED) {
            Log.e(getTag(clazz), message);
        }
    }

    public static void exception(Exception e) {
        if (LOGGING_ENABLED) {
            e.printStackTrace();
        }
    }

    public static void showBigLogs(final String launchedString) {
        if (LOGGING_ENABLED) {
            int maxLogSize = 3 * 1024;
            for (int i = 0; i <= launchedString.length() / maxLogSize; i++) {
                int start = i * maxLogSize;
                int end = (i + 1) * maxLogSize;
                end = end > launchedString.length() ? launchedString.length() : end;
                Log.w("BIG LOG", launchedString.substring(start, end));
            }
        }
    }

}