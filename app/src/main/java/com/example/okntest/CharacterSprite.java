package com.example.okntest;

import android.content.res.Resources;
import android.graphics.Bitmap;

import android.graphics.Canvas;

public class CharacterSprite {

    private Bitmap image;
    public int x,y;
    private int xVelocity = 10;
    private int yVelocity = 5;
    public int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    public int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
    private double holder;
    public int frequency;

    public CharacterSprite(Bitmap bmp) {
        image = bmp;
        x=0;
        y=0;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, x, y, null);

        canvas.drawBitmap(image, x, y - 300, null);


        canvas.drawBitmap(image, x, y - 600, null);


        canvas.drawBitmap(image, x, y - 900, null);


        canvas.drawBitmap(image, x, y - 1200, null);


        canvas.drawBitmap(image, x, y - 1500, null);


        canvas.drawBitmap(image, x, y - 1800, null);

        canvas.drawBitmap(image, x, y +300, null);


        canvas.drawBitmap(image, x, y +600, null);


        canvas.drawBitmap(image, x, y + 900, null);


        canvas.drawBitmap(image, x, y + 1200, null);


        canvas.drawBitmap(image, x, y +1500, null);


        canvas.drawBitmap(image, x, y + 1800, null);
        if (y>=1800){
            y=0;
        }

    }

    public void update() {
        frequency = (MainActivity.  freq/2)*10;

        //frequency
        y= y+frequency;


    }
}
