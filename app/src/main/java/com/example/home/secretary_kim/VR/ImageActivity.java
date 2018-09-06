package com.example.home.secretary_kim.VR;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

public class ImageActivity {
    private Bitmap imgArray[];
    private static Bitmap img;

    public ImageActivity(){
        imgArray = null;
        img = null;
    }

    public ImageActivity(Bitmap[] img){
        imgArray = img;
        img = null;
    }

    public Bitmap drawBoard(Bitmap tmp){    //3:2 비율 이미지에 좌표 그리기 5*4
        Bitmap result = null;
        Bitmap bit = Bitmap.createBitmap(tmp).copy(Bitmap.Config.ARGB_8888, true);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = true;
        options.inPurgeable = true;

        float width = bit.getWidth();
        float height = bit.getHeight();
        Log.i("ImageActivity", "width / height : " + width + " / " + height);

        float wLength = width / 8;
        float hLength = height / 6;
        Log.i("ImageActivity", "wLength / hLength : " + wLength + " / " + hLength);

        result = Bitmap.createScaledBitmap(bit, bit.getWidth(), bit.getHeight(), true);
        Canvas canvas = new Canvas(result);
        Paint p = new Paint();
        p.setStrokeWidth(1f);
        p.setColor(Color.YELLOW);
        p.setStyle(Paint.Style.STROKE);

        canvas.drawBitmap(img, 0,0, p);
        for(int i = 1; i <+ 8; i++){
            canvas.drawLine(0,i*hLength, width, i*hLength, p);
        }
        for(int i = 1; i <= 6; i++){
            canvas.drawLine(i*wLength, 0, i*wLength, height, p);
        }
        canvas.drawLine(width, 0, width, height, p);


        return result;
    }


    public Bitmap makePanorama(){ //비트맵 이미지 4장 연결
        for(int i = 0; i < 4; i++){
            imgArray[i] = drawBoard(imgArray[i]);   //좌표 생성
        }

        Bitmap tmp = imgArray[0];

        for(int i = 0; i < 3; i++){
            tmp = panomaraImg(tmp, imgArray[i+1], false);
        }

        tmp = panomaraImg(tmp, tmp, true);

        img = tmp;

        return img;
    }

    public Bitmap panomaraImg(Bitmap  bit1, Bitmap bit2, boolean mode){ //비트맵 이미지 2장 연결
        Bitmap result = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = true;
        options.inPurgeable = true;

        if(!mode){
            result = Bitmap.createScaledBitmap(bit1, bit1.getWidth() + bit2.getWidth(), bit1.getHeight(), true);
        }
        else{
            result = Bitmap.createScaledBitmap(bit1, bit1.getWidth(), bit1.getHeight() + bit2.getHeight(), true);
        }

        Paint p = new Paint();
        p.setDither(true);
        p.setFlags(Paint.ANTI_ALIAS_FLAG);

        Canvas c = new Canvas(result);
        c.drawBitmap(bit1, 0, 0, p);
        if(!mode){
            c.drawBitmap(bit2, bit1.getWidth(), 0, p);
        }
        else{
            c.drawBitmap(bit2, 0, bit1.getHeight(), p);
        }

        return result;
    }
}
