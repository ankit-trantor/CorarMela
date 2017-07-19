package com.cpsdbd.corarmela.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.youtube.model.Playlist;
import com.cpsdbd.corarmela.Adapter.PlayListAdapter;
import com.cpsdbd.corarmela.R;
import com.cpsdbd.corarmela.Utility.MyYoutubeHelper;
import com.google.api.services.youtube.model.Subscription;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class YoutubeMainFragment extends Fragment implements PlayListAdapter.ItemClickListener {

    private GoogleAccountCredential mCredential;
    private RecyclerView rvPlayList;
    private List<Playlist> itemList;
    private PlayListAdapter adapter;


    private Observable<List<Playlist>> playListObservable;
    private Observable<List<Subscription>> subsListObservable;


    public YoutubeMainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_youtube_main, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {

        rvPlayList = (RecyclerView) view.findViewById(R.id.rv_playlist);
        itemList = new ArrayList<>();
        adapter = new PlayListAdapter(getActivity(),itemList);
        adapter.setItemClickListener(this);
        rvPlayList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvPlayList.setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(mCredential!=null){
            final MyYoutubeHelper helper =MyYoutubeHelper.getInstance(getActivity(),mCredential);
            getPlayList(helper);

        }


    }

    private void viewAllSubsCription(final MyYoutubeHelper helper){
        subsListObservable=Observable.create(new Observable.OnSubscribe<List<Subscription>>() {
            @Override
            public void call(Subscriber<? super List<Subscription>> subscriber) {
                try {
                    List<Subscription> data = helper.getAllSubscription();
                    subscriber.onNext(data); // Emit the contents of the URL
                    subscriber.onCompleted(); // Nothing more to emit
                } catch (Exception e) {
                    subscriber.onError(e); // In case there are network errors
                }
            }
        });

        subsListObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Subscription>>() {
                    @Override
                    public void call(List<Subscription> subscriptions) {
                        for(Subscription x: subscriptions){
                        }
                    }
                });
    }

    private void getPlayList(final MyYoutubeHelper helper) {
        playListObservable = Observable.create(new Observable.OnSubscribe<List<Playlist>>() {
            @Override
            public void call(Subscriber<? super List<Playlist>> subscriber) {
                try {
                    List<Playlist> data = helper.getPlayListData();
                    subscriber.onNext(data); // Emit the contents of the URL
                    subscriber.onCompleted(); // Nothing more to emit
                } catch (Exception e) {
                    subscriber.onError(e); // In case there are network errors
                }

            }
        });

        playListObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Playlist>>() {
                    @Override
                    public void call(List<Playlist> playlists) {

                        for(Playlist x: playlists){
                            adapter.addPlayList(x);
                        }

                    }
                });
    }

    @Override
    public void onItemClick(int position) {
        Playlist item = itemList.get(position);

        String playListId = item.getId();

        Bundle bundle = new Bundle();
        bundle.putString("pl",playListId);

        VideoListFragment videoListFragment = new VideoListFragment();
        videoListFragment.setCredential(mCredential);
        videoListFragment.setArguments(bundle);

        getFragmentManager().beginTransaction().replace(R.id.main_container,videoListFragment).addToBackStack(null).commit();




    }



    public void setCredential(GoogleAccountCredential mCredential){
        this.mCredential=mCredential;

    }

}
