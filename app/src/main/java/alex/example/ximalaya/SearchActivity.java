package alex.example.ximalaya;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import java.util.List;

import alex.example.ximalaya.base.BaseActivity;
import alex.example.ximalaya.interfaces.ISearchCallBack;
import alex.example.ximalaya.presenters.SearchPresenter;
import alex.example.ximalaya.utils.LogUtil;

public class SearchActivity extends BaseActivity implements ISearchCallBack {

    private static final String TAG ="SearchActivity" ;
    private View mBackBtn;
    private EditText mInputBox;
    private TextView mSearchBtn;
    private FrameLayout mResultContainer;
    private SearchPresenter mSearchPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        initEvent();
        initPresenter();
    }

    private void initView() {
        mBackBtn = this.findViewById(R.id.search_back);
        mInputBox = this.findViewById(R.id.search_input);
        mSearchBtn = this.findViewById(R.id.search_btn);
        mResultContainer = this.findViewById(R.id.search_container);
    }

    private void initEvent() {
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:搜索
            }
        });
        mInputBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                LogUtil.d(TAG,"content is ..." +s);
                LogUtil.d(TAG,"start is ..." +start);
                LogUtil.d(TAG,"count is ..." +count);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initPresenter() {
        mSearchPresenter = SearchPresenter.getSearchPresenter();
        //注册UI更新的接口
        mSearchPresenter.registerViewCallBack(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消注册UI的接口
        if (mSearchPresenter != null) {
            mSearchPresenter.unRegisterViewCallBack(this);
        }
    }

    @Override
    public void onSearchResultLoaded(List<Album> result) {

    }

    @Override
    public void onHotWordLoaded(List<HotWord> hotWordList) {

    }

    @Override
    public void onLoadMoreResult(List<Album> result, boolean isOkay) {

    }

    @Override
    public void onRecommendWordLoaded(List<QueryResult> keyWordList) {

    }
}
