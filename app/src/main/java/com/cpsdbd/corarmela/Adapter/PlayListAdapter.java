package com.cpsdbd.corarmela.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cpsdbd.corarmela.R;
import com.google.api.services.youtube.model.Playlist;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Genius 03 on 6/18/2017.
 */

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.PlayListHolder> {

    private Context context;
    private List<Playlist> itemList;
    private LayoutInflater inflater;
    private ItemClickListener listener;

    public PlayListAdapter(Context context, List<Playlist> itemList) {
        this.context = context;
        this.itemList = itemList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public PlayListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.my_playlist,parent,false);
        PlayListHolder holder = new PlayListHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(PlayListHolder holder, int position) {
        Playlist item = itemList.get(position);

        Picasso.with(context)
                .load(item.getSnippet().getThumbnails().getHigh().getUrl())
                .into(holder.imageView);

        holder.tvPlayList.setText(item.getSnippet().getTitle());

        holder.tvVideoCount.setText(item.getContentDetails().getItemCount()+" Videos");


    }

    public void addPlayList(Playlist playlist){
        itemList.add(playlist);

        int position = itemList.indexOf(playlist);
        notifyItemInserted(position);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }

    class PlayListHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView imageView;
        TextView tvPlayList,tvVideoCount;

        public PlayListHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            tvPlayList= (TextView) itemView.findViewById(R.id.desc);
            tvVideoCount= (TextView) itemView.findViewById(R.id.video_count);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if(listener!= null){
                listener.onItemClick(getAdapterPosition());
            }

        }
    }


    public interface ItemClickListener{
        public void onItemClick(int position);
    }
}
