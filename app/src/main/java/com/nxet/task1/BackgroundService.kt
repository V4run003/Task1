package com.nxet.task1

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class BackgroundService : AccessibilityService() {
    var socialList = ArrayList<String>()
    private var startTime: Long = 0
    private var diff: Long = 0
    private var change: String = ""
    private var i: Int = 0
    private var USETIME_PREF = "usetimeprefs"

    override fun onServiceConnected() {
        super.onServiceConnected()

        val config = AccessibilityServiceInfo()
        config.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
        config.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
        config.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS
        serviceInfo = config
        socialList.add("com.instagram.android")
        socialList.add("com.twitter.android")
        socialList.add("com.whatsapp")
        socialList.add("com.snapchat.android")


    }

    @SuppressLint("SimpleDateFormat")
    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if (event.packageName != null && event.className != null) {
                val componentName = ComponentName(
                    event.packageName.toString(),
                    event.className.toString()
                )


                val activityInfo = tryGetActivity(componentName)
                val isActivity = activityInfo != null
                if (isActivity) {
                    val name = componentName.flattenToShortString()
                    val separated: Array<String> = name.split("/").toTypedArray()
                    val currentActivity = separated[0]
                    if (currentActivity != change) {
                        change = currentActivity
                        if (socialList.contains(currentActivity)) {
                            val prefs = getSharedPreferences(USETIME_PREF, MODE_PRIVATE)
                            val isfirst = prefs.getBoolean(
                                currentActivity,
                                false
                            )
                            var timesOpened = prefs.getInt(
                                "$currentActivity+timesopened",
                                0
                            )
                            if (!isfirst) {
                                val df: DateFormat = SimpleDateFormat(" dd/mm/yyyy  HH:mm")
                                val date: String = df.format(Calendar.getInstance().time)
                                val editor = getSharedPreferences(USETIME_PREF, MODE_PRIVATE).edit()
                                editor.putBoolean(currentActivity, true)
                                editor.putInt("$currentActivity+timesopened", 1)
                                editor.putString("$currentActivity+used", date)
                                editor.apply()
                                Toast.makeText(this, "First time opened", Toast.LENGTH_SHORT).show()
                            } else {
                                val prefs = getSharedPreferences(USETIME_PREF, MODE_PRIVATE)
                                val date = prefs.getString(
                                    "$currentActivity+used",
                                    "null"
                                )
                                val opened = prefs.getInt(
                                    "$currentActivity+timesopened",
                                    0
                                )
                                val editor = getSharedPreferences(USETIME_PREF, MODE_PRIVATE).edit()
                                val df2: DateFormat = SimpleDateFormat(" dd/mm/yyyy  HH:mm")
                                val date2: String = df2.format(Calendar.getInstance().time)
                                editor.putString("$currentActivity+used", date2)
                                editor.putInt("$currentActivity+timesopened", opened + 1)
                                editor.apply()
                                Toast.makeText(
                                    this,
                                    "opened before at $date total $opened times opened",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }

                    }

                }


            }
        }
    }

    private fun tryGetActivity(componentName: ComponentName): ActivityInfo? {
        return try {
            packageManager.getActivityInfo(componentName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

    override fun onInterrupt() {}
}