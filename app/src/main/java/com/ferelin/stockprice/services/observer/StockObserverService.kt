package com.ferelin.stockprice.services.observer

/*
 * Copyright 2021 Leah Nichita
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.ferelin.stockprice.R
import com.ferelin.stockprice.notification.Notification

/**
 * [StockObserverService] is a foreground service that shows the price of a
 * favourite company in real time
 */
internal class StockObserverService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.getStringExtra(KEY_STOP) != null) {
            stopService()
            return START_NOT_STICKY
        }

        val companyName = intent?.getStringExtra(KEY_COMPANY_NAME_STR) ?: ""
        val priceStr = intent?.getStringExtra(KEY_PRICE_STR) ?: ""
        val profitStr = intent?.getStringExtra(KEY_PROFIT_STR) ?: ""
        val profitTextColorRes = intent?.getIntExtra(KEY_PROFIT_TEXT_COLOR_RES, 0)!!

        // To avoid RemoteServiceException
        val notification = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            val packageName = resources.getResourcePackageName(R.layout.item_notification_observer)
            val notificationLayout = RemoteViews(packageName, R.layout.item_notification_observer)
            notificationLayout.setTextViewText(R.id.textViewCompanyName, companyName)
            notificationLayout.setTextViewText(R.id.textViewCurrentPrice, priceStr)
            notificationLayout.setTextViewText(R.id.textViewDayProfit, profitStr)
            notificationLayout.setTextColor(R.id.textViewDayProfit, profitTextColorRes)

            NotificationCompat.Builder(this, Notification.PRICE_OBSERVER_CHANNEL_ID)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContent(notificationLayout)
                .build()
        } else {
            NotificationCompat.Builder(this, Notification.PRICE_OBSERVER_CHANNEL_ID)
                .setContentTitle(companyName)
                .setContentText(priceStr)
                .setSmallIcon(R.drawable.ic_favourite_active)
                .build()
        }

        startForeground(1, notification)
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun stopService() {
        val notification = NotificationCompat.Builder(this, Notification.PRICE_OBSERVER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_favourite_active)
            .build()
        startForeground(1, notification)
        stopForeground(true)
        stopSelf()
    }

    companion object {
        const val KEY_COMPANY_NAME_STR = "company"
        const val KEY_PRICE_STR = "price"
        const val KEY_PROFIT_STR = "profit"
        const val KEY_PROFIT_TEXT_COLOR_RES = "color"
        const val KEY_STOP = "stop"
    }
}