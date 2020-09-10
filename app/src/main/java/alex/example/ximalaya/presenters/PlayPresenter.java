package alex.example.ximalaya.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.constants.PlayerConstants;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import alex.example.ximalaya.data.XimalayaApi;
import alex.example.ximalaya.base.BaseApplication;
import alex.example.ximalaya.interfaces.IPlayerCallBack;
import alex.example.ximalaya.interfaces.IPlayerPresenter;
import alex.example.ximalaya.utils.LogUtil;

import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;

public class PlayPresenter implements IPlayerPresenter, IXmAdsStatusListener, IXmPlayerStatusListener {

    private static final String TAG = "PlayPresenter";
    private final XmPlayerManager mPlayerManager;
    private List<IPlayerCallBack> mIPlayerCallBacks = new ArrayList<>();
    private Track mCurrentTrack;
    public static final int DEFAULT_PLAY_INDEX = 0;
    private int mCurrentIndex = DEFAULT_PLAY_INDEX;
    private final SharedPreferences mPlayModeSp;

    /**
     * PLAY_MODEL_LIST
     * PLAY_MODEL_LIST_LOOP
     * PLAY_MODEL_RANDOM
     * PLAY_MODEL_SINGLE_LOOP
     */
    private static final int PLAY_MODEL_LIST_INT = 0;
    private static final int PLAY_MODEL_LIST_LOOP_INT = 1;
    private static final int PLAY_MODEL_RANDOM_INT = 2;
    private static final int PLAY_MODEL_SINGLE_LOOP_INT = 3;
    //sp's key and name
    public static final String PLAY_MODE_SP_NAME = "PlayMod";
    public static final String PLAY_MODE_SP_KEY = "currentPlayMode";
    private XmPlayListControl.PlayMode mCurrentPlayMode = PLAY_MODEL_LIST;
    private boolean mIsReverse = false;
    private int mCurrentProgressPosition = 0;
    private int mProgressDuration = 0;

    private PlayPresenter(){
        mPlayerManager = XmPlayerManager.getInstance(BaseApplication.getAppContext());
        //广告相关的接口
        mPlayerManager.addAdsStatusListener(this);
        //注册播放器状态相关的接口
        mPlayerManager.addPlayerStatusListener(this);
        //简要记录当前的播放模式
        mPlayModeSp = BaseApplication.getAppContext().getSharedPreferences(PLAY_MODE_SP_NAME, Context.MODE_PRIVATE);

    }
    private static PlayPresenter sPlayPresenter;
    public static PlayPresenter getPlayPresenter(){
        if (sPlayPresenter == null) {
            synchronized (PlayPresenter.class){
                if (sPlayPresenter == null) {
                    sPlayPresenter = new PlayPresenter();
                }
            }
        }
        return sPlayPresenter;
    }

    private boolean isPlayListSet =false;
    public void setPlayList(List<Track> list , int playIndex){
        if (mPlayerManager != null) {

            mPlayerManager.setPlayList(list,playIndex);
            isPlayListSet=true;
            mCurrentTrack = list.get(playIndex);
            mCurrentIndex = playIndex;
        }else {
            LogUtil.d(TAG,"mPlayerManager is null");
        }
    }

    @Override
    public void play() {
        if (isPlayListSet){
            //int playerStatus = mPlayerManager.getPlayerStatus();
            //LogUtil.d(TAG,"status is == > " + playerStatus);
            mPlayerManager.play();
        }
    }

