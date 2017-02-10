package moe.xing.galleryapp;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;

import moe.xing.gallery.GalleryActivity;
import moe.xing.galleryapp.databinding.ActivityDemoBinding;

public class DemoActivity extends AppCompatActivity {

    private ActivityDemoBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_demo, null, false);

        setContentView(mBinding.getRoot());

        mBinding.showImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<String> images = new ArrayList<String>();
                images.add("http://vignette1.wikia.nocookie.net/love-live/images/e/e2/S2Ep07_00286.png/revision/latest?cb=20140522145104");

                images.add("http://adn.i.ntere.st/p/14423060/image");

                images.add("http://images6.fanpop.com/image/photos/37500000/Yazawa-Nico-3-love-live-school-idol-project-37589004-500-281.jpg");

                images.add("https://manekin3k0.files.wordpress.com/2015/08/nicosmile.jpg");

                startActivity(GalleryActivity.startIntent(DemoActivity.this, images, 0, true));
            }
        });
    }
}
