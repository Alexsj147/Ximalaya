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
     * 网络错误
     */
    void onNetWorkError();

    /**
     * 数据为空
     */
    void onEmpty();

    /**
     * 正在加载
     */
    void onLoading();
}
