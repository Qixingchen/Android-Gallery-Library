package moe.xing.gallery;

import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

/**
 * Created by Qi Xingchen on 16-11-3.
 * <p>
 * 图片查看器
 */

class GalleryAdapter extends PagerAdapter {
    @DrawableRes
    int holder;
    @Nullable
    private List<String> pics;
    @Nullable
    private List<Integer> picsRes;
    private boolean allowZoom;

    GalleryAdapter(@Nullable List<String> pics, @Nullable List<Integer> picsRes, @DrawableRes int holder, boolean allowZoom) {
        this.pics = pics;
        this.picsRes = picsRes;
        this.holder = holder;
        this.allowZoom = allowZoom;
    }

    @Override
    public int getCount() {
        if (picsRes != null) {
            return picsRes.size();
        } else if (pics != null) {
            return pics.size();
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {

        View imageView = LayoutInflater.from(container.getContext()).inflate(R.layout.item_big_pic, container, false);
        final SubsamplingScaleImageView scaleImageView = imageView.findViewById(R.id.pic);
        if (scaleImageView == null) {
            return imageView;
        }
        scaleImageView.setImage(ImageSource.resource(holder));
        scaleImageView.setPanEnabled(allowZoom);
        scaleImageView.setZoomEnabled(allowZoom);

        if (pics != null) {


            final FutureTarget<File> future = Glide.with(container.getContext())
                    .load(pics.get(position))
                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        final File cacheFile = future.get();
                        scaleImageView.post(new Runnable() {
                            @Override
                            public void run() {
                                scaleImageView.setImage(ImageSource.uri(Uri.fromFile(cacheFile)));
                            }
                        });

                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (picsRes != null) {
            scaleImageView.setImage(ImageSource.resource(picsRes.get(position)));
        }
        container.addView(imageView);
        return imageView;
    }

}
