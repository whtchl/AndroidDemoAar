package me.iwf.photopicker;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.iwf.photopicker.entity.Photo;
import me.iwf.photopicker.event.OnItemCheckListener;
import me.iwf.photopicker.fragment.ImagePagerFragment;
import me.iwf.photopicker.fragment.PhotoPickerFragment;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;
import top.zibin.luban.OnRenameListener;

import static android.widget.Toast.LENGTH_LONG;
import static me.iwf.photopicker.PhotoPicker.DEFAULT_COLUMN_NUMBER;
import static me.iwf.photopicker.PhotoPicker.DEFAULT_MAX_COUNT;
import static me.iwf.photopicker.PhotoPicker.EXTRA_COMPRESS;
import static me.iwf.photopicker.PhotoPicker.EXTRA_GRID_COLUMN;
import static me.iwf.photopicker.PhotoPicker.EXTRA_MAX_COUNT;
import static me.iwf.photopicker.PhotoPicker.EXTRA_ORIGINAL_PHOTOS;
import static me.iwf.photopicker.PhotoPicker.EXTRA_PREVIEW_ENABLED;
import static me.iwf.photopicker.PhotoPicker.EXTRA_SHOW_CAMERA;
import static me.iwf.photopicker.PhotoPicker.EXTRA_SHOW_GIF;
import static me.iwf.photopicker.PhotoPicker.KEY_SELECTED_PHOTOS;

public class PhotoPickerActivity extends AppCompatActivity {

    private PhotoPickerFragment pickerFragment;
    private ImagePagerFragment imagePagerFragment;
    private MenuItem menuDoneItem;

    private int maxCount = DEFAULT_MAX_COUNT;

    /**
     * to prevent multiple calls to inflate menu
     */
    private boolean menuIsInflated = false;

    private boolean showGif = false;
    private ArrayList<String> originalPhotos = null;
    private CompositeDisposable mDisposable;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean showCamera = getIntent().getBooleanExtra(EXTRA_SHOW_CAMERA, true);
        boolean showGif = getIntent().getBooleanExtra(EXTRA_SHOW_GIF, false);
        boolean previewEnabled = getIntent().getBooleanExtra(EXTRA_PREVIEW_ENABLED, true);
        context = this;
        mDisposable = new CompositeDisposable();
        setShowGif(showGif);

