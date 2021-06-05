package com.example.mushroom_inspector.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class ImageLoader {
    public interface ImageLoadSuccessCallback {
        void onImageLoadSuccess(final Bitmap imageBitmap);
    }

    public interface ImageLoadFailedCallback {
        void onImageLoadFailed(final Uri imageUri);
    }

    public static class PicassoTarget implements Target {
        private final Uri imageUri;
        private final ImageLoadSuccessCallback imageLoadSuccessCallback;
        private final ImageLoadFailedCallback imageLoadFailedCallback;

        public PicassoTarget(Uri imageUri, ImageLoadSuccessCallback imageLoadSuccessCallback,
                ImageLoadFailedCallback imageLoadFailedCallback) {
            this.imageUri = imageUri;
            this.imageLoadSuccessCallback = imageLoadSuccessCallback;
            this.imageLoadFailedCallback = imageLoadFailedCallback;
        }

        @Override
        public void onBitmapLoaded (final Bitmap bitmap, Picasso.LoadedFrom from){
            Bitmap resultBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false);
            imageLoadSuccessCallback.onImageLoadSuccess(resultBitmap);
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
            imageLoadFailedCallback.onImageLoadFailed(imageUri);
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    }

    public static PicassoTarget bitmapFromUri(
            final Uri imageUri,
            final ImageLoadSuccessCallback imageLoadSuccessCallback,
            final ImageLoadFailedCallback imageLoadFailedCallback) {
        PicassoTarget target = new PicassoTarget(
                imageUri,
                imageLoadSuccessCallback,
                imageLoadFailedCallback
        );

        Picasso.get()
                .load(imageUri)
                .config(Bitmap.Config.ARGB_8888)
                .into(target);

        return target;
    }
}
