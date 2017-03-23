package com.reboot.locately.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.reboot.locately.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MyCircle extends Fragment {

    private BottomSheetBehavior mBottomSheetBehavior;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_circle, container, false);
        View mBottomSheet = view.findViewById(R.id.bottom_sheet_scroll);
        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet);
        mBottomSheetBehavior.setPeekHeight(315);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);


        return  view;

    }

}