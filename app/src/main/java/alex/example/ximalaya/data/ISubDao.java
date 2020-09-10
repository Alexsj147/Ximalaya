package alex.example.ximalaya.data;

import com.ximalaya.ting.android.opensdk.model.album.Album;

public interface ISubDao {

    void setCallback(ISubDaoCallback callback);
    /**
     * 添加准基订阅
     * @param album
     */
    void addAlbum(Album album);

    /**
     * 删除订阅内容
     * @param album
     */
    void deleteAlbum(Album album);

    /**
     * 获取订阅内容
     *
     */
    void listAlbums();
}
