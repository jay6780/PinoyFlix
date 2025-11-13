package com.m.freemovie.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.m.freemovie.R;
import com.m.freemovie.Utils.SPUtils;
import com.m.freemovie.adapter.DetailAdapter;
import com.m.freemovie.mvp.ClassBean.DetailBean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
public class BookmarkFragment extends Fragment {
    private RecyclerView rv_bookmark;
    private DetailAdapter detailAdapter;
    private SPUtils spUtils;
    private List<DetailBean> movieBeanList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmark, container, false);
        rv_bookmark = view.findViewById(R.id.rv_bookmark);
        spUtils = SPUtils.getInstance("detailPrefs");
        initRecycler();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadBookmarkData();
    }

    private void initRecycler() {
        rv_bookmark.setLayoutManager(new GridLayoutManager(getContext(), 2));
        detailAdapter = new DetailAdapter(getContext(), movieBeanList);
        rv_bookmark.setAdapter(detailAdapter);
    }

    private void loadBookmarkData() {
        List<DetailBean> bookmarks = getDetails();
        movieBeanList.clear();
        movieBeanList.addAll(bookmarks);
        detailAdapter.notifyDataSetChanged();
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