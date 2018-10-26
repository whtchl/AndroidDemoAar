package com.jdjz.weex;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jdjz.weexlib.R;
import com.jdjz.weexlib.weex.AbsWeexActivity;
import com.jdjz.weexlib.weex.hotreload.HotReloadManager;
import com.jdjz.weexlib.weex.modle.Event.ContactsEvent;
import com.jdjz.weexlib.weex.modle.Event.GetImageInfoEvent;
import com.jdjz.weexlib.weex.modle.Event.LocationEvent;
import com.jdjz.weexlib.weex.modle.Event.PhoneEvent;
import com.jdjz.weexlib.weex.modle.Event.PreviewImageEvent;
import com.jdjz.weexlib.weex.modle.Event.SaveImageToPhotosAlbumEvent;
import com.jdjz.weexlib.weex.modle.Event.StartAutoLBSEvent;
import com.jdjz.weexlib.weex.modle.Event.StopAutoLBSEvent;
import com.jdjz.weexlib.weex.util.AppConfig;
import com.jdjz.weexlib.weex.util.Constants;
import com.jdjz.weexlib.weex.util.StrUtil;
import com.jude.utils.JUtils;
import com.jude.utils.permission.PermissionRequestCode;
import com.jude.utils.permission.PermissionsUtil;
import com.taobao.weex.WXEnvironment;
import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.ui.component.NestedContainer;
import com.taobao.weex.utils.WXLogUtils;
import com.taobao.weex.utils.WXSoInstallMgrSdk;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

