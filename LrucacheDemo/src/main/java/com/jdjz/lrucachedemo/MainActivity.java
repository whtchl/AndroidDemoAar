package com.jdjz.lrucachedemo;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.jdjz.contacts.ContactsActivity;
import com.jdjz.db.DBActivity;
import com.jdjz.testConfig.SealConst;
import com.jdjz.weex.WXPageActivity;
import com.jdjz.weex.modle.PreviewImagesData;
import com.jdjz.weex.util.ImgUtil;
import com.jude.utils.JUtils;
import com.jude.utils.permission.PermissionListener;
import com.jude.utils.permission.PermissionsUtil;
import com.whamu2.previewimage.Preview;
import com.whamu2.previewimage.entity.Image;

import org.json.JSONArray;
import org.json.JSONException;

import java.time.temporal.JulianFields;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.iwf.photopicker.PhotoPicker;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn_lrucache)
    Button btnLrucache;
    @BindView(R.id.btn_contacts)
    Button btnContacts;
    @BindView(R.id.btn_db)
    Button btnDb;
    @BindView(R.id.btn_weex)
    Button btnWeex;

    @BindView(R.id.btn_preview_image)
    Button btnPreviewImage;

    @BindView(R.id.btn_choose_image)
    Button btnChooseImage;

    @BindView(R.id.btn_permission)
    Button btnPermission;

    @BindView(R.id.btn_permission2)
    Button btnPermission2;
    private BroadcastReceiver batteryLevelRcvr;
    private IntentFilter batteryLevelFilter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        monitorBatteryState();
    }

    private void initView() {

    }

    private void monitorBatteryState() {
        batteryLevelRcvr = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                StringBuilder sb = new StringBuilder();
                int rawlevel = intent.getIntExtra("level", -1);
                int scale = intent.getIntExtra("scale", -1);
                int status = intent.getIntExtra("status", -1);
                int health = intent.getIntExtra("health", -1);
                int level = -1; // percentage, or -1 for unknown
                if (rawlevel >= 0 && scale > 0) {
                    level = (rawlevel * 100) / scale;
                }
                JUtils.getSharedPreference().edit().putString(SealConst.BATTYER_LEVEL,String.valueOf(level)+"%").apply();
                sb.append("The phone");
                if (BatteryManager.BATTERY_HEALTH_OVERHEAT == health) {
                    sb.append("'s battery feels very hot!");
                } else {
                    switch (status) {
                        case BatteryManager.BATTERY_STATUS_UNKNOWN:
                            sb.append("no battery.");
                            break;
                        case BatteryManager.BATTERY_STATUS_CHARGING:
                            sb.append("'s battery");
                            if (level <= 33)
                                sb.append(" is charging, battery level is low" + "[" + level + "]");
                            else if (level <= 84) sb.append(" is charging." + "[" + level + "]");
                            else sb.append(" will be fully charged.");
                            break;
                        case BatteryManager.BATTERY_STATUS_DISCHARGING:
                        case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                            if (level == 0) sb.append(" needs charging right away.");
                            else if (level > 0 && level <= 33)
                                sb.append(" is about ready to be recharged, battery level is low" + "[" + level + "]");
                            else sb.append("'s battery level is" + "[" + level + "]");
                            break;
                        case BatteryManager.BATTERY_STATUS_FULL:
                            sb.append(" is fully charged.");
                            break;
                        default:
                            sb.append("'s battery is indescribable!");
                            break;
                    }
                }
                sb.append(' ');
                JUtils.Log(sb.toString());
                //batterLevel.setText(sb.toString());


            }
        };
        batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevelRcvr, batteryLevelFilter);
    }


    @OnClick({R.id.btn_lrucache, R.id.btn_contacts, R.id.btn_db,R.id.btn_weex,R.id.btn_preview_image,R.id.btn_choose_image,R.id.btn_saveimg_gallery, R.id.btn_permission, R.id.btn_permission2})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_lrucache:
                startActivity(new Intent(this, LrucacheDemo.class));
                break;
            case R.id.btn_contacts:
                startActivity(new Intent(this, ContactsActivity.class));
                break;
            case R.id.btn_db:
                startActivity(new Intent(this, DBActivity.class));
                break;
            case R.id.btn_weex:
                Intent intent = new Intent(this, WXPageActivity.class);
                Uri data = getIntent().getData();
                if (data != null) {
                    intent.setData(data);
                }
                intent.putExtra("from", "splash");
                startActivity(intent);
                break;
            case R.id.btn_preview_image:

                /*if( hasPermission(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})){
                    JUtils.Log("have storage 权限");

                }else{
                    JUtils.Log("do not have storage 权限");
                }*/



                //imageView.setImageDrawable(this.getDrawable(R.drawable.jpg));
                List<PreviewImagesData> datas = new ArrayList<>();

                PreviewImagesData previewImagesData = new PreviewImagesData();
                previewImagesData.setOriginUrl("/sdcard/jpg.jpg");
                previewImagesData.setThumbnailUrl("/sdcard/jpg.jpg");
                datas.add(previewImagesData);

                previewImagesData = new PreviewImagesData();
                previewImagesData.setOriginUrl("/storage/emulated/0/jpg.jpg");
                previewImagesData.setThumbnailUrl("/storage/emulated/0/jpg.jpg");
                datas.add(previewImagesData);

                previewImagesData = new PreviewImagesData();
                previewImagesData.setOriginUrl("http://img6.16fan.com/attachments/wenzhang/201805/18/152660818127263ge.jpeg");
                previewImagesData.setThumbnailUrl("http://img3.16fan.com/live/origin/201805/21/E421b24c08446.jpg");
                datas.add(previewImagesData);

                previewImagesData = new PreviewImagesData();
                previewImagesData.setOriginUrl("http://img6.16fan.com/attachments/wenzhang/201805/18/152660818716180ge.jpeg");
                previewImagesData.setThumbnailUrl("http://img3.16fan.com/live/origin/201805/21/4D7B35fdf082e.jpg");
                datas.add(previewImagesData);

                previewImagesData = new PreviewImagesData();
                previewImagesData.setOriginUrl("http://img6.16fan.com/attachments/wenzhang/201805/18/152660818716180ge.jpeg");
                previewImagesData.setThumbnailUrl("http://img3.16fan.com/live/origin/201805/21/2D02ebc5838e6.jpg");
                datas.add(previewImagesData);

                previewImagesData = new PreviewImagesData();
                previewImagesData.setOriginUrl("http://img6.16fan.com/attachments/wenzhang/201805/18/152660818127263ge.jpeg");
                previewImagesData.setThumbnailUrl("http://img3.16fan.com/live/origin/201805/21/14C5e483e7583.jpg");
                datas.add(previewImagesData);

              /*  data = new PreviewImagesData();
                data.setOriginUrl("http://img6.16fan.com/attachments/wenzhang/201805/18/152660818716180ge.jpeg");
                data.setThumbnailUrl("http://img3.16fan.com/live/origin/201805/21/A1B17c5f59b78.jpg");
                datas.add(data);

                data = new PreviewImagesData();
                data.setOriginUrl("http://img6.16fan.com/attachments/wenzhang/201805/18/152660818127263ge.jpeg");
                data.setThumbnailUrl("http://img3.16fan.com/live/origin/201805/21/94699b2be3cfa.jpg");
                datas.add(data);

                data = new PreviewImagesData();
                data.setThumbnailUrl("http://img3.16fan.com/live/origin/201805/21/EB298ce595dd2.jpg");
                datas.add(data);

                data = new PreviewImagesData();
                data.setThumbnailUrl("http://img3.16fan.com/live/origin/201805/21/264Ba4860d469.jpg");
                datas.add(data);*/


                List<Image> images = new ArrayList<>();
                for (PreviewImagesData d : datas) {
                    Image image = new Image();
                    image.setOriginUrl(d.getOriginUrl());
                    image.setThumbnailUrl(d.getThumbnailUrl());
                    images.add(image);
                }

                Preview.with(MainActivity.this)
                        .builder()
                        .load(images)
                        .displayCount(true)
                        .markPosition(0)
                        .showDownload(true)
                        .showOriginImage(true)
                        .downloadLocalPath("Preview")
                        .show();
                break;
            case R.id.btn_choose_image:
                PhotoPicker.builder()
                        .setPhotoCount(9)
                        .setGridColumnCount(4)
                        .start(this);
                break;
            case R.id.btn_saveimg_gallery:
                requestStorage();

                break;
            case R.id.btn_permission:
               /* PermissionsUtil.requestPermissiontest(this, new PermissionListener() {
                    @Override
                    public void permissionGranted(@NonNull String[] permissions) {
                       // saveImage();
                    }

                    @Override
                    public void permissionDenied(@NonNull String[] permissions) {
                        Toast.makeText(MainActivity.this, "用户拒绝使用读写存储权限", Toast.LENGTH_LONG).show();
                    }
                }, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, false, null);
*/
               // requestCemera();
                PermissionsUtil.requestPermissionNoActivity(this, new PermissionListener() {
                    @Override
                    public void permissionGranted(@NonNull String[] permissions) {
                        // saveImage();
                        Toast.makeText(MainActivity.this, "用户同意使用读写存储权限", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void permissionDenied(@NonNull String[] permissions) {
                        Toast.makeText(MainActivity.this, "用户拒绝使用读写存储权限", Toast.LENGTH_LONG).show();
                    }
                }, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, false, null);
                break;
            case R.id.btn_permission2:
                if( hasPermission(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE})){
                    JUtils.Log("have permission");

                }else{
                    JUtils.Log("have not permission");
                }
                break;

        }
    }






    private void requestCemera() {
        PermissionsUtil.requestPermission(getApplication(), new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permissions) {
                Toast.makeText(MainActivity.this, "访问摄像头", Toast.LENGTH_LONG).show();
            }

            @Override
            public void permissionDenied(@NonNull String[] permissions) {
                Toast.makeText(MainActivity.this, "用户拒绝了访问摄像头", Toast.LENGTH_LONG).show();
            }
        }, Manifest.permission.CAMERA);
    }

    private void requestStorage() {
        PermissionsUtil.requestPermission(this, new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permissions) {
                saveImage();
            }

            @Override
            public void permissionDenied(@NonNull String[] permissions) {
                Toast.makeText(MainActivity.this, "用户拒绝使用读写存储权限", Toast.LENGTH_LONG).show();
            }
        }, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, false, null);
    }

    void saveImage(){

        String localPath = "/sdcard/jpg.jpg";
        Bitmap bmp = BitmapFactory.decodeFile(localPath);
        if(bmp!=null){
            ImgUtil.saveImageToGallery(this,bmp);
        }else{
            JUtils.Log("bmp is null");
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(batteryLevelRcvr);
    }
    public  boolean hasPermission(@NonNull Context context, @NonNull String... permissions) {

        if (permissions.length == 0) {
            return false;
        }

        for (String per : permissions ) {
            int result = PermissionChecker.checkSelfPermission(context, per);
            if ( result != PermissionChecker.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

}
