package alex.example.ximalaya;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.List;

import alex.example.ximalaya.adapters.IndicatorAdapter;
import alex.example.ximalaya.adapters.MainContentAdapter;
import alex.example.ximalaya.interfaces.IPlayerCallBack;
import alex.example.ximalaya.presenters.PlayPresenter;
import alex.example.ximalaya.presenters.RecommendPresenter;
import alex.example.ximalaya.utils.LogUtil;
import alex.example.ximalaya.views.RoundRectImageView;

public class MainActivity extends FragmentActivity implements IPlayerCallBack {

    private static final String TAG="MainActivity";
    private MagicIndicator mMagicIndicator;
    private ViewPager mContentPager;
    private IndicatorAdapter mIndicatorAdapter;
    private RoundRectImageView mRoundRectImageView;
    private TextView mHeadTitle;
    private TextView mSubTitle;
    private ImageView mPlayControl;
    private PlayPresenter mPlayPresenter;
    private View mPlayControlItem;
    private View mSearchBtn;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();
        initPresenter();
        /*Map<String, String> map = new HashMap<>();
        CommonRequest.getCategories(map, new IDataCallBack<CategoryList>() {
            @Override
            public void onSuccess(CategoryList categoryList) {
               List<Category> categories=categoryList.getCategories();
                if (categories!=null) {
                  int size=categories.size();
                     Log.d(TAG, "categories size ---<"+size);
                    for (Category category : categories) {
                        //Log.d(TAG,"category -->"+category.getCategoryName());
                        LogUtil.d(TAG,"category -->"+category.getCategoryName());
                    }

                }

            }

            @Override
            public void onError(int i, String s) {
                //Log.d(TAG,"error code --"+ i +"error message ==>" + s);
                  LogUtil.d(TAG,"error code --"+ i +"error message ==>" + s);
            }
        });*/
    }

    private void initPresenter() {
        mPlayPresenter = PlayPresenter.getPlayPresenter();
        mPlayPresenter.registerViewCallBack(this);
    }

    private void initEvent() {
        mIndicatorAdapter.setOnIndicatorTapClickListener(new IndicatorAdapter.OnIndicatorTapClickListener() {
            @Override
            public void onTapClick(int index) {
                LogUtil.d(TAG,"click index is ==> " + index);
                if (mContentPager != null) {
                    mContentPager.setCurrentItem(index);
                }
            }
        });
        mPlayControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayPresenter != null) {
                    boolean hasPlayList = mPlayPresenter.hasPlayList();
                    if (!hasPlayList) {
                        //没有设置播放，就播放第一个推荐专辑
                        //第一个专辑每天都会变化
                        playFirstRecommend();
                    }else {
                        if (mPlayPresenter.isPlaying()) {
                            mPlayPresenter.pause();
                        }else {
                            mPlayPresenter.play();
                        }
                    }
                }
            }
        });
        mPlayControlItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hasPlayList = mPlayPresenter.hasPlayList();
                if (!hasPlayList) {
                    playFirstRecommend();
                }
                //跳转到播放器界面
                startActivity(new Intent(MainActivity.this,PlayerActivity.class));
            }
        });
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到搜索界面
                startActivity(new Intent(MainActivity.this,SearchActivity.class));
            }
        });
    }

    /**
     * 播放第一个推荐的内容
     */
    private void playFirstRecommend() {
        List<Album> currentRecommend = RecommendPresenter.getInstance().getCurrentRecommend();
        if (currentRecommend != null && currentRecommend.size()>0) {
            Album album = currentRecommend.get(0);
            long albumId = album.getId();
            mPlayPresenter.playByAlbumId(albumId);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initView() {
        mMagicIndicator = this.findViewById(R.id.main_indicator);
        mMagicIndicator.setBackgroundColor(this.getColor(R.color.mainColor));
        //创建适配器
        mIndicatorAdapter = new IndicatorAdapter(this);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(mIndicatorAdapter);
        //设置要显示的内容


        //ViewPager
        mContentPager = this.findViewById(R.id.content_pager);
        //创建内容适配器
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        MainContentAdapter mainContentAdapter = new MainContentAdapter(supportFragmentManager);
        mContentPager.setAdapter(mainContentAdapter);
        //把ViewPager和indicator绑定
        mMagicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mMagicIndicator,mContentPager);

        //播放器相关的控件
        mRoundRectImageView = this.findViewById(R.id.main_track_cover);
        mHeadTitle = this.findViewById(R.id.main_head_title);
        mHeadTitle.setSelected(true);
        mSubTitle = this.findViewById(R.id.main_sub_title);
        mPlayControl = this.findViewById(R.id.main_play_control);
        mPlayControlItem = this.findViewById(R.id.main_play_control_item);

        //搜索的控件
        mSearchBtn = this.findViewById(R.id.main_search_btn);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayPresenter != null) {
            mPlayPresenter.unRegisterViewCallBack(this);
        }
    }

    @Override
    public void onPlayStart() {
        updatePlayControl(true);
    }

    private void updatePlayControl(boolean isPlaying){
        if (mPlayControl != null) {
            mPlayControl.setImageResource(isPlaying?R.drawable.selector_player_pause:R.drawable.selector_player_play);
        }
    }

    @Override
    public void onPlayPause() {
        updatePlayControl(false);
    }

    @Override
    public void onPlayStop() {
        updatePlayControl(false);
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
            String trackTitle = track.getTrackTitle();
            String nickname = track.getAnnouncer().getNickname();
            String coverUrlMiddle = track.getCoverUrlMiddle();
            LogUtil.d(TAG,"trackTitle......" + trackTitle);
            if (mHeadTitle != null) {
                mHeadTitle.setText(trackTitle);
            }
            LogUtil.d(TAG,"nickname......"+ nickname);
            if (mSubTitle != null) {
                mSubTitle.setText(nickname);
            }
            LogUtil.d(TAG,"coverUrlMiddle......"+coverUrlMiddle);
            Picasso.with(this).load(coverUrlMiddle).into(mRoundRectImageView);
        }
    }

    @Override
    public void updateListOrder(boolean isReverse) {

    }
}
