package com.example.owner.mrtoadswildride.grafx;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Greg on 2/21/2018.
 */

public class PlasmaView extends SurfaceView implements SurfaceHolder.Callback {

    private PlasmaThread thread;

    public PlasmaView(Context context, AttributeSet attrs){
        super(context, attrs);
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        if(isInEditMode() == false) {
            thread = new PlasmaThread(holder, context, new Handler(){
                public void handleMessage(Message m) {
                    // for future use.
                }
            });
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        thread.setSurfaceSize(i1, i2);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        boolean r = true;
        thread.setRunning(false);
        while(r){
            try {
                thread.join();
                r = false;
            } catch (InterruptedException ex) {

            }
        }
    }

    class PlasmaThread extends Thread {
        private SurfaceHolder surfaceHolder;
        private Context context;
        private Handler handler;
        private boolean running;
        private boolean drawing;
        private PlasmaDisplay plasmaDisplay;
        private final Paint paint;

        public PlasmaThread(SurfaceHolder surfaceHolder, Context context, Handler handler)
        {
            this.surfaceHolder = surfaceHolder;
            this.context = context;
            this.handler = handler;
            this.plasmaDisplay = new PlasmaDisplay();
            this.drawing = true;
            paint = new Paint();
            paint.setAntiAlias(false);
            paint.setFilterBitmap(false);
        }
        private void doDraw(Canvas canvas) {
            Bitmap b = plasmaDisplay.getUpdate();
            if(b != null && canvas != null)
                canvas.drawBitmap(b, plasmaDisplay.getRect(), canvas.getClipBounds(), paint);
        }
        public void run() {
            while(running){
                Canvas cvs = null;
                try {
                    cvs = surfaceHolder.lockCanvas(null);
                    if(drawing)
                        doDraw(cvs);
                } finally {
                    if(cvs != null)
                        surfaceHolder.unlockCanvasAndPost(cvs);
                }
            }
        }
        public void setRunning(boolean run) {
            this.running = run;
        }
        public void setSurfaceSize(int width, int height) {
            synchronized (surfaceHolder) {
                plasmaDisplay.setRectSize(64, 64);
            }
        }
    }

    class PlasmaDisplay {
        private int width;
        private int height;
        private Bitmap bmp;
        private List<Integer> gradient;
        private final long startTime;
        private Rect rect;
        private boolean isRunning;

        public PlasmaDisplay() {
            startTime = System.currentTimeMillis();
            isRunning = false;
            init();
        }
        public void setRectSize(int w, int h) {
            isRunning = false;
            width = w;
            height = h;
            bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            rect = new Rect(0, 0, w, h);
            isRunning = true;
        }
        public final Rect getRect() {
            return rect;
        }
        public final Bitmap getUpdate() {
            if(isRunning) {
                int delta = (int) (System.currentTimeMillis() - startTime); // cast doesn't matter, should never be over 32-bit max
                // not optimal
                for (int row = height - 2; row > 0; --row) {
                    for (int col = 1; col < width; ++col) {
                        int color = (int) ((64 + (64 * Math.sin(Math.sqrt((col - width / 2) * (col - width / 2) + (row - height / 2) * (row - height / 2)) / 6))));
                        color += 64 + (64 * Math.sin(col / 12));
                        color += 64 + (64 * Math.sin(row / 14));
                        color += (delta / 3);
                        color /= 3;
                        color %= 255;
                        if (gradient.size() > color) {
                            int grad = gradient.get(color);
                            bmp.setPixel(col, row, grad);
                        }
                    }
                }
                return bmp;
            }
            return null;
        }
        private LinkedList<Integer> buildPalette(int start, int end, int degrees) {
            LinkedList<Integer> pal = new LinkedList<>();
            // just being lazy
            ArrayList<Integer> first = new ArrayList<>();
            ArrayList<Integer> last = new ArrayList<>();
            double a = 0;
            first.add((start >> 16) & 0xFF);
            first.add((start >> 8) & 0xFF);
            first.add(start & 0xFF);
            last.add((end >> 16) & 0xFF);
            last.add((end >> 8) & 0xFF);
            last.add(end & 0xFF);
            for(int i = 0; i < degrees; ++i){
                a += (1.0f / degrees);
                int a1 = (int)Math.floor(first.get(0) * a + (1 - a) * last.get(0));
                int a2 = (int)Math.floor(first.get(1) * a + (1 - a) * last.get(1));
                int a3 = (int)Math.floor(first.get(2) * a + (1 - a) * last.get(2));
                int color = 0xFF000000 | (a1 << 16) | (a2 << 8) | a3;
                pal.addFirst(color);
                pal.addLast(color);
            }
            return pal;
        }
        private void init() {
            gradient = buildPalette(0xffff0000, 0xff0000ff, 128);
        }
    }
}
