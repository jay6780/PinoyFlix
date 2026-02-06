package com.m.freemovie.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.core.Controller;
import com.app.hubert.guide.listener.OnGuideChangedListener;
import com.app.hubert.guide.model.GuidePage;
import com.app.hubert.guide.model.HighLight;
import com.m.freemovie.R;
import com.m.freemovie.adapter.OthersAdapter;
import com.m.freemovie.databinding.FragmentOtherBinding;
import com.m.freemovie.mvp.Contract.OthersContract;
import com.m.freemovie.mvp.Model.ClassBean.OtherBean;
import com.m.freemovie.mvp.Presenter.OthersPresenter;

import java.util.ArrayList;
import java.util.List;

public class OtherFragment extends Fragment implements AdapterView.OnItemSelectedListener, OthersContract.View {
    private FragmentOtherBinding binding;
    private OthersPresenter presenter;
    private int page = 1;
    private OthersAdapter othersAdapter;
    private List<OtherBean.ResultsBean> otherList = new ArrayList<>();
    private boolean isNomore = false;
    private boolean isLoading = false;
    private int genrePosition = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOtherBinding.inflate(inflater);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.genre, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        binding.fragmentSpinner.setAdapter(adapter);
        binding.fragmentSpinner.setOnItemSelectedListener(this);
        binding.fragmentSpinner.setSelection(0);

