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

import com.cpsdbd.corarmela.Model.OfflineVideo;
import com.cpsdbd.corarmela.R;
import com.cpsdbd.corarmela.Utility.MyUtils;

import java.util.List;

/**
 * Created by Genius 03 on 7/8/2017.
 */

public class OfflineVideoAdapter extends RecyclerView.Adapter<OfflineVideoAdapter.OfflineVideoHolder> {
    private Activity context;
    private List<OfflineVideo> offlineVideoList;
    private LayoutInflater inflater;
    private ItemClickListener listener;

    public OfflineVideoAdapter(Activity context, List<OfflineVideo> offlineVideoList) {
        this.context = context;
        this.offlineVideoList = offlineVideoList;
        this.inflater = LayoutInflater.from(context);
    }

    public void add(OfflineVideo x){
        offlineVideoList.add(x);
        int pos = offlineVideoList.indexOf(x);
        notifyItemInserted(pos);
    }

    @Override
    public OfflineVideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.off_line_videolist,parent,false);

        OfflineVideoHolder holder = new OfflineVideoHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(OfflineVideoHolder holder, int position) {

        OfflineVideo video = offlineVideoList.get(position);

        int width = MyUtils.getDeviceWidth(context);

        Bitmap bitmap = MyUtils.resizeBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.offlinesinglevideo),width,
                width);

        holder.imageView.setImageBitmap(bitmap);
        holder.tvTitle.setText( video.getName().replace(".3gp",""));

    }

    @Override
    public int getItemCount() {
        return offlineVideoList.size();
    }

    class OfflineVideoHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView imageView;
        TextView tvTitle;

        public OfflineVideoHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.title);
            imageView = (ImageView) itemView.findViewById(R.id.image);

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
