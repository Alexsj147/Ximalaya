package alex.example.ximalaya.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import alex.example.ximalaya.utils.Constants;

import static alex.example.ximalaya.utils.Constants.HISTORY_AUTHOR;
import static alex.example.ximalaya.utils.Constants.HISTORY_COVER;
import static alex.example.ximalaya.utils.Constants.HISTORY_DURATION;
import static alex.example.ximalaya.utils.Constants.HISTORY_ID;
import static alex.example.ximalaya.utils.Constants.HISTORY_PLAY_COUNT;
import static alex.example.ximalaya.utils.Constants.HISTORY_TITLE;
import static alex.example.ximalaya.utils.Constants.HISTORY_TRACK_ID;
import static alex.example.ximalaya.utils.Constants.HISTORY_UPDATE_TIME;
import static alex.example.ximalaya.utils.Constants.SUB_ALBUM_ID;
import static alex.example.ximalaya.utils.Constants.SUB_AUTHOR_NAME;
import static alex.example.ximalaya.utils.Constants.SUB_COVER_URL;
import static alex.example.ximalaya.utils.Constants.SUB_DESCRIPTION;
import static alex.example.ximalaya.utils.Constants.SUB_ID;
import static alex.example.ximalaya.utils.Constants.SUB_PLAY_COUNT;
import static alex.example.ximalaya.utils.Constants.SUB_TITLE;
import static alex.example.ximalaya.utils.Constants.SUB_TRACKS_COUNT;

public class XimalayaDBHelper extends SQLiteOpenHelper {

    public XimalayaDBHelper(Context context) {
        //name 数据库名称 factory游标工厂 version 版本号
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION_CODE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据表
        //订阅相关的字段
        //图片、title、描述、播放量、节目数量、作者名称（详情界面） 专辑id
        String subTBSql ="create table "+Constants.SUB_TB_NAME+"(" +
                SUB_ID+" integer primary key autoincrement," +
                SUB_COVER_URL +" varchar," +
                SUB_TITLE+" varchar," +
                SUB_DESCRIPTION+" varchar," +
                SUB_PLAY_COUNT +" integer," +
                SUB_TRACKS_COUNT +" integer," +
                SUB_AUTHOR_NAME +" varchar," +
                SUB_ALBUM_ID +" integer"+
                ")";
        db.execSQL(subTBSql);
        //创建历史记录表
        String histortTbSql = "create table "+Constants.HISTORY_TB_NAME+"(" +
                HISTORY_ID+" integer primary key autoincrement," +
                HISTORY_TITLE +" varchar," +
                HISTORY_COVER +" varchar," +
                HISTORY_AUTHOR +" varchar," +
                HISTORY_PLAY_COUNT +" integer," +
                HISTORY_DURATION +" integer," +
                HISTORY_UPDATE_TIME +" integer," +
                HISTORY_TRACK_ID +" integer"+
                ")";
        db.execSQL(histortTbSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
