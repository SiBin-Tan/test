package com.however.yiduobang_v20;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.however.yiduobang_v20.Base.BaseActivity;
import com.however.yiduobang_v20.Controller.BaseController;
import com.however.yiduobang_v20.Model.ErrorModel;
import com.however.yiduobang_v20.Model.ResponseModel;
import com.however.yiduobang_v20.Utils.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tansibin on 2018/2/12.
 */

public class HistoryActivity extends BaseActivity {

    @BindColor(R.color.green)
    int GREEN;
    @BindColor(R.color.appTheme)
    int RED;

    @BindView(R.id.toolbar_tvTitle)
    TextView toolbarTvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tvAll)
    TextView tvAll;
    @BindView(R.id.listView)
    ListView listView;

    private View loadingView;
    private TextView loadingViewTv;
    private ProgressBar loadingViewPro;
    private MyAdapter myAdapter;

    private ArrayList<String> listTime = new ArrayList<>();
    private ArrayList<String> listState = new ArrayList<>();
    private ArrayList<String> listMoney = new ArrayList<>();

    private String phone;

    private int start = 0;

    private Boolean isLoading = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        getIntentData();
        initView();
        getHistoryData();
    }

    /**
     * 获取相关数据
     */
    private void getIntentData() {
        Bundle bundle = this.getIntent().getExtras();
        phone = bundle.getString(BaseController.PHONE);
    }

    /**
     * 初始化ui相关
     */
    private void initView() {
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadingView = LayoutInflater.from(this).inflate(R.layout.listview_item_loading, null);
        loadingViewTv = (TextView)loadingView.findViewById(R.id.listview_tvLoad);
        loadingViewPro = (ProgressBar)loadingView.findViewById(R.id.listview_progress);
        loadingView.setVisibility(View.GONE);
        listView.addFooterView(loadingView);
        myAdapter = new MyAdapter(this);
        listView.setAdapter(myAdapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstItem, int visibleItem, int totalItem) {
                LogUtils.e(totalItem+"");
                if (!isLoading){
                    int position = listView.getLastVisiblePosition();
                    if (position == totalItem -3)
                        getHistoryData();
                }

            }
        });
    }

    /**
     * 销毁HistoryActivity
     */
    private void finishActivity() {
        finish();
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finishActivity();
        return super.onOptionsItemSelected(item);
    }

    /**
     * 获取服务器历史订单数据
     */
    private void getHistoryData() {
        isLoading = true;
        int end = 0;
        if (start == 0)
            end = 10;
        else
            end = start + 10;
        loadingView.setVisibility(View.VISIBLE);
        baseController.getHistoryData(phone,start+"",end+"");
    }

    private class MyAdapter extends BaseAdapter {

        private final String SUCCESS = "已支付";
        private final String FAIL = "取消订单";

        private class ViewHolder {
            public TextView tvTime;
            public TextView tvState;
            public TextView tvMoney;
        }

        private Context context;
        private ViewHolder viewHolder;

        public MyAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return listTime.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.listview_item, null);
                viewHolder = new ViewHolder();
                viewHolder.tvTime = (TextView) view.findViewById(R.id.tvTime);
                viewHolder.tvState = (TextView) view.findViewById(R.id.tvState);
                viewHolder.tvMoney = (TextView) view.findViewById(R.id.tvMoney);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.tvTime.setText(listTime.get(i));
            viewHolder.tvMoney.setText("¥ "+listMoney.get(i));
            String msg = listState.get(i);
            if (msg.equals(FAIL)) {
                msg = FAIL;
                viewHolder.tvState.setTextColor(RED);
            } else {
                msg = SUCCESS;
                viewHolder.tvState.setTextColor(GREEN);
            }
            viewHolder.tvState.setText(msg);
            return view;
        }
    }

    /**
     * EventBus 接收方法，接收服务器访问成功相关
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResponseSuccess(String msg) {
        if (msg.equals("[]")){
            if (start == 0){
                loadingView.setVisibility(View.VISIBLE);
                loadingViewTv.setText("暂无订单");
                loadingViewPro.setVisibility(View.GONE);
            }else{
                loadingView.setVisibility(View.VISIBLE);
                loadingViewTv.setText("已显示所有订单");
                loadingViewPro.setVisibility(View.GONE);
            }
        }else{
            JsonParser jsonParser = new JsonParser();
            JsonArray jsonArray = jsonParser.parse(msg).getAsJsonArray();
            MyModel myModel;
            int size = 0;
            for (JsonElement element : jsonArray){
                myModel = gson.fromJson(element,MyModel.class);
                String time = myModel.updatedAt;
                time = time.substring(0, time.indexOf("T"));
                listTime.add(time);
                listState.add(myModel.state);
                listMoney.add(myModel.money);
                size++;
            }
            loadingView.setVisibility(View.GONE);
            if (size <10 ){
                loadingView.setVisibility(View.VISIBLE);
                loadingViewTv.setText("以显示所有订单");
                loadingViewPro.setVisibility(View.GONE);
            }
            tvAll.setText("共"+listTime.size()+"单");
        }
        myAdapter.notifyDataSetChanged();
        start = listTime.size();
        isLoading = false;
    }

    /**
     * EventBus 接收方法，接收服务器访问失败相关
     *
     * @param model
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResponseFailure(ErrorModel model) {
        int code = model.errorCode;
        if (code == BaseController.SERVERS_ERROR_CODE) {
            //服务器错误
        }
        isLoading = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private class MyModel{
        private String updatedAt;
        private String state;
        private String money;
    }
}
