package alex.example.ximalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public interface IHistoryCallback {
    /**
     * 历史内容的加载
     * @param tracks
     */
    void onHistoriesLoaded(List<Track> tracks);
}
