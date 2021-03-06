package alex.example.ximalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import alex.example.ximalaya.base.IBasePresenter;

/**
 * 订阅有上限，不能超过100个
 */
public interface ISubscriptionPresenter extends IBasePresenter<ISubscriptionCallBack> {

    /**
     * 添加订阅
     * @param album
     */
    void addSubscription(Album album);

    /**
     * 删除订阅
     * @param album
     */
    void deleteSubscription(Album album);

    /**
     * 获取订阅列表
     */
    void getSubscriptionList();

    /**
     * 判断当前专辑是否已经订阅/收藏
     * @param album
     * @return
     */
    boolean isSub(Album album);
}
