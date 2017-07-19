package com.cpsdbd.corarmela.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cpsdbd.corarmela.Utility.MyYoutubeHelper;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.youtube.model.PlaylistItem;
import com.cpsdbd.corarmela.Adapter.VideoAdapter;
import com.cpsdbd.corarmela.R;
import com.cpsdbd.corarmela.Utility.Constant;
import com.cpsdbd.corarmela.VideoActivity;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoListFragment extends Fragment implements VideoAdapter.VideoClickListener{

    private RecyclerView rvVideos;
    private List<PlaylistItem> itemList;
    private VideoAdapter adapter;
    private GoogleAccountCredential mCredential;

    private Subscription videoSubscription;

    private Observable<List<PlaylistItem>> playListObservable;

    private String playListId;

    MyYoutubeHelper helper;



    public VideoListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        itemList = new ArrayList<>();
        adapter = new VideoAdapter(getActivity(),itemList);
        adapter.setVideoClickedListener(this);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video_list, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {

        rvVideos = (RecyclerView) view.findViewById(R.id.rvVideos);
        rvVideos.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvVideos.setAdapter(adapter);

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        playListId = getArguments().getString("pl");

       if(mCredential!=null){
           helper = MyYoutubeHelper.getInstance(getActivity(),mCredential);
           getPlayListItems(helper);
       }
    }

    private void getPlayListItems(final MyYoutubeHelper helper) {
        playListObservable = Observable.create(new Observable.OnSubscribe<List<PlaylistItem>>() {
            @Override
            public void call(Subscriber<? super List<PlaylistItem>> subscriber) {
                try {
                    List<PlaylistItem> data = helper.getAllVideo(playListId);
                    subscriber.onNext(data); // Emit the contents of the URL
                    subscriber.onCompleted(); // Nothing more to emit
                } catch (Exception e) {
                    subscriber.onError(e); // In case there are network errors
                }
            }
        });

        videoSubscription =playListObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<PlaylistItem>>() {
                    @Override
                    public void call(List<PlaylistItem> playlistItems) {

                        for(PlaylistItem x: playlistItems){
                            adapter.addItem(x);
                        }

                    }
                });
    }

    public void setCredential(GoogleAccountCredential mCredential){
        this.mCredential=mCredential;
    }

    @Override
    public void onItemClicked(int position) {
        PlaylistItem item = itemList.get(position);

        rateVideo(item);




        /*VideoFragment videoFragment = new VideoFragment();
        videoFragment.setArguments(bundle);

        getFragmentManager().beginTransaction().replace(R.id.main_container,videoFragment).addToBackStack(null).commit();*/
    }

    private void gotoVideoActivity(PlaylistItem item){
        Intent intent = new Intent(getActivity(), VideoActivity.class);
        intent.putExtra(Constant.RESOURCE_ID,item.getSnippet().getResourceId().getVideoId());
        intent.putExtra(Constant.PLAYLIST_ID,item.getSnippet().getPlaylistId());
        intent.putExtra(Constant.DESCRIPTION,item.getSnippet().getDescription());
        intent.putExtra(Constant.TITLE,item.getSnippet().getTitle());
        startActivity(intent);
    }

    private void rateVideo(final PlaylistItem item) {
        Observable<String> rateObservable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    String data = helper.rateVideo(item.getSnippet().getResourceId().getVideoId());
                    subscriber.onNext(data); // Emit the contents of the URL
                    subscriber.onCompleted(); // Nothing more to emit
                } catch (Exception e) {
                    subscriber.onError(e); // In case there are network errors
                }

            }
        });

        rateObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                       gotoVideoActivity(item);
                    }
                });
    }
}
