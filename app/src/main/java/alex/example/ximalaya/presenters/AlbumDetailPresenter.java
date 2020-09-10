package alex.example.ximalaya.presenters;

import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.ArrayList;
import java.util.List;

import alex.example.ximalaya.data.XimalayaApi;
import alex.example.ximalaya.interfaces.IAlbumDetailPresenter;
import alex.example.ximalaya.interfaces.IAlbumDetailViewCallBack;
import alex.example.ximalaya.utils.LogUtil;

public class AlbumDetailPresenter implements IAlbumDetailPresenter {

    private static final String TAG = "AlbumDetailPresenter";
    private Album mTargetAlbum = null;
    private List<IAlbumDetailViewCallBack> mCallBacks = new ArrayList<>();
    private List<Track> mTracks = new ArrayList<>();
    //当前的专辑id
    private int mCurrentAlbumId = -1;
    //当前的页码
    private int mCurrentPageIndex = 0;

    private AlbumDetailPresenter(){

    }
    private static AlbumDetailPresenter sInstance = null;
    public static AlbumDetailPresenter getInstance(){
        if (sInstance==null) {
            synchronized (AlbumDetailPresenter.class){
                if (sInstance==null) {
                    sInstance = new AlbumDetailPresenter();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void pull2RefreshMore() {

    }

    @Override
    public void loadMore() {
        //加载更多内容
        mCurrentPageIndex++;
        //传入true，表示结果会追加到列表后方
        doLoaded(true);
    }

    private void doLoaded(final boolean isLoadedMore){
        XimalayaApi ximalayaApi = XimalayaApi.getXimalayaApi();
        ximalayaApi.getAlbumDetail(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                if (trackList != null) {
                    List<Track> tracks = trackList.getTracks();
                    //LogUtil.d(TAG,"tracks size is ==> " +tracks.size());
                    if (isLoadedMore) {
                        //上拉加载，结果放在后面
                        mTracks.addAll(tracks);
                        int size = tracks.size();
                        handleLoaderMoreResult(size);
                    }else {
                        //下拉加载，结果放在前面
                        mTracks.addAll(0,tracks);
                    }
                    handleAlbumDetailResult(mTracks);
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                if (isLoadedMore) {
                    mCurrentPageIndex--;
                }
                LogUtil.d(TAG,"errorCode -- > " + errorCode);
                LogUtil.d(TAG,"errorMsg  -- > " + errorMsg);
                handlerError(errorCode,errorMsg);
            }
        },mCurrentAlbumId,mCurrentPageIndex);
    }

    /**
     *处理加载更多的结果
     * @param size
     */
    private void handleLoaderMoreResult(int size) {
        for (IAlbumDetailViewCallBack callBack : mCallBacks) {
            callBack.onLoaderMoreFinished(size);
        }
    }

    @Override
    public void getAlbumDetail(int albumID, int page) {
        mTracks.clear();

        this.mCurrentAlbumId = albumID;
        this.mCurrentPageIndex = page;
        //根据页码和专辑id获取数据
        doLoaded(false);
    }

    /**
     * 如果发生错误就通知UI
     * @param errorCode
     * @param errorMsg
     */
    private void handlerError(int errorCode , String errorMsg) {
        for (IAlbumDetailViewCallBack mCallBack : mCallBacks) {
            mCallBack.onNetworkError(errorCode,errorMsg);
        }
    }

    private void handleAlbumDetailResult(List<Track> tracks) {
        for (IAlbumDetailViewCallBack mCallback : mCallBacks) {
            mCallback.onDetailListLoaded(tracks);
        }
    }

    @Override
    public void registerViewCallBack(IAlbumDetailViewCallBack detailViewCallBack) {
        if (!mCallBacks.contains(detailViewCallBack)) {
            mCallBacks.add(detailViewCallBack);
            if (mTargetAlbum != null) {
                detailViewCallBack.onAlbumLoaded(mTargetAlbum);
            }
        }
    }

    @Override
    public void unRegisterViewCallBack(IAlbumDetailViewCallBack detailViewCallBack) {
        mCallBacks.remove(detailViewCallBack);
    }

    public void setTargetAlbum(Album targetAlbum){
        this.mTargetAlbum = targetAlbum;
    }
}
