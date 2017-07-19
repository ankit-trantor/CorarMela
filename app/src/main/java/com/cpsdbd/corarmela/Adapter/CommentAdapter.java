package com.cpsdbd.corarmela.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cpsdbd.corarmela.R;
import com.cpsdbd.corarmela.Utility.MyUtils;
import com.google.api.services.youtube.model.Comment;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Genius 03 on 7/10/2017.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {
    private Context context;
    private List<Comment> commentList;
    private LayoutInflater inflater;

    public CommentAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.single_comment,parent,false);

        CommentHolder holder = new CommentHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(CommentHolder holder, int position) {

        Comment comment = commentList.get(position);

        holder.tvName.setText(comment.getSnippet().getAuthorDisplayName());
        holder.tvComment.setText(comment.getSnippet().getTextOriginal());
        holder.tvDate.setText(MyUtils.getActualFormat(comment.getSnippet().getPublishedAt().toString()));

        //comment.getSnippet().getPublishedAt()

        if(comment.getSnippet().getAuthorProfileImageUrl().equals("") || comment.getSnippet().getAuthorProfileImageUrl()==null){

        }else{
            Picasso.with(context)
                    .load(comment.getSnippet().getAuthorProfileImageUrl())
                    .into(holder.ivProfile);
        }

    }

    public void addComment(Comment comment){
        commentList.add(comment);
        int position = commentList.indexOf(comment);
        notifyItemInserted(position);
    }

    public void addCommentatTop(Comment comment){
        commentList.add(0,comment);
        int position = commentList.indexOf(comment);
        notifyItemInserted(position);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }


    public class CommentHolder extends RecyclerView.ViewHolder{
        CircleImageView ivProfile;
        TextView tvName,tvComment,tvDate;

        public CommentHolder(View itemView) {
            super(itemView);

            ivProfile = (CircleImageView) itemView.findViewById(R.id.profile_image);

            tvName = (TextView) itemView.findViewById(R.id.name);
            tvComment = (TextView) itemView.findViewById(R.id.comment);
            tvDate = (TextView) itemView.findViewById(R.id.date);
        }
    }
}
