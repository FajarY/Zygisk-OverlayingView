package com.fajary.zygisk_overlayingviewui;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class OverlayService extends Service {
    private WindowManager windowManager;
    private View overlayingView;
    private View floatingOpenButton;
    private View overlayMenu;
    private Button closeMenuButton;
    private WindowManager.LayoutParams params;

    private boolean isOpen = false;

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        overlayingView = LayoutInflater.from(this).inflate(R.layout.overlay_layout, null);
        floatingOpenButton = overlayingView.findViewById(R.id.openButton);
        overlayMenu = overlayingView.findViewById(R.id.overlayMenu);
        closeMenuButton = overlayingView.findViewById(R.id.closeButton);

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        params.x = 0;
        params.y = 100;

        windowManager.addView(overlayingView, params);
        initialize();

        Toast.makeText(this, "Overlay service created!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private void initialize()
    {
        overlayMenu.setVisibility(View.GONE);
        overlayingView.setOnTouchListener((v, e) -> false);

        floatingOpenButton.setOnTouchListener(new View.OnTouchListener() {
            int initialX;
            int initialY;
            float initialTouchX;
            float initialTouchY;
            boolean isDragged = false;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = motionEvent.getRawX();
                        initialTouchY = motionEvent.getRawY();
                        return false;

                    case MotionEvent.ACTION_MOVE:
                        isDragged = true;
                        params.x = initialX + (int) (motionEvent.getRawX() - initialTouchX);
                        params.y = initialY + (int) (motionEvent.getRawY() - initialTouchY);

                        windowManager.updateViewLayout(overlayingView, params);
                        return true;

                    case MotionEvent.ACTION_UP:
                        if(!isDragged)
                        {
                            view.performClick();
                            if(!isOpen)
                            {
                                isOpen = true;
                                overlayMenu.setVisibility(View.VISIBLE);
                                floatingOpenButton.setVisibility(View.GONE);

                                params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
                                windowManager.updateViewLayout(overlayingView, params);
                            }
                        }
                        view.setPressed(false);
                        view.refreshDrawableState();
                        isDragged = false;
                        return true;
                }

                return false;
            }
        });

        closeMenuButton.setOnClickListener(clickListen -> {
            if(isOpen)
            {
                isOpen = false;
                overlayMenu.setVisibility(View.GONE);
                floatingOpenButton.setVisibility(View.VISIBLE);
                params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                windowManager.updateViewLayout(overlayingView, params);
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(overlayingView != null)
        {
            windowManager.removeView(overlayingView);
            overlayingView = null;
        }
    }
}
