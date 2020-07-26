package alex.example.ximalaya.interfaces;

import alex.example.ximalaya.base.IBasePresenter;

public interface IAlbumDetailPresenter extends IBasePresenter<IAlbumDetailViewCallBack> {
    /**
     * 下拉刷新更多内容
     */
    void pull2RefreshMore();
    /**
     * 加载更多
     */
    void loadMore();

    /**
     * 获取详情
     * @param albumID
     * @param page
     */
    void getAlbumDetail(int albumID,int page);

 /*   *//**
     * 用于注册UI的回调
     * @param detailViewCallBack
     *//*
    void registerViewCallBack(IAlbumDetailViewCallBack detailViewCallBack);
    *//**
     * 取消注册
     * @param detailViewCallBack
     *//*
    void unRegisterViewCallBack(IAlbumDetailViewCallBack detailViewCallBack);*/
}
