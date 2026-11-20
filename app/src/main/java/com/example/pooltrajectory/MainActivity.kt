package com.example.pooltrajectory

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    private var statusText: TextView? = null
    private val requestCodeCapture = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(28, 36, 28, 36)
        }
        val title = TextView(this).apply {
            text = "Pool Trajectory\\nПрототип для Android 16"
            textSize = 23f
            gravity = Gravity.CENTER
        }
        statusText = TextView(this).apply {
            text = "Этап 1: проверка захвата экрана и прозрачного окна. Автораспознавание шаров ещё не подключено."
            textSize = 16f
            gravity = Gravity.CENTER
            setPadding(0, 24, 0, 24)
        }
        val overlayButton = Button(this).apply {
            text = "1. Разрешить отображение поверх приложений"
            setOnClickListener {
                if (!Settings.canDrawOverlays(this@MainActivity)) {
                    startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")))
                } else statusText?.text = "Разрешение оверлея уже выдано."
            }
        }
        val startButton = Button(this).apply {
            text = "2. Запустить тест захвата экрана"
            setOnClickListener {
                if (!Settings.canDrawOverlays(this@MainActivity)) {
                    statusText?.text = "Сначала разреши отображение поверх других приложений."
                    return@setOnClickListener
                }
                val manager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
                @Suppress("DEPRECATION")
                startActivityForResult(manager.createScreenCaptureIntent(), requestCodeCapture)
            }
        }
        val stopButton = Button(this).apply {
            text = "Остановить"
            setOnClickListener {
                stopService(Intent(this@MainActivity, CaptureOverlayService::class.java))
                statusText?.text = "Остановлено."
            }
        }
        layout.addView(title)
        layout.addView(statusText)
        layout.addView(overlayButton)
        layout.addView(startButton)
        layout.addView(stopButton)
        setContentView(layout)
    }

    @Deprecated("Deprecated by Android; retained for broad platform compatibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestCodeCapture) {
            if (resultCode == RESULT_OK && data != null) {
                val service = Intent(this, CaptureOverlayService::class.java).apply {
                    action = CaptureOverlayService.ACTION_START
                    putExtra(CaptureOverlayService.EXTRA_RESULT_CODE, resultCode)
                    putExtra(CaptureOverlayService.EXTRA_RESULT_DATA, data)
                }
                if (Build.VERSION.SDK_INT >= 26) startForegroundService(service) else startService(service)
                statusText?.text = "Захват запущен. Вернись в игру через кнопку «Недавние»."
            } else statusText?.text = "Захват экрана не разрешён."
        }
    }
}