        setContentView(R.layout.__picker_activity_photo_picker);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        setTitle(R.string.__picker_title);

        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            actionBar.setElevation(25);
        }

        maxCount = getIntent().getIntExtra(EXTRA_MAX_COUNT, DEFAULT_MAX_COUNT);
        int columnNumber = getIntent().getIntExtra(EXTRA_GRID_COLUMN, DEFAULT_COLUMN_NUMBER);
        originalPhotos = getIntent().getStringArrayListExtra(EXTRA_ORIGINAL_PHOTOS);

        pickerFragment = (PhotoPickerFragment) getSupportFragmentManager().findFragmentByTag("tag");
        if (pickerFragment == null) {
            pickerFragment = PhotoPickerFragment
                    .newInstance(showCamera, showGif, previewEnabled, columnNumber, maxCount, originalPhotos);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, pickerFragment, "tag")
                    .commit();
            getSupportFragmentManager().executePendingTransactions();
        }

        pickerFragment.getPhotoGridAdapter().setOnItemCheckListener(new OnItemCheckListener() {
            @Override
            public boolean onItemCheck(int position, Photo photo, final int selectedItemCount) {

                menuDoneItem.setEnabled(selectedItemCount > 0);

                if (maxCount <= 1) {
                    List<String> photos = pickerFragment.getPhotoGridAdapter().getSelectedPhotos();
                    if (!photos.contains(photo.getPath())) {
                        photos.clear();
                        pickerFragment.getPhotoGridAdapter().notifyDataSetChanged();
                    }
                    return true;
                }

                if (selectedItemCount > maxCount) {
                    Toast.makeText(getActivity(), getString(R.string.__picker_over_max_count_tips, maxCount),
                            LENGTH_LONG).show();
                    return false;
                }
                if (maxCount > 1) {
                    menuDoneItem.setTitle(getString(R.string.__picker_done_with_count, selectedItemCount, maxCount));
                } else {
                    menuDoneItem.setTitle(getString(R.string.__picker_done));
                }
                return true;
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDisposable.clear();
    }

    //刷新右上角按钮文案
    public void updateTitleDoneItem() {
        if (menuIsInflated) {
            if (pickerFragment != null && pickerFragment.isResumed()) {
                List<String> photos = pickerFragment.getPhotoGridAdapter().getSelectedPhotos();
                int size = photos == null ? 0 : photos.size();
                menuDoneItem.setEnabled(size > 0);
                if (maxCount > 1) {
                    menuDoneItem.setTitle(getString(R.string.__picker_done_with_count, size, maxCount));
                } else {
                    menuDoneItem.setTitle(getString(R.string.__picker_done));
                }

            } else if (imagePagerFragment != null && imagePagerFragment.isResumed()) {
                //预览界面 完成总是可点的，没选就把默认当前图片
                menuDoneItem.setEnabled(true);
            }

        }
    }

    /**
     * Overriding this method allows us to run our exit animation first, then exiting
     * the activity when it complete.
     */
    @Override
    public void onBackPressed() {
        if (imagePagerFragment != null && imagePagerFragment.isVisible()) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            }
        } else {
            super.onBackPressed();
        }
    }


    public void addImagePagerFragment(ImagePagerFragment imagePagerFragment) {
        this.imagePagerFragment = imagePagerFragment;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, this.imagePagerFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!menuIsInflated) {
            getMenuInflater().inflate(R.menu.__picker_menu_picker, menu);
            menuDoneItem = menu.findItem(R.id.done);
            if (originalPhotos != null && originalPhotos.size() > 0) {
                menuDoneItem.setEnabled(true);
                menuDoneItem.setTitle(
                        getString(R.string.__picker_done_with_count, originalPhotos.size(), maxCount));
            } else {
                menuDoneItem.setEnabled(false);
            }
            menuIsInflated = true;
            return true;
        }
        return false;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }


        if (item.getItemId() == R.id.done) {
            Intent intent = new Intent();
            ArrayList<String> selectedPhotos = null;
            if (pickerFragment != null) {
                selectedPhotos = pickerFragment.getPhotoGridAdapter().getSelectedPhotoPaths();
            }
            //当在列表没有选择图片，又在详情界面时默认选择当前图片
            if (selectedPhotos.size() <= 0) {
                if (imagePagerFragment != null && imagePagerFragment.isResumed()) {
                    // 预览界面
                    selectedPhotos = imagePagerFragment.getCurrentPath();
                }
            }
            if (selectedPhotos != null && selectedPhotos.size() > 0) {
                 /*intent.putStringArrayListExtra(KEY_SELECTED_PHOTOS, selectedPhotos);
                     setResult(RESULT_OK, intent);
                finish();*/
                for (int i = 0; i < selectedPhotos.size(); i++) {
                    Log.i("tchl origin master", selectedPhotos.get(i));
                }


                int mCompress = getIntent().getIntExtra(EXTRA_COMPRESS, 2);
                Log.i("tchl","************* getExtra :"+mCompress);
                if (mCompress == 1) { //comressed
                    withRx(selectedPhotos);
                } else if (mCompress == 0) {  //origin
                    Log.i("tchl","************* mCompress == 0");
                    EventBus.getDefault().post(listPathsToListFiles(selectedPhotos));
                    finish();
                } else {
                    withRxBoth(selectedPhotos);
                }
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public PhotoPickerActivity getActivity() {
        return this;
    }

    public boolean isShowGif() {
        return showGif;
    }

    public void setShowGif(boolean showGif) {
        this.showGif = showGif;
    }

    public List<File> listPathsToListFiles(List<String> paths){
        List<File> files = new ArrayList<>();
        for(int i=0;i<paths.size();i++){
            File f = new File(paths.get(i));
            if(f.exists()){
                files.add(f);
            }
        }
        return files;
    }

    //original compressed 两种图片都有
    private void withRxBoth(final List<String> photos) {
        mDisposable.add(Flowable.just(photos)
                .observeOn(Schedulers.io())
                .map(new Function<List<String>, List<File>>() {
                    @Override
                    public List<File> apply(@NonNull List<String> list) throws Exception {
                        return Luban.with(context)
                                .setTargetDir(getPath())
                                .load(list)
                                .get();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        Log.e("tchl", throwable.getMessage());
                    }
                })
                .onErrorResumeNext(Flowable.<List<File>>empty())
                .subscribe(new Consumer<List<File>>() {
                    @Override
                    public void accept(@NonNull List<File> list) {
                        for (File file : list) {
                            Log.i("tchl withRxBoth 压缩后文件", file.getAbsolutePath());
                            //showResult(originPhotos, file);
                        }
                        List<File> fileList = new ArrayList<>();
                        fileList.addAll(list);
                        fileList.addAll(list.size(),listPathsToListFiles(photos));

                        EventBus.getDefault().post(fileList);
                        finish();
                    }
                }));
    }

    private <T> void withRx(final List<T> photos) {
        mDisposable.add(Flowable.just(photos)
                .observeOn(Schedulers.io())
                .map(new Function<List<T>, List<File>>() {
                    @Override
                    public List<File> apply(@NonNull List<T> list) throws Exception {
                        return Luban.with(context)
                                .setTargetDir(getPath())
                                .load(list)
                                .get();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        Log.e("tchl", throwable.getMessage());
                    }
                })
                .onErrorResumeNext(Flowable.<List<File>>empty())
                .subscribe(new Consumer<List<File>>() {
                    @Override
                    public void accept(@NonNull List<File> list) {
                        for (File file : list) {
                            Log.i("tchl withRx 压缩后文件", file.getAbsolutePath());
                            //showResult(originPhotos, file);
                        }

                        EventBus.getDefault().post(list);
                        finish();
                    }
                }));
    }

    private <T> void withLs(final List<T> photos) {
        Luban.with(this)
                .load(photos)
                .ignoreBy(100)
                .setTargetDir(getPath())
                .setFocusAlpha(false)
                .filter(new CompressionPredicate() {
                    @Override
                    public boolean apply(String path) {
                        return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
                    }
                })
                .setRenameListener(new OnRenameListener() {
                    @Override
                    public String rename(String filePath) {
                        try {
                            MessageDigest md = MessageDigest.getInstance("MD5");
                            md.update(filePath.getBytes());
                            return new BigInteger(1, md.digest()).toString(32);
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                        return "";
                    }
                })
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess(File file) {
                        Log.i("tchl  compresss", file.getAbsolutePath());
                        //showResult(originPhotos, file);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                }).launch();
    }

    private String getPath() {
        String path = Environment.getExternalStorageDirectory() + "/compressed/";
        File file = new File(path);
        if (file.mkdirs()) {
            return path;
        }
        return path;
    }

}
