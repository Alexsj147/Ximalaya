package alex.example.ximalaya.data;

import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public interface IHistoryDaoCallback {
    /**
     * 添加历史的结果
     * @param isSuccess
     */
    void onHistoryAdd(boolean isSuccess);

    /**
     * 删除历史的结果
     * @param isSuccess
     */
    void onHistoryDel(boolean isSuccess);

    /**
     * 清除历史的结果
     * @param isSuccess
     */
    void onHistoryClear(boolean isSuccess);

    /**
     * 获取历史的结果
     * @param tracks
     */
    void onHistoryLoaded(List<Track> tracks);
}
