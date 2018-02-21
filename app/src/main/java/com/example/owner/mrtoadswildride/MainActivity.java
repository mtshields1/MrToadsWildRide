package com.example.owner.mrtoadswildride;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.owner.mrtoadswildride.grafx.PlasmaView;

public class MainActivity extends AppCompatActivity {
    private PlasmaView plasmaView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        plasmaView = findViewById(R.id.plasmaView);
    }

//    private class CanvasView extends View {
//        public CanvasView(Context context) {
//            super(context);
//        }
//
//        @Override
//        protected void onDraw(Canvas canvas) {
//            super.onDraw(canvas);
//            canvas.drawBitmap(plasmaView.getUpdate(), plasmaView.getRect(), canvas.getClipBounds(), null);
//        }
//    }
}
