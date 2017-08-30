package com.qingfeng.livesocial.ui.fragment;

import android.view.View;

import com.google.gson.Gson;
import com.qingfeng.livesocial.R;
import com.qingfeng.livesocial.bean.GiftRespBean;
import com.qingfeng.livesocial.common.QFApplication;
import com.qingfeng.livesocial.common.Urls;
import com.qingfeng.livesocial.ui.base.BaseFragment;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

import static com.qingfeng.livesocial.common.Constants.PARAM_GIFT_RECEIVE_TYPE;
import static com.qingfeng.livesocial.common.Constants.PARAM_TYPE;
import static com.qingfeng.livesocial.common.Constants.PARAM_UID;
import static com.qingfeng.livesocial.common.Constants.PARAM_Y;

/**
 * Created by Administrator on 2017/8/29.
 */

public class MyGiftFragment extends BaseFragment {
    @Override
    protected int getLayoutId() {
        return R.layout.my_gift_fragment_layout;
    }

    @Override
    protected void initWidget(View root) {

    }

    @Override
    protected void initData() {
        receiveGift();
    }

    private void receiveGift() {
        RequestParams params = new RequestParams(Urls.GIFT);
        params.addParameter(PARAM_UID, QFApplication.getInstance().getLoginUser().getUid());
        params.addParameter(PARAM_TYPE, PARAM_GIFT_RECEIVE_TYPE);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("receiveGift == " + result);
                GiftRespBean respBean = new Gson().fromJson(result, GiftRespBean.class);
                if (respBean != null && PARAM_Y.equals(respBean.getMsg())) {
                    List<GiftRespBean.GiftBean> datas = respBean.getResult();
                    if (datas != null && datas.size() > 0) {

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
}
