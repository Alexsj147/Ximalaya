package alex.example.ximalaya.presenters;

import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alex.example.ximalaya.interfaces.IRecommendCallBack;
import alex.example.ximalaya.interfaces.IRecommendPresenter;
import alex.example.ximalaya.utils.Constants;
import alex.example.ximalaya.utils.LogUtil;

public class RecommendPresenter implements IRecommendPresenter {

    private static final String TAG = "RecommendPresenter";

    private List<IRecommendCallBack> mCallBacks = new ArrayList<>();

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
     * 获取推荐内容，其实就是猜你喜欢
     * 接口：3.10.6 获取猜你喜欢专辑
     */
    @Override
    public void getRecommendList() {
        //获取推荐内容
        //封装参数
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.LIKE_COUNT, Constants.RECOMMEND_COUNT+"");
        CommonRequest.getGuessLikeAlbum(map, new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(GussLikeAlbumList gussLikeAlbumList) {
                LogUtil.d(TAG,"thread name -->" + Thread.currentThread().getName());
                List<Album> albumList = gussLikeAlbumList.getAlbumList();
                if (albumList != null) {
                    //LogUtil.d(TAG,"size --> " + albumList.size());
                    //更新UIread.currentThread().getName());
                    //获取数据成功
                    if (gussLikeAlbumList != null) {
                        //updateRecommendUI(albumList);
                        handlerRecommendResult(albumList);
                    }
                }
            }
            @Override
            public void onError(int i, String s) {
                //获取数据失败
                LogUtil.d(TAG,"error --> " + i);
                LogUtil.d(TAG,"errorMsg --> " + s);
            }
        });
    }


    private void handlerRecommendResult(List<Album> albumList) {
        //通知UI
        if (mCallBacks != null) {
            for (IRecommendCallBack callBack : mCallBacks) {
                callBack.onRecommendListLoaded(albumList);
            }
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
            mCallBacks.remove(mCallBacks);
        }
    }
}
