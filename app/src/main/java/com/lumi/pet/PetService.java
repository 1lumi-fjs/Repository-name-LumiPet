package com.lumi.pet;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.Toast;

public class PetService extends Service {

    private WindowManager windowManager;
    private PetView petView;
    private WindowManager.LayoutParams params;

    @Override
    public void onCreate() {
        super.onCreate();

        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(
                    this,
                    "请先开启悬浮窗权限",
                    Toast.LENGTH_SHORT
            ).show();

            stopSelf();
            return;
        }

        windowManager =
                (WindowManager) getSystemService(WINDOW_SERVICE);

        petView = new PetView(this);

        params = new WindowManager.LayoutParams(
                300,
                390,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        params.gravity =
                Gravity.TOP | Gravity.START;

        params.x = 30;
        params.y = 300;

        petView.setOnTouchListener(
                new android.view.View.OnTouchListener() {

                    float downX;
                    float downY;

                    int startX;
                    int startY;

                    long downTime;

                    @Override
                    public boolean onTouch(
                            android.view.View v,
                            MotionEvent event) {

                        if (event.getAction() ==
                                MotionEvent.ACTION_DOWN) {

                            downX = event.getRawX();
                            downY = event.getRawY();

                            startX = params.x;
                            startY = params.y;

                            downTime =
                                    System.currentTimeMillis();

                            return true;
                        }

                        if (event.getAction() ==
                                MotionEvent.ACTION_MOVE) {

                            params.x =
                                    startX +
                                    (int)(event.getRawX() - downX);

                            params.y =
                                    startY +
                                    (int)(event.getRawY() - downY);

                            windowManager.updateViewLayout(
                                    petView,
                                    params
                            );

                            return true;
                        }

                        if (event.getAction() ==
                                MotionEvent.ACTION_UP) {

                            long duration =
                                    System.currentTimeMillis()
                                    - downTime;

                            if (duration < 250) {
                                petView.nextMood();
                            }

                            return true;
                        }

                        return true;
                    }
                }
        );

        windowManager.addView(
                petView,
                params
        );
    }

    @Override
    public void onDestroy() {

        if (windowManager != null &&
                petView != null) {

            try {
                windowManager.removeView(petView);
            } catch (Exception ignored) {
            }
        }

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
