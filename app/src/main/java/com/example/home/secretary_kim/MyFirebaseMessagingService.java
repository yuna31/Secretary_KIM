package com.example.home.secretary_kim;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;


public class MyFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = "FirebaseMsgService";

    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        //****설정 알림 on/off로 옮기기
        DBnotification NotiOnOff = new DBnotification(MyFirebaseMessagingService.this, "NotiOnOff.db", null, 1);
        SQLiteDatabase dbNoti;

        dbNoti = NotiOnOff.getWritableDatabase();
        NotiOnOff.onCreate(dbNoti);
        ContentValues values = new ContentValues();
        values.put("id", 1);
        values.put("OnOff", "ON");
        //values.put("OnOff", "OFF");
        dbNoti.insert("NotiOnOff", null,values);
        //dbNoti.update("NotiOnOff", values, "1", null);
        //dbNoti.delete("NotiOnOff", "1", null);
        String notification = NotiOnOff.getResult();
        System.out.println("noti On/Off : " + notification);

        if(notification.equals("ON")) {
            sendNotification(remoteMessage.getData().get("message"));
        }
    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, S3DownloadActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("문의가 접수되었습니다.") // 이부분은 어플 켜놓은 상태에서 알림 메세지 받으면 저 텍스트로 띄워준다.
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}
