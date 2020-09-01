package alex.example.ximalaya.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.List;

import alex.example.ximalaya.R;
import alex.example.ximalaya.utils.LogUtil;

public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.InnewHolder> {

    private List<Album> mData = new ArrayList<>();
    private static final String TAG = "AlbumListAdapter";
    private onRecommendItemClickListener mItemClickListener = null;

    @NonNull
    @Override
    public AlbumListAdapter.InnewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //加载view
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend, parent, false);

        return new InnewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumListAdapter.InnewHolder holder, final int position) {
        //设置数据
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    int clickPositon = (int) v.getTag();
                    mItemClickListener.onItemClick(clickPositon,mData.get(clickPositon));
                }
                LogUtil.d(TAG,"holder.itemView click -->" + v.getTag());
            }
        });
        holder.setData(mData.get(position));
    }

    @Override
    public int getItemCount() {
        //返回要显示的个数
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    public void setData(List<Album> albumList) {
        if (mData != null) {
            mData.clear();
            mData.addAll(albumList);
        }
        //更新UI
        notifyDataSetChanged();
    }

    public class InnewHolder extends RecyclerView.ViewHolder {
        public InnewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setData(Album album) {
            //找到这个控件
            //设置数据
            //专辑的封面
            ImageView albumCoverIv = itemView.findViewById(R.id.album_cover);
            //title
            TextView albumTitleTv = itemView.findViewById(R.id.album_title_tv);
            //描述
            TextView albumDesTv = itemView.findViewById(R.id.album_description_tv);
            //播放数量
            TextView albumPlayCountTv = itemView.findViewById(R.id.album_play_count);
            //专辑内容数量
            TextView albumContentCountTv = itemView.findViewById(R.id.album_content_size);

            albumTitleTv.setText(album.getAlbumTitle());
            albumDesTv.setText(album.getAlbumIntro());
            albumPlayCountTv.setText(album.getPlayCount()+"");
            albumContentCountTv.setText(album.getIncludeTrackCount()+"");

            Picasso.with(itemView.getContext()).load(album.getCoverUrlLarge()).into(albumCoverIv);
        }
    }

    public void setOnRecommendItemClickListener(onRecommendItemClickListener listener){
        this.mItemClickListener = listener;
    }
    public interface onRecommendItemClickListener{
        void onItemClick(int position, Album album);
    }
}
