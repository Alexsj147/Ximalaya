package alex.example.ximalaya.data;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

public interface ISubDaoCallback {
    /**
     * 添加的结果回调
     * @param isSuccess
     */
    void onAddResult(boolean isSuccess);

    /**
     * 删除的回调结果
     * @param isSuccess
     */
    void onDeleteResult(boolean isSuccess);

    /**
     * 加载结果的回调
     * @param result
     */
    void onSubListLoaded(List<Album> result);
}
