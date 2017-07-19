package com.cpsdbd.corarmela.Adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cpsdbd.corarmela.Model.OfflinePlayList;
import com.cpsdbd.corarmela.R;
import com.cpsdbd.corarmela.Utility.MyUtils;

import java.util.List;

/**
 * Created by Genius 03 on 7/6/2017.
 */

public class OffLinePlayListAdapter extends RecyclerView.Adapter<OffLinePlayListAdapter.PlayListHolder> {
    private Activity context;
    private List<OfflinePlayList> offlinePlayListList;
    private LayoutInflater inflater;
    private ItemClickListener listener;


    public OffLinePlayListAdapter(Activity context, List<OfflinePlayList> offlinePlayListList) {
        this.context = context;
        this.offlinePlayListList = offlinePlayListList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public PlayListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.off_line_playlist,parent,false);

        PlayListHolder holder = new PlayListHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(PlayListHolder holder, int position) {
        OfflinePlayList playList = offlinePlayListList.get(position);

        int width = MyUtils.getDeviceWidth(context);
        int height = holder.image.getHeight();

        Bitmap bitmap = MyUtils.resizeBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.offlineplaylist),width,
                width);

        holder.image.setImageBitmap(bitmap);
        holder.tvTitle.setText(playList.getTempName());
    }

    public void add(OfflinePlayList x){
        offlinePlayListList.add(x);
        int pos = offlinePlayListList.indexOf(x);
        notifyItemInserted(pos);
    }

    @Override
    public int getItemCount() {
        return offlinePlayListList.size();
    }

    class PlayListHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView image;
        private TextView tvTitle;

        public PlayListHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.title);
            image = (ImageView) itemView.findViewById(R.id.image);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if(listener!=null){
                listener.onItemClick(getAdapterPosition());
            }

        }
    }

    public void setItemClickListener(ItemClickListener listener){
        this.listener= listener;
    }

    public interface ItemClickListener{
        public void onItemClick(int position);
    }
}
