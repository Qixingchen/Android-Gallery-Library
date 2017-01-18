package moe.xing.gallery;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.List;

import moe.xing.gallery.databinding.ActivityGalleryBinding;

/**
 * Created by Qi Xingchen on 16-11-3.
 * <p>
 * 图片查看
 */

public class GalleryActivity extends AppCompatActivity {
    private static final String PICS = "PICS";
    private static final String PICSRES = "PICSRES";
    private static final String NEEDCLOSE = "NEEDCLOSE";
    private static final String POSITION = "POSITION";
    private ActivityGalleryBinding mBinding;

    public static Intent startIntent(Context context, ArrayList<String> pics) {
        return startIntent(context, pics, 0);
    }

    public static Intent startIntent(Context context, ArrayList<String> pics, int position) {
        Intent intent = new Intent(context, GalleryActivity.class);
        intent.putStringArrayListExtra(PICS, pics);
        intent.putExtra(POSITION, position);
        return intent;
    }

    public static Intent startIntent(Context context, String pic) {
        ArrayList<String> pics = new ArrayList<>();
        pics.add(pic);
        return startIntent(context, pics);
    }

    /**
     * @param needClose 是否需要在最后一张点击退出页面
     */
    public static Intent startIntent(Context context, ArrayList<Integer> picsRes, boolean needClose) {
        Intent intent = new Intent(context, GalleryActivity.class);
        intent.putIntegerArrayListExtra(PICSRES, picsRes);
        intent.putExtra(NEEDCLOSE, needClose);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_gallery, null, false);
        setContentView(mBinding.getRoot());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        List<String> pics = intent.getStringArrayListExtra(PICS);
        List<Integer> picsRes = intent.getIntegerArrayListExtra(PICSRES);
        final boolean needClose
                = getIntent().getBooleanExtra(NEEDCLOSE, false);

        GalleryAdapter galleryAdapter = new GalleryAdapter(pics, picsRes);
        final int size = galleryAdapter.getCount();
        mBinding.picsViewPager.setAdapter(galleryAdapter);
        mBinding.picsViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (needClose && position == size - 1) {
                    setResult(RESULT_OK);
                    finish();
                }
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        int position = getIntent().getIntExtra(POSITION, 0);
        mBinding.picsViewPager.setCurrentItem(position);
    }
}
