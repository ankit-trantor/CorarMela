package com.cpsdbd.corarmela.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cpsdbd.corarmela.R;
import com.google.api.services.youtube.model.PlaylistItem;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Genius 03 on 6/19/2017.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoHolder> {
    private Context context;
    private List<PlaylistItem> itemList;
    private LayoutInflater inflater;
    private VideoClickListener listener;

    public VideoAdapter(Context context, List<PlaylistItem> itemList) {
        this.context = context;
        this.itemList = itemList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.single_playlist,parent,false);
        VideoHolder holder = new VideoHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(VideoHolder holder, int position) {

        PlaylistItem item = itemList.get(position);
        Picasso.with(context)
                .load(item.getSnippet().getThumbnails().getHigh().getUrl())
                .into(holder.imageView);

        holder.tvTitle.setText(item.getSnippet().getTitle());


    }

    public void addItem(PlaylistItem item){
        itemList.add(item);
        int position = itemList.indexOf(item);
        notifyItemInserted(position);
    }

    public void setVideoClickedListener(VideoClickListener listener){
        this.listener=listener;
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class VideoHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView imageView;
        TextView tvTitle;

        public VideoHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.image);
            tvTitle = (TextView) itemView.findViewById(R.id.title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if(listener!=null){
                listener.onItemClicked(getAdapterPosition());
            }

        }
    }

    public interface VideoClickListener{
        public void onItemClicked(int position);
    }
}
