package com.cpsdbd.corarmela.Fragments;


import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cpsdbd.corarmela.Adapter.OfflineVideoAdapter;
import com.cpsdbd.corarmela.Model.OfflinePlayList;
import com.cpsdbd.corarmela.Model.OfflineVideo;
import com.cpsdbd.corarmela.OfflinePlayVideoActivity;
import com.cpsdbd.corarmela.R;
import com.cpsdbd.corarmela.Utility.Constant;
import com.cpsdbd.corarmela.Utility.FileUtils;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * A simple {@link Fragment} subclass.
 */
public class OfflineVideoList extends Fragment implements OfflineVideoAdapter.ItemClickListener {
    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION=1000;

    private TextView tvTitle;
    private RecyclerView rvPlaylist;

    private List<OfflineVideo> offlineVideoList;
    private OfflineVideoAdapter adapter;

    private OfflinePlayList offlinePlayList;


    public OfflineVideoList() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            offlinePlayList = (OfflinePlayList) getArguments().getSerializable(Constant.PLAYLIST_OFFLINE);
        }

        offlineVideoList = new ArrayList<>();
        adapter = new OfflineVideoAdapter(getActivity(),offlineVideoList);
        adapter.setItemClickListener(this);

        writeDataOnExternalStorage();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_offline_video_list, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        tvTitle = (TextView) view.findViewById(R.id.title);
        rvPlaylist = (RecyclerView) view.findViewById(R.id.playList);
        rvPlaylist.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvPlaylist.setAdapter(adapter);

    }

    @Override
    public void onItemClick(int position) {

        OfflineVideo offlineVideo = offlineVideoList.get(position);
        Intent intent = new Intent(getActivity(), OfflinePlayVideoActivity.class);
        intent.putExtra("VIDEO_PATH",offlineVideo.getPath());
        startActivity(intent);

    }

    @AfterPermissionGranted(WRITE_EXTERNAL_STORAGE_PERMISSION)
    private void writeDataOnExternalStorage() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(getActivity(), perms)) {

            for(OfflineVideo x : FileUtils.getOfflineVideoList(getActivity(),offlinePlayList.getName())){
                adapter.add(x);
            }

        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_external_storage),
                    WRITE_EXTERNAL_STORAGE_PERMISSION, perms);
        }
    }
}
