package alex.example.ximalaya.interfaces;

import android.os.Trace;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

public interface IPlayerCallBack {
    /**
     * 开始播放
     */
    void onPlayStart();

    /**
     * 播放暂停
     */
    void onPlayPause();

    /**
     * 播放停止
     */
    void onPlayStop();

    /**
     * 播放错误
     */
    void onPlayError();

    /**
     * 下一首
     */
    void onNextPlay(Track track);

    /**
     * 上一首
     */
    void onPrePlay(Track track);

    /**
     * 播放列表数据返回
     * @param list 播放器列表数据
     */
    void onListLoaded(List<Track> list);

    /**
     * 播放器状态改变
     * @param playMode
     */
    void onPlayModeChange(XmPlayListControl.PlayMode playMode);

    /**
     * 进度条改变
     * @param currentProgress
     * @param total
     */
    void onProgressChange(int currentProgress,int total);

    /**
     * 广告正在加载
     */
    void onAdLoading();

    /**
     * 广告结束
     */
    void onAdFinished();

    /**
     * 更新当前节目的标题
     * @param title
     */
    void onTrackTitleUpdate(String title);
}
