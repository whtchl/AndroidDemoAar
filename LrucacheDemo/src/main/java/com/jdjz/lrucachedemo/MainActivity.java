package com.jdjz.lrucachedemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.jdjz.contacts.ContactsActivity;
import com.jdjz.db.DBActivity;
import com.jdjz.testConfig.SealConst;
import com.jdjz.weex.WXPageActivity;
import com.jdjz.weex.modle.PreviewImagesData;
import com.jude.utils.JUtils;
import com.whamu2.previewimage.Preview;
import com.whamu2.previewimage.entity.Image;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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


    @OnClick({R.id.btn_lrucache, R.id.btn_contacts, R.id.btn_db,R.id.btn_weex,R.id.btn_preview_image})
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
                //imageView.setImageDrawable(this.getDrawable(R.drawable.jpg));
                List<PreviewImagesData> datas = new ArrayList<>();

                PreviewImagesData previewImagesData = new PreviewImagesData();
                previewImagesData.setOriginUrl("/sdcard/jpg.jpg");
                previewImagesData.setThumbnailUrl("/sdcard/jpg.jpg");
                datas.add(previewImagesData);

                previewImagesData = new PreviewImagesData();
                previewImagesData.setOriginUrl("/sdcard/jpg.jpg");
                previewImagesData.setThumbnailUrl("/sdcard/jpg.jpg");
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
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(batteryLevelRcvr);
    }



}
