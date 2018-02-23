package com.however.yiduobang_v20;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.avos.avoscloud.AVInstallation;
import com.however.yiduobang_v20.Base.BaseActivity;
import com.however.yiduobang_v20.Controller.BaseController;
import com.however.yiduobang_v20.Dialog.MoneyDialog;
import com.however.yiduobang_v20.Model.ErrorModel;
import com.however.yiduobang_v20.Model.ResponseModel;
import com.however.yiduobang_v20.Utils.MyUtils;
import com.however.yiduobang_v20.Utils.PermissionUtils;
import com.however.yiduobang_v20.Utils.SharedPreferencesUtils;
import com.however.yiduobang_v20.Utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by tansibin on 2018/1/20.
 */

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar_tvTitle)
    TextView toolbarTvTitle;
    @BindView(R.id.toolbar_imgUp)
    ImageView toolbarImgUp;
    @BindView(R.id.toolbar_imgUpdate)
    ImageView toolbarImgUpdate;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_loayout)
    DrawerLayout drawerLoayout;
    @BindView(R.id.navView)
    NavigationView navView;
    @BindView(R.id.btnOrder)
    Button btnOrder;
    @BindView(R.id.map)
    MapView map;
    @BindView(R.id.imgLocation)
    ImageButton imgLocation;
    @BindView(R.id.imgClientLocation)
    ImageButton imgClientLocation;
    @BindView(R.id.order_bg)
    View orderBg;
    @BindView(R.id.order_tvHint)
    TextView orderTvHint;
    @BindView(R.id.order_ImgTime)
    ImageView orderImgTime;
    @BindView(R.id.order_tvTime)
    TextView orderTvTime;
    @BindView(R.id.order_tvState)
    TextView orderTvState;
    @BindView(R.id.order_btnCancel)
    Button orderBtnCancel;
    @BindView(R.id.order_btnConfirm)
    Button orderBtnConfirm;
    @BindView(R.id.order_relContent)
    RelativeLayout orderRelContent;
    @BindView(R.id.order_line)
    LinearLayout orderLine;
    @BindView(R.id.order_tvAddress)
    TextView orderTvAddress;
    @BindView(R.id.order_tvPhone)
    TextView orderTvPhone;
    @BindView(R.id.order_tvMoney)
    TextView orderTvMoney;
    @BindView(R.id.order_btnPhone)
    Button orderBtnPhone;
    @BindView(R.id.order_btnMoney)
    Button orderBtnMoney;
    //地图以及定位相关
    private AMap aMap;
    //声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient = null;
    private MyLocationStyle myLocationStyle;
    private double locationLat = 0, locationLng = 0;
    private double clientLat = 0, clientLng = 0;
    private boolean firstInitMap = true;

    //订单信息页面相关
    private final int ORDER_STATE_UNFOLD = 0;
    private final int ORDER_STATE_SHRINK = 1;
    private int ORDER_STATE = ORDER_STATE_UNFOLD;
    private boolean ANIM_RUNNING = false;

    @BindString(R.string.order_tvPhone_wait)
    String stringWait;
    @BindString(R.string.order_tvState_not_receive)
    String orderStrNot;
    @BindString(R.string.order_tvState_wait)
    String orderStrWait;
    @BindString(R.string.order_tvState_receive)
    String orderStrReceive;

    private final int ORDER_DETAIL_NOT = 0;
    private final int ORDER_DETAIL_NOT_RECEIVE = 1;
    private final int ORDER_DETAIL_RECEIVE = 2;
    private final int ORDER_DETAIL_SEND = 3;

    private String orderStrTime, orderStrState, orderStrAddress, orderStrPhone, orderStrMoney;

    //动画相关
    private final int animTime = 500;
    private RotateAnimation roAnimUnfold;
    private RotateAnimation roAnimShrink;
    private AlphaAnimation alphaAnimUnfold;
    private AlphaAnimation alphaAnimShrink;

    //用户参数相关
    private String phone, name, sort, job, token, jwt, ini, district;

    //主页面按钮相关
    @BindString(R.string.main_btn_start)
    String stringStart;
    @BindString(R.string.main_btn_stop)
    String stringStop;
    @BindString(R.string.main_btn_detail)
    String stringDetail;
    //订单详情页面按钮相关
    @BindString(R.string.order_btnConfirm_receive)
    String stringReceive;
    @BindString(R.string.order_btnConfirm_send)
    String stringSend;

    private Runnable testRun;

    private MoneyDialog moneyDialog;
    @BindString(R.string.moneyDialog_noNull)
    String moneyDialog_strNoNull;
    @BindString(R.string.moneyDialog_noZero)
    String moneyDialog_strNoZero;
    @BindString(R.string.moneyDialog_max)
    String moneyDialog_strMax;

    private CountDownTimer countDownTimer;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        map.onCreate(savedInstanceState);
        getIntentData();
        initView();
        initMap();
    }

    /**
     * 获取intent数据
     */
    private void getIntentData() {
        Bundle bundle = this.getIntent().getExtras();
        String jsonStr = bundle.getString(KEY);
        ResponseModel model = gson.fromJson(jsonStr, ResponseModel.class);
        phone = model.phone;
        name = model.name;
        sort = model.sort;
        job = model.job;
        token = model.token;
        jwt = model.jwt;
        ini = AVInstallation.getCurrentInstallation().getInstallationId();
    }

    /**
     * 初始化View相关
     */
    private void initView() {
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLoayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLoayout.setDrawerListener(toggle);
        toggle.syncState();
        View v = navView.inflateHeaderView(R.layout.nav_header_layout);
        TextView nav_name = (TextView) v.findViewById(R.id.navheader_name);
        nav_name.setText(name);
        TextView nav_job = (TextView) v.findViewById(R.id.navheader_job);
        nav_job.setText(job);
        navView.setNavigationItemSelectedListener(this);
    }

    /**
     * 初始化地图相关
     */
    private void initMap() {
        if (aMap == null) {
            aMap = map.getMap();
        }
        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);

        myLocationStyle = new MyLocationStyle();
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
        //连续定位、蓝点不会移动到地图中心点，定位点依照设备方向旋转，并且蓝点会跟随设备移动
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setMyLocationEnabled(true);

        //定位相关
        AMapLocationClientOption option = new AMapLocationClientOption();
        //高精度模式
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否强制刷新WIFI，默认为true，强制刷新。
        option.setWifiActiveScan(false);
        //关闭缓存机制
        option.setLocationCacheEnable(false);
        option.setInterval(5000);
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        mLocationClient.startLocation();

    }

    //定位监听
    AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation.getErrorCode() == 12) {
                return;
            }
            locationLat = aMapLocation.getLatitude();
            locationLng = aMapLocation.getLongitude();
            if (firstInitMap) {
                moveToLocation();
                firstInitMap = false;
            }
            district = aMapLocation.getDistrict();

        }
    };

    /**
     * 移动地图中心到定位点
     */
    private void moveToLocation() {
        if (locationLat == 0 || locationLng == 0) {
            ToastUtils.showLongToastCenter("定位失败");
        } else {
            CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(
                    new CameraPosition(new LatLng(locationLat, locationLng), 15, 0, 0));
            aMap.animateCamera(mCameraUpdate);
        }
    }

    /**
     * 移动地图中心点到客户位置点
     */
    private void moveToClientPosition() {
        if (clientLat == 0 || clientLng == 0) {
            ToastUtils.showLongToastCenter("暂无订单");
        } else {
            CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(
                    new LatLng(clientLat, clientLng), 15, 0, 0));
            aMap.animateCamera(mCameraUpdate);
        }
    }

    /**
     * 添加标示物到地图
     */
    private void showMarker() {
        cleanMarker();
        LatLng latLng = new LatLng(clientLat, clientLng);
        MarkerOptions options = new MarkerOptions();
        options.position(latLng);
        aMap.addMarker(options);
    }

    /**
     * 清除地图上标示物
     */
    private void cleanMarker() {
        aMap.clear();
    }

    /**
     * 开启动画
     */
    private void runAnim() {
        if (!ANIM_RUNNING) {
            rotateAnim();
            alphaAnim();
            tranAnim();
            ANIM_RUNNING = true;
        }
    }

    /**
     * 旋转动画相关
     */
    private void rotateAnim() {
        if (roAnimShrink == null || roAnimUnfold == null) {
            roAnimUnfold = new RotateAnimation(180f, 360f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            roAnimUnfold.setDuration(animTime);
            roAnimUnfold.setFillAfter(true);

            roAnimShrink = new RotateAnimation(0f, 180f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            roAnimShrink.setDuration(animTime);
            roAnimShrink.setFillAfter(true);
        }

        if (ORDER_STATE == ORDER_STATE_UNFOLD)
            toolbarImgUp.startAnimation(roAnimShrink);
        else
            toolbarImgUp.startAnimation(roAnimUnfold);

    }

    /**
     * 渐变动画相关
     */
    private void alphaAnim() {
        if (alphaAnimShrink == null || alphaAnimUnfold == null) {
            alphaAnimUnfold = new AlphaAnimation(0, 1);
            alphaAnimUnfold.setDuration(animTime);
            alphaAnimUnfold.setFillAfter(true);

            alphaAnimShrink = new AlphaAnimation(1, 0);
            alphaAnimShrink.setDuration(animTime);
            alphaAnimShrink.setFillAfter(true);
        }

        if (ORDER_STATE == ORDER_STATE_UNFOLD) {
            toolbarImgUpdate.startAnimation(alphaAnimShrink);
            orderBg.startAnimation(alphaAnimShrink);
        } else {
            toolbarImgUpdate.startAnimation(alphaAnimUnfold);
            orderBg.startAnimation(alphaAnimUnfold);
        }
    }

    /**
     * 位移动画相关
     */
    private void tranAnim() {
        float end = (float) orderRelContent.getHeight();
        TranslateAnimation translateAnimation;
        if (ORDER_STATE == ORDER_STATE_UNFOLD) {
            translateAnimation = new TranslateAnimation(0, 0, 0, -end);
            translateAnimation.setDuration(animTime);
            translateAnimation.setFillAfter(true);
            translateAnimation.setAnimationListener(animationListener);

        } else {
            translateAnimation = new TranslateAnimation(0, 0, -end, 0);
            translateAnimation.setDuration(animTime);
            translateAnimation.setFillAfter(true);
            translateAnimation.setAnimationListener(animationListener);
        }
        orderRelContent.startAnimation(translateAnimation);
    }

    /**
     * 动画监听事件
     */
    Animation.AnimationListener animationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            if (ORDER_STATE == ORDER_STATE_SHRINK) {
                orderRelContent.setVisibility(View.VISIBLE);
                toolbarImgUpdate.setVisibility(View.VISIBLE);
                orderBg.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            ANIM_RUNNING = false;
            if (ORDER_STATE == ORDER_STATE_UNFOLD) {
                orderRelContent.clearAnimation();
                orderRelContent.setVisibility(View.GONE);
                toolbarImgUpdate.clearAnimation();
                orderBg.clearAnimation();
                toolbarImgUpdate.setVisibility(View.GONE);
                orderBg.setVisibility(View.GONE);
                ORDER_STATE = ORDER_STATE_SHRINK;
            } else {
                ORDER_STATE = ORDER_STATE_UNFOLD;
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    /**
     * 显示订单详情页面信息
     */
    private void showOrderDetails(int state) {
        if (state == ORDER_DETAIL_NOT) {
            orderLine.setVisibility(View.GONE);
            orderTvHint.setVisibility(View.VISIBLE);
            return;
        }
        orderBtnConfirm.setText(stringReceive);
        orderLine.setVisibility(View.VISIBLE);
        orderTvHint.setVisibility(View.GONE);
        orderTvTime.setText(orderStrTime);
        orderTvAddress.setText(orderStrAddress);
        if (state == ORDER_DETAIL_NOT_RECEIVE) {
            orderTvState.setText(orderStrState);
            orderTvPhone.setText(stringWait);
            orderTvMoney.setText(stringWait);
            orderBtnPhone.setVisibility(View.GONE);
            orderBtnMoney.setVisibility(View.GONE);
            return;
        }
        orderBtnConfirm.setText(stringSend);
        String phone = orderStrPhone.substring(0, 3) + "****" + orderStrPhone.substring(7, 11);
        orderTvPhone.setText(phone);
        orderTvMoney.setText(orderStrMoney);
        orderBtnPhone.setVisibility(View.VISIBLE);
        orderBtnMoney.setVisibility(View.VISIBLE);
        if (state == ORDER_DETAIL_RECEIVE) {
            orderTvState.setText(orderStrReceive);
            return;
        }
        orderTvState.setText(orderStrWait);

    }

    /**
     * 设置客户数据
     *
     * @param model
     */
    private void setClientData(ResponseModel model) {
        orderStrTime = model.orderTime;
        orderStrState = model.orderState;
        orderStrAddress = model.orderAddress;
        orderStrPhone = model.orderPhone;
        orderStrMoney = model.orderMoney;
        clientLat = model.orderLat;
        clientLng = model.orderLng;
    }

    /**
     * 清除客户数据
     */
    private void cleanClientData() {
        orderStrTime = "";
        orderStrState = "";
        orderStrAddress = "";
        orderStrPhone = "";
        orderStrMoney = "";
        clientLat = 0;
        clientLng = 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        map.onDestroy();
        mLocationClient.stopLocation();
        mLocationClient.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        map.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        map.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        map.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (drawerLoayout.isDrawerOpen(GravityCompat.START)) {
            drawerLoayout.closeDrawer(GravityCompat.START);
            return;
        }
        if (ORDER_STATE == ORDER_STATE_UNFOLD) {
            runAnim();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_history:
                startHistoryActivity();
                break;
            case R.id.menu_update:
//                ToastUtils.showLongToast("已是最新版本");
                break;
            case R.id.menu_exit:
                exitLogin();
                break;
        }
        return false;
    }

    /**
     * 退出登录
     */
    private void exitLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.putExtra(BaseController.TOKEN, token);
        startActivity(intent);
        SharedPreferencesUtils.getInstance().removeParams(this, BaseController.JWT);
        finish();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    /**
     * 跳往HistoryActivity
     */
    private void startHistoryActivity() {
        Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
        intent.putExtra(BaseController.PHONE, phone);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    @OnClick({R.id.toolbar_imgUp, R.id.toolbar_imgUpdate, R.id.btnOrder, R.id.imgLocation,
            R.id.imgClientLocation, R.id.order_bg, R.id.order_btnCancel, R.id.order_btnConfirm,
            R.id.order_btnPhone, R.id.order_btnMoney})
    public void onViewClicked(View view) {
        judgeLocationPermission();
        switch (view.getId()) {
            case R.id.toolbar_imgUp:
                runAnim();
                break;
            case R.id.toolbar_imgUpdate:
                getOrder(token, jwt);
                break;
            case R.id.btnOrder:
                btnOrderEvent();
                break;
            case R.id.imgLocation:
                moveToLocation();
                break;
            case R.id.imgClientLocation:
                moveToClientPosition();
                break;
            case R.id.order_bg:
                runAnim();
                break;
            case R.id.order_btnCancel:
                cancelOrder();
                break;
            case R.id.order_btnConfirm:
                btnConfirmEvent();
                break;
            case R.id.order_btnPhone:
                callPhone();
                break;
            case R.id.order_btnMoney:
                showMoneyDialog();
                break;
        }
    }

    /**
     * EventBus 接收方法，接收服务器访问成功相关
     *
     * @param model
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResponseSuccess(ResponseModel model) {
        cancelLoadingDialog();
        switch (model.requestCode) {
            case BaseController.GET_ORDER_CODE:
                getOrderSuccess(model);
                break;
            case BaseController.START_ORDER_CODE:
                startOrderSuccess(model);
                break;
            case BaseController.CANCEL_ORDER_CODE:
                cancelOrderSuccess();
                break;
            case BaseController.RECEIVE_ORDER_CODE:
                receiveOrderSuccess();
                break;
            case BaseController.SEND_ORDER_CODE:
                sendOrderSuccess();
                break;
            case BaseController.SEND_CHANGE_MONEY_CODE:
                changeMoneySuccess();
                break;
        }
    }

    /**
     * EventBus 接收方法，接收服务器访问失败相关
     *
     * @param model
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResponseFailure(ErrorModel model) {
        cancelLoadingDialog();
        if (model.errorCode == BaseController.SERVERS_ERROR_CODE) {
            //服务器错误
        }
        switch (model.requestCode) {
            case BaseController.GET_ORDER_CODE:
                getOrderFailure(model);
                break;
            case BaseController.SEND_CHANGE_MONEY_CODE:
                changeMoneyFailure();
                break;
        }
    }

    /**
     * 判断是否拥有定位权限
     */
    private void judgeLocationPermission(){
        if (!PermissionUtils.checkPermission(this,PermissionUtils.Permission_Location))
            showPermissionDialog(PermissionUtils.Permission_Location);
    }

    /**
     * 获取订单信息
     *
     * @param token
     * @param jwt
     */
    private void getOrder(String token, String jwt) {
        loadingDialog.show();
        baseController.getOrder(BaseController.GET_ORDER_CODE, token, jwt);
    }

    /**
     * 有订单
     *
     * @param model
     */
    private void getOrderSuccess(ResponseModel model) {
        setClientData(model);
        showOrderDetails(ORDER_DETAIL_NOT_RECEIVE);
        btnOrder.setText(stringDetail);
        if (ORDER_STATE == ORDER_STATE_SHRINK)
            runAnim();
        showMarker();
        moveToClientPosition();
    }

    /**
     * 获取订单信息失败
     *
     * @param model
     */
    private void getOrderFailure(ErrorModel model) {
        if (model.errorCode == 1) {
            ToastUtils.showLongToast(model.errorMsg);
        }
    }

    /**
     * 开始接单按钮点击事件
     */
    private void btnOrderEvent() {
        if (testRun == null) {
            testRun = new Runnable() {
                @Override
                public void run() {
                    baseController.startOrder(token, jwt);
                }
            };
        }
        String msg = btnOrder.getText().toString();
        if (msg.equals(stringStart)) {
            btnOrder.setText(stringStop);
            //延时3秒模拟接到订单
            handler.postDelayed(testRun, 3000);
        } else if (msg.equals(stringStop)) {
            btnOrder.setText(stringStart);
            handler.removeCallbacks(testRun);
        } else if (msg.equals(stringDetail)) {
            runAnim();
        }
    }

    /**
     * 订单来袭
     *
     * @param model
     */
    private void startOrderSuccess(ResponseModel model) {
        getOrderSuccess(model);
    }

    /**
     * 取消订单
     */
    private void cancelOrder() {
        loadingDialog.show();
        baseController.cancelOrder(token, jwt);
    }

    /**
     * 取消订单成功
     */
    private void cancelOrderSuccess() {
        cleanClientData();
        showOrderDetails(ORDER_DETAIL_NOT);
        btnOrder.setText(stringStart);
        cleanMarker();
        ToastUtils.showLongToast("取消订单成功");
    }

    /**
     * 接受订单和发送账单的按钮事件
     */
    private void btnConfirmEvent() {
        String msg = orderBtnConfirm.getText().toString();
        if (msg.equals(stringReceive)) {
            loadingDialog.show();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    baseController.receiveOrder(token, jwt);
                }
            }, 500);
        } else if (msg.equals(stringSend)) {
            loadingDialog.show();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    baseController.sendOrder(token, jwt);
                }
            }, 500);
        }
    }

    /**
     * 接单成功
     */
    private void receiveOrderSuccess() {
        showOrderDetails(ORDER_DETAIL_RECEIVE);
        ToastUtils.showLongToast("接受订单成功");
        showMarker();
    }

    /**
     * 发送账单成功
     */
    private void sendOrderSuccess() {
        showOrderDetails(ORDER_DETAIL_SEND);
        ToastUtils.showLongToast("发送账单成功");
        showMarker();
    }

    /**
     * 拨打电话
     */
    private void callPhone() {
        if (PermissionUtils.checkPermission(this, PermissionUtils.Permission_Call)) {
            MyUtils.callPhone(this, orderStrPhone);
        } else {
            showPermissionDialog(PermissionUtils.Permission_Call);
        }
    }

    /**
     * 显示变更金额提示框
     */
    private void showMoneyDialog(){
        if (moneyDialog == null){
            moneyDialog = new MoneyDialog(this) {
                @Override
                public void onListener(boolean is, String msg) {
                    if (is){
                        if (msg.equals("")) {
                            ToastUtils.showShortToast(moneyDialog_strNoNull);
                            return;
                        }
                        int money = Integer.parseInt(msg);
                        if (money == 0){
                            ToastUtils.showShortToast(moneyDialog_strNoZero);
                            return;
                        }
                        if (money>1000) {
                            ToastUtils.showShortToast(moneyDialog_strMax);
                            return;
                        }
                        sendChangeMoney(msg);
                    }
                }
            };
        }
        moneyDialog.show();
    }

    /**
     * 发送变更金额请求
     * @param msg
     */
    private void sendChangeMoney(String msg){
        loadingDialog.show();
        baseController.sendChangeMoney(token,jwt);
    }

    /**
     * 变更金额成功
     */
    private void changeMoneySuccess(){
        moneyDialog.dismiss();
        int x = (int)(Math.random()*100);
        orderStrMoney = x+"";
        orderTvMoney.setText(orderStrMoney);
        ToastUtils.showShortToast("变更金额成功");
    }

    /**
     * 变更金额失败
     */
    private void changeMoneyFailure(){
        ToastUtils.showShortToast("变更金额失败");
    }

}
