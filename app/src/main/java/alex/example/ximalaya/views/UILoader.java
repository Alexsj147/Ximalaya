package alex.example.ximalaya.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import alex.example.ximalaya.R;
import alex.example.ximalaya.base.BaseApplication;

public abstract class UILoader extends FrameLayout {

    private View mLoaidngView;
    private View mSuccessView;
    private View mNetWorkErrorView;
    private View mEmptyView;
    private OnRetryClickListener mRetryClickListener = null;

    public enum UIStatus{
        LOADING,SUCCESS,NETWORK_ERROR,EMPTY,NONE
    }
    public UIStatus mCurrentStatus = UIStatus.NONE;

    public UILoader(@NonNull Context context) {
        this(context,null);
    }

    public UILoader(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public UILoader(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //
        init();
    }

    public void updateStatus(UIStatus status){
        mCurrentStatus=status;
        //更新UI一定要在主线程
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                switchUIByCurrnetStatus();
            }
        });
    }

    /**
     * 初始化
     */
    private void init() {
        switchUIByCurrnetStatus();
    }

    private void switchUIByCurrnetStatus() {
        //加载中
        if (mLoaidngView == null) {
            mLoaidngView = getLoadingView();
            addView(mLoaidngView);
        }
        //根据状态设置是否可见
        mLoaidngView.setVisibility(mCurrentStatus==UIStatus.LOADING ? VISIBLE:GONE);

        //成功
        if (mSuccessView == null) {
            mSuccessView = getSuccessView(this);
            addView(mSuccessView);
        }
        //根据状态设置是否可见
        mSuccessView.setVisibility(mCurrentStatus==UIStatus.SUCCESS ? VISIBLE:GONE);

        //网络错误
        if (mNetWorkErrorView == null) {
            mNetWorkErrorView = getNetworkError();
            addView(mNetWorkErrorView);
        }
        //根据状态设置是否可见
        mNetWorkErrorView.setVisibility(mCurrentStatus==UIStatus.NETWORK_ERROR ? VISIBLE:GONE);

        //数据为空
        if (mEmptyView == null) {
            mEmptyView = getEmptyView();
            addView(mEmptyView);
        }
        //根据状态设置是否可见
        mEmptyView.setVisibility(mCurrentStatus==UIStatus.EMPTY ? VISIBLE:GONE);



    }

    private View getEmptyView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_empty_view,this,false);
    }

    private View getNetworkError() {
        View networkView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_network_error_view,this,false);
        networkView.findViewById(R.id.network_error_icon).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //重新获取数据
                if (mRetryClickListener != null) {
                    mRetryClickListener.onRetryClick();
                }
            }
        });
        return networkView;
    }

    protected abstract View getSuccessView(ViewGroup container);

    private View getLoadingView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_loading_view,this,false);
    }

    public void setOnRetryClickListener(OnRetryClickListener listener){
        this.mRetryClickListener = listener;
    }
    public interface OnRetryClickListener{
        void onRetryClick();
    }
}
