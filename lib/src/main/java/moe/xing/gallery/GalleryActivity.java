package moe.xing.gallery;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import moe.xing.baseutils.Init;
import moe.xing.baseutils.utils.PXUtils;
import moe.xing.gallery.databinding.ActivityGalleryBinding;

/**
 * Created by Qi Xingchen on 16-11-3.
 * <p>
 * 图片查看
 */

@SuppressWarnings("SameParameterValue")
public class GalleryActivity extends AppCompatActivity {
    private static final String PICS = "PICS";
    private static final String PICSRES = "PICSRES";
    private static final String NEEDCLOSE = "NEEDCLOSE";
    private static final String POSITION = "POSITION";
    private static final String NEED_POINT = "NEED_POINT";
    private static final String HOLDER = "HOLDER";
    private static final String ALLOW_ZOOM = "ALLOW_ZOOM";
    private ActivityGalleryBinding mBinding;

    public static Intent startIntent(Context context, List<String> pics) {
        return startIntent(context, pics, 0);
    }

    public static Intent startIntent(Context context, List<String> pics, int position) {
        return startIntent(context, pics, position, false);
    }

    public static Intent startIntent(Context context, List<String> pics, int position, boolean needPoint) {
        return startIntent(context, pics, position, needPoint, R.drawable.img_holder);
    }

    public static Intent startIntent(Context context, List<String> pics, int position, boolean needPoint, @DrawableRes int holder) {
        return startIntent(context, pics, position, needPoint, holder, false);
    }

    /**
     * 启动的 intent
     *
     * @param context   context
     * @param pics      图片地址列表
     * @param position  当前图片位置
     * @param needPoint 是否需要位置指示器
     * @param holder    加载等待图
     */
    @SuppressWarnings("SameParameterValue")
    public static Intent startIntent(Context context, List<String> pics, int position, boolean needPoint, @DrawableRes int holder, boolean allowZoom) {
        Intent intent = new Intent(context, GalleryActivity.class);

        if (pics instanceof ArrayList) {
            intent.putStringArrayListExtra(PICS, (ArrayList<String>) pics);
        } else {
            ArrayList<String> picList = new ArrayList<>();
            for (String s : pics) {
                picList.add(s);
                intent.putStringArrayListExtra(PICS, picList);
            }
        }
        intent.putExtra(POSITION, position);
        intent.putExtra(NEED_POINT, needPoint);
        intent.putExtra(HOLDER, holder);
        intent.putExtra(ALLOW_ZOOM, allowZoom);
        return intent;
    }

    public static Intent startIntent(Context context, String pic) {
        return startIntent(context, pic, R.drawable.img_holder);
    }

    /**
     * 启动的 intent
     *
     * @param context context
     * @param pic     图片地址
     * @param holder  加载等待图
     */
    public static Intent startIntent(Context context, String pic, @DrawableRes int holder) {
        ArrayList<String> pics = new ArrayList<>();
        pics.add(pic);
        return startIntent(context, pics);
    }

    /**
     * @param needClose 是否需要在最后一张点击退出页面
     */
    public static Intent startIntent(Context context, List<Integer> picsRes, boolean needClose) {
        return startIntent(context, picsRes, needClose, false);
    }

    /**
     * 启动的 intent
     *
     * @param context   context
     * @param picsRes   图片资源列表
     * @param needPoint 是否需要位置指示器
     * @param needClose 是否需要在最后一张点击退出页面
     */
    public static Intent startIntent(Context context, List<Integer> picsRes, boolean needClose, boolean needPoint) {
        Intent intent = new Intent(context, GalleryActivity.class);
        if (picsRes instanceof ArrayList) {
            intent.putIntegerArrayListExtra(PICSRES, (ArrayList<Integer>) picsRes);
        } else {
            ArrayList<Integer> res = new ArrayList<>();
            for (Integer i : picsRes) {
                res.add(i);
            }
            intent.putIntegerArrayListExtra(PICSRES, res);
        }
        intent.putExtra(NEEDCLOSE, needClose);
        intent.putExtra(NEED_POINT, needPoint);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Init.getInstance(getApplication(), true, "1.0", "gallery_demo");
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_gallery, null, false);
        setContentView(mBinding.getRoot());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        List<String> pics = intent.getStringArrayListExtra(PICS);
        List<Integer> picsRes = intent.getIntegerArrayListExtra(PICSRES);
        int holder = getIntent().getIntExtra(HOLDER, R.drawable.img_holder);
        final boolean needClose
                = getIntent().getBooleanExtra(NEEDCLOSE, false);
        boolean allowZoom = getIntent().getBooleanExtra(ALLOW_ZOOM, false);

        GalleryAdapter galleryAdapter = new GalleryAdapter(pics, picsRes, holder, allowZoom);
        final int size = galleryAdapter.getCount();
        mBinding.picsViewPager.setAdapter(galleryAdapter);
        mBinding.picsViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (needClose && position == size - 1) {
                    setResult(RESULT_OK);
                    finish();
                } else if (getIntent().getBooleanExtra(NEED_POINT, false)) {
                    drawPointChange(size, position);
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
        if (getIntent().getBooleanExtra(NEED_POINT, false)) {
            drawPointInit(size - (getIntent().getBooleanExtra(NEEDCLOSE, false) ? 1 : 0), position);
        }
    }

    /**
     * 绘制指示点
     *
     * @param sum 图片总数
     * @param now 目前位置
     */
    private void drawPointInit(int sum, int now) {
        mBinding.point.removeAllViews();
        for (int i = 0; i < sum; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setPadding(PXUtils.dpToPx(3), PXUtils.dpToPx(6), PXUtils.dpToPx(3), PXUtils.dpToPx(6));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

//            layoutParams.setMargins(PXUtils.dpToPx(3), PXUtils.dpToPx(6), PXUtils.dpToPx(3), PXUtils.dpToPx(6));
            if (i == now) {
                imageView.setImageResource(R.drawable.point_now);
                mBinding.point.addView(imageView, layoutParams);
            } else {
                imageView.setImageResource(R.drawable.point_not_now);
                mBinding.point.addView(imageView, layoutParams);
            }
            final int finalI = i;
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBinding.picsViewPager.setCurrentItem(finalI);
                }
            });
        }
    }

    /**
     * 绘制指示点
     *
     * @param sum 图片总数
     * @param now 目前位置
     */
    private void drawPointChange(int sum, int now) {

        for (int i = 0; i < Math.min(sum, now); i++) {
            if (i == now) {
                ((ImageView) mBinding.point.getChildAt(i)).setImageResource(R.drawable.point_now);
            } else {
                ((ImageView) mBinding.point.getChildAt(i)).setImageResource(R.drawable.point_not_now);
            }
        }
    }
}
