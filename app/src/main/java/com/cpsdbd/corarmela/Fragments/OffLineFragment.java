package com.cpsdbd.corarmela.Fragments;


import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cpsdbd.corarmela.Adapter.OffLinePlayListAdapter;
import com.cpsdbd.corarmela.Model.OfflinePlayList;
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
public class OffLineFragment extends Fragment implements OffLinePlayListAdapter.ItemClickListener {
    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION=1000;

    private TextView tvTitle;
    private RecyclerView rvPlaylist;

    private List<OfflinePlayList> offlinePlayListList;
    private OffLinePlayListAdapter adapter;


    public OffLineFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        offlinePlayListList = new ArrayList<>();

        adapter = new OffLinePlayListAdapter(getActivity(),offlinePlayListList);
        adapter.setItemClickListener(this);

        // After View Initializa tion Call the Method
        writeDataOnExternalStorage();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_off_line, container, false);
        initView(view);

        return view;
    }

    private void initView(View view) {
        tvTitle = (TextView) view.findViewById(R.id.title);
        rvPlaylist = (RecyclerView) view.findViewById(R.id.playList);
        rvPlaylist.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvPlaylist.setAdapter(adapter);
    }

    @AfterPermissionGranted(WRITE_EXTERNAL_STORAGE_PERMISSION)
    private void writeDataOnExternalStorage() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(getActivity(), perms)) {
            // Already have permission, do the thing
            // ...
            //createDirectory(playListId,videoTitle);
            //downloadFile();

           /* Log.d("SOHEL", FileUtils.isRootDirectoryExist(getActivity())+"");
            Log.d("SOHEL", FileUtils.getPlayList(getActivity()).get(0).getTempName()+"");*/

            for(OfflinePlayList x : FileUtils.getPlayList(getActivity())){
                adapter.add(x);
            }

        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_external_storage),
                    WRITE_EXTERNAL_STORAGE_PERMISSION, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onItemClick(int position) {
        OfflinePlayList offlinePlayList = offlinePlayListList.get(position);

        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.PLAYLIST_OFFLINE,offlinePlayList);

        OfflineVideoList offlineVideoList = new OfflineVideoList();
        offlineVideoList.setArguments(bundle);

        getFragmentManager().beginTransaction().replace(R.id.main_container,offlineVideoList)
                .addToBackStack(null).commit();

        //Log.d("NAME",offlinePlayList.getTempName());
    }
}
