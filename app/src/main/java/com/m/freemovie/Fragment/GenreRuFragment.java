package com.m.freemovie.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.m.freemovie.Activity.MovieRuAllActivity;
import com.m.freemovie.R;
import com.m.freemovie.adapter.MovieRuAllAdapter;
import com.m.freemovie.databinding.FragmentGenreRuBinding;
import com.m.freemovie.mvp.Contract.PinoyRuMovieAllContract;
import com.m.freemovie.mvp.Model.ClassBean.PinoyAllBean;
import com.m.freemovie.mvp.Model.ClassBean.PinoyMovieRuBean;
import com.m.freemovie.mvp.Presenter.PinoyRuAllPresenter;

import java.util.ArrayList;
import java.util.List;

public class GenreRuFragment extends Fragment implements View.OnClickListener, PinoyRuMovieAllContract.View {
    private FragmentGenreRuBinding binding;
    private PinoyRuAllPresenter presenter;
    private int perPage = 10;
    private int page = 1;
    private MovieRuAllAdapter actionAdapter, romanceAdapter, comedyAdapter, vivaAdapter;
    private List<PinoyAllBean> actionList = new ArrayList<>();
    private List<PinoyAllBean> romanceList = new ArrayList<>();
    private List<PinoyAllBean> comedyList = new ArrayList<>();
    private List<PinoyAllBean> vivaList = new ArrayList<>();
    private boolean isRefresh = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGenreRuBinding.inflate(inflater);
        presenter = new PinoyRuAllPresenter(this);
        presenter.getActionPageQuery("26", perPage, page);
        presenter.getRomanceQuery("52", perPage, page);
        presenter.getComedyQuery("15", perPage, page);
        presenter.getPage(1);

        binding.tvViva.setOnClickListener(this);
        binding.tvAction.setOnClickListener(this);
        binding.tvRomance.setOnClickListener(this);
        binding.tvComedy.setOnClickListener(this);


