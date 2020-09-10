package alex.example.ximalaya.presenters;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alex.example.ximalaya.base.BaseApplication;
import alex.example.ximalaya.data.ISubDaoCallback;
import alex.example.ximalaya.data.SubscriptionDao;
import alex.example.ximalaya.interfaces.ISubscriptionCallBack;
import alex.example.ximalaya.interfaces.ISubscriptionPresenter;
import alex.example.ximalaya.utils.Constants;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SubscriptionPresenter implements ISubscriptionPresenter, ISubDaoCallback {

    private final SubscriptionDao mSubscriptionDao;
    private Map<Long, Album> mData = new HashMap<>();
    private List<ISubscriptionCallBack> mCallBacks = new ArrayList<>();


    private SubscriptionPresenter() {
        mSubscriptionDao = SubscriptionDao.getInstance();
        mSubscriptionDao.setCallback(this);
    }

    private void listSubscriptions(){
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                    //只调用，不处理结果
                if (mSubscriptionDao != null) {
                    mSubscriptionDao.listAlbums();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    private static SubscriptionPresenter sInstance = null;

    public static SubscriptionPresenter getInstance() {
        if (sInstance == null) {
            synchronized (SubscriptionPresenter.class) {
                sInstance = new SubscriptionPresenter();
            }
        }
        return sInstance;
    }

    @Override
    public void addSubscription(final Album album) {
        //判断当前的订阅数量，不能超过100个
        if (mData.size()>= Constants.MAX_SUB_COUNT) {
            //提示
            for (ISubscriptionCallBack iSubscriptionCallBack : mCallBacks) {
                iSubscriptionCallBack.onSubFull();
            }
            return;
        }
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mSubscriptionDao != null) {
                    mSubscriptionDao.addAlbum(album);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void deleteSubscription(final Album album) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mSubscriptionDao != null) {
                    mSubscriptionDao.deleteAlbum(album);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void getSubscriptionList() {
        listSubscriptions();
    }

    @Override
    public boolean isSub(Album album) {
        Album result = mData.get(album.getId());
        //不为空，表示已订阅
        return result!=null;
    }

    @Override
    public void registerViewCallBack(ISubscriptionCallBack iSubscriptionCallBack) {
        if (!mCallBacks.contains(iSubscriptionCallBack)) {
            mCallBacks.add(iSubscriptionCallBack);
        }
    }

    @Override
    public void unRegisterViewCallBack(ISubscriptionCallBack iSubscriptionCallBack) {
        mCallBacks.remove(iSubscriptionCallBack);
    }

    @Override
    public void onAddResult(final boolean isSuccess) {
        listSubscriptions();
        //添加结果的回调
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallBack iSubscriptionCallBack : mCallBacks) {
                    iSubscriptionCallBack.onAddResult(isSuccess);
                }
            }
        });
    }

    @Override
    public void onDeleteResult(final boolean isSuccess) {
        listSubscriptions();
        //删除结果的回调
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallBack iSubscriptionCallBack : mCallBacks) {
                    iSubscriptionCallBack.onDeleteResult(isSuccess);
                }
            }
        });
    }

    @Override
    public void onSubListLoaded(final List<Album> result) {
        //加载结果的回调
        mData.clear();
        for (Album album : result) {
            mData.put(album.getId(), album);
        }
        //通知UI更新
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallBack iSubscriptionCallBack : mCallBacks) {
                    iSubscriptionCallBack.onSubscriptionLoaded(result);
                }
            }
        });

    }
}
