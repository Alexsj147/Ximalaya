package alex.example.ximalaya.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import alex.example.ximalaya.R;
import alex.example.ximalaya.base.BaseFragment;

public class SubscriptionFragment extends BaseFragment {


    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        View rootView =layoutInflater.inflate(R.layout.fragment_subscription,container,false);
        return rootView;
    }
}