        initRecycler();

        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        return binding.getRoot();
    }


    private void refresh() {
        if (!isNetworkAvailable()) {
            if (binding.swipeRefreshLayout.isRefreshing()) {
                binding.swipeRefreshLayout.setRefreshing(false);
            }
            Toast.makeText(getContext(), "Please check network and try again", Toast.LENGTH_SHORT).show();
            return;
        }
        clearAllData();
        page = 1;
        isRefresh = true;
        presenter.getActionPageQuery("26", perPage, page);
        presenter.getRomanceQuery("52", perPage, page);
        presenter.getComedyQuery("15", perPage, page);
        presenter.getPage(1);
        if (isRefresh) {
            isRefresh = false;
            List<MovieRuAllAdapter> adapterList = new ArrayList<>();
            adapterList.add(actionAdapter);
            adapterList.add(romanceAdapter);
            adapterList.add(comedyAdapter);
            adapterList.add(vivaAdapter);
            for (MovieRuAllAdapter adapter : adapterList) {
                adapter.clearCache();
            }
        }
    }

    private void clearAllData() {
        binding.rvAction.setVisibility(View.GONE);
        binding.rlAction.setVisibility(View.GONE);

        binding.rvRomance.setVisibility(View.GONE);
        binding.rlRomance.setVisibility(View.GONE);

        binding.rvComedy.setVisibility(View.GONE);
        binding.rlComedy.setVisibility(View.GONE);

        binding.rvViva.setVisibility(View.GONE);
        binding.rlViva.setVisibility(View.GONE);

        actionList.clear();
        romanceList.clear();
        comedyList.clear();
        vivaList.clear();

        actionAdapter.setNewData(actionList);
        binding.rvAction.scrollToPosition(0);

        romanceAdapter.setNewData(actionList);
        binding.rvRomance.scrollToPosition(0);

        comedyAdapter.setNewData(actionList);
        binding.rvComedy.scrollToPosition(0);

        vivaAdapter.setNewData(actionList);
        binding.rvViva.scrollToPosition(0);
    }


    @SuppressWarnings("deprecation")
    @SuppressLint("MissingPermission")
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void initRecycler() {
        actionAdapter = new MovieRuAllAdapter();
        romanceAdapter = new MovieRuAllAdapter();
        comedyAdapter = new MovieRuAllAdapter();
        vivaAdapter = new MovieRuAllAdapter();

        binding.rvAction.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvAction.setAdapter(actionAdapter);

        binding.rvRomance.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvRomance.setAdapter(romanceAdapter);

        binding.rvComedy.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvComedy.setAdapter(comedyAdapter);

        binding.rvViva.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvViva.setAdapter(vivaAdapter);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_action:
                startActivity(new Intent(getContext(), MovieRuAllActivity.class)
                        .putExtra("type", "Action")
                        .putExtra("typeData", 1));
                break;
            case R.id.tv_romance:
                startActivity(new Intent(getContext(), MovieRuAllActivity.class)
                        .putExtra("type", "Romance")
                        .putExtra("typeData", 2));
                break;
            case R.id.tv_comedy:
                startActivity(new Intent(getContext(), MovieRuAllActivity.class)
                        .putExtra("type", "Comedy")
                        .putExtra("typeData", 3));
                break;
            case R.id.tv_viva:
                startActivity(new Intent(getContext(), MovieRuAllActivity.class)
                        .putExtra("type", "VivaMax")
                        .putExtra("typeData", 4));
                break;
        }

    }

    @Override
    public void showLoading() {
        binding.swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void showError(String error) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), "Error fetching data: " + error, Toast.LENGTH_SHORT).show();
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        }, 500);
    }

    @Override
    public void hideLoading() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        }, 500);
    }


    @Override
    public void getActionList(List<PinoyMovieRuBean> bean) {
        if (bean != null) {
            for (PinoyMovieRuBean data : bean) {
                actionList.add(new PinoyAllBean(data.getLink(), data.getTitle().getRendered(), data.getId(), 1));
            }
            if (!actionList.isEmpty()) {
                binding.rvAction.setVisibility(View.VISIBLE);
                binding.rlAction.setVisibility(View.VISIBLE);
                actionAdapter.setNewData(actionList);
                if (!isRefresh) {
                    actionAdapter.clearCache();
                }
            } else {
                binding.rvAction.setVisibility(View.GONE);
                binding.rlAction.setVisibility(View.GONE);
                Toast.makeText(getContext(), "No data", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void getRomanceList(List<PinoyMovieRuBean> bean) {
        if (bean != null) {
            for (PinoyMovieRuBean data : bean) {
                romanceList.add(new PinoyAllBean(data.getLink(), data.getTitle().getRendered(), data.getId(), 2));
            }
            if (!romanceList.isEmpty()) {
                binding.rvRomance.setVisibility(View.VISIBLE);
                binding.rlRomance.setVisibility(View.VISIBLE);
                romanceAdapter.setNewData(romanceList);
                if (!isRefresh) {
                    romanceAdapter.clearCache();
                }
            } else {
                binding.rvRomance.setVisibility(View.GONE);
                binding.rlRomance.setVisibility(View.GONE);
                Toast.makeText(getContext(), "No data", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void getComedyList(List<PinoyMovieRuBean> bean) {
        if (bean != null) {
            for (PinoyMovieRuBean data : bean) {
                comedyList.add(new PinoyAllBean(data.getLink(), data.getTitle().getRendered(), data.getId(), 3));
            }
            if (!comedyList.isEmpty()) {
                binding.rvComedy.setVisibility(View.VISIBLE);
                binding.rlComedy.setVisibility(View.VISIBLE);
                comedyAdapter.setNewData(comedyList);
                if (!isRefresh) {
                    comedyAdapter.clearCache();
                }
            } else {
                binding.rvComedy.setVisibility(View.GONE);
                binding.rlComedy.setVisibility(View.GONE);
                Toast.makeText(getContext(), "No data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void getMovieList(List<PinoyMovieRuBean> bean) {
        if (bean != null) {
            for (PinoyMovieRuBean data : bean) {
                vivaList.add(new PinoyAllBean(data.getLink(), data.getTitle().getRendered(), data.getId(), 4));
            }
            if (!vivaList.isEmpty()) {
                binding.rvViva.setVisibility(View.VISIBLE);
                binding.rlViva.setVisibility(View.VISIBLE);
                vivaAdapter.setNewData(vivaList);
                if (!isRefresh) {
                    vivaAdapter.clearCache();
                }
            } else {
                binding.rvViva.setVisibility(View.GONE);
                binding.rlViva.setVisibility(View.GONE);
                Toast.makeText(getContext(), "No data", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
