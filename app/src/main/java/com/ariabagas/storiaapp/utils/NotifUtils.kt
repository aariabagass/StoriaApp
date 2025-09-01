package com.ariabagas.storiaapp.utils

import android.app.Activity
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.ariabagas.storiaapp.R

object NotifUtils {
    fun show(
        activity: Activity,
        message: String,
        isError: Boolean = false,
        duration: Long = 3000L
    ) {
        val rootView = activity.findViewById<ViewGroup>(android.R.id.content)

        val banner = activity.layoutInflater.inflate(R.layout.custom_top_popup, rootView, false)
        val textView = banner.findViewById<TextView>(R.id.bannerText)
        val icon = banner.findViewById<ImageView>(R.id.bannerIcon)

        textView.text = message

        val bg: Drawable? =
            ContextCompat.getDrawable(activity, R.drawable.container_white_rounded)?.mutate()
        bg?.setTint(
            if (isError) ContextCompat.getColor(activity, R.color.bg_surface)
            else ContextCompat.getColor(activity, R.color.bg_surface)
        )
        banner.background = bg

        icon.setImageResource(if (isError) R.drawable.ic_error else R.drawable.ic_done)

        banner.translationY = -banner.measuredHeight.toFloat()
        rootView.addView(banner, 0)

        banner.post {
            banner.translationY = -banner.height.toFloat()
            banner.animate()
                .translationY(0f)
                .setDuration(300)
                .start()
        }

        banner.postDelayed({
            banner.animate()
                .translationY(-banner.height.toFloat())
                .setDuration(300)
                .withEndAction { rootView.removeView(banner) }
                .start()
        }, duration)
    }
}
