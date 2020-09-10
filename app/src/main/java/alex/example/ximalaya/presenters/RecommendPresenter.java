package alex.example.ximalaya.presenters;

import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.ArrayList;
import java.util.List;

import alex.example.ximalaya.data.XimalayaApi;
import alex.example.ximalaya.interfaces.IRecommendCallBack;
import alex.example.ximalaya.interfaces.IRecommendPresenter;
import alex.example.ximalaya.utils.LogUtil;

public class RecommendPresenter implements IRecommendPresenter {

    private static final String TAG = "RecommendPresenter";

    private List<IRecommendCallBack> mCallBacks = new ArrayList<>();
    private List<Album> mCurrentRecommend = null;
    private List<Album> mRecommendList;

    private RecommendPresenter(){

    }
    private static RecommendPresenter sInstance = null;

    /**
     * 获取单例对象
     * @return
     */
    public static RecommendPresenter getInstance(){
        if (sInstance==null) {
            synchronized (RecommendPresenter.class){
                if (sInstance==null) {
                    sInstance = new RecommendPresenter();
                }
            }
        }
        return sInstance;
    }

    /**
     * 获取当前的推荐专辑的列表
     * @return 推荐专辑的列表，使用前判空
     */
    public List<Album> getCurrentRecommend(){
        return mCurrentRecommend;
    }

    /**
     * 获取推荐内容，其实就是猜你喜欢
     * 接口：3.10.6 获取猜你喜欢专辑
     */
    @Override
    public void getRecommendList() {
        //如果内容不为空，那么直接使用当前内容
        if (mRecommendList != null&& mRecommendList.size()>0) {
            handlerRecommendResult(mRecommendList);
            return;
        }
        //获取推荐内容
        //封装参数
        updateLoading();
        XimalayaApi ximalayaApi = XimalayaApi.getXimalayaApi();
        ximalayaApi.getRecommendList(new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(GussLikeAlbumList gussLikeAlbumList) {
                LogUtil.d(TAG,"thread name -->" + Thread.currentThread().getName());
                    //LogUtil.d(TAG,"size --> " + albumList.size());
                    //更新UIread.currentThread().getName());
                    //获取数据成功
                    if (gussLikeAlbumList != null) {
                        mRecommendList = gussLikeAlbumList.getAlbumList();
                        //updateRecommendUI(albumList);
                        handlerRecommendResult(mRecommendList);
                    }
                }

            @Override
            public void onError(int i, String s) {
                //获取数据失败
                LogUtil.d(TAG,"error --> " + i);
                LogUtil.d(TAG,"errorMsg --> " + s);
                handlerError();
            }
        });
    }

    private void handlerError() {
        if (mCallBacks != null) {
            for (IRecommendCallBack callBack : mCallBacks) {
                callBack.onNetWorkError();
            }
        }
    }


    private void handlerRecommendResult(List<Album> albumList) {
        //通知UI更新
        if (albumList!=null) {
            //测试为空
            //albumList.clear();
            if (albumList.size()==0) {
                for (IRecommendCallBack callBack : mCallBacks) {
                    callBack.onEmpty();
                }
            }else {
                for (IRecommendCallBack callBack : mCallBacks) {
                    callBack.onRecommendListLoaded(albumList);
                }
                this.mCurrentRecommend = albumList;
            }
        }
    }

    private void updateLoading(){
        for (IRecommendCallBack callBack : mCallBacks) {
            callBack.onLoading();
        }
    }

    @Override
    public void pull2RefreshMore() {

    }

    @Override
    public void loadMore() {

    }

    @Override
    public void registerViewCallBack(IRecommendCallBack callBack) {
        if (!mCallBacks.contains(callBack) && mCallBacks!=null) {
            mCallBacks.add(callBack);
        }
    }

    @Override
    public void unRegisterViewCallBack(IRecommendCallBack callBack) {
        if (mCallBacks!=null) {
            mCallBacks.remove(callBack);
        }
    }
}
