package com.nxet.task1

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import de.hdodenhof.circleimageview.CircleImageView


class DetailActivity : AppCompatActivity() {

    private lateinit var lastused: TextView
    private lateinit var timesUsed: TextView
    private lateinit var icon: CircleImageView
    private lateinit var name: TextView
    private lateinit var packages: String
    private var USETIME_PREF = "usetimeprefs"
    private lateinit var toolbar: MaterialToolbar

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val intent = intent
        val extras = intent.extras

        if (extras != null) {
            packages = extras.getString("package", "")

        }

        lastused = findViewById(R.id.last_used)
        timesUsed = findViewById(R.id.usedTime)
        icon = findViewById(R.id.detailview_icon)
        name = findViewById(R.id.appname_detail)
        toolbar = findViewById(R.id.toolbar_detail)
        toolbar.setNavigationOnClickListener {
            finish()
        }


        if (packages != "") {
            val appName = packageManager.getApplicationLabel(
                packageManager.getApplicationInfo(
                    packages,
                    PackageManager.GET_META_DATA
                )
            ) as String
            val appIcon = packageManager.getApplicationIcon(
                packageManager.getApplicationInfo(
                    packages,
                    PackageManager.GET_META_DATA
                )
            )

            name.text = appName
            icon.setImageDrawable(appIcon)


            val prefs = getSharedPreferences(USETIME_PREF, MODE_PRIVATE)
            val date = prefs.getString(
                "$packages+used",
                "null"
            )
            val opened = prefs.getInt(
                "$packages+timesopened",
                0
            )

            lastused.text = "Last opened : $date"
            timesUsed.text = "Times used this app : $opened"
        }


    }
}