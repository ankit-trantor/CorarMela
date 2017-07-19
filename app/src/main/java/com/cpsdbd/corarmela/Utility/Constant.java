package com.cpsdbd.corarmela.Utility;

/**
 * Created by Genius 03 on 6/18/2017.
 */

public class Constant {
    public static final String BASE_URL="https://www.googleapis.com/youtube/v3/";
    public static final String WEB_API_KEY="AIzaSyA75YG2chLl0ylmi36WdrWIIQb4phJxa0w";
    public static final String CHANNEL_ID="UCBzXpYLvnsiiBjcrUn5Lopg";

    public static final String RESOURCE_ID="RESOURCE_ID";
    public static final String PLAYLIST_ID="PLAYLIST_ID";
    public static final String DESCRIPTION="DESCRIPTION";
    public static final String TITLE="TITLE";
    public static final String PLAYLIST_OFFLINE="PLAYLIST_OFFLINE";


    //public static final String TEST_URL="https://www.googleapis.com/youtube/v3/playlists?maxResults=25&channelId=UCfsw17OqpY9j0MhIWnjDtZA&part=snippet%2CcontentDetails&key=AIzaSyDkoQ7w4kj7AObR8tZsv_k8OhIEAF8mqic";

    public static final String PLAYLIST=BASE_URL+"playlists?maxResults=25&channelId="+CHANNEL_ID+"&part=snippet%2CcontentDetails&key="+WEB_API_KEY;
    public static final String VIDEO_LIST=BASE_URL+"playlists?maxResults=25&channelId="+CHANNEL_ID+"&part=snippet%2CcontentDetails&key="+WEB_API_KEY;

    public static String getVideoListURl(String playlistId){
        String VIDEO_LIST=BASE_URL+"playlistItems?maxResults=25&playlistId="+playlistId+"&part=snippet&key="+WEB_API_KEY;

        return VIDEO_LIST;

    }

    public static String getVideoUrl(String resourceId){
        return "http://youtube.com/watch?v="+resourceId;
    }

}
