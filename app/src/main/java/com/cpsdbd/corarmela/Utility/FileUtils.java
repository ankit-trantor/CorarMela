package com.cpsdbd.corarmela.Utility;

import android.content.Context;
import android.os.Environment;

import com.cpsdbd.corarmela.Model.OfflinePlayList;
import com.cpsdbd.corarmela.Model.OfflineVideo;
import com.cpsdbd.corarmela.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Genius 03 on 7/3/2017.
 */

public class FileUtils {

    public void traverse (File dir) {
        if (dir.exists()) {
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; ++i) {
                File file = files[i];
                if (file.isDirectory()) {
                    traverse(file);
                } else {
                    // do something here with the file
                }
            }
        }
    }

    public static boolean isFileExist(File dir,String fileName){
        boolean retbool = false;

        if(dir.exists()){
            File[] files = dir.listFiles();

            for(File x: files){
                if(x.getName().equals(fileName)){
                    retbool = true;
                    break;
                }
            }
        }

        return retbool;
    }

    public static boolean isRootDirectoryExist(Context context){
        boolean retBool = false;

        File file = Environment.getExternalStorageDirectory();

        File[] files = file.listFiles();



        for(File x: files){
            if(x.getName().equals(context.getString(R.string.app_name)) && x.isDirectory()){
                retBool=true;
                break;
            }
        }
        return retBool;



    }

    public static List<OfflinePlayList> getPlayList(Context context){
        List<OfflinePlayList> playList = new ArrayList<>();

        File file = Environment.getExternalStorageDirectory();

        File newFile = new File(file.getPath(),context.getString(R.string.app_name));

        File[] files = newFile.listFiles();

        for (int i = 0; i < files.length; ++i) {
            File folder = files[i];
            if (folder.isDirectory()) {
                OfflinePlayList offlinePlayList = new OfflinePlayList();
                offlinePlayList.setTempName("Playlist "+(i+1));
                offlinePlayList.setName(folder.getName());
                playList.add(offlinePlayList);

            }
        }

        /*for(File x: files){
            if(x.isDirectory()){
                OfflinePlayList offlinePlayList = new OfflinePlayList();
                playList.add(x.getName());
            }
        }*/

        return playList;

    }

    public static List<OfflineVideo> getOfflineVideoList(Context context,String playListName){

        List<OfflineVideo> offlineVideoList = new ArrayList<>();

        File file = Environment.getExternalStorageDirectory();

        File root = new File(file.getPath(),context.getString(R.string.app_name));

        File playList = new File(root.getPath(),playListName);

        File[] files = playList.listFiles();

        for (File x: files){

            if(!x.isDirectory()){
                OfflineVideo offlineVideo = new OfflineVideo(x.getName(),x.getPath());
                offlineVideoList.add(offlineVideo);

            }

        }

        return offlineVideoList;



    }
}
