package com.example.home.secretary_kim;

import android.graphics.Bitmap;

import com.google.vr.sdk.widgets.pano.VrPanoramaView;

public class PANORAMAVIEW {
    //파노라마뷰 호출하는거랑 만들어놓을테니까 들고가서 붙여넣으렴..
    //팝업 레이아웃에 추가하는건 vr_layout.xml 안에꺼 그대로 들고가서 쓰면 됨
    //setcontentview 안 해놔서 레이아웃에서 못 들고오는거임..

    public void set(){
        VrPanoramaView panoramaView;
        Bitmap bmp = null;  //다운받아온 비트맵으로 변경하기

        panoramaView = (VrPanoramaView)findViewById(R.id.pano_view);
        panoramaView.setDisplayMode(1);
        panoramaView.loadImageFromBitmap(bmp);
    }
}
