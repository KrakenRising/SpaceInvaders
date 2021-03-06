/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.krakenrising.spaceinvaders.view;

import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Rob
 */
public class DrawThread extends Thread {
    private SurfaceHolder surfaceHolder;
    private Context context;
    private AtomicBoolean killSwitch = new AtomicBoolean(false);
    private Drawable delegate;
    private double fps = 30.0;
    public DrawThread(SurfaceHolder surfaceHolder, Context context, Drawable delegate) {
        this.context = context;
        this.surfaceHolder = surfaceHolder;
        this.delegate = delegate;
    }
    public void run() {
        long startTime;
        long endTime;
        while(!killSwitch.get()) {
            startTime = System.currentTimeMillis();
            Canvas canvas = null;
            try {
                canvas = surfaceHolder.lockCanvas(null);
                synchronized(surfaceHolder) {
                    delegate.draw(canvas);
                }
            } finally {
                if(canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch(Exception e) {
                        close();
                    }
                }
            }
            endTime = System.currentTimeMillis();
            long diff = endTime - startTime;
            if(diff < fps) {
                try {    
                    Thread.sleep((long)(fps-diff));
                } catch (InterruptedException ex) {
                    //This never happens
                }
            }
        }
    }
    public void close() {
        killSwitch.set(true);
    }
}
