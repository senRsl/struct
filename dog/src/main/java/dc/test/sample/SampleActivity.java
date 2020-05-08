package dc.test.sample;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import butterknife.BindArray;
import butterknife.OnClick;
import dc.android.common.BridgeContext;
import dc.android.common.BridgeOpcode;
import dc.android.common.handler.CrashHandler;
import dc.android.common.net.WebUtils;
import dc.android.common.utils.KeepInstance;
import dc.android.common.utils.SharePreferencesUtils;
import dc.android.common.utils.TaskUtils;
import dc.android.libs.PermissionUtils;
import dc.android.libs.permission.AbsPermissionCallback;
import dc.android.libs.stat.HooksStatUtils;
import dc.android.libs.stat.SlackInstance;
import dc.common.Logger;
import dc.test.sample.bridge.BaseSampleActivity;
import dc.test.sample.dog.view.activity.DogListActivity;
import dc.test.sample.domain.DogBean;

/**
 * @author senrsl
 * @ClassName: SampleActivity
 * @Package: dc.test.sample
 * @CreateTime: 2018/11/18 9:52 PM
 */
public class SampleActivity extends BaseSampleActivity {

    @BindArray(R.array.dogs)
    String[] dogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        KeepInstance.getInstance().setStatus(BridgeOpcode.YES);
        setShowStatus(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        setImmersiveStatusBar(true, Color.WHITE);
        //AutoMarginUtils.auto(mFrameLayoutContent);
        initBindView();
    }

    @Override
    protected void initLayout() {
        super.initLayout();
        tvTitle.setText(R.string.app_name);
    }

    @Override
    protected void initData() {
        generateFromXml();
        initCrash();
    }


    @OnClick({R.id.btn_list_dog, R.id.btn_list_dog_wrapper, R.id.btn_add_dog, R.id.btn_display, R.id.btn_crash,
            R.id.btn_hooks, R.id.btn_tasks, R.id.btn_swipe_1, R.id.btn_swipe_2, R.id.btn_swipe_2_nest_scroll, R.id.btn_swipe_3,
            R.id.btn_permission, R.id.btn_net_post_json, R.id.btn_net_post, R.id.btn_net_get})
    public void onViewClickedContent(View v) {
        switch (v.getId()) {
            case R.id.btn_list_dog:
                DogListActivity.start(this, Constants.LIST_TYPE_SWIPE);
                break;
            case R.id.btn_list_dog_wrapper:
                DogListActivity.start(this, Constants.LIST_TYPE_SWIPE_WRAPPER);
                break;
            case R.id.btn_add_dog:
                break;
            case R.id.btn_crash:
                int i = 1 / 0;
                break;
            case R.id.btn_hooks:
                BridgeContext.isDebug = false;
                BridgeContext.isReport = true;
                HooksStatUtils.hooks(getApplication());
                break;
            case R.id.btn_tasks:
                BridgeContext.CLS_LOGIN = DisplayActivity.class.getCanonicalName();
                new TaskUtils().startLogin(this, Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP, R.string.app_name);
                break;
//            case R.id.btn_swipe_1:
//                TestSwipe1Activity.start(this);
//                break;
//            case R.id.btn_swipe_2:
//                TestSwipe2Activity.start(this);
//                break;
//            case R.id.btn_swipe_2_nest_scroll:
//                TestSwipe2NestScrollActivity.start(this);
//                break;
//            case R.id.btn_swipe_3:
//                TestSwipe3Activity.start(this);
//            break;
            case R.id.btn_permission:
//                dc.android.libs.permission.TestActivity.start(this);
                PermissionUtils.with(this)
                        .permisson(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA})
                        .callback(new AbsPermissionCallback() {
                            @Override
                            public void onResult(boolean isAllGrant, boolean hasDenied, boolean hasRationale) {
                                Logger.w(getClass().getName(), isAllGrant, hasDenied, hasRationale, listGrant, listDenied, listRationale);
                            }
                        }).request();
                break;
            case R.id.btn_net_post_json:
                new Thread(() -> {
                    try {
                        String result = WebUtils.doPostAsJson("https://hooks.slack.com/services/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", "{'text':'啊啊啊啊'}");
                        Logger.w(getClass().getName(), result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
                break;
            case R.id.btn_net_post:
                new Thread(() -> {
                    try {
//                        String result = WebUtils.doPost("https://free-api.heweather.net/s6/weather/now", "location=auto_ip&key=b272a27633424bb59a4b12acbfb4ab39");
                        Map<String, Object> map = new HashMap<>();
                        map.put("location", "auto_ip");
                        map.put("key", "b272a27633424bb59a4b12acbfb4ab39");
                        map.put("sb", 1);
                        String result = WebUtils.doPost("https://free-api.heweather.net/s6/weather/now", map);
                        Logger.w(getClass().getName(), result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
                break;
            case R.id.btn_net_get:
                new Thread(() -> {
                    try {
//                        String result = WebUtils.doGet("https://free-api.heweather.net/s6/weather/now?location=auto_ip&key=b272a27633424bb59a4b12acbfb4ab39");
                        Map<String, Object> map = new HashMap<>();
                        map.put("location", "auto_ip");
                        map.put("key", "b272a27633424bb59a4b12acbfb4ab39");
                        map.put("sb", 1);
                        String result = WebUtils.doGet("https://free-api.heweather.net/s6/weather/now", map);
                        Logger.w(getClass().getName(), result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
                break;
            default:
                DisplayActivity.start(this);
                break;
        }
    }

    private void initCrash() {
        String env = null;
        JSONObject jo = new JSONObject();
        try {
            jo.put("className", getClass().getName());
            jo.put("currentTime", System.currentTimeMillis());
            env = jo.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SharePreferencesUtils sp = new SharePreferencesUtils(this);
        sp.saveSharedPreferencesValue(BridgeContext.KEY_ENV, env);
        //VsfApplication中，先!isDebug，再初始化
        SlackInstance.getInstance().init(getApplication());
        CrashHandler.getInstance().init(getApplicationContext(), cbCrash);
        BridgeContext.CLS_WELCOME = SampleActivity.class.getCanonicalName();
//        SlackInstance.getInstance().send(getClass().getSimpleName());
    }

    private void generateFromXml() {
        Logger.w(dogs);
        int i = 0;
        for (String dog : dogs) {
            Logger.w(dog);
            String[] dogValue = dog.split(",");
            Logger.w(dogValue);
            DogBean bean = new DogBean(dogValue[0], dogValue[1] + i++, dogValue[2]);
            Constants.listDogs.add(bean);
        }
    }


    private CrashHandler.Callback cbCrash = (ex, info) -> SlackInstance.getInstance().send(getClass().getName() + BridgeContext.TAB + info);

}
