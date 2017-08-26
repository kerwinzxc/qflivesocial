package com.qingfeng.livesocial.ui.fragment;

import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.qingfeng.livesocial.R;
import com.qingfeng.livesocial.adapter.HRecyclerViewAdapter;
import com.qingfeng.livesocial.adapter.MViewPagerAdapter;
import com.qingfeng.livesocial.bean.RecommedRespBean;
import com.qingfeng.livesocial.bean.SlideRepsBean;
import com.qingfeng.livesocial.bean.TotalLiveUserBean;
import com.qingfeng.livesocial.common.QFApplication;
import com.qingfeng.livesocial.common.Urls;
import com.qingfeng.livesocial.ui.TestActivity;
import com.qingfeng.livesocial.ui.base.BaseFragment;
import com.qingfeng.livesocial.util.StringUtils;
import com.qingfeng.livesocial.widget.MViewPager;
import com.qingfeng.livesocial.widget.SlideShowView;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

import static com.qingfeng.livesocial.common.Constants.PARAM_TYPE;
import static com.qingfeng.livesocial.common.Constants.PARAM_UID;
import static com.qingfeng.livesocial.common.Constants.PARAM_Y;


/**
 * Created by Administrator on 2017/8/22.
 */
public class HomeFragment extends BaseFragment {
    @Bind(R.id.tab)
    TabLayout mTabLayout;
    @Bind(R.id.tab_viewpage)
    MViewPager mViewPager;
    @Bind(R.id.slideshowview)
    SlideShowView mSlideShowView;

    RecyclerView hRecyclerView;
    LinearLayout ll_all_container, ll_popular_container, ll_youngshow_container;
    private View view1, view2, view3, view4;
    private LayoutInflater mInflater;
    private final List<View> mViewList = new ArrayList<>();
    private final String[] mTitleArr = {"全部", "认证", "人气", "新秀"};
    private static final String TYPE_ALL_USER = "1"; //全部
    private static final String TYPE_POPULAR_USER = "2";//人气
    private static final String TYPE_YOUNGSHOW_USER = "3";//新秀

    @Override
    protected int getLayoutId() {
        return R.layout.home_fragment_layout;
    }

