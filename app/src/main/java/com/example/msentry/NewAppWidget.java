package com.example.msentry;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        SharedPreferences pref = context.getSharedPreferences("image", Context.MODE_PRIVATE);
        String image = pref.getString("imageString", "");
        Bitmap bitmap = StringtoBitmap(image);
        //  Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.barcode_preview);

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
//        views.setTextViewText(R.id.new_app, widgetText);

        Intent intent = new Intent(context, MainActivity.class);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(new ComponentName(context, MainActivity.class));
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
        //views.setOnClickPendingIntent(pi);
        views.setOnClickPendingIntent(R.id.widgetView, pi);


        views.setImageViewBitmap(R.id.imageWidget, bitmap);

        if (bitmap != null) {
            views.setImageViewBitmap(R.id.imageWidget, bitmap);
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
    //  updateViews.setImageViewBitmap(R.id.widget_picture, decodeBitmapFromList(IMAGE_PATH);

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    // String 값을 Bitmap으로 전환하기
    public static Bitmap StringtoBitmap(String encodedString) {
        try {
            byte[] encodedByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodedByte, 0, encodedByte.length);
            return bitmap;

        } catch (Exception e) {
            e.getMessage();
            return null;

        }

    }

}