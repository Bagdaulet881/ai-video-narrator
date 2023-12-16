package kz.narrator.app.permission.utils;

public class ToastUtils {

    //TO DO
//    private static final long SHORT_DURATION_TIMEOUT = 2500;
//
//    public static void show(Context context, String text) {
//        Toast.makeText(context, text != null ? text : "", Toast.LENGTH_SHORT).show();
//    }
//
//    public static void showError(Context context, String text, Window window) {
//        showCustom(context, text, window, R.color.fieldErrorTextColor);
//    }
//
//    private static void showCustom(Context context, String text, Window window, @ColorRes int color) {
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//        View view = inflater.inflate(R.layout.view_toast, null);
//
//        ViewGroup vgContainer = view.findViewById(R.id.vg_container);
//        vgContainer.setBackgroundResource(color);
//
//        TextView tvText = view.findViewById(R.id.tv_text);
//        tvText.setText(text != null ? text : "");
//
//        Toast toast = new Toast(context.getApplicationContext());
//        toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.TOP, 0, 0);
//        toast.setDuration(Toast.LENGTH_SHORT);
//        toast.setView(view);
//        toast.show();
//
//        try {
//            if (Build.VERSION.SDK_INT >= 21) {
//                setStatusBarColor(context, window, color);
//                startTimer(context, window);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    private static void setStatusBarColor(Context context, Window window, @ColorRes int color) {
//        if (window != null) {
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.setStatusBarColor(ContextCompat.getColor(context, color));
//        }
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    private static void startTimer(final Context context, final Window window) {
//        CountDownTimer countDownTimer = new CountDownTimer(SHORT_DURATION_TIMEOUT, 1000) {
//
//            public void onTick(long millisUntilFinished) {
//                //do nothing
//            }
//
//            public void onFinish() {
//                setStatusBarColor(context, window, R.color.colorPrimaryDark);
//            }
//
//        }.start();
//    }

}