public class WXPageActivity extends AbsWeexActivity implements
    WXSDKInstance.NestedInstanceInterceptor {

  private static final String TAG = "WXPageActivity";
  private ProgressBar mProgressBar;
  private TextView mTipView;
  private boolean mFromSplash = false;
  private HotReloadManager mHotReloadManager;

  private BroadcastReceiver batteryLevelRcvr;
  private IntentFilter batteryLevelFilter;
  @Override
  public void onCreateNestInstance(WXSDKInstance instance, NestedContainer container) {
    Log.d(TAG, "Nested Instance created.");
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_wxpage);
    mContainer = (ViewGroup) findViewById(R.id.container);
    mProgressBar = (ProgressBar) findViewById(R.id.progress);
    mTipView = (TextView) findViewById(R.id.index_tip);

    monitorBatteryState();


    Intent intent = getIntent();
    Uri uri = intent.getData();
    String from = intent.getStringExtra("from");
    mFromSplash = "splash".equals(from);

    if (uri == null) {
      uri = Uri.parse("{}");
    }
    if (uri != null) {
      try {
        JSONObject initData = new JSONObject(uri.toString());
        String bundleUrl = initData.optString("WeexBundle", null);
        if (bundleUrl != null) {
          mUri = Uri.parse(bundleUrl);
        }

        String ws = initData.optString("Ws", null);
        if (!TextUtils.isEmpty(ws)) {
          mHotReloadManager = new HotReloadManager(ws, new HotReloadManager.ActionListener() {
            @Override
            public void reload() {
              runOnUiThread(new Runnable() {
                @Override
                public void run() {
                  Toast.makeText(WXPageActivity.this, "Hot reload", Toast.LENGTH_SHORT).show();
                  createWeexInstance();
                  renderPage();
                }
              });
            }

            @Override
            public void render(final String bundleUrl) {
              runOnUiThread(new Runnable() {
                @Override
                public void run() {
                  Toast.makeText(WXPageActivity.this, "Render: " + bundleUrl, Toast.LENGTH_SHORT).show();
                  createWeexInstance();
                  loadUrl(bundleUrl);
                }
              });
            }
          });
        } else {
          WXLogUtils.w("Weex", "can not get hot reload config");
        }
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }

    if (mUri == null) {
      mUri = Uri.parse(AppConfig.getLaunchUrl());
    }

    if (!WXSoInstallMgrSdk.isCPUSupport()) {
      mProgressBar.setVisibility(View.INVISIBLE);
      mTipView.setText(R.string.cpu_not_support_tip);
      return;
    }

    String url = getUrl(mUri);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setTitle(url);
      getSupportActionBar().hide();
    }
    url= "file://assets/dist/index.js";
    loadUrl(url);

    //getWindow().addContentView();
  }

  private String getUrl(Uri uri) {
    String url = uri.toString();
    String scheme = uri.getScheme();
    if (uri.isHierarchical()) {
      if (TextUtils.equals(scheme, "http") || TextUtils.equals(scheme, "https")) {
        String weexTpl = uri.getQueryParameter(Constants.WEEX_TPL_KEY);
        if (!TextUtils.isEmpty(weexTpl)) {
          url = weexTpl;
        }
      }
    }
    return url;
  }

  protected void preRenderPage() {
    mProgressBar.setVisibility(View.VISIBLE);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    JUtils.Log("onRequestPermissionsResult:" + requestCode);
    for(int i=0;i<permissions.length;i++){
        JUtils.Log("permission name:"+permissions[i]+"  grantResults:"+grantResults[i]);
    }

    JUtils.Log("PermissionsUtil.isGranted:"+ PermissionsUtil.isGranted(grantResults)+"  "+ PermissionsUtil.hasPermission(this, permissions));

    if (requestCode == PermissionRequestCode.PERMISSION_REQUEST_CODE_PHONE && PermissionsUtil.isGranted(grantResults)
            && PermissionsUtil.hasPermission(this, permissions)) {
        EventBus.getDefault().post(new PhoneEvent());
        JUtils.Toast("可以使用电话功能了了");
    } else if (requestCode == PermissionRequestCode.PERMISSION_REQUEST_CODE_LOCATION && PermissionsUtil.isGranted(grantResults)
            && PermissionsUtil.hasPermission(this, permissions)) {
        EventBus.getDefault().post(new LocationEvent());
        JUtils.Toast("可以定位了");
    }else if (requestCode == PermissionRequestCode.PERMISSION_REQUEST_CODE_CONTACTS && PermissionsUtil.isGranted(grantResults)
            && PermissionsUtil.hasPermission(this, permissions)) {
        EventBus.getDefault().post(new ContactsEvent());
        JUtils.Toast("可以使用联系人了");
    }else if (requestCode == PermissionRequestCode.PERMISSION_REQUEST_CODE_STARTAUTO_LOCATION && PermissionsUtil.isGranted(grantResults)
            && PermissionsUtil.hasPermission(this, permissions)) {
        EventBus.getDefault().post(new StartAutoLBSEvent());
        JUtils.Toast("可以自动获取地理职位了");
    }else if (requestCode == PermissionRequestCode.PERMISSION_REQUEST_CODE_STOPAUTO_LOCATION && PermissionsUtil.isGranted(grantResults)
            && PermissionsUtil.hasPermission(this, permissions)) {
        EventBus.getDefault().post(new StopAutoLBSEvent());
        JUtils.Toast("可以停止自动获取地理职位了");
    }else if (requestCode == PermissionRequestCode.PERMISSION_REQUEST_CODE_STORAGE_PREVIEWIMAGE && PermissionsUtil.isGranted(grantResults)
            && PermissionsUtil.hasPermission(this, permissions)) {
        EventBus.getDefault().post(new PreviewImageEvent());
        JUtils.Toast("可以预览图片了");
    }else if (requestCode == PermissionRequestCode.PERMISSION_REQUEST_CODE_STORAGE_GETIMAGEINFO && PermissionsUtil.isGranted(grantResults)
            && PermissionsUtil.hasPermission(this, permissions)) {
        EventBus.getDefault().post(new GetImageInfoEvent());
        JUtils.Toast("可以获取图片信息了");
    }else if (requestCode == PermissionRequestCode.PERMISSION_REQUEST_CODE_STORAGE_SAVEIMAGETOPHOTOSALBUM && PermissionsUtil.isGranted(grantResults)
            && PermissionsUtil.hasPermission(this, permissions)) {
        EventBus.getDefault().post(new SaveImageToPhotosAlbumEvent());
        JUtils.Toast("可以保存图片到相册了");
    }
    else {
      JUtils.Toast(this.getString(R.string.permissontip));
    }
  }


  @Override
  public void onRenderSuccess(WXSDKInstance instance, int width, int height) {
    mProgressBar.setVisibility(View.GONE);
    mTipView.setVisibility(View.GONE);
  }

  @Override
  public void onException(WXSDKInstance instance, String errCode, String msg) {
    mProgressBar.setVisibility(View.GONE);
    mTipView.setVisibility(View.VISIBLE);
   /* if (TextUtils.equals(errCode, WXRenderErrorCode.WX_NETWORK_ERROR)) {
      mTipView.setText(R.string.index_tip);
    } else {
      mTipView.setText("render error:" + errCode);
    }*/
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    //getMenuInflater().inflate(mFromSplash ? R.menu.main_scan : R.menu.main, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
   /* switch (item.getItemId()) {
      case R.id.action_refresh:
        createWeexInstance();
        renderPage();
        break;
      case R.id.action_scan:
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Scan a barcode");
        //integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setBeepEnabled(true);
        integrator.setOrientationLocked(false);
        integrator.setBarcodeImageEnabled(true);
        integrator.setPrompt(getString(R.string.capture_qrcode_prompt));
        integrator.initiateScan();
        break;
      case android.R.id.home:
        finish();
      default:
        break;
    }*/

    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    /*IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
    if (result != null) {
      if (result.getContents() == null) {
        Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
      } else {
        handleDecodeInternally(result.getContents());
      }
    }*/
    super.onActivityResult(requestCode, resultCode, data);
  }

  // Put up our own UI for how to handle the decoded contents.
  private void handleDecodeInternally(String code) {

    if (!TextUtils.isEmpty(code)) {
      Uri uri = Uri.parse(code);
      if (uri.getQueryParameterNames().contains("bundle")) {
        WXEnvironment.sDynamicMode = uri.getBooleanQueryParameter("debug", false);
        WXEnvironment.sDynamicUrl = uri.getQueryParameter("bundle");
        String tip = WXEnvironment.sDynamicMode ? "Has switched to Dynamic Mode" : "Has switched to Normal Mode";
        Toast.makeText(this, tip, Toast.LENGTH_SHORT).show();
        finish();
        return;
      } else if (uri.getQueryParameterNames().contains("_wx_devtool")) {
        WXEnvironment.sRemoteDebugProxyUrl = uri.getQueryParameter("_wx_devtool");
        WXEnvironment.sDebugServerConnectable = true;
        WXSDKEngine.reload();
        Toast.makeText(this, "devtool", Toast.LENGTH_SHORT).show();
        return;
      } else if (code.contains("_wx_debug")) {
        uri = Uri.parse(code);
        String debug_url = uri.getQueryParameter("_wx_debug");
      //  WXSDKEngine.switchDebugModel(true, debug_url);
      //  WXSDKEngine.de
        finish();
      } else {
        JSONObject data = new JSONObject();
        try {
          data.put("WeexBundle", Uri.parse(code).toString());
          Intent intent = new Intent(WXPageActivity.this, WXPageActivity.class);
          intent.setData(Uri.parse(data.toString()));
          startActivity(intent);
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (mHotReloadManager != null) {
      mHotReloadManager.destroy();
    }
    unregisterReceiver(batteryLevelRcvr);

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
        JUtils.getSharedPreference().edit().putString(StrUtil.BATTYER_LEVEL,String.valueOf(level)+"%").apply();
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
}
