package moe.xing.galleryapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import java.util.ArrayList;

import io.reactivex.functions.Consumer;
import moe.xing.gallery.GalleryActivity;
import moe.xing.gallery.GalleryAdapter;
import moe.xing.galleryapp.databinding.ActivityDemoBinding;
import moe.xing.rx2_utils.RxBus;

public class DemoActivity extends AppCompatActivity {

    private ActivityDemoBinding mBinding;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_demo, null, false);

        setContentView(mBinding.getRoot());


        RxBus.getInstance().toObserverable().subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                if (o instanceof GalleryAdapter.SaveImage) {
                    Toast.makeText(DemoActivity.this, ((GalleryAdapter.SaveImage) o).getUrl(), Toast.LENGTH_LONG).show();
                }
            }
        });

        mBinding.showImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<String> images = new ArrayList<String>();
                images.add("https://lc-ayndtsch.cn-n1.lcfile.com/398b9b355ecebe791964.png");

                images.add("https://lc-ayndtsch.cn-n1.lcfile.com/15878660a09763b7b052.jpg");

                images.add("http://images6.fanpop.com/image/photos/37500000/Yazawa-Nico-3-love-live-school-idol-project-37589004-500-281.jpg");
                images.add("https://upload.wikimedia.org/wikipedia/commons/3/3d/LARGE_elevation.jpg");
                images.add("https://upload.wikimedia.org/wikipedia/commons/thumb/3/3d/LARGE_elevation.jpg/1980px-LARGE_elevation.jpg");


                startActivity(GalleryActivity.startIntent(DemoActivity.this, images, 2, true, R.drawable.img_holder, true));
            }
        });
    }
}
