package com.grgbanking.ct.utils;


import android.app.DialogFragment;
import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.grgbanking.ct.R;


public class WaitDialogFragment extends DialogFragment {
    private String noteMsg;
    private View rootView;  //表示这个fragment的根视图
    private ImageView dialogImageView;  //实现转圈的动画
    private TextView dialogTextView;   //文字提示
    private Animation animation;

    public WaitDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_wait_dialog, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dialogImageView = (ImageView) rootView.findViewById(R.id.dialog_fragment_iv);
        dialogTextView = (TextView) rootView.findViewById(R.id.dialog_fragment_tv);
    //给 imageView设置动画
        animation = new RotateAnimation(0f,360,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        animation.setDuration(700);
        animation.setRepeatMode(Animation.RESTART);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        dialogImageView.setAnimation(animation);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
    //通过getArguments() 方法得到 从activity传来的String值用来当作提示信息；
        Bundle bundle = getArguments();
        noteMsg = bundle.getString("noteMsg");
        dialogTextView.setText(noteMsg);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}
