package com.nxet.task1


import android.R.attr.thumb
import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.accessibility.AccessibilityManager
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.nxet.task1.Interfaces.RecyclerItemClick
import com.nxet.task1.Models.AppModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), RecyclerItemClick {

    private var apps = ArrayList<AppModel>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var accessibilityToggle: SwitchMaterial
    var socialList = ArrayList<String>()
    private lateinit var nocontent: RelativeLayout



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        socialList.add("com.instagram.android")
        socialList.add("com.twitter.android")
        socialList.add("com.whatsapp")
        socialList.add("com.snapchat.android")


        recyclerView = findViewById(R.id.main_recyclerView)
        accessibilityToggle = findViewById(R.id.accessibility_service_toggle)
        val enabled = isAccessibilityServiceEnabled(this, BackgroundService::class.java)
        Log.e("check", enabled.toString())
        accessibilityToggle.isChecked = enabled
        nocontent = findViewById(R.id.nocontent)

        accessibilityToggle.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!enabled) {
                val openSettings = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                openSettings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY)
                startActivity(openSettings)
            }

        }


        if (!enabled) {

            AlertDialog.Builder(this)
                .setTitle("Turn on Accessibility service")
                .setMessage("please turn on accessiblity service for Task1 app from the following screen")
                .setPositiveButton("Continue") { dialog, which ->
                    val openSettings = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    openSettings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY)
                    startActivity(openSettings)
                }
                .setNegativeButton("Cancel", null).show()

        }

        getInstalledApps()


    }

    private fun setRecyclerView(appArray: ArrayList<AppModel>) {
        recyclerView.layoutManager = LinearLayoutManager(this)
        val viewAdapter = AppAdapter(this, appArray, this)
        recyclerView.adapter = viewAdapter
        recyclerView.visibility = View.VISIBLE
        nocontent.visibility = View.GONE

    }


    private fun getInstalledApps() {
        recyclerView.visibility = View.GONE
        nocontent.visibility = View.VISIBLE

        lifecycleScope.launch {
            val operation = async(Dispatchers.Default) {
                val packs = packageManager.getInstalledPackages(0)
                for (items in packs) {
                    val packageName = items.packageName
                    val appName = packageManager.getApplicationLabel(
                        packageManager.getApplicationInfo(
                            packageName,
                            PackageManager.GET_META_DATA
                        )
                    ) as String
                    val appIcon = packageManager.getApplicationIcon(
                        packageManager.getApplicationInfo(
                            packageName,
                            PackageManager.GET_META_DATA
                        )
                    )
                    if (socialList.contains(packageName)) {
                        apps.add(AppModel(appName, packageName, appIcon))
                    }

                }
            }

            operation.await()
            if (!apps.isNullOrEmpty()) {
                setRecyclerView(apps)
            }
        }


    }

    override fun OnClick(packageName: String) {
        val i = Intent(this@MainActivity, DetailActivity::class.java)
        i.putExtra("package", packageName)
        startActivity(i)
        Log.e("clicked", packageName)
    }


    private fun isAccessibilityServiceEnabled(
        context: Context,
        service: Class<out AccessibilityService?>
    ): Boolean {
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices =
            am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        for (enabledService in enabledServices) {
            val enabledServiceInfo: ServiceInfo = enabledService.resolveInfo.serviceInfo
            if (enabledServiceInfo.packageName.equals(context.packageName) && enabledServiceInfo.name.equals(
                    service.name
                )
            ) return true
        }
        return false
    }

    override fun onPause() {
        super.onPause()

    }

    override fun onResume() {
        super.onResume()

        val enabled = isAccessibilityServiceEnabled(this, BackgroundService::class.java)
        accessibilityToggle.isChecked = enabled


        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                Log.d("used", intent.extras!!.getString("usedagain")!!)
                Log.d("used", intent.extras!!.getString("usedagaintime")!!)
                setNotification(intent.extras!!.getString("usedagain")!!,intent.extras!!.getString("usedagaintime")!!)
            }
        }, IntentFilter(packageName + "service_used"))


    }



    private fun setNotification(string: String, string1: String) {
        val intent = Intent(applicationContext, MainActivity::class.java)
        val pIntent = PendingIntent.getActivity(
            applicationContext,
            System.currentTimeMillis().toInt(), intent, 0
        )

        val appName = packageManager.getApplicationLabel(
            packageManager.getApplicationInfo(
                string,
                PackageManager.GET_META_DATA
            )
        ) as String

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel1 =
                NotificationChannel("Channel 1", "Channel(1)", NotificationManager.IMPORTANCE_HIGH)
            channel1.description = "Channel 1 Dec.."
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel1)
        }


        val notification: NotificationCompat.Builder =
            NotificationCompat.Builder(this,"Channel 1")
                .setSmallIcon(R.drawable.ic_baseline_notifications_active_24)
                .setContentTitle("$appName opened again")
                .setContentIntent(pIntent)
                .setContentText("Last opened on $string1")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        notification.setSmallIcon(R.drawable.ic_baseline_notifications_active_24)
        notification.color = resources.getColor(R.color.white)
        val notificationManager = NotificationManagerCompat.from(
            applicationContext
        )

        notificationManager.notify(((Date().getTime() / 1000L % Int.MAX_VALUE).toInt()), notification.build())






    }







}