    @Override
    protected void initWidget(View root) {
        mInflater = LayoutInflater.from(getActivity());
        view1 = mInflater.inflate(R.layout.home_tab_all_layout, null);
        view2 = mInflater.inflate(R.layout.home_tab_authen_layout, null);
        view3 = mInflater.inflate(R.layout.home_tab_popularity_layout, null);
        view4 = mInflater.inflate(R.layout.home_tab_youngshow_layout, null);
        hRecyclerView = (RecyclerView) view1.findViewById(R.id.hor_listview);
        ll_all_container = (LinearLayout) view1.findViewById(R.id.ll_container);
        ll_popular_container = (LinearLayout) view3.findViewById(R.id.ll_container);
        ll_youngshow_container = (LinearLayout) view4.findViewById(R.id.ll_container);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void initData() {
        mViewList.clear();
        mViewList.add(view1);
        mViewList.add(view2);
        mViewList.add(view3);
        mViewList.add(view4);

        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleArr[0]), true);
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleArr[1]));
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleArr[2]));
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleArr[3]));

        MViewPagerAdapter mAdapter = new MViewPagerAdapter(mViewList, mTitleArr);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabsFromPagerAdapter(mAdapter);

        getSlideImg();
        getRecommendData();
        getAllUserInfo();
        getPopularUserInfo();
        getYoungShowUserInfo();
    }

    @OnClick({R.id.ranklist})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ranklist:
                gotoActivity(getActivity(), TestActivity.class);
                break;
        }
    }

    /**
     * 轮播图
     */
    private void getSlideImg() {
        RequestParams params = new RequestParams(Urls.SLIDE);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("getSlideImg == " + result);
                SlideRepsBean slideRepsBean = new Gson().fromJson(result, SlideRepsBean.class);
                List<SlideRepsBean.SlideBean> slideBeanList = slideRepsBean.getResult();
                String[] imageUrls = new String[slideBeanList.size()];
                for (int i = 0; i < slideBeanList.size(); i++) {
                    imageUrls[i] = slideBeanList.get(i).getPic();
                }
                mSlideShowView.bindImgSource(imageUrls);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e(ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }

    /**
     * 全部
     */
    private void getAllUserInfo() {
        RequestParams params = new RequestParams(Urls.RECOMMEND);
        params.addParameter(PARAM_UID, QFApplication.getInstance().getLoginUser().getUid());
        params.addParameter(PARAM_TYPE, TYPE_ALL_USER);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("getAllUserInfo == " + result);
                Gson gson = new Gson();
                TotalLiveUserBean bean = gson.fromJson(result, TotalLiveUserBean.class);
                if (PARAM_Y.equals(bean.getMsg())) {
                    List<TotalLiveUserBean.LiveUserBean> datas = bean.getResult();
                    int size = datas.size() % 2 == 0 ? datas.size() / 2 : datas.size() / 2 + 1;
                    for (int i = 0; i < size; i++) {
                        LinearLayout ll = (LinearLayout) mInflater.inflate(R.layout.anchor_show_layout, null);
                        for (int j = 0; j < datas.size(); j += 2) {
                            TextView nickName1 = (TextView) ll.findViewById(R.id.tv_nickname1);
                            TextView nickName2 = (TextView) ll.findViewById(R.id.tv_nickname2);
                            TextView age = (TextView) ll.findViewById(R.id.tv_age);
                            TextView age2 = (TextView) ll.findViewById(R.id.tv_age2);
                            TextView signature1 = (TextView) ll.findViewById(R.id.tv_signature1);
                            TextView signature2 = (TextView) ll.findViewById(R.id.tv_signature2);
                            ImageView imageHead1 = (ImageView) ll.findViewById(R.id.img_head1);
                            ImageView imageHead2 = (ImageView) ll.findViewById(R.id.img_head2);
                            Button btnlabel11 = (Button) ll.findViewById(R.id.btn_label11);
                            Button btnlabel12 = (Button) ll.findViewById(R.id.btn_label12);
                            Button btnlabel13 = (Button) ll.findViewById(R.id.btn_label13);
                            List<Button> btnContainer1 = new ArrayList<Button>();
                            btnContainer1.add(btnlabel11);
                            btnContainer1.add(btnlabel12);
                            btnContainer1.add(btnlabel13);
                            Button btnlabel21 = (Button) ll.findViewById(R.id.btn_label21);
                            Button btnlabel22 = (Button) ll.findViewById(R.id.btn_label22);
                            Button btnlabel23 = (Button) ll.findViewById(R.id.btn_label23);
                            List<Button> btnContainer2 = new ArrayList<Button>();
                            btnContainer2.add(btnlabel21);
                            btnContainer2.add(btnlabel22);
                            btnContainer2.add(btnlabel23);
                            if (datas.get(j) != null) {
                                nickName1.setText(datas.get(j).getNickname());
                                age.setText(datas.get(j).getAge());
                                signature1.setText(datas.get(j).getSignature());
                                String labels = datas.get(j).getLabels();
                                if (!StringUtils.isEmpty(labels)) {
                                    String[] labelArr = labels.split(",");
                                    for (int k = 0; k < labelArr.length; k++) {
                                        btnContainer1.get(k).setVisibility(View.VISIBLE);
                                        btnContainer1.get(k).setText(labelArr[k]);
                                    }
                                }
                                x.image().bind(imageHead1,
                                        datas.get(j).getAnchorpic(),
                                        imageOptions,
                                        null);
                            }
                            if (datas.get(j + 1) != null) {
                                nickName2.setText(datas.get(j + 1).getNickname());
                                age2.setText(datas.get(j + 1).getAge());
                                signature2.setText(datas.get(j + 1).getSignature());
                                String labels = datas.get(j + 1).getLabels();
                                if (!StringUtils.isEmpty(labels)) {
                                    String[] labelArr = labels.split(",");
                                    for (int k = 0; k < labelArr.length; k++) {
                                        btnContainer2.get(k).setVisibility(View.VISIBLE);
                                        btnContainer2.get(k).setText(labelArr[k]);
                                    }
                                }
                                x.image().bind(imageHead2,
                                        datas.get(j + 1).getAnchorpic(),
                                        imageOptions,
                                        null);
                            }
                        }
                        ll_all_container.addView(ll);
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e(ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }


    /**
     * 人气
     */
    private void getPopularUserInfo() {
        RequestParams params = new RequestParams(Urls.RECOMMEND);
        params.addParameter(PARAM_UID, QFApplication.getInstance().getLoginUser().getUid());
        params.addParameter(PARAM_TYPE, TYPE_POPULAR_USER);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("getPopularUserInfo == " + result);
                Gson gson = new Gson();
                TotalLiveUserBean bean = gson.fromJson(result, TotalLiveUserBean.class);
                if (PARAM_Y.equals(bean.getMsg())) {
                    List<TotalLiveUserBean.LiveUserBean> datas = bean.getResult();
                    int size = datas.size() % 2 == 0 ? datas.size() / 2 : datas.size() / 2 + 1;
                    for (int i = 0; i < size; i++) {
                        LinearLayout ll = (LinearLayout) mInflater.inflate(R.layout.anchor_show_layout, null);
                        for (int j = 0; j < datas.size(); j += 2) {
                            TextView nickName1 = (TextView) ll.findViewById(R.id.tv_nickname1);
                            TextView nickName2 = (TextView) ll.findViewById(R.id.tv_nickname2);
                            TextView age = (TextView) ll.findViewById(R.id.tv_age);
                            TextView age2 = (TextView) ll.findViewById(R.id.tv_age2);
                            TextView signature1 = (TextView) ll.findViewById(R.id.tv_signature1);
                            TextView signature2 = (TextView) ll.findViewById(R.id.tv_signature2);
                            ImageView imageHead1 = (ImageView) ll.findViewById(R.id.img_head1);
                            ImageView imageHead2 = (ImageView) ll.findViewById(R.id.img_head2);
                            Button btnlabel11 = (Button) ll.findViewById(R.id.btn_label11);
                            Button btnlabel12 = (Button) ll.findViewById(R.id.btn_label12);
                            Button btnlabel13 = (Button) ll.findViewById(R.id.btn_label13);
                            List<Button> btnContainer1 = new ArrayList<Button>();
                            btnContainer1.add(btnlabel11);
                            btnContainer1.add(btnlabel12);
                            btnContainer1.add(btnlabel13);
                            Button btnlabel21 = (Button) ll.findViewById(R.id.btn_label21);
                            Button btnlabel22 = (Button) ll.findViewById(R.id.btn_label22);
                            Button btnlabel23 = (Button) ll.findViewById(R.id.btn_label23);
                            List<Button> btnContainer2 = new ArrayList<Button>();
                            btnContainer2.add(btnlabel21);
                            btnContainer2.add(btnlabel22);
                            btnContainer2.add(btnlabel23);

                            if (datas.get(j) != null) {
                                nickName1.setText(datas.get(j).getNickname());
                                age.setText(datas.get(j).getAge());
                                signature1.setText(datas.get(j).getSignature());
                                String labels = datas.get(j).getLabels();
                                if (!StringUtils.isEmpty(labels)) {
                                    String[] labelArr = labels.split(",");
                                    for (int k = 0; k < labelArr.length; k++) {
                                        btnContainer1.get(k).setVisibility(View.VISIBLE);
                                        btnContainer1.get(k).setText(labelArr[k]);
                                    }
                                }
                                x.image().bind(imageHead1,
                                        datas.get(j).getAnchorpic(),
                                        imageOptions,
                                        null);
                            }
                            if (datas.get(j + 1) != null) {
                                nickName2.setText(datas.get(j + 1).getNickname());
                                age2.setText(datas.get(j + 1).getAge());
                                signature2.setText(datas.get(j + 1).getSignature());
                                String labels = datas.get(j + 1).getLabels();
                                if (!StringUtils.isEmpty(labels)) {
                                    String[] labelArr = labels.split(",");
                                    for (int k = 0; k < labelArr.length; k++) {
                                        btnContainer2.get(k).setVisibility(View.VISIBLE);
                                        btnContainer2.get(k).setText(labelArr[k]);
                                    }
                                }
                                x.image().bind(imageHead2,
                                        datas.get(j + 1).getAnchorpic(),
                                        imageOptions,
                                        null);
                            }
                        }
                        ll_popular_container.addView(ll);
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e(ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }

    /**
     * 新秀
     */
    private void getYoungShowUserInfo() {
        RequestParams params = new RequestParams(Urls.RECOMMEND);
        params.addParameter(PARAM_UID, QFApplication.getInstance().getLoginUser().getUid());
        params.addParameter(PARAM_TYPE, TYPE_YOUNGSHOW_USER);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("getPopularUserInfo == " + result);
                Gson gson = new Gson();
                TotalLiveUserBean bean = gson.fromJson(result, TotalLiveUserBean.class);
                if (PARAM_Y.equals(bean.getMsg())) {
                    List<TotalLiveUserBean.LiveUserBean> datas = bean.getResult();
                    int size = datas.size() % 2 == 0 ? datas.size() / 2 : datas.size() / 2 + 1;
                    for (int i = 0; i < size; i++) {
                        LinearLayout ll = (LinearLayout) mInflater.inflate(R.layout.anchor_show_layout, null);
                        for (int j = 0; j < datas.size(); j += 2) {
                            TextView nickName1 = (TextView) ll.findViewById(R.id.tv_nickname1);
                            TextView nickName2 = (TextView) ll.findViewById(R.id.tv_nickname2);
                            TextView age = (TextView) ll.findViewById(R.id.tv_age);
                            TextView age2 = (TextView) ll.findViewById(R.id.tv_age2);
                            TextView signature1 = (TextView) ll.findViewById(R.id.tv_signature1);
                            TextView signature2 = (TextView) ll.findViewById(R.id.tv_signature2);
                            ImageView imageHead1 = (ImageView) ll.findViewById(R.id.img_head1);
                            ImageView imageHead2 = (ImageView) ll.findViewById(R.id.img_head2);
                            Button btnlabel11 = (Button) ll.findViewById(R.id.btn_label11);
                            Button btnlabel12 = (Button) ll.findViewById(R.id.btn_label12);
                            Button btnlabel13 = (Button) ll.findViewById(R.id.btn_label13);
                            List<Button> btnContainer1 = new ArrayList<Button>();
                            btnContainer1.add(btnlabel11);
                            btnContainer1.add(btnlabel12);
                            btnContainer1.add(btnlabel13);
                            Button btnlabel21 = (Button) ll.findViewById(R.id.btn_label21);
                            Button btnlabel22 = (Button) ll.findViewById(R.id.btn_label22);
                            Button btnlabel23 = (Button) ll.findViewById(R.id.btn_label23);
                            List<Button> btnContainer2 = new ArrayList<Button>();
                            btnContainer2.add(btnlabel21);
                            btnContainer2.add(btnlabel22);
                            btnContainer2.add(btnlabel23);

                            if (datas.get(j) != null) {
                                nickName1.setText(datas.get(j).getNickname());
                                age.setText(datas.get(j).getAge());
                                signature1.setText(datas.get(j).getSignature());
                                String labels = datas.get(j).getLabels();
                                if (!StringUtils.isEmpty(labels)) {
                                    String[] labelArr = labels.split(",");
                                    for (int k = 0; k < labelArr.length; k++) {
                                        btnContainer1.get(k).setVisibility(View.VISIBLE);
                                        btnContainer1.get(k).setText(labelArr[k]);
                                    }
                                }
                                x.image().bind(imageHead1,
                                        datas.get(j).getAnchorpic(),
                                        imageOptions,
                                        null);
                            }
                            if (datas.get(j + 1) != null) {
                                nickName2.setText(datas.get(j + 1).getNickname());
                                age2.setText(datas.get(j + 1).getAge());
                                signature2.setText(datas.get(j + 1).getSignature());
                                String labels = datas.get(j + 1).getLabels();
                                if (!StringUtils.isEmpty(labels)) {
                                    String[] labelArr = labels.split(",");
                                    for (int k = 0; k < labelArr.length; k++) {
                                        btnContainer2.get(k).setVisibility(View.VISIBLE);
                                        btnContainer2.get(k).setText(labelArr[k]);
                                    }
                                }
                                x.image().bind(imageHead2,
                                        datas.get(j + 1).getAnchorpic(),
                                        imageOptions,
                                        null);
                            }
                        }
                        ll_youngshow_container.addView(ll);
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e(ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }

    /**
     * 今日推荐数据
     */
    private void getRecommendData() {
        RequestParams params = new RequestParams(Urls.DAY_RECOMMEND);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("getRecommendData == " + result);
                RecommedRespBean respon = new Gson().fromJson(result, RecommedRespBean.class);
                if (PARAM_Y.equals(respon.getMsg())) {
                    HRecyclerViewAdapter hRecyclerViewAdapter = new HRecyclerViewAdapter(getActivity(), respon.getResult(), imageOptions);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    layoutManager.setOrientation(OrientationHelper.HORIZONTAL);
                    hRecyclerView.setLayoutManager(layoutManager);
                    hRecyclerView.setHasFixedSize(true);
                    hRecyclerView.setAdapter(hRecyclerViewAdapter);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e(ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }
}
