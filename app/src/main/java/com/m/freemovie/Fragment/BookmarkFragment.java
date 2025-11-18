package com.m.freemovie.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.m.freemovie.Utils.SPUtils;
import com.m.freemovie.adapter.DetailAdapter;
import com.m.freemovie.databinding.FragmentBookmarkBinding;
import com.m.freemovie.mvp.ClassBean.DetailBean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
public class BookmarkFragment extends Fragment {
    private DetailAdapter detailAdapter;
    private SPUtils spUtils;
    private List<DetailBean> movieBeanList = new ArrayList<>();
    private FragmentBookmarkBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBookmarkBinding.inflate(inflater);
        spUtils = SPUtils.getInstance("detailPrefs");
        initRecycler();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadBookmarkData();
    }

    private void initRecycler() {
        binding.rvBookmark.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        detailAdapter = new DetailAdapter();
        binding.rvBookmark.setAdapter(detailAdapter);
    }

    private void loadBookmarkData() {
        List<DetailBean> bookmarks = getDetails();
        movieBeanList.clear();
        movieBeanList.addAll(bookmarks);
        detailAdapter.setNewData(movieBeanList);
    }

    private List<DetailBean> getDetails() {
        List<DetailBean> detailBeanArrayList = new ArrayList<>();
        try {
            String scoresJson = spUtils.getString("detailPrefs", "[]");
            JSONArray jsonArray = new JSONArray(scoresJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                DetailBean detailBean = new DetailBean();
                detailBean.setVideoId(jsonObject.getString("videoId"));
                detailBean.setTimeStamp(jsonObject.getString("timeStamp"));
                detailBean.setTempImage(jsonObject.getString("tempImage"));
                detailBean.setMovieName(jsonObject.getString("movieName"));
                detailBeanArrayList.add(detailBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detailBeanArrayList;
    }
}