package com.m.freemovie.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.m.freemovie.databinding.FragmentNineAnimeBinding;

public class NineAnimeFragment extends Fragment {
    FragmentNineAnimeBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentNineAnimeBinding.inflate(inflater);
        return binding.getRoot();
    }
}