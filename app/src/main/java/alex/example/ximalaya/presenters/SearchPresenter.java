package alex.example.ximalaya.presenters;

import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.ArrayList;
import java.util.List;

import alex.example.ximalaya.api.XimalayaApi;
import alex.example.ximalaya.interfaces.ISearchCallBack;
import alex.example.ximalaya.interfaces.ISearchPresenter;
import alex.example.ximalaya.utils.LogUtil;

public class SearchPresenter implements ISearchPresenter {

    private static final String TAG = "SearchPresenter";
    //当前搜索的关键字
    private String mCurrentKeyWord = null;
    private final XimalayaApi mXimalayaApi;
    private static final  int DEFAULT_PAGE = 1;
    private int mCurrentPage = DEFAULT_PAGE;

    private SearchPresenter(){
        mXimalayaApi = XimalayaApi.getXimalayaApi();
    }
    private static SearchPresenter sSearchPresenter = null;

    public static SearchPresenter getSearchPresenter(){
        if (sSearchPresenter==null) {
            synchronized (SearchPresenter.class){
                if (sSearchPresenter==null) {
                    sSearchPresenter=new SearchPresenter();
                }
            }
        }
        return sSearchPresenter;
    }

    private List<ISearchCallBack> mCallBacks = new ArrayList<>();

    @Override
    public void doSearch(String keyword) {
        //网络不好时，重新获取
        this.mCurrentKeyWord = keyword;
        search(keyword);
    }

    private void search(String keyword) {
        mXimalayaApi.searchByKeyword(keyword, mCurrentPage, new IDataCallBack<SearchAlbumList>() {
            @Override
            public void onSuccess(SearchAlbumList searchAlbumList) {
                List<Album> albums = searchAlbumList.getAlbums();
                if (albums != null) {
                    LogUtil.d(TAG,"albums size -- > " + albums.size());
                }else {
                    LogUtil.d(TAG,"albums is null");
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                    LogUtil.d(TAG,"errorCode..."+errorCode);
                    LogUtil.d(TAG,"errorMsg..."+errorMsg);
            }
        });
    }

    @Override
    public void reSearch() {
        search(mCurrentKeyWord);
    }

    @Override
    public void loadMore() {

    }

    @Override
    public void getHotWord() {
        mXimalayaApi.getHotWords(new IDataCallBack<HotWordList>() {
            @Override
            public void onSuccess(HotWordList hotWordList) {
                List<HotWord> hotWords = hotWordList.getHotWordList();
                LogUtil.d(TAG,"hotWords size is "+hotWords.size());
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtil.d(TAG,"getHotWord errorCode..."+errorCode);
                LogUtil.d(TAG,"getHotWord errorMsg..."+errorMsg);
            }
        });
    }

    @Override
    public void getRecommendWord(String keyword) {
            mXimalayaApi.getSuggestWord(keyword, new IDataCallBack<SuggestWords>() {
                @Override
                public void onSuccess(SuggestWords suggestWords) {
                    if (suggestWords != null) {
                        List<QueryResult> keyWordList = suggestWords.getKeyWordList();
                        LogUtil.d(TAG,"keyWordList size is "+keyWordList.size());
                    }
                }

                @Override
                public void onError(int errorCode, String errorMsg) {
                    LogUtil.d(TAG,"getRecommendWord errorCode..."+errorCode);
                    LogUtil.d(TAG,"getRecommendWord errorMsg..."+errorMsg);

                }
            });
    }

    @Override
    public void registerViewCallBack(ISearchCallBack iSearchCallBack) {
        if (!mCallBacks.contains(iSearchCallBack)) {
            mCallBacks.add(iSearchCallBack);
        }
    }

    @Override
    public void unRegisterViewCallBack(ISearchCallBack iSearchCallBack) {
        mCallBacks.remove(iSearchCallBack);
    }
}
