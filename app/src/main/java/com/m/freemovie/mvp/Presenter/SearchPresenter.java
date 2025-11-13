package com.m.freemovie.mvp.Presenter;

import com.m.freemovie.mvp.ClassBean.MovieBean;
import com.m.freemovie.mvp.Contract.SearchContract;
import com.m.freemovie.mvp.Model.MovieModel;

public class SearchPresenter implements SearchContract.Presenter {
    private SearchContract.View view;
    private MovieModel model;

    public SearchPresenter(SearchContract.View view) {
        this.view = view;
        this.model = new MovieModel();
    }


    @Override
    public void getSearchQuery(String apiKey, String query, int page) {
        view.showLoading();
        model.getSearch(apiKey,query,page, new MovieModel.VideoMovieListerner() {
            @Override
            public void onSuccess(MovieBean movieBean) {
                view.hideLoading();
                view.getSearchResponse(movieBean);
            }

            @Override
            public void onError(String error) {
                view.hideLoading();
                view.showError(error);
            }
        });
    }
}