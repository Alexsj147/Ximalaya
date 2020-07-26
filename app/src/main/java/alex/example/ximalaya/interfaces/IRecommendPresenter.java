package alex.example.ximalaya.interfaces;

import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;

import alex.example.ximalaya.base.IBasePresenter;

public interface IRecommendPresenter extends IBasePresenter<IRecommendCallBack> {
    /**
     * 获取推荐内容
     */
    void getRecommendList();
    /**
     * 下拉刷新更多内容
     */
    void pull2RefreshMore();
    /**
     * 加载更多
     */
    void loadMore();
   /* *//**
     * 用于注册UI的回调
     * @param callBack
     *//*
    void registerViewCallBack(IRecommendCallBack callBack);
    *//**
     * 取消注册
     * @param callBack
     *//*
    void unRegisterViewCallBack(IRecommendCallBack callBack);*/
}
