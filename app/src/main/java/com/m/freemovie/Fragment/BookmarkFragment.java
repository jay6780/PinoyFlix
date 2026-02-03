package com.m.freemovie.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.core.Controller;
import com.app.hubert.guide.listener.OnGuideChangedListener;
import com.app.hubert.guide.model.GuidePage;
import com.app.hubert.guide.model.HighLight;
import com.m.freemovie.R;
import com.m.freemovie.Utils.DbHelper.BookmarkDbHelper;
import com.m.freemovie.adapter.DetailAdapter;
import com.m.freemovie.databinding.FragmentBookmarkBinding;
import com.m.freemovie.mvp.Model.ClassBean.DetailBean;

import java.util.ArrayList;
import java.util.List;
public class BookmarkFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private DetailAdapter detailAdapter;
    private List<DetailBean> movieBeanList = new ArrayList<>();
    private FragmentBookmarkBinding binding;
    private BookmarkDbHelper dbHelper;
    private int bookmarkposition = 1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBookmarkBinding.inflate(inflater);
        dbHelper = new BookmarkDbHelper(getContext());
        initRecycler();
        loadBookmarkData();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.Select_bookmark, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        binding.fragmentSpinner.setAdapter(adapter);
        binding.fragmentSpinner.setOnItemSelectedListener(this);
        binding.fragmentSpinner.setSelection(0);

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
        binding.llReset.setVisibility(View.GONE);
        binding.llReset.setOnClickListener(view -> reset());
        binding.rvBookmark.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
                    int[] lastVisiblePositions = layoutManager.findLastVisibleItemPositions(null);
                    if (lastVisiblePositions[0] > 5) {
                        binding.llReset.setVisibility(View.VISIBLE);
                        initGuide();
                    } else if (lastVisiblePositions[0] == 0) {
                        binding.llReset.setVisibility(View.GONE);
                    }
            }
        });

    }

    private void initGuide() {
        NewbieGuide.with(getActivity())
                .setLabel("book_reset")
                .setOnGuideChangedListener(new OnGuideChangedListener() {
                    @Override
                    public void onShowed(Controller controller) {

                    }

                    @Override
                    public void onRemoved(Controller controller) {
                    }
                })
                .addGuidePage(GuidePage.newInstance()
                        .addHighLight(binding.llReset, HighLight.Shape.ROUND_RECTANGLE, 1)
                        .setLayoutRes(R.layout.ll_reset_guide)
                )
                .show();
    }
    private void reset(){
        binding.rvBookmark.scrollToPosition(0);
        binding.llReset.setVisibility(View.GONE);
    }
    private void loadBookmarkData() {
        List<DetailBean> bookmarks = dbHelper.getBookmarksByType(bookmarkposition);
        movieBeanList.clear();
        movieBeanList.addAll(bookmarks);
        if(detailAdapter !=null){
            detailAdapter.setNewData(movieBeanList);
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

        switch (position){
            case 0:
                bookmarkposition = 1;
                break;
            case 1:
                bookmarkposition = 2;
                break;
            case 2:
                bookmarkposition = 7;
                break;
            case 3:
                bookmarkposition = 3;
                break;
            case 4:
                bookmarkposition = 4;
                break;
            case 5:
                bookmarkposition = 5;
                break;
        }
        loadBookmarkData();
        detailAdapter.isTv(bookmarkposition);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}