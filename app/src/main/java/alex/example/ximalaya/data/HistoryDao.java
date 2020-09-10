package alex.example.ximalaya.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ximalaya.ting.android.opensdk.model.album.Announcer;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

import alex.example.ximalaya.base.BaseApplication;
import alex.example.ximalaya.utils.Constants;
import alex.example.ximalaya.utils.LogUtil;

import static alex.example.ximalaya.utils.Constants.HISTORY_AUTHOR;
import static alex.example.ximalaya.utils.Constants.HISTORY_COVER;
import static alex.example.ximalaya.utils.Constants.HISTORY_DURATION;
import static alex.example.ximalaya.utils.Constants.HISTORY_PLAY_COUNT;
import static alex.example.ximalaya.utils.Constants.HISTORY_TITLE;
import static alex.example.ximalaya.utils.Constants.HISTORY_TRACK_ID;
import static alex.example.ximalaya.utils.Constants.HISTORY_UPDATE_TIME;

public class HistoryDao implements IHistoryDao {

    private static final String TAG = "HistoryDao";
    private final XimalayaDBHelper mDbHelper;
    private IHistoryDaoCallback mCallback = null;
    private Object mLock = new Object();

    public HistoryDao() {
        mDbHelper = new XimalayaDBHelper(BaseApplication.getAppContext());
    }

    @Override
    public void setCallback(IHistoryDaoCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public void addHistory(Track track) {
        synchronized (mLock) {
            SQLiteDatabase db = null;
            boolean isSuccess = false;
            try {
                db = mDbHelper.getWritableDatabase();
                //先去删除
                int delResult = db.delete(Constants.HISTORY_TB_NAME,Constants.HISTORY_TRACK_ID + "=?",new String[]{track.getDataId() + ""});
                LogUtil.d(TAG,"delResult -- > " + delResult);
                //删除以后再添加
                db.beginTransaction();
                ContentValues contentValues = new ContentValues();
                //封装数据
                contentValues.put(HISTORY_TITLE, track.getTrackTitle());
                contentValues.put(HISTORY_PLAY_COUNT, track.getPlayCount());
                contentValues.put(HISTORY_DURATION, track.getDuration());
                contentValues.put(HISTORY_UPDATE_TIME, track.getUpdatedAt());
                contentValues.put(HISTORY_TRACK_ID, track.getDataId());
                contentValues.put(HISTORY_COVER, track.getCoverUrlLarge());
                contentValues.put(HISTORY_AUTHOR, track.getAnnouncer().getNickname());
                //插入数据
                db.insert(Constants.HISTORY_TB_NAME, null, contentValues);
                db.setTransactionSuccessful();
                isSuccess = true;
            } catch (Exception e) {
                isSuccess = false;
                e.printStackTrace();
            } finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }
                if (mCallback != null) {
                    mCallback.onHistoryAdd(isSuccess);
                }
            }
        }
    }

    @Override
    public void delHistory(Track track) {
        synchronized (mLock) {
            SQLiteDatabase db = null;
            boolean isDeleteSuccess = false;
            try {
                db = mDbHelper.getWritableDatabase();
                db.beginTransaction();
                int delete = db.delete(Constants.HISTORY_TB_NAME, Constants.HISTORY_ID + "=?", new String[]{track.getDataId() + ""});
                LogUtil.d(TAG, "delete -->" + delete);
                db.setTransactionSuccessful();
                isDeleteSuccess = true;
            } catch (Exception e) {
                e.printStackTrace();
                isDeleteSuccess = false;
            } finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }
                if (mCallback != null) {
                    mCallback.onHistoryDel(isDeleteSuccess);
                }
            }
        }
    }

    @Override
    public void clearHistory() {
        synchronized (mLock) {


            SQLiteDatabase db = null;
            boolean isDeleteSuccess = false;
            try {
                db = mDbHelper.getWritableDatabase();
                db.beginTransaction();
                db.delete(Constants.HISTORY_TB_NAME, null, null);
                db.setTransactionSuccessful();
                isDeleteSuccess = true;
            } catch (Exception e) {
                e.printStackTrace();
                isDeleteSuccess = false;
            } finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }
                if (mCallback != null) {
                    mCallback.onHistoryClear(isDeleteSuccess);
                }
            }
        }
    }

    @Override
    public void listHistories() {
        synchronized (mLock) {


            //从数据表中查出所有记录
            SQLiteDatabase db = null;
            List<Track> histories = new ArrayList<>();
            try {
                db = mDbHelper.getReadableDatabase();
                db.beginTransaction();
                Cursor cursor = db.query(Constants.HISTORY_TB_NAME, null, null, null, null, null, "_id desc");
                while (cursor.moveToNext()) {
                    Track track = new Track();
                    int trackId = cursor.getInt(cursor.getColumnIndex(HISTORY_TRACK_ID));
                    track.setDataId(trackId);
                    String title = cursor.getString(cursor.getColumnIndex(HISTORY_TITLE));
                    track.setTrackTitle(title);
                    int playCount = cursor.getInt(cursor.getColumnIndex(HISTORY_PLAY_COUNT));
                    track.setPlayCount(playCount);
                    int duration = cursor.getInt(cursor.getColumnIndex(HISTORY_DURATION));
                    track.setDuration(duration);
                    long updateTime = cursor.getLong(cursor.getColumnIndex(HISTORY_UPDATE_TIME));
                    track.setUpdatedAt(updateTime);
                    String cover = cursor.getString(cursor.getColumnIndex(HISTORY_COVER));
                    track.setCoverUrlLarge(cover);
                    track.setCoverUrlMiddle(cover);
                    track.setCoverUrlSmall(cover);
                    String author = cursor.getString(cursor.getColumnIndex(HISTORY_AUTHOR));
                    Announcer announcer = new Announcer();
                    announcer.setNickname(author);
                    track.setAnnouncer(announcer);
                    histories.add(track);
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }
                //通知出去
                if (mCallback != null) {
                    mCallback.onHistoryLoaded(histories);
                }
            }
        }
    }
}
