package com.example.home.secretary_kim.VR;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

public class ImageActivity {
    private static final String TAG = "ImageActivity";
    private Bitmap imgArray[];
    private static Bitmap img;

    public ImageActivity(){
        imgArray = null;
        this.img = null;
    }

    public ImageActivity(Bitmap[] img){
        imgArray = img;
        this.img = null;
    }

    //3:2 비율 이미지에 좌표 그리기 8*6
    public Bitmap drawBoard(Bitmap tmp, int cnt){
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
        Paint p1 = new Paint();
        p1.setStrokeWidth(1f);
        p1.setColor(Color.YELLOW);
        p1.setStyle(Paint.Style.STROKE);
        Paint p2 = new Paint();
        p2.setColor(Color.DKGRAY);
        p2.setTextAlign(Paint.Align.CENTER);
        p2.setTextSize(12);
        //paint 색 변경하기

        canvas.drawBitmap(bit, 0,0, p1);
        for(int i = 1; i <= 6; i++){
            canvas.drawLine(0,i*hLength, width, i*hLength, p1);
            float ycoor = (hLength*i) - (hLength*2/3);
            //drawText for문
            for(int j = 0; j < 8; j++){
                float xcoor = (wLength/2) + (wLength*j);
                int t = cnt*100 + (i-1)*10 + j+1;
                canvas.drawText(Integer.toString(t), xcoor, ycoor, p2);
            }
        }
        for(int i = 1; i <= 8; i++){
            canvas.drawLine(i*wLength, 0, i*wLength, height, p1);
        }
        canvas.drawLine(width-1f, 0, width-1f, height, p1);

        return result;
    }

    //비트맵 이미지 4장 연결
    public Bitmap makePanorama(){
        for(int i = 0; i < 4; i++){
            imgArray[i] = drawBoard(imgArray[i], i+1);   //좌표판, 숫자 생성
        }

        Bitmap tmp = imgArray[0];

        for(int i = 0; i < 3; i++){
            tmp = panomaraImg(tmp, imgArray[i+1], false);
        }

        tmp = panomaraImg(tmp, tmp, true);

        img = tmp;

        return img;
    }

    //비트맵 이미지 2장 연결
    public Bitmap panomaraImg(Bitmap  bit1, Bitmap bit2, boolean mode){
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

    //사각형 그리기
    public Bitmap drawRect(Bitmap tmp, String r1, String r2){
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

        float[] coorR1 = detachInt(r1, width, height, wLength, hLength);
        float[] coorR2 = detachInt(r2, width, height, wLength, hLength);

        result = Bitmap.createScaledBitmap(bit, bit.getWidth(), bit.getHeight(), true);
        Canvas canvas = new Canvas(result);
        Paint p = new Paint();
        p.setStrokeWidth(3f);
        p.setColor(Color.RED);
        canvas.drawRect(coorR1[0], coorR1[1], coorR1[2], coorR1[3], p);
        canvas.drawRect(coorR2[0], coorR2[1], coorR2[2], coorR2[3], p);

        return result;
    }

    //사각형 좌표 찾기
    public float[] detachInt(String str, float width, float height, float wLength, float hLength){
        int[] strint = new int[3];
        float[] result = new float[4];
        int tmp = Integer.parseInt(str);

        strint[0] = tmp / 100;  //100 자리 -> 사진 번호
        strint[1] = (tmp - strint[0]) / 10; //10 자리 -> 가로 줄 번호
        strint[2] = tmp - strint[0] - strint[1];    //1 자리 -> 세로 줄 번호

        result[0] = (strint[0]-1)*width+(strint[2]-1)*wLength;
        result[1] = strint[1]*hLength;
        result[2] = result[0] + wLength;
        result[3] = result[1] + hLength;

        return result;
    }
}
