package com.m.freemovie.mvp.Presenter;

import com.m.freemovie.mvp.ClassBean.DetailBean;
import com.m.freemovie.mvp.Contract.DetailContract;
import com.m.freemovie.mvp.Model.DetailModel;

public class DetailPresenter implements DetailContract.Presenter {
    private DetailContract.View view;
    private DetailModel model;

    public DetailPresenter(DetailContract.View view) {
        this.view = view;
        this.model = new DetailModel();
    }


    @Override
    public void getDetail(String id, String apiKey) {
        view.showLoading();
        model.getDetailData(id,apiKey, new DetailModel.DetailListener() {
            @Override
            public void onSuccess(DetailBean movieBean) {
                view.hideLoading();
                view.getDetailResponse(movieBean);
            }

            @Override
            public void onError(String error) {
                view.hideLoading();
                view.showError(error);
            }
        });
    }
}