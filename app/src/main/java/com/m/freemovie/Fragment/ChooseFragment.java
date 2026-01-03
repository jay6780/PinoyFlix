package com.m.freemovie.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.m.freemovie.R;
import com.m.freemovie.Retrofit.AppConstant;
import com.m.freemovie.Utils.SPUtils;
import com.m.freemovie.databinding.FragmentChooseBinding;
import com.m.freemovie.mvp.ClassBean.MovieEvent;

import org.greenrobot.eventbus.EventBus;

public class ChooseFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    FragmentChooseBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChooseBinding.inflate(inflater, container, false);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.server_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        binding.fragmentSpinner.setAdapter(adapter);
        binding.fragmentSpinner.setOnItemSelectedListener(this);
        binding.fragmentSpinner.setSelection(0);

        return binding.getRoot();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        Fragment selectedFragment = null;
        
        switch (position) {
            case 0:
                selectedFragment = new TagalogSeriesFragment();
                EventBus.getDefault().post(new MovieEvent(3));
                SPUtils.getInstance().put(AppConstant.lastposition,3);
                break;
            case 1:
                selectedFragment = new TagalogServer2Fragment();
                EventBus.getDefault().post(new MovieEvent(4));
                SPUtils.getInstance().put(AppConstant.lastposition,4);
                break;
            case 2:
                selectedFragment = new TagalogMovieFragment();
                EventBus.getDefault().post(new MovieEvent(5));
                SPUtils.getInstance().put(AppConstant.lastposition,5);
                break;
            case 3:
                selectedFragment = new NineAnimeFragment();
                EventBus.getDefault().post(new MovieEvent(6));
                break;
        }
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_choose, selectedFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onStart() {
        EventBus.getDefault().post(new MovieEvent(3));
        super.onStart();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
}