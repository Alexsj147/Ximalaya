package alex.example.ximalaya.presenters;

import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alex.example.ximalaya.interfaces.IAlbumDetailPresenter;
import alex.example.ximalaya.interfaces.IAlbumDetailViewCallBack;
import alex.example.ximalaya.utils.Constants;
import alex.example.ximalaya.utils.LogUtil;
import okhttp3.internal.http2.ErrorCode;

public class AlbumDetailPresenter implements IAlbumDetailPresenter {

    private static final String TAG = "AlbumDetailPresenter";
    private Album mTargetAlbum = null;
    private List<IAlbumDetailViewCallBack> mCallBacks = new ArrayList<>();

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

    }

    @Override
    public void getAlbumDetail(int albumID, int page) {
        //根据页码和专辑id获取数据
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUM_ID, albumID+"");
        map.put(DTransferConstants.SORT, "asc");
        map.put(DTransferConstants.PAGE, page+"");
        map.put(DTransferConstants.PAGE_SIZE, Constants.COUNT_DEFAULT+"");
        CommonRequest.getTracks(map, new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                if (trackList != null) {
                    List<Track> tracks = trackList.getTracks();
                    LogUtil.d(TAG,"tracks size is ==> " +tracks.size());
                    handleAlbumDetailResult(tracks);
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtil.d(TAG,"errorCode -- > " + errorCode);
                LogUtil.d(TAG,"errorMsg  -- > " + errorMsg);
                handlerError(errorCode,errorMsg);
            }
        });
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
