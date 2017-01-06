package moe.xing.gallery;

import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.List;

import moe.xing.gallery.databinding.ItemBigPicBinding;

/**
 * Created by Qi Xingchen on 16-11-3.
 * <p>
 * 图片查看器
 */

class GalleryAdapter extends PagerAdapter {
    @Nullable
    private List<String> pics;

    @Nullable
    private List<Integer> picsRes;

    GalleryAdapter(@Nullable List<String> pics, @Nullable List<Integer> picsRes) {
        this.pics = pics;
        this.picsRes = picsRes;
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
    public Object instantiateItem(ViewGroup container, final int position) {
        ItemBigPicBinding picBinding = DataBindingUtil.inflate(LayoutInflater.from(container.getContext()), R.layout.item_big_pic, container, false);
        if (pics != null) {
            Glide.with(container.getContext()).load(pics.get(position)).placeholder(R.drawable.img_holder).fitCenter().into(picBinding.pic);
        } else if (picsRes != null) {
            Glide.with(container.getContext()).load(picsRes.get(position)).fitCenter().into(picBinding.pic);
        }
        container.addView(picBinding.getRoot());
        return picBinding.getRoot();
    }

}
