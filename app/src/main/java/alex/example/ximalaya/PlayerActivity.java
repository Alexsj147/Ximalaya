package alex.example.ximalaya;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alex.example.ximalaya.adapters.PlayerTrackPagerAdapter;
import alex.example.ximalaya.base.BaseActivity;
import alex.example.ximalaya.interfaces.IPlayerCallBack;
import alex.example.ximalaya.presenters.PlayPresenter;
import alex.example.ximalaya.utils.LogUtil;

import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;

public class PlayerActivity extends BaseActivity implements IPlayerCallBack, ViewPager.OnPageChangeListener {

    private static final String TAG ="PlayerActivity" ;
    private ImageView mControlBtn;
    private PlayPresenter mPlayPresenter;
    private SimpleDateFormat mMinFormat = new SimpleDateFormat("mm:ss");
    private SimpleDateFormat mHourFormat = new SimpleDateFormat("hh:mm:ss");
    private TextView mTotalDuration;
    private TextView mCurrentPosition;
    private SeekBar mDurationBar;
    private int mCurrentProgress = 0 ;
    private boolean MisUserTouchProgressBar = false;
    private ImageView mPlayNextBtn;
    private ImageView mPlayPreBtn;
    private TextView mTrackTitleTv;
    private String mTrackTitleText;
    private ViewPager mTrackPagerView;
    private PlayerTrackPagerAdapter mTrackPagerAdapter;
    private boolean mIsUserSlidePager = false;
    private ImageView mPlayerModeSwitchBtn;
    //
    private XmPlayListControl.PlayMode mCurrentMode = PLAY_MODEL_LIST;
    private static Map<XmPlayListControl.PlayMode, XmPlayListControl.PlayMode> sPlayModeRule = new HashMap<>();
    //1.默认的是PLAY_MODEL_LIST
    //2.列表循环PLAY_MODEL_LIST_LOOP
    //3.随机播放PLAY_MODEL_RANDOM
    //4.单曲循环PLAY_MODEL_SINGLE_LOOP
    static {
        sPlayModeRule.put(PLAY_MODEL_LIST,PLAY_MODEL_LIST_LOOP);
        sPlayModeRule.put(PLAY_MODEL_LIST_LOOP,PLAY_MODEL_RANDOM);
        sPlayModeRule.put(PLAY_MODEL_RANDOM,PLAY_MODEL_SINGLE_LOOP);
        sPlayModeRule.put(PLAY_MODEL_SINGLE_LOOP,PLAY_MODEL_LIST);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        mPlayPresenter = PlayPresenter.getPlayPresenter();
        mPlayPresenter.registerViewCallBack(this);


        initView();
        //在界面初始化后获取数据
        mPlayPresenter.getPlayList();
        initEvent();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放资源
        if (mPlayPresenter != null) {
            mPlayPresenter.unRegisterViewCallBack(this);
            mPlayPresenter=null;
        }
    }



