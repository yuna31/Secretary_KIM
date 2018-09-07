package com.example.home.secretary_kim;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * Created by s0woo on 2018-08-20.
 */

public class S3DownloadActivity extends AppCompatActivity {

    ImageView imageView;
    Bitmap bmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s3download);

        ImageView imageView = (ImageView) findViewById(R.id.bitmapView);

        new Thread() {
            public void run() {
                CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    getApplicationContext(),
                    "ap-northeast-2:29422a03-b373-4e0a-85e7-4c1a9a28d16d", // 자격 증명 풀 ID
                    Regions.AP_NORTHEAST_2 // 리전
                );

                final AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
                TransferUtility transferUtility = new TransferUtility(s3, getApplicationContext());

                s3.setRegion(Region.getRegion(Regions.AP_NORTHEAST_2));
                s3.setEndpoint("s3.ap-northeast-2.amazonaws.com");

                /*
                GetObjectRequest getObjectRequest = new GetObjectRequest(
                        "s0woo",
                        OBJECT_KEY
                );

                GetObjectMetadataRequest getObjectMetadataRequest = new GetObjectMetadataRequest(
                        "s0woo",
                        OBJECT_KEY
                );
                */

                S3Object s3Object = s3.getObject("s0woo", "first file");

                try (InputStream is = s3Object.getObjectContent()) {
                    byte[] bytes = IOUtils.toByteArray(is);
                    System.out.println("********************download bytearray : " + bytes);
                    System.out.println("********************temp length : " + bytes.length);
                    bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                    //UI를 변경하기 위한 Thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ImageView imageView = (ImageView) findViewById(R.id.bitmapView);
                            imageView.setImageBitmap(bmp);
                        }
                    });

                    is.close();
                } catch (IOException e){
                    e.printStackTrace();
                } catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        }.start();


        /*
        TransferObserver observer = transferUtility.download(
                "s0woo",     // 업로드 할 버킷 이름
                OBJECT_KEY,    // 버킷에 저장할 파일의 이름
                MY_FILE        // 버킷에 저장할 파일
        );

        transferUtility.download(
                Path.Combine(Environment.SpecialFolder.ApplicationData, "file"),
        "s0woo",
        "key"
        );


        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {

            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

            }

            @Override
            public void onError(int id, Exception ex) {
                ex.printStackTrace();
            }
        });
        */

    }

}
