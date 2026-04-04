package online.mohmedbakr.newsfeed.util

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import androidx.core.view.isGone
import androidx.core.view.isVisible
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

@BindingAdapter("android:setTitle")
fun TextView.setTitle(title: String) {
    title.let {
        text = it
    }
}

@BindingAdapter("android:setDescription")
fun TextView.setDescription(description: String) {
    description.let {
        text = it
    }
}

@BindingAdapter(value = ["android:setImage", "android:setProgressColor"], requireAll = false)
fun ImageView.setImage(imageLink: String, progressColor: Int? = null) {
    val circularProgressDrawable = CircularProgressDrawable(this.context)
    circularProgressDrawable.strokeWidth = 5f
    circularProgressDrawable.centerRadius = 30f
    progressColor?.let { circularProgressDrawable.setColorSchemeColors(it) }
    circularProgressDrawable.start()
    imageLink.let {
        Glide.with(this.context)
            .load(imageLink)
            .override(150, 100)
            .placeholder(circularProgressDrawable)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean,
                ): Boolean {
                    this@setImage.visibility = View.GONE
                    return true
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean,
                ): Boolean {
                    return false
                }
            }).into(this)

    }
}

@SuppressLint("ResourceAsColor")
@BindingAdapter("android:setDate")
fun TextView.setDate(publicationDate: String) {
    publicationDate.let {
        text = calculateDate(it)
    }
}

@BindingAdapter("android:fadeVisible")
fun setFadeVisible(view: View, visible: Boolean = true) {
    if (view.tag == null) {
        view.tag = true
        view.visibility = if (visible) View.VISIBLE else View.GONE
    } else {
        view.animate().cancel()
        if (visible) {
            if (view.isGone)
                view.fadeIn()
        } else {
            if (view.isVisible)
                view.fadeOut()
        }
    }
}


fun calculateDate(publicationDate: String?): String {
    if (publicationDate.isNullOrBlank()) return "Unknown"

    return try {
        val published = parseDate(publicationDate)
        val now = Instant.now()

        val seconds = Duration.between(published, now).seconds

        when {
            seconds < 60 -> format(seconds, "second")
            seconds < 3600 -> format(seconds / 60, "minute")
            seconds < 86400 -> format(seconds / 3600, "hour")
            seconds < 604800 -> format(seconds / 86400, "day")
            seconds < 2419200 -> format(seconds / 604800, "week")
            seconds < 29030400 -> format(seconds / 2419200, "month")
            else -> format(seconds / 29030400, "year")
        }

    } catch (_: Exception) {
       publicationDate
    }
}

fun parseDate(date: String): Instant {
    val rfcFormatter = DateTimeFormatter.RFC_1123_DATE_TIME.withLocale(Locale.ENGLISH)

    return try {
        ZonedDateTime.parse(date, rfcFormatter).toInstant()
    } catch (_: DateTimeParseException) {
        try {
            val usFormatter = DateTimeFormatter.ofPattern("M/d/yyyy h:mm:ss a", Locale.ENGLISH)
            val local = LocalDateTime.parse(date, usFormatter)
            local.atZone(ZoneId.systemDefault()).toInstant()
        } catch (_: Exception) {
            throw IllegalArgumentException("Unsupported date format: $date")
        }
    }
}

private fun format(value: Long, unit: String): String {
    return if (value == 1L) "$value $unit ago"
    else "$value ${unit}s ago"
}