    @SuppressLint("ClickableViewAccessibility")
    private void initEvent() {
        mControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果正在播放，那么暂停
                if (mPlayPresenter.isPlay()) {
                    mPlayPresenter.pause();
                }else {
                    //非播放状态，那么就让播放器播放节目
                    mPlayPresenter.play();
                }
            }
        });
        mDurationBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean isFromUser) {
                if (isFromUser) {
                    mCurrentProgress = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //触摸
                MisUserTouchProgressBar=true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MisUserTouchProgressBar=false;
                //手离开的时候更新进度条
                mPlayPresenter.seekTo(mCurrentProgress);
            }
        });
        mPlayPreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //播放前一个节目
                mPlayPresenter.playPre();
            }
        });
        mPlayNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //播放下一个节目
                mPlayPresenter.playNext();
            }
        });
        mTrackPagerView.addOnPageChangeListener(this);
        mTrackPagerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action){
                   case  MotionEvent.ACTION_DOWN:
                       mIsUserSlidePager =true;
                    break;
                }
                return false;
            }
        });
        mPlayerModeSwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //处理播放模式的切换
                //根据当前的mode获取下一个mode
                XmPlayListControl.PlayMode playMode = sPlayModeRule.get(mCurrentMode);
                //修改播放模式
                if (mPlayPresenter != null) {
                    mPlayPresenter.switchPlayMode(playMode);
                    mCurrentMode = playMode;
                    updatePlayModeBtnImg();
                }

            }
        });
    }

    /**
     * PLAY_MODEL_LIST
     * PLAY_MODEL_LIST_LOOP
     * PLAY_MODEL_RANDOM
     * PLAY_MODEL_SINGLE_LOOP
     */
    private void updatePlayModeBtnImg() {
        //根据当前的状态更新播放模式的图标
        int resId = R.drawable.selector_player_mode_list_order;

        switch (mCurrentMode){
            case PLAY_MODEL_LIST:
               resId = R.drawable.selector_player_mode_list_order;
                break;
            case PLAY_MODEL_LIST_LOOP:
                resId = R.drawable.selector_player_mode_list_order_looper;
                break;
            case PLAY_MODEL_RANDOM:
                resId = R.drawable.selector_player_mode_random;
                break;
            case PLAY_MODEL_SINGLE_LOOP:
                resId = R.drawable.selector_player_mode_single_looper;
                break;
        }
        mPlayerModeSwitchBtn.setImageResource(resId);
    }

    private void initView() {
        mControlBtn = this.findViewById(R.id.play_or_pause_btn);
        mTotalDuration = this.findViewById(R.id.track_duration);
        mCurrentPosition = this.findViewById(R.id.current_position);
        mDurationBar = this.findViewById(R.id.track_seek_bar);
        mPlayNextBtn = this.findViewById(R.id.play_next);
        mPlayPreBtn = this.findViewById(R.id.play_pre);
        mTrackTitleTv = this.findViewById(R.id.track_title);
        if (!TextUtils.isEmpty(mTrackTitleText)) {
            mTrackTitleTv.setText(mTrackTitleText);
        }
        mTrackPagerView = this.findViewById(R.id.track_pager_view);
        //创建适配器
        mTrackPagerAdapter = new PlayerTrackPagerAdapter();
        //设置适配器
        mTrackPagerView.setAdapter(mTrackPagerAdapter);

        //切换播放模式的按钮
        mPlayerModeSwitchBtn = this.findViewById(R.id.player_mode_switch_btn);

    }

    @Override
    public void onPlayStart() {
        //开始播放。修改UI成暂停的按钮
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.drawable.selector_player_stop);
        }
    }

    @Override
    public void onPlayPause() {
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.drawable.selector_player_play);
        }
    }

    @Override
    public void onPlayStop() {
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.drawable.selector_player_play);
        }
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
        //LogUtil.d(TAG,"list is == > " + list);
        // 把数据设置到适配器中
        if (mTrackPagerAdapter != null) {
            mTrackPagerAdapter.setData(list);
        }
    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {

    }

    @Override
    public void onProgressChange(int currentDuration, int total) {
        mDurationBar.setMax(total);
        //更新播放进度，更新进度条
        String totalDuration;
        String currentPosition;
        if (total>1000*60*60) {
            totalDuration = mHourFormat.format(total);
            currentPosition = mHourFormat.format(currentDuration);
        }else {
            totalDuration = mMinFormat.format(total);
            currentPosition = mMinFormat.format(currentDuration);
        }
        if (mTotalDuration != null) {
            mTotalDuration.setText(totalDuration);

        }
        //更新当前的时间
        if (mCurrentPosition != null) {
            mCurrentPosition.setText(currentPosition);
        }
        //更新进度
        //计算进度
        if (!MisUserTouchProgressBar) {
            //int percent = (int) (currentDuration * 1.0f / total * 100);
            //LogUtil.d(TAG,"percent is ==> " + percent);
            mDurationBar.setProgress(currentDuration);
        }
    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdFinished() {

    }

    @Override
    public void onTrackUpdate(Track track, int playIndex) {
        this.mTrackTitleText = track.getTrackTitle();
        if (mTrackTitleTv != null) {
            //设置当前节目的标题
            mTrackTitleTv.setText(mTrackTitleText);
        }
        //当节目改变的时候，获取当前播放的播放位置
        //当前节目改变后，修改页面的图片
        if (mTrackPagerView != null) {
            mTrackPagerView.setCurrentItem(playIndex,true);
        }
    }



    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        LogUtil.d(TAG,"position == > " + position);
        //当页面选中的时候，切换内容
        if (mPlayPresenter != null && mIsUserSlidePager) {
            mPlayPresenter.playByIndex(position);
        }
        mIsUserSlidePager = false;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