        presenter = new OthersPresenter(this);
        initApi(genrePosition);
        presenter.getScienceFictionPage(page);
        initRecycler();

        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!isNetworkAvailable()){
                    Toast.makeText(getContext(),"Please check your internet and try again",Toast.LENGTH_SHORT).show();
                    binding.swipeRefreshLayout.setRefreshing(false);
                    return;
                }
                isNomore = false;
                page = 1;
                if(binding.llReset.getVisibility() == View.VISIBLE){
                    binding.llReset.setVisibility(View.GONE);
                }
                otherList.clear();
                othersAdapter.setNewData(otherList);

                initApi(genrePosition);
            }
        });


        return binding.getRoot();
    }

    private void initApi(int genrePosition) {
        switch (genrePosition) {
            case 0:
                presenter.getSciFiFantasyPage(page);
                break;

            case 1:
                presenter.getThaiDramaPage(page);
                break;

            case 2:
                presenter.getCrimePage(page);
                break;

            case 3:
                presenter.getRomancePage(page);
                break;

            case 4:
                presenter.getHistoryPage(page);
                break;

            case 5:
                presenter.getWarPage(page);
                break;

            case 6:
                presenter.getActionPage(page);
                break;

            case 7:
                presenter.getDramaPage(page);
                break;

            case 8:
                presenter.getMovieSpeakKhmerPage(page);
                break;

            case 9:
                presenter.getThrillerPage(page);
                break;

            case 10:
                presenter.getFantasyPage(page);
                break;

            case 11:
                presenter.getMusicPage(page);
                break;

            case 12:
                presenter.getWarPoliticsPage(page);
                break;

            case 13:
                presenter.getVivamaxPage(page);
                break;

            case 14:
                presenter.getTvMoviePage(page);
                break;

            case 15:
                presenter.getDocumentaryPage(page);
                break;

            case 16:
                presenter.getKoreaDramaPage(page);
                break;

            case 17:
                presenter.getMysteryPage(page);
                break;

            case 18:
                presenter.getAdventurePage(page);
                break;

            case 19:
                presenter.getComedyPage(page);
                break;

            case 20:
                presenter.getChineseDramaPage(page);
                break;

            case 21:
                presenter.getScienceFictionPage(page);
                break;

            case 22:
                presenter.getFamilyPage(page);
                break;

            case 23:
                presenter.getTvShowsPage(page);
                break;

            case 24:
                presenter.getEroticPage(page);
                break;

            case 25:
                presenter.getMoviePage(page);
                break;

            case 26:
                presenter.getAnimationPage(page);
                break;

            case 27:
                presenter.getHorrorPage(page);
                break;

            case 28:
                presenter.getAllMoviesPage(page);
                break;
        }
    }

    private void initRecycler() {
        binding.rvOther.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        othersAdapter = new OthersAdapter();
        binding.rvOther.setAdapter(othersAdapter);
        binding.llReset.setVisibility(View.GONE);
        binding.llReset.setOnClickListener(view -> reset());
        binding.rvOther.setHasFixedSize(true);
        binding.rvOther.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading && layoutManager != null) {
                    int[] lastVisiblePositions = layoutManager.findLastVisibleItemPositions(null);
                    if (lastVisiblePositions[0] > 5) {
                        binding.llReset.setVisibility(View.VISIBLE);
                        initGuide();
                    } else if (lastVisiblePositions[0] == 0) {
                        binding.llReset.setVisibility(View.GONE);
                    }
                    int lastVisiblePosition = getMaxPosition(lastVisiblePositions);


                    if (lastVisiblePosition >= otherList.size() - 1) {
                        if (isNomore) {
                            return;
                        }
                        isLoading = true;
                        page++;
                        initApi(genrePosition);
                    }

                }
            }
            private int getMaxPosition(int[] positions) {
                int max = positions[0];
                for (int position : positions) {
                    if (position > max) {
                        max = position;
                    }
                }
                return max;
            }
        });

        initialGuide();

    }

    private void initialGuide() {
        NewbieGuide.with(getActivity())
                .setLabel("genre_filter")
                .setOnGuideChangedListener(new OnGuideChangedListener() {
                    @Override
                    public void onShowed(Controller controller) {

                    }

                    @Override
                    public void onRemoved(Controller controller) {
                    }
                })
                .addGuidePage(GuidePage.newInstance()
                        .addHighLight(binding.fragmentSpinner, HighLight.Shape.ROUND_RECTANGLE, 1)
                        .setLayoutRes(R.layout.ll_genre)
                )
                .show();
    }

    private void initGuide() {
        NewbieGuide.with(getActivity())
                .setLabel("other_reset")
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
        binding.rvOther.scrollToPosition(0);
        binding.llReset.setVisibility(View.GONE);
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("MissingPermission")
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        this.genrePosition = position;
        page = 1;
        isNomore = false;
        otherList.clear();
        othersAdapter.setNewData(otherList);
        if(binding.llReset.getVisibility() == View.VISIBLE){
            binding.llReset.setVisibility(View.GONE);
        }
        initApi(genrePosition);


    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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
                Toast.makeText(getContext(),"Error fetching data: "+error,Toast.LENGTH_SHORT).show();
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
    public void getSciFiFantasy(OtherBean otherBean) {
        if(otherBean !=null && otherBean.getResults() !=null){
            if(!otherBean.getResults().isEmpty()){
                otherList.addAll(otherBean.getResults());
                othersAdapter.setNewData(otherList);
            }else{
                isNomore = true;
            }

        }else{
            isNomore = true;
        }

    }

    @Override
    public void getThaiDrama(OtherBean otherBean) {
        if(otherBean !=null && otherBean.getResults() !=null){
            isLoading = false;
            if(!otherBean.getResults().isEmpty()){
                otherList.addAll(otherBean.getResults());
                othersAdapter.setNewData(otherList);
            }else{
                isNomore = true;
            }

        }else{
            isNomore = true;
        }

    }

    @Override
    public void getCrime(OtherBean otherBean) {
        if(otherBean !=null && otherBean.getResults() !=null){
            isLoading = false;
            if(!otherBean.getResults().isEmpty()){
                otherList.addAll(otherBean.getResults());
                othersAdapter.setNewData(otherList);
            }else{
                isNomore = true;
            }

        }else{
            isNomore = true;
        }
    }

    @Override
    public void getRomance(OtherBean otherBean) {
        if(otherBean !=null && otherBean.getResults() !=null){
            isLoading = false;
            if(!otherBean.getResults().isEmpty()){
                otherList.addAll(otherBean.getResults());
                othersAdapter.setNewData(otherList);
            }else{
                isNomore = true;
            }

        }else{
            isNomore = true;
        }
    }

    @Override
    public void getHistory(OtherBean otherBean) {
        if(otherBean !=null && otherBean.getResults() !=null){
            isLoading = false;
            if(!otherBean.getResults().isEmpty()){
                otherList.addAll(otherBean.getResults());
                othersAdapter.setNewData(otherList);
            }else{
                Toast.makeText(getContext(),"No more data",Toast.LENGTH_SHORT).show();
                isNomore = true;
            }

        }else{
            isNomore = true;
        }
    }

    @Override
    public void getWar(OtherBean otherBean) {
        if(otherBean !=null && otherBean.getResults() !=null){
            isLoading = false;
            if(!otherBean.getResults().isEmpty()){
                otherList.addAll(otherBean.getResults());
                othersAdapter.setNewData(otherList);
            }else{
                Toast.makeText(getContext(),"No more data",Toast.LENGTH_SHORT).show();
                isNomore = true;
            }

        }else{
            isNomore = true;
        }
    }

    @Override
    public void getAction(OtherBean otherBean) {
        if(otherBean !=null && otherBean.getResults() !=null){
            isLoading = false;
            if(!otherBean.getResults().isEmpty()){
                otherList.addAll(otherBean.getResults());
                othersAdapter.setNewData(otherList);
            }else{
                Toast.makeText(getContext(),"No more data",Toast.LENGTH_SHORT).show();
                isNomore = true;
            }

        }else{
            isNomore = true;
        }
    }

    @Override
    public void getDrama(OtherBean otherBean) {
        if(otherBean !=null && otherBean.getResults() !=null){
            isLoading = false;
            if(!otherBean.getResults().isEmpty()){
                otherList.addAll(otherBean.getResults());
                othersAdapter.setNewData(otherList);
            }else{
                Toast.makeText(getContext(),"No more data",Toast.LENGTH_SHORT).show();
                isNomore = true;
            }

        }else{
            isNomore = true;
        }
    }

    @Override
    public void getMovieSpeakKhmer(OtherBean otherBean) {
        if(otherBean !=null && otherBean.getResults() !=null){
            isLoading = false;
            if(!otherBean.getResults().isEmpty()){
                otherList.addAll(otherBean.getResults());
                othersAdapter.setNewData(otherList);
            }else{
                Toast.makeText(getContext(),"No more data",Toast.LENGTH_SHORT).show();
                isNomore = true;
            }

        }else{
            isNomore = true;
        }
    }

    @Override
    public void getThriller(OtherBean otherBean) {
        if(otherBean !=null && otherBean.getResults() !=null){
            isLoading = false;
            if(!otherBean.getResults().isEmpty()){
                otherList.addAll(otherBean.getResults());
                othersAdapter.setNewData(otherList);
            }else{
                Toast.makeText(getContext(),"No more data",Toast.LENGTH_SHORT).show();
                isNomore = true;
            }

        }else{
            isNomore = true;
        }
    }

    @Override
    public void getFantasy(OtherBean otherBean) {
        if(otherBean !=null && otherBean.getResults() !=null){
            isLoading = false;
            if(!otherBean.getResults().isEmpty()){
                otherList.addAll(otherBean.getResults());
                othersAdapter.setNewData(otherList);
            }else{
                Toast.makeText(getContext(),"No more data",Toast.LENGTH_SHORT).show();
                isNomore = true;
            }

        }else{
            isNomore = true;
        }
    }

    @Override
    public void getMusic(OtherBean otherBean) {
        if(otherBean !=null && otherBean.getResults() !=null){
            isLoading = false;
            if(!otherBean.getResults().isEmpty()){
                otherList.addAll(otherBean.getResults());
                othersAdapter.setNewData(otherList);
            }else{
                Toast.makeText(getContext(),"No more data",Toast.LENGTH_SHORT).show();
                isNomore = true;
            }

        }else{
            isNomore = true;
        }
    }

    @Override
    public void getWarPolitics(OtherBean otherBean) {
        if(otherBean !=null && otherBean.getResults() !=null){
            isLoading = false;
            if(!otherBean.getResults().isEmpty()){
                otherList.addAll(otherBean.getResults());
                othersAdapter.setNewData(otherList);
            }else{
                Toast.makeText(getContext(),"No more data",Toast.LENGTH_SHORT).show();
                isNomore = true;
            }

        }else{
            isNomore = true;
        }
    }

    @Override
    public void getVivamax(OtherBean otherBean) {
        if(otherBean !=null && otherBean.getResults() !=null){
            isLoading = false;
            if(!otherBean.getResults().isEmpty()){
                otherList.addAll(otherBean.getResults());
                othersAdapter.setNewData(otherList);
            }else{
                Toast.makeText(getContext(),"No more data",Toast.LENGTH_SHORT).show();
                isNomore = true;
            }

        }else{
            Toast.makeText(getContext(),"No more data",Toast.LENGTH_SHORT).show();
            isNomore = true;
        }
    }

    @Override
    public void getTvMovie(OtherBean otherBean) {
        if(otherBean !=null && otherBean.getResults() !=null){
            isLoading = false;
            if(!otherBean.getResults().isEmpty()){
                otherList.addAll(otherBean.getResults());
                othersAdapter.setNewData(otherList);
            }else{
                Toast.makeText(getContext(),"No more data",Toast.LENGTH_SHORT).show();
                isNomore = true;
            }

        }else{
            Toast.makeText(getContext(),"No more data",Toast.LENGTH_SHORT).show();
            isNomore = true;
        }
    }

    @Override
    public void getDocumentary(OtherBean otherBean) {
        if(otherBean !=null && otherBean.getResults() !=null){
            isLoading = false;
            if(!otherBean.getResults().isEmpty()){
                otherList.addAll(otherBean.getResults());
                othersAdapter.setNewData(otherList);
            }else{
                Toast.makeText(getContext(),"No more data",Toast.LENGTH_SHORT).show();
                isNomore = true;
            }

        }else{
            Toast.makeText(getContext(),"No more data",Toast.LENGTH_SHORT).show();
            isNomore = true;
        }
    }

    @Override
    public void getKoreaDrama(OtherBean otherBean) {
        if(otherBean !=null && otherBean.getResults() !=null){
            isLoading = false;
            if(!otherBean.getResults().isEmpty()){
                otherList.addAll(otherBean.getResults());
                othersAdapter.setNewData(otherList);
            }else{
                Toast.makeText(getContext(),"No more data",Toast.LENGTH_SHORT).show();
                isNomore = true;
            }

        }else{
            Toast.makeText(getContext(),"No more data",Toast.LENGTH_SHORT).show();
            isNomore = true;
        }
    }

    @Override
    public void getMystery(OtherBean otherBean) {
        if(otherBean !=null && otherBean.getResults() !=null){
            isLoading = false;
            if(!otherBean.getResults().isEmpty()){
                otherList.addAll(otherBean.getResults());
                othersAdapter.setNewData(otherList);
            }else{
                Toast.makeText(getContext(),"No more data",Toast.LENGTH_SHORT).show();
                isNomore = true;
            }

        }else{
            Toast.makeText(getContext(),"No more data",Toast.LENGTH_SHORT).show();
            isNomore = true;
        }
    }

    @Override
    public void getAdventure(OtherBean otherBean) {
        if(otherBean !=null && otherBean.getResults() !=null){
            isLoading = false;
            if(!otherBean.getResults().isEmpty()){
                otherList.addAll(otherBean.getResults());
                othersAdapter.setNewData(otherList);
            }else{
                Toast.makeText(getContext(),"No more data",Toast.LENGTH_SHORT).show();
                isNomore = true;
            }

        }else{
            Toast.makeText(getContext(),"No more data",Toast.LENGTH_SHORT).show();
            isNomore = true;
        }
    }

    @Override
    public void getComedy(OtherBean otherBean) {
        if(otherBean !=null && otherBean.getResults() !=null){
            isLoading = false;
            if(!otherBean.getResults().isEmpty()){
                otherList.addAll(otherBean.getResults());
                othersAdapter.setNewData(otherList);
            }else{
                Toast.makeText(getContext(),"No more data",Toast.LENGTH_SHORT).show();
                isNomore = true;
            }

        }else{
            Toast.makeText(getContext(),"No more data",Toast.LENGTH_SHORT).show();
            isNomore = true;
        }
    }

    @Override
    public void getChineseDrama(OtherBean otherBean) {
        if(otherBean !=null && otherBean.getResults() !=null){
            isLoading = false;
            if(!otherBean.getResults().isEmpty()){
                otherList.addAll(otherBean.getResults());
                othersAdapter.setNewData(otherList);
            }else{
                Toast.makeText(getContext(),"No more data",Toast.LENGTH_SHORT).show();
                isNomore = true;
            }

        }else{
            Toast.makeText(getContext(),"No more data",Toast.LENGTH_SHORT).show();
            isNomore = true;
        }
    }

    @Override
    public void getScienceFiction(OtherBean otherBean) {
        if(otherBean !=null && otherBean.getResults() !=null){
            isLoading = false;
            if(!otherBean.getResults().isEmpty()){
                otherList.addAll(otherBean.getResults());
                othersAdapter.setNewData(otherList);
            }else{
                Toast.makeText(getContext(),"No more data",Toast.LENGTH_SHORT).show();
                isNomore = true;
            }

        }else{
            Toast.makeText(getContext(),"No more data",Toast.LENGTH_SHORT).show();
            isNomore = true;
        }
    }

    @Override
    public void getFamily(OtherBean otherBean) {
        if(otherBean !=null && otherBean.getResults() !=null){
            isLoading = false;
            if(!otherBean.getResults().isEmpty()){
                otherList.addAll(otherBean.getResults());
                othersAdapter.setNewData(otherList);
            }else{
                Toast.makeText(getContext(),"No more data",Toast.LENGTH_SHORT).show();
                isNomore = true;
            }

        }else{
            Toast.makeText(getContext(),"No more data",Toast.LENGTH_SHORT).show();
            isNomore = true;
        }
    }

    @Override
    public void getTvShows(OtherBean otherBean) {
        if(otherBean !=null && otherBean.getResults() !=null){
            isLoading = false;
            if(!otherBean.getResults().isEmpty()){
                otherList.addAll(otherBean.getResults());
                othersAdapter.setNewData(otherList);
            }else{
                Toast.makeText(getContext(),"No more data",Toast.LENGTH_SHORT).show();
                isNomore = true;
            }

        }else{
            Toast.makeText(getContext(),"No more data",Toast.LENGTH_SHORT).show();
            isNomore = true;
        }
    }

    @Override
    public void getErotic(OtherBean otherBean) {
        if(otherBean !=null && otherBean.getResults() !=null){
            isLoading = false;
            if(!otherBean.getResults().isEmpty()){
                otherList.addAll(otherBean.getResults());
                othersAdapter.setNewData(otherList);
            }else{
                Toast.makeText(getContext(),"No more data",Toast.LENGTH_SHORT).show();
                isNomore = true;
            }

        }else{
            Toast.makeText(getContext(),"No more data",Toast.LENGTH_SHORT).show();
            isNomore = true;
        }
    }

    @Override
    public void getMovie(OtherBean otherBean) {
        if(otherBean !=null && otherBean.getResults() !=null){
            isLoading = false;
            if(!otherBean.getResults().isEmpty()){
                otherList.addAll(otherBean.getResults());
                othersAdapter.setNewData(otherList);
            }else{
                Toast.makeText(getContext(),"No more data",Toast.LENGTH_SHORT).show();
                isNomore = true;
            }

        }else{
            Toast.makeText(getContext(),"No more data",Toast.LENGTH_SHORT).show();
            isNomore = true;
        }
    }

    @Override
    public void getAnimation(OtherBean otherBean) {
        if(otherBean !=null && otherBean.getResults() !=null){
            isLoading = false;
            if(!otherBean.getResults().isEmpty()){
                otherList.addAll(otherBean.getResults());
                othersAdapter.setNewData(otherList);
            }else{
                Toast.makeText(getContext(),"No more data",Toast.LENGTH_SHORT).show();
                isNomore = true;
            }

        }else{
            Toast.makeText(getContext(),"No more data",Toast.LENGTH_SHORT).show();
            isNomore = true;
        }
    }

    @Override
    public void getHorror(OtherBean otherBean) {
        if(otherBean !=null && otherBean.getResults() !=null){
            isLoading = false;
            if(!otherBean.getResults().isEmpty()){
                otherList.addAll(otherBean.getResults());
                othersAdapter.setNewData(otherList);
            }else{
                Toast.makeText(getContext(),"No more data",Toast.LENGTH_SHORT).show();
                isNomore = true;
            }

        }else{
            Toast.makeText(getContext(),"No more data",Toast.LENGTH_SHORT).show();
            isNomore = true;
        }
    }

    @Override
    public void getAllMovies(OtherBean otherBean) {
        if(otherBean !=null && otherBean.getResults() !=null){
            isLoading = false;
            if(!otherBean.getResults().isEmpty()){
                otherList.addAll(otherBean.getResults());
                othersAdapter.setNewData(otherList);
            }else{
                Toast.makeText(getContext(),"No more data",Toast.LENGTH_SHORT).show();
                isNomore = true;
            }

        }else{
            Toast.makeText(getContext(),"No more data",Toast.LENGTH_SHORT).show();
            isNomore = true;
        }
    }
}