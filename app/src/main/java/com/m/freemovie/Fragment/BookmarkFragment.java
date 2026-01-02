package com.m.freemovie.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.m.freemovie.Utils.DbHelper.BookmarkDbHelper;
import com.m.freemovie.adapter.DetailAdapter;
import com.m.freemovie.databinding.FragmentBookmarkBinding;
import com.m.freemovie.mvp.ClassBean.DetailBean;
import com.m.freemovie.mvp.ClassBean.MovieEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
public class BookmarkFragment extends Fragment {
    private DetailAdapter detailAdapter;
    private List<DetailBean> movieBeanList = new ArrayList<>();
    private FragmentBookmarkBinding binding;
    private BookmarkDbHelper dbHelper;
    private int position = 1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBookmarkBinding.inflate(inflater);
        dbHelper = new BookmarkDbHelper(getContext());
        if(position == 1){
            initRecycler();
            loadBookmarkData();
        }
        return binding.getRoot();
    }

    private boolean isValidPosition() {
        return position == 1 || position == 2;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isValidPosition()) {
            loadBookmarkData();
        }
    }
    private void initRecycler() {
        binding.rvBookmark.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        detailAdapter = new DetailAdapter();
        binding.rvBookmark.setAdapter(detailAdapter);
    }

    private void loadBookmarkData() {
        boolean isTvSeries = false;
        if(position == 1){
            isTvSeries = false;
        }else if (position == 2){
            isTvSeries = true;
        }
        List<DetailBean> bookmarks = dbHelper.getBookmarksByType(isTvSeries);
        movieBeanList.clear();
        movieBeanList.addAll(bookmarks);
        if(detailAdapter !=null){
            detailAdapter.setNewData(movieBeanList);
        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void changeSearch(MovieEvent event) {
        this.position = event.getPosition();
        if(isValidPosition()){
            initRecycler();
            loadBookmarkData();
        }else{
            detailAdapter.setNewData(new ArrayList<>());
        }
        boolean isTvSeries = false;
        if(position == 1){
            isTvSeries = false;
        }else if (position == 2){
            isTvSeries = true;
        }
        detailAdapter.isTv(isTvSeries);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}