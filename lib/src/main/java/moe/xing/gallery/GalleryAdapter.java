package moe.xing.gallery;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.io.File;
import java.net.URLConnection;
import java.util.List;

import moe.xing.baseutils.Init;
import moe.xing.baseutils.utils.IntentUtils;

import static androidx.core.content.FileProvider.getUriForFile;

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

        final View imageView = LayoutInflater.from(container.getContext()).inflate(R.layout.item_big_pic, container, false);
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
                        scaleImageView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {

                                if (imageView.getContext() instanceof Activity) {
                                    if (((Activity) (imageView.getContext())).isFinishing()) {
                                        return false;
                                    }
                                }

                                final String[] actions = new String[]{"保存图片"};
                                new AlertDialog.Builder(imageView.getContext(), R.style.Theme_AppCompat_Light_Dialog_Alert)
                                        .setTitle("请选择操作")
                                        .setItems(actions, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch (which) {
                                                    case 0:
                                                        String url = pics.get(position);
                                                        Uri uri = Uri.parse(url);
                                                        // Create request for android download manager
                                                        DownloadManager downloadManager = (DownloadManager) Init.getApplication().getSystemService(Context.DOWNLOAD_SERVICE);
                                                        DownloadManager.Request request = new DownloadManager.Request(uri);
                                                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                                                                DownloadManager.Request.NETWORK_MOBILE);

// set title and description
                                                        request.setTitle("图片下载");
                                                        request.setDescription("正在保存您选择的图片");

                                                        request.allowScanningByMediaScanner();
                                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

//set the local destination for download file to a path within the application's external files directory
                                                        request.setDestinationInExternalFilesDir(Init.getApplication(), Environment.DIRECTORY_PICTURES, url.substring(url.lastIndexOf('/') + 1));
                                                        request.setMimeType("*/*");
                                                        downloadManager.enqueue(request);
                                                        break;
                                                    case 1:
                                                        Intent share = new Intent(Intent.ACTION_SEND);
                                                        share.setType("image/jpeg");
                                                        Uri contentUri = getUriForFile(Init.getApplication(), Init.getApplication().getPackageName() + ".fileprovider", cacheFile);
                                                        String mime = URLConnection.guessContentTypeFromName("image/jpeg");
                                                        for (ResolveInfo resolveInfo : moe.xing.baseutils.utils.IntentUtils.getIntentAppIcon(share)) {
                                                            String packageName = resolveInfo.activityInfo.packageName;
                                                            Init.getApplication().grantUriPermission(packageName, contentUri,
                                                                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                                                                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                                        }
                                                        share.setDataAndType(contentUri, mime);
                                                        share.putExtra(Intent.EXTRA_STREAM, contentUri);
                                                        IntentUtils.startIntent(share);
                                                        break;
                                                }
                                            }
                                        }).setNegativeButton(android.R.string.cancel, null).show();

                                return false;
                            }
                        });
                    } catch (Exception e) {
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
