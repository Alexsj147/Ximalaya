package alex.example.ximalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import java.util.List;

public interface ISearchCallBack {

    /**
     * 搜索结果的回调
     * @param result
     */
    void onSearchResultLoaded(List<Album> result);

    /**
     * 获取推荐热词的回调
     * @param hotWordList
     */
    void onHotWordLoaded(List<HotWord> hotWordList);

    /**
     * 加载更多的结果返回
     * @param result 结果
     * @param isOkay true成功，false没有更多
     */
    void onLoadMoreResult(List<Album> result , boolean isOkay);

    /**
     * 联想关键字的回调
     * @param keyWordList
     */
    void onRecommendWordLoaded(List<QueryResult> keyWordList);
}