    @Override
    public void pause() {
        if (mPlayerManager != null) {
            mPlayerManager.pause();
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void playPre() {
        //播放上一个
        if (mPlayerManager != null) {
            mPlayerManager.playPre();
        }
    }

    @Override
    public void playNext() {
        //播放下一个
        if (mPlayerManager != null) {
            mPlayerManager.playNext();
        }
    }

    /**
     * 判断是否播放有播放列表的节目
     * @return
     */
    public boolean hasPlayList(){
        return isPlayListSet;
    }

    @Override
    public void switchPlayMode(XmPlayListControl.PlayMode mode) {
        if (mPlayerManager != null) {
            mCurrentPlayMode=mode;
            mPlayerManager.setPlayMode(mode);
            //通知UI更新播放模式
            for (IPlayerCallBack iPlayerCallBack : mIPlayerCallBacks) {
                iPlayerCallBack.onPlayModeChange(mode);
            }
            //保存到sp
            SharedPreferences.Editor edit = mPlayModeSp.edit();
            edit.putInt(PLAY_MODE_SP_KEY,getIntByPlayMode(mode));
            edit.commit();
        }
    }

    private int getIntByPlayMode(XmPlayListControl.PlayMode mode){
        switch (mode){
            case PLAY_MODEL_RANDOM:
                return PLAY_MODEL_RANDOM_INT;
            case PLAY_MODEL_SINGLE_LOOP:
                return PLAY_MODEL_SINGLE_LOOP_INT;
            case PLAY_MODEL_LIST_LOOP:
                return PLAY_MODEL_LIST_LOOP_INT;
            case PLAY_MODEL_LIST:
                return PLAY_MODEL_LIST_INT;
        }
        return PLAY_MODEL_LIST_INT;
    }

    private XmPlayListControl.PlayMode getModeByInt(int index){
        switch (index){
            case PLAY_MODEL_RANDOM_INT:
                return PLAY_MODEL_RANDOM ;
            case PLAY_MODEL_SINGLE_LOOP_INT:
                return PLAY_MODEL_SINGLE_LOOP;
            case PLAY_MODEL_LIST_LOOP_INT:
                return PLAY_MODEL_LIST_LOOP;
            case PLAY_MODEL_LIST_INT:
                return PLAY_MODEL_LIST;
        }
        return PLAY_MODEL_LIST;
    }

    @Override
    public void getPlayList() {
        if (mPlayerManager != null) {
            List<Track> playList = mPlayerManager.getPlayList();
            for (IPlayerCallBack iPlayerCallBack : mIPlayerCallBacks) {
                iPlayerCallBack.onListLoaded(playList);
            }
        }
    }

    @Override
    public void playByIndex(int index) {
        //切换播放器到第index位置进行播放
        if (mPlayerManager != null) {
            mPlayerManager.play(index);
        }
    }

    @Override
    public void seekTo(int progress) {
        //更新播放器的进度
        mPlayerManager.seekTo(progress);
    }

    @Override
    public boolean isPlaying() {
        //返回当前是否在播放
        return  mPlayerManager.isPlaying();
    }

    @Override
    public void reversePlayList() {
        //播放列表反转
        List<Track> playList = mPlayerManager.getPlayList();
        Collections.reverse(playList);
        mIsReverse = !mIsReverse;
        //1.播放列表  2.开始播放是下标
        //切换播放列表顺序后，下标1->8 ==> 新的下标=总个数 - 1 -  当前的下标
        mCurrentIndex = playList.size() - 1 -mCurrentIndex;
        mPlayerManager.setPlayList(playList,mCurrentIndex);
        //更新UI
        mCurrentTrack = (Track) mPlayerManager.getCurrSound();
        for (IPlayerCallBack iPlayerCallBack : mIPlayerCallBacks) {
            iPlayerCallBack.onListLoaded(playList);
            iPlayerCallBack.onTrackUpdate(mCurrentTrack,mCurrentIndex);
            iPlayerCallBack.updateListOrder(mIsReverse);
        }
    }

    @Override
    public void playByAlbumId(long id) {
        //1.获取到专辑的内容
        XimalayaApi ximalayaApi = XimalayaApi.getXimalayaApi();
        ximalayaApi.getAlbumDetail(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                //2.设置给播放器
                List<Track> tracks = trackList.getTracks();
                if (trackList != null &&tracks.size()>0) {
                    mPlayerManager.setPlayList(tracks,DEFAULT_PLAY_INDEX);
                    isPlayListSet=true;
                    mCurrentTrack = tracks.get(DEFAULT_PLAY_INDEX);
                    mCurrentIndex = DEFAULT_PLAY_INDEX;
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(BaseApplication.getAppContext(),"数据请求错误...",Toast.LENGTH_SHORT).show();
            }
        },(int)id,1);

        //3.播放

    }

    @Override
    public void registerViewCallBack(IPlayerCallBack iPlayerCallBack) {
        if (!mIPlayerCallBacks.contains(iPlayerCallBack)) {
            mIPlayerCallBacks.add(iPlayerCallBack);
        }
        //更新之前，让UI的Pager有数据
        getPlayList();
        //通知当前节目
        iPlayerCallBack.onTrackUpdate(mCurrentTrack,mCurrentIndex);
        iPlayerCallBack.onProgressChange(mCurrentProgressPosition,mCurrentProgressPosition);
        //更新状态
        handlePlayState(iPlayerCallBack);
        //从sp里获取
        int modeIndex = mPlayModeSp.getInt(PLAY_MODE_SP_KEY, PLAY_MODEL_LIST_INT);
        mCurrentPlayMode=getModeByInt(modeIndex);
        iPlayerCallBack.onPlayModeChange(mCurrentPlayMode);
    }

    private void handlePlayState(IPlayerCallBack iPlayerCallBack) {
        int playerStatus = mPlayerManager.getPlayerStatus();
        //根据状态调用
        if (PlayerConstants.STATE_STARTED==playerStatus) {
            iPlayerCallBack.onPlayStart();
        }else {
            iPlayerCallBack.onPlayPause();
        }
    }

    @Override
    public void unRegisterViewCallBack(IPlayerCallBack iPlayerCallBack) {
        mIPlayerCallBacks.remove(iPlayerCallBack);
    }

    //===================广告相关的方法回调 start =========================//
    @Override
    public void onStartGetAdsInfo() {
        LogUtil.d(TAG,"onStartGetAdsInfo");
    }

    @Override
    public void onGetAdsInfo(AdvertisList advertisList) {
        LogUtil.d(TAG,"onGetAdsInfo");
    }

    @Override
    public void onAdsStartBuffering() {
        LogUtil.d(TAG,"onAdsStartBuffering");
    }

    @Override
    public void onAdsStopBuffering() {
        LogUtil.d(TAG,"onAdsStopBuffering");
    }

    @Override
    public void onStartPlayAds(Advertis advertis, int i) {
        LogUtil.d(TAG,"onStartPlayAds");
    }

    @Override
    public void onCompletePlayAds() {
        LogUtil.d(TAG,"onCompletePlayAds");
    }

    @Override
    public void onError(int what, int extra) {
        LogUtil.d(TAG,"onError what = > " +what + "extra = > " + extra);
    }
    //===================广告相关的方法回调 end =========================//
    //
    //===================播放器相关的方法 start ============================//
    @Override
    public void onPlayStart() {
        LogUtil.d(TAG,"onPlayStart");
        for (IPlayerCallBack iPlayerCallBack : mIPlayerCallBacks) {
            iPlayerCallBack.onPlayStart();
        }
    }

    @Override
    public void onPlayPause() {
        LogUtil.d(TAG,"onPlayPause");
        for (IPlayerCallBack iPlayerCallBack : mIPlayerCallBacks) {
            iPlayerCallBack.onPlayPause();
        }
    }

    @Override
    public void onPlayStop() {
        LogUtil.d(TAG,"onPlayStop");
        for (IPlayerCallBack iPlayerCallBack : mIPlayerCallBacks) {
            iPlayerCallBack.onPlayStop();
        }
    }

    @Override
    public void onSoundPlayComplete() {
        LogUtil.d(TAG,"onSoundPlayComplete");
    }

    @Override
    public void onSoundPrepared() {
        LogUtil.d(TAG,"onSoundPrepared");
        mPlayerManager.setPlayMode(mCurrentPlayMode);
        //LogUtil.d(TAG,"current status is == >" + mPlayerManager.getPlayerStatus());
        if (mPlayerManager.getPlayerStatus()== PlayerConstants.STATE_PREPARED) {
            //播放器准备好了
            mPlayerManager.play();
        }

    }

    @Override
    public void onSoundSwitch(PlayableModel lastModel, PlayableModel curModel) {
        LogUtil.d(TAG,"onSoundSwitch");
        if (lastModel != null) {
            LogUtil.d(TAG,"lastModel" + lastModel.getKind());
        }
        if (curModel != null) {
            LogUtil.d(TAG,"curModel"  + curModel.getKind());
        }
        //curModel代表的是当前的内容
        //通过getKind()获取种类
        //track代表track
        //1.不推荐
        /*if ("track".equals(curModel.getKind())){
            Track currentTrack = (Track) curModel;
            LogUtil.d(TAG,"title == > "  + currentTrack.getTrackTitle());
        }*/
        //2.
        mCurrentIndex = mPlayerManager.getCurrentIndex();
        if (curModel instanceof Track){
            Track currentTrack = (Track) curModel;
            mCurrentTrack = currentTrack;
            //保存播放记录
            HistoryPresenter historyPresenter = HistoryPresenter.getHistoryPresenter();
            historyPresenter.addHistory(currentTrack);
            //LogUtil.d(TAG,"title == > "  + currentTrack.getTrackTitle());
            //更新UI
            for (IPlayerCallBack iPlayerCallBack : mIPlayerCallBacks) {
                iPlayerCallBack.onTrackUpdate(mCurrentTrack,mCurrentIndex);
            }
        }


    }

    @Override
    public void onBufferingStart() {
        LogUtil.d(TAG,"onBufferingStart");
    }

    @Override
    public void onBufferingStop() {
        LogUtil.d(TAG,"onBufferingStop");
    }

    @Override
    public void onBufferProgress(int progress) {
        LogUtil.d(TAG,"onBufferProgress......" + progress);
    }

    @Override
    public void onPlayProgress(int currPos, int duration) {
        this.mCurrentProgressPosition = currPos;
        this.mProgressDuration = duration;
        //单位是毫秒
        for (IPlayerCallBack iPlayerCallBack : mIPlayerCallBacks) {
            iPlayerCallBack.onProgressChange(currPos,duration);
        }
        LogUtil.d(TAG,"onPlayProgress");
    }

    @Override
    public boolean onError(XmPlayerException e) {
        LogUtil.d(TAG,"onError e -- >" + e);
        return false;
    }
    //=================播放器相关的方法 end=======================//

}
