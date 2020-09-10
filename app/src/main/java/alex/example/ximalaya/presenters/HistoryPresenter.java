package alex.example.ximalaya.presenters;

import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

import alex.example.ximalaya.base.BaseApplication;
import alex.example.ximalaya.data.HistoryDao;
import alex.example.ximalaya.data.IHistoryDao;
import alex.example.ximalaya.data.IHistoryDaoCallback;
import alex.example.ximalaya.interfaces.IHistoryCallback;
import alex.example.ximalaya.interfaces.IHistoryPresenter;
import alex.example.ximalaya.utils.LogUtil;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static alex.example.ximalaya.utils.Constants.MAX_HISTORY_COUNT;

/**
 * 历史数量最多100，如果超过了100，则删除最前面添加的，再添加当前的
 */
public class HistoryPresenter implements IHistoryPresenter, IHistoryDaoCallback {

    private static final String TAG = "HistoryPresenter";
    private final IHistoryDao mHistoryDao;
    private List<IHistoryCallback> mCallbacks = new ArrayList<>();
    private List<Track> mCurrentHistories = null;
    private Track mCurrentAddTrack = null;

    private HistoryPresenter() {
        mHistoryDao = new HistoryDao();
        mHistoryDao.setCallback(this);
        listHistories();
    }

    private static HistoryPresenter sHistoryPresenter = null;

    public static HistoryPresenter getHistoryPresenter() {
        if (sHistoryPresenter == null) {
            synchronized (HistoryPresenter.class) {
                if (sHistoryPresenter == null) {
                    sHistoryPresenter = new HistoryPresenter();
                }
            }
        }
        return sHistoryPresenter;
    }

    @Override
    public void listHistories() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mHistoryDao != null) {
                    mHistoryDao.listHistories();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    private boolean isDoDelOutOfSize = false;

    @Override
    public void addHistory(Track track) {
        //判断是否已经>=100条
        if (mCurrentHistories != null && mCurrentHistories.size() >= MAX_HISTORY_COUNT) {
            isDoDelOutOfSize = true;
            this.mCurrentAddTrack = track;
            //先不能添加，先删除最前的一条，再添加
            delHistory(mCurrentHistories.get(mCurrentHistories.size() - 1));
        } else {
            doAddHistory(track);
        }
    }

    private void doAddHistory(Track track) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mHistoryDao != null) {
                    mHistoryDao.addHistory(track);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void delHistory(Track track) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mHistoryDao != null) {
                    mHistoryDao.delHistory(track);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void clearHistory() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mHistoryDao != null) {
                    mHistoryDao.clearHistory();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void registerViewCallBack(IHistoryCallback iHistoryCallback) {
        //UI注册过来
        if (!mCallbacks.contains(iHistoryCallback)) {
            mCallbacks.add(iHistoryCallback);
        }
    }

    @Override
    public void unRegisterViewCallBack(IHistoryCallback iHistoryCallback) {
        //删除UI的回调
        mCallbacks.remove(iHistoryCallback);
    }

    @Override
    public void onHistoryAdd(boolean isSuccess) {
        //不需要动作
        listHistories();
    }

    @Override
    public void onHistoryDel(boolean isSuccess) {
        //不需要动作
        if (isDoDelOutOfSize && mCurrentAddTrack != null) {
            isDoDelOutOfSize=false;
            //添加当前的数据进入数据库
            addHistory(mCurrentAddTrack);
        } else {
            listHistories();
        }
    }

    @Override
    public void onHistoryClear(boolean isSuccess) {
        //不需要动作
        listHistories();
    }

    @Override
    public void onHistoryLoaded(List<Track> tracks) {
        this.mCurrentHistories = tracks;
        LogUtil.d(TAG,"histories size -- > " + tracks.size());
        //通知UI更新数据
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (IHistoryCallback iHistoryCallback : mCallbacks) {
                    iHistoryCallback.onHistoriesLoaded(tracks);
                }
            }
        });
    }
}
