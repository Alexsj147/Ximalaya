package alex.example.ximalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

public interface IRecommendCallBack {
    /**
     * 获取推荐内容
     * @param result
     */
    void onRecommendListLoaded(List<Album> result);

    /**
     * 加载更多
     * @param result
     */
    void onLoaderMore(List<Album> result);

    /**
     * 刷新
     * @param result
     */
    void onRefreshMore(List<Album> result);
}
