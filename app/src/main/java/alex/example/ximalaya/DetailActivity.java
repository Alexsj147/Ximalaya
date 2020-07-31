package alex.example.ximalaya;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.bezierlayout.BezierLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

import alex.example.ximalaya.adapters.DetailListAdapter;
import alex.example.ximalaya.base.BaseActivity;
import alex.example.ximalaya.base.BaseApplication;
import alex.example.ximalaya.interfaces.IAlbumDetailViewCallBack;
import alex.example.ximalaya.interfaces.IPlayerCallBack;
import alex.example.ximalaya.presenters.AlbumDetailPresenter;
import alex.example.ximalaya.presenters.PlayPresenter;
import alex.example.ximalaya.utils.ImageBlur;
import alex.example.ximalaya.utils.LogUtil;
import alex.example.ximalaya.views.RoundRectImageView;
import alex.example.ximalaya.views.UILoader;

public class DetailActivity extends BaseActivity implements IAlbumDetailViewCallBack, UILoader.OnRetryClickListener, DetailListAdapter.ItemClickListener, IPlayerCallBack {

    private static final String TAG ="DetailActivity" ;
    private ImageView mLargeCover;
    private RoundRectImageView mSmallCover;
    private TextView mAlbumTitle;
    private TextView mAlbumAuthor;
    private AlbumDetailPresenter mAlbumDetailPresenter;
    private int mCurrentPage = 1 ;
    private RecyclerView mDetailList;
    private DetailListAdapter mDetailListAdapter;
    private FrameLayout mDetailListContainer;
    private UILoader mUiLoader;
    private long mCurrentId=-1;
    private ImageView mPlayControlBtn;
    private TextView mPlayControlTv;
    private PlayPresenter mPlayPresenter;
    private List<Track> mCurrentTracks = null;
    private final static int DEFAULT_PLAY_INDEX = 0;
    private TwinklingRefreshLayout mRefreshLayout;
    private String mCurrentTrackTitle;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        initView();
        //这个是专辑详情的presenter
        mAlbumDetailPresenter = AlbumDetailPresenter.getInstance();
        mAlbumDetailPresenter.registerViewCallBack(this);
        //这个是播放器的presenter
        mPlayPresenter = PlayPresenter.getPlayPresenter();
        mPlayPresenter.registerViewCallBack(this);
        updatePlayState(mPlayPresenter.isPlaying());
        initListener();
    }




    private void initView() {
        mLargeCover = this.findViewById(R.id.iv_large_cover);
        mSmallCover = this.findViewById(R.id.riv_small_cover);
        mAlbumTitle = this.findViewById(R.id.tv_album_title);
        mAlbumAuthor = this.findViewById(R.id.tv_album_author);
        //播放控制的图标
        mPlayControlBtn = this.findViewById(R.id.detail_play_control);
        mPlayControlTv = this.findViewById(R.id.play_control_tv);
        mPlayControlTv.setSelected(true);

        mDetailListContainer = this.findViewById(R.id.detail_list_container);
        //
        if (mUiLoader==null) {
            mUiLoader = new UILoader(this) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView(container);
                }
            };
            mDetailListContainer.removeAllViews();
            mDetailListContainer.addView(mUiLoader);
            mUiLoader.setOnRetryClickListener(DetailActivity.this);
        }
    }

    private void initListener() {
        if (mPlayControlBtn != null) {
            mPlayControlBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPlayPresenter != null) {
                        //判断播放器是否有播放列表
                        //TODO:
                        boolean has = mPlayPresenter.hasPlayList();
                        if (has){
                            //控制播放器的状态
                            handlePlayControl();
                        }else {
                            handleNoPlayList();
                        }
                    }
                }
            });
        }
    }

    /**
     * 当播放器内没有播放内容
     */
    private void handleNoPlayList() {
        mPlayPresenter.setPlayList(mCurrentTracks,DEFAULT_PLAY_INDEX);
    }

    private void handlePlayControl() {
        if (mPlayPresenter.isPlaying()) {
            //正在播放就暂停
            mPlayPresenter.pause();
        }else {
            mPlayPresenter.play();
        }
    }

    private boolean mIsLoaderMore = false;

    private View createSuccessView(ViewGroup container) {
        View detailListView = LayoutInflater.from(this).inflate(R.layout.item_detail_list, container, false);
        mDetailList = detailListView.findViewById(R.id.album_detail_list);
        mRefreshLayout = detailListView.findViewById(R.id.refresh_layout);
        //1.设置布局管理器
        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(this);
        mDetailList.setLayoutManager(linearLayoutManager);
        //2.设置适配器
        mDetailListAdapter = new DetailListAdapter();
        mDetailList.setAdapter(mDetailListAdapter);
        //设置item的间距
        mDetailList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top= UIUtil.dip2px(view.getContext(),2);
                outRect.bottom= UIUtil.dip2px(view.getContext(),2);
                outRect.left= UIUtil.dip2px(view.getContext(),2);
                outRect.right= UIUtil.dip2px(view.getContext(),2);
            }
        });
        mDetailListAdapter.setItemClickListener(this);
        BezierLayout headerView = new BezierLayout(this);
        mRefreshLayout.setHeaderView(headerView);
        mRefreshLayout.setMaxHeadHeight(140);
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);

                BaseApplication.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DetailActivity.this,"刷新成功...",Toast.LENGTH_SHORT).show();
                        mRefreshLayout.finishRefreshing();
                    }
                },2000);
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                    //加载更多内容
                if (mAlbumDetailPresenter != null) {
                    mAlbumDetailPresenter.loadMore();
                    mIsLoaderMore=true;
                }
            }
        });
        return detailListView;
    }

    @Override
    public void onDetailListLoaded(List<Track> tracks) {
        if (mIsLoaderMore && mRefreshLayout!=null) {
            mRefreshLayout.finishLoadmore();
            mIsLoaderMore=false;
        }
        this.mCurrentTracks = tracks;
        //判断结果，根据结果控制UI显示
        if (tracks==null||tracks.size()==0) {
            if (mUiLoader != null) {
                mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
            }
        }
        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
        //更新/设置UI
        mDetailListAdapter.setData(tracks);
    }

    @Override
    public void onNetworkError(int errorCode, String errorMsg) {
        //请求发生错误，显示网络异常状态
        mUiLoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
    }

    @Override
    public void onAlbumLoaded(Album album) {
        //获取专辑的详情内容
        long id = album.getId();
        mCurrentId = id;
        if (mAlbumDetailPresenter != null) {
            mAlbumDetailPresenter.getAlbumDetail((int) id,mCurrentPage);
        }
        //拿数据，显示loading状态
        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
        }

        if (mAlbumTitle!=null) {
            mAlbumTitle.setText(album.getAlbumTitle());
        }
        if (mAlbumAuthor!=null) {
            mAlbumAuthor.setText(album.getAnnouncer().getNickname());
        }
        //做毛玻璃效果
        if (mLargeCover!=null && null!=mLargeCover) {
                Picasso.with(this).load(album.getCoverUrlLarge()).into(mLargeCover, new Callback() {
                    @Override
                    public void onSuccess() {
                        Drawable drawable = mLargeCover.getDrawable();
                        if (drawable != null) {
                            //到这里才说明是由图片的
                            ImageBlur.makeBlur(mLargeCover, DetailActivity.this);
                        }
                    }
                    @Override
                    public void onError() {
                        LogUtil.d(TAG,"onError");
                    }
                });
        }
        if (mSmallCover!=null) {
            Picasso.with(this).load(album.getCoverUrlSmall()).into(mSmallCover);
        }
    }

    @Override
    public void onLoaderMoreFinished(int size) {
        if (size>0) {
            Toast.makeText(this,"成功加载了"+size+"个节目",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this,"没有更多了",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRefreshFinished(int size) {

    }

    @Override
    public void onRetryClick() {
        //表示用户网络不佳的时候点击了重新加载
        if (mAlbumDetailPresenter != null) {
            mAlbumDetailPresenter.getAlbumDetail((int) mCurrentId ,mCurrentPage);
        }
    }

    @Override
    public void onItemClick(List<Track> detailData, int position) {
        //设置播放器的数据
        PlayPresenter playPresenter = PlayPresenter.getPlayPresenter();
        playPresenter.setPlayList(detailData,position);
        //跳转到播放器界面
        Intent intent = new Intent(this,PlayerActivity.class);
        startActivity(intent);
    }

    /**
     * 根据播放状态修改
     * @param playing
     */
    private void updatePlayState(boolean playing) {
        if (mPlayControlBtn != null && mPlayControlTv!=null) {
            mPlayControlBtn.setImageResource(playing?R.drawable.selector_play_control_pause:R.drawable.selector_play_control_play);
            if (!playing) {
                mPlayControlTv.setText(R.string.click_play_tips_text);
            }else {
                if (TextUtils.isEmpty(mCurrentTrackTitle)) {
                    mPlayControlTv.setText(mCurrentTrackTitle);
                }
            }
        }
    }
    @Override
    public void onPlayStart() {
        //修改图标为暂停的，文字修改为正在播放
        updatePlayState(true);
    }

    @Override
    public void onPlayPause() {
        //修改图标为播放的，文字修改为已暂停
        updatePlayState(false);
    }

    @Override
    public void onPlayStop() {
        //修改图标为播放的，文字修改为已暂停
        updatePlayState(false);
    }

    @Override
    public void onPlayError() {

    }

    @Override
    public void onNextPlay(Track track) {

    }

    @Override
    public void onPrePlay(Track track) {

    }

    @Override
    public void onListLoaded(List<Track> list) {

    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {

    }

    @Override
    public void onProgressChange(int currentProgress, int total) {

    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdFinished() {

    }

    @Override
    public void onTrackUpdate(Track track, int playIndex) {
        if (track != null) {
            mCurrentTrackTitle = track.getTrackTitle();
            if (!TextUtils.isEmpty(mCurrentTrackTitle) && mPlayControlTv!=null) {
                mPlayControlTv.setText(mCurrentTrackTitle);
            }
        }
    }

    @Override
    public void updateListOrder(boolean isReverse) {

    }
}
