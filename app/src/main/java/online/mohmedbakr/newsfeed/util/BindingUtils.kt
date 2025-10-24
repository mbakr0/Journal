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
import java.util.Date

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

fun calculateDate(publicationDate: String): String {

    val date = Date(publicationDate)
    val current = Date()
    val seconds = (current.time - date.time) / 1000


    if (seconds < 60)
        return "$seconds Seconds ago"
    else {
        val minutes = seconds / 60
        return if (minutes < 60)
            "$minutes Minutes ago"
        else {
            val hours = minutes / 60
            if (hours < 24)
                "$hours Hours ago"
            else {
                val days = hours / 24
                if (days < 7)
                    "$days Days ago"
                else {
                    val weeks = days / 7
                    if (weeks < 4)
                        "$weeks Weeks ago"
                    else {
                        val month = weeks / 4
                        if (month < 12)
                            "$month Month ago"
                        else {
                            val year = month / 12
                            "$year Years ago"
                        }
                    }
                }
            }
        }
    }
}