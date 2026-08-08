package online.mohmedbakr.newsfeed.util;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.databinding.BindingAdapter;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class BindingAdapters {

    @BindingAdapter("android:setTitle")
    public static void setTitle(TextView view, String title) {
        if (title != null) {
            view.setText(title);
        }
    }

    @BindingAdapter("android:setDescription")
    public static void setDescription(TextView view, String description) {
        if (description != null) {
            view.setText(description);
        }
    }

    @BindingAdapter(value = {"android:setImage", "android:setProgressColor"}, requireAll = false)
    public static void setImage(ImageView view, String imageLink, Integer progressColor) {
        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(view.getContext());
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        if (progressColor != null) {
            circularProgressDrawable.setColorSchemeColors(progressColor);
        }
        circularProgressDrawable.start();

        if (imageLink != null) {
            Glide.with(view.getContext())
                    .load(imageLink)
                    .override(150, 100)
                    .placeholder(circularProgressDrawable)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            view.setVisibility(View.GONE);
                            return true;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    }).into(view);
        }
    }

    @SuppressLint("ResourceAsColor")
    @BindingAdapter("android:setDate")
    public static void setDate(TextView view, String publicationDate) {
        view.setText(BindingUtilsKt.calculateDate(publicationDate));
    }

    @BindingAdapter("android:fadeVisible")
    public static void setFadeVisible(View view, Boolean visible) {
        boolean isVisible = visible != null && visible;
        if (view.getTag() == null) {
            view.setTag(true);
            view.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        } else {
            view.animate().cancel();
            if (isVisible) {
                if (view.getVisibility() == View.GONE) {
                    ExtentionKt.fadeIn(view);
                }
            } else {
                if (view.getVisibility() == View.VISIBLE) {
                    ExtentionKt.fadeOut(view);
                }
            }
        }
    }
}
