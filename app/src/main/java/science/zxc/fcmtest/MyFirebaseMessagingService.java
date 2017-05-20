package science.zxc.fcmtest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Calendar;


/**
 * Created by Taosky on 2017/2/24 0024.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {


    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and normal_notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated normal_notification is displayed.
        // When the user taps on the normal_notification they are returned to the app. Messages containing both normal_notification
        // and data payloads are treated as normal_notification messages. The Firebase console always sends normal_notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        //Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }
       /* // Check if message contains a normal_notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }*/


        int nowConCode;
        if (remoteMessage.getData().get("cond_code") != null) {
            nowConCode = Integer.parseInt(remoteMessage.getData().get("cond_code"));
        } else {
            nowConCode = 0;
        }

        int icon = getIcon(nowConCode);

        RemoteViews normalRemoteViewsNow = new RemoteViews(getPackageName(), R.layout.normal_notification);
        normalRemoteViewsNow.setTextViewText(R.id.normal_city, remoteMessage.getData().get("city"));
        normalRemoteViewsNow.setTextViewText(R.id.normal_info, remoteMessage.getData().get("info"));
        normalRemoteViewsNow.setTextViewText(R.id.nomarl_time, remoteMessage.getData().get("now_time"));
        normalRemoteViewsNow.setImageViewResource(R.id.normal_icon, icon);

        RemoteViews bigRemoteViewsNow = new RemoteViews(getPackageName(), R.layout.big_notification);
        bigRemoteViewsNow.setTextViewText(R.id.normal_city, remoteMessage.getData().get("city"));
        bigRemoteViewsNow.setTextViewText(R.id.normal_info, remoteMessage.getData().get("info"));
        bigRemoteViewsNow.setTextViewText(R.id.nomarl_time, remoteMessage.getData().get("now_time"));
        bigRemoteViewsNow.setImageViewResource(R.id.normal_icon, icon);
        bigRemoteViewsNow.setTextViewText(R.id.big_tmp, remoteMessage.getData().get("day_tmp"));
        bigRemoteViewsNow.setTextViewText(R.id.big_wind, remoteMessage.getData().get("day_wind"));
        bigRemoteViewsNow.setTextViewText(R.id.big_sundown, remoteMessage.getData().get("day_sun_down"));
        bigRemoteViewsNow.setTextViewText(R.id.big_pop, remoteMessage.getData().get("day_pop"));
        bigRemoteViewsNow.setTextViewText(R.id.big_hum, remoteMessage.getData().get("day_hum"));

        sendNotification(normalRemoteViewsNow, bigRemoteViewsNow);


    }

    private int getIcon(int nowConCode) {
        int iconId = 0;
        /*获取当前时间的并判断白天晚上*/
        long time = System.currentTimeMillis();
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(time);
        int mHour = mCalendar.get(Calendar.HOUR_OF_DAY);//HOUR为12小时m,HOUR_OF_DAY为24小时
        Log.d("MyService", "mhour = " + mHour);
        boolean isDay;
        isDay = !(mHour >= 18 || mHour < 6);


        if (nowConCode == 100) {
            if (isDay) {
                iconId = R.drawable.sunny;
            } else {
                iconId = R.drawable.clear;
            }
        } else if (nowConCode == 101) {
            if (isDay) {
                iconId = R.drawable.mostlycloudy_d;
            } else {
                iconId = R.drawable.mostlycloudy_n;
            }
        } else if (nowConCode > 101 && nowConCode <= 104) {
            if (isDay) {
                iconId = R.drawable.mostlycloudy_d;
            } else {
                iconId = R.drawable.mostlycloudy_n;
            }
        } else if (nowConCode > 104 && nowConCode < 214) {
            iconId = R.drawable.cloudy;
        } else if (nowConCode >= 300 && nowConCode < 313) {
            switch (nowConCode) {
                case 300:
                    if (isDay) {
                        iconId = R.drawable.chanceofrain_d;
                    } else {
                        iconId = R.drawable.chanceofrain_n;
                    }
                    break;
                default:
                    iconId = R.drawable.rain;
                    break;
            }
        } else if (nowConCode >= 313 && nowConCode <= 407) {
            switch (nowConCode) {
                case 313:
                case 404:
                case 405:
                case 406:
                    iconId = R.drawable.icyrain;
                    break;
                case 407:
                    if (isDay) {
                        iconId = R.drawable.chanceofsnow_d;
                    } else {
                        iconId = R.drawable.chanceofsnow_n;
                    }
                    break;
                default:
                    iconId = R.drawable.snow;
                    break;
            }
        } else if (nowConCode >= 500) {
            switch (nowConCode) {
                case 500:
                    if (isDay) {
                        iconId = R.drawable.mist_d;
                    } else {
                        iconId = R.drawable.mist_n;
                    }
                    break;
                case 501:
                case 502:
                    iconId = R.drawable.fog;
                    break;
                default:
                    iconId = R.drawable.unknown;
                    break;
            }
        }
        return iconId;
    }


    private void sendNotification(RemoteViews normalRemoteViews, RemoteViews bigRemoteViews) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle("title")
                .setContentText("text")
                .setOngoing(true)
                .setContent(normalRemoteViews)
                .setContentIntent(pendingIntent);

        Notification notification = notificationBuilder.build();
        notification.bigContentView = bigRemoteViews;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(1, notification);
    }
}