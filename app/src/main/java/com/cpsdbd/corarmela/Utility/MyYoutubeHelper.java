package com.cpsdbd.corarmela.Utility;

import android.app.Activity;

import com.cpsdbd.corarmela.VideoActivity;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Comment;
import com.google.api.services.youtube.model.CommentSnippet;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.CommentThreadListResponse;
import com.google.api.services.youtube.model.CommentThreadSnippet;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.PlaylistListResponse;
import com.cpsdbd.corarmela.MainActivity;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.Subscription;
import com.google.api.services.youtube.model.SubscriptionListResponse;
import com.google.api.services.youtube.model.SubscriptionSnippet;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.VideoStatistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Genius 03 on 6/21/2017.
 */

public class MyYoutubeHelper {

    private Activity activity;
    private YouTube youTube;
    private static MyYoutubeHelper myYoutubeHelper = null;



    private MyYoutubeHelper(Activity activity,GoogleAccountCredential mCredential) {
        this.activity = activity;

        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        youTube = new YouTube.Builder(
                transport, jsonFactory, mCredential)
                .setApplicationName("DollTube")
                .build();

    }

    public static MyYoutubeHelper getInstance(Activity activity,GoogleAccountCredential mCredential){
        if(myYoutubeHelper==null){
            myYoutubeHelper = new MyYoutubeHelper(activity,mCredential);
        }

        return myYoutubeHelper;

    }

    public List<Playlist> getPlayListData(){
        List<Playlist> retList= new ArrayList<>();
        try {
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("part", "snippet,contentDetails");
            parameters.put("channelId", Constant.CHANNEL_ID);
            parameters.put("maxResults", "25");

            YouTube.Playlists.List playlistsListByChannelIdRequest = youTube.playlists().list(parameters.get("part").toString());
            if (parameters.containsKey("channelId") && parameters.get("channelId") != "") {
                playlistsListByChannelIdRequest.setChannelId(parameters.get("channelId").toString());
            }

            if (parameters.containsKey("maxResults")) {
                playlistsListByChannelIdRequest.setMaxResults(Long.parseLong(parameters.get("maxResults").toString()));
            }

            PlaylistListResponse response = playlistsListByChannelIdRequest.execute();
            retList.addAll(response.getItems());
        }catch (GoogleJsonResponseException e) {
            e.printStackTrace();
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
        } catch (UserRecoverableAuthIOException e) {
            activity.startActivityForResult(e.getIntent(), MainActivity.REQUEST_AUTHORIZATION);
        }catch (Throwable t) {
            t.printStackTrace();
        }

        return retList;
    }

    public List<PlaylistItem> getAllVideo(String playListId){
        List<PlaylistItem> retList = new ArrayList<>();

        try {
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("part", "snippet,contentDetails");
            parameters.put("maxResults", "25");
            parameters.put("playlistId", playListId);


            YouTube.PlaylistItems.List playlistItemsListByPlaylistIdRequest = youTube.playlistItems().list(parameters.get("part").toString());
            if (parameters.containsKey("maxResults")) {
                playlistItemsListByPlaylistIdRequest.setMaxResults(Long.parseLong(parameters.get("maxResults").toString()));
            }

            if (parameters.containsKey("playlistId") && parameters.get("playlistId") != "") {
                playlistItemsListByPlaylistIdRequest.setPlaylistId(parameters.get("playlistId").toString());
            }

            PlaylistItemListResponse response = playlistItemsListByPlaylistIdRequest.execute();
            retList.addAll(response.getItems());


        }catch (GoogleJsonResponseException e) {
            e.printStackTrace();
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
        } catch (UserRecoverableAuthIOException e) {
            activity.startActivityForResult(e.getIntent(), MainActivity.REQUEST_AUTHORIZATION);
        }catch (Throwable t) {
            t.printStackTrace();
        }

        return retList;



    }

    public List<Subscription> getAllSubscription(){
        List<Subscription> retList = new ArrayList<>();

        try {
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("part", "snippet,contentDetails");
            parameters.put("channelId", Constant.CHANNEL_ID);

            YouTube.Subscriptions.List subscriptionsListByChannelIdRequest = youTube.subscriptions().list(parameters.get("part").toString());
            if (parameters.containsKey("channelId") && parameters.get("channelId") != "") {
                subscriptionsListByChannelIdRequest.setChannelId(parameters.get("channelId").toString());
            }

            SubscriptionListResponse response = subscriptionsListByChannelIdRequest.execute();

            retList.addAll(response.getItems());
            System.out.println(response);
        }catch (GoogleJsonResponseException e) {
            e.printStackTrace();
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
        } catch (UserRecoverableAuthIOException e) {
            activity.startActivityForResult(e.getIntent(), MainActivity.REQUEST_AUTHORIZATION);
        }catch (Throwable t) {
            t.printStackTrace();
        }

        return retList;
    }


    public boolean subscribeChannel(){
        boolean isSubsCribe = false;
        try {
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("part", "snippet");


            Subscription subscription = new Subscription();
            SubscriptionSnippet snippet = new SubscriptionSnippet();
            ResourceId resourceId = new ResourceId();
            resourceId.set("channelId", Constant.CHANNEL_ID);
            resourceId.set("kind", "youtube#channel");

            snippet.setResourceId(resourceId);
            subscription.setSnippet(snippet);

            YouTube.Subscriptions.Insert subscriptionsInsertRequest = youTube.subscriptions().insert(parameters.get("part").toString(), subscription);

            Subscription response = subscriptionsInsertRequest.execute();


            isSubsCribe = true;


        } catch (GoogleJsonResponseException e) {
            e.printStackTrace();
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
        } catch (UserRecoverableAuthIOException e) {
            activity.startActivityForResult(e.getIntent(), MainActivity.REQUEST_AUTHORIZATION);
        }catch (Throwable t) {
            t.printStackTrace();
        }

        return isSubsCribe;
    }


    public VideoStatistics getStatistics(String videoId){
        VideoStatistics statistics = null;
        try {
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("part", "statistics");
            parameters.put("id", videoId);

            YouTube.Videos.List videosListByIdRequest = youTube.videos().list(parameters.get("part").toString());
            if (parameters.containsKey("id") && parameters.get("id") != "") {
                videosListByIdRequest.setId(parameters.get("id").toString());
            }

            VideoListResponse response = videosListByIdRequest.execute();
            statistics = response.getItems().get(0).getStatistics();

        } catch (GoogleJsonResponseException e) {
            e.printStackTrace();
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
        } catch (UserRecoverableAuthIOException e) {
            activity.startActivityForResult(e.getIntent(), MainActivity.REQUEST_AUTHORIZATION);
        }catch (Throwable t) {
            t.printStackTrace();
        }

        return statistics;
    }

    public String rateVideo(String id){
        String retStr = "";
        try {
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("id", id);
            parameters.put("rating", "like");

            YouTube.Videos.Rate videosRateRequest = youTube.videos().rate(parameters.get("id").toString(), parameters.get("rating").toString());
            videosRateRequest.execute();

           // Log.d()


            retStr = "Rate";



        } catch (GoogleJsonResponseException e) {
            retStr = "Not Rate";
            e.printStackTrace();
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
        } catch (UserRecoverableAuthIOException e) {
            activity.startActivityForResult(e.getIntent(), MainActivity.REQUEST_AUTHORIZATION);
            retStr = "Not Rate";
        }catch (Throwable t) {
            retStr = "Not Rate";
            t.printStackTrace();
        }

        return retStr;
    }


    public List<Comment> getAllComments(String id){

        List<Comment> myCommentList = new ArrayList<>();

        try {
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("part", "snippet,replies");
            parameters.put("videoId", id);

            YouTube.CommentThreads.List commentThreadsListByVideoIdRequest = youTube.commentThreads().list(parameters.get("part").toString());
            if (parameters.containsKey("videoId") && parameters.get("videoId") != "") {
                commentThreadsListByVideoIdRequest.setVideoId(parameters.get("videoId").toString());
            }

            CommentThreadListResponse response = commentThreadsListByVideoIdRequest.execute();

            for(CommentThread x : response.getItems()){
                myCommentList.add(x.getSnippet().getTopLevelComment());
            }
        }catch (GoogleJsonResponseException e) {;
            e.printStackTrace();
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
        } catch (UserRecoverableAuthIOException e) {
            activity.startActivityForResult(e.getIntent(), VideoActivity.REQUEST_AUTHORIZATION);
        }catch (Throwable t) {
            t.printStackTrace();
        }

        return myCommentList;
    }


    public Comment insertComment(String comment,String videoId){
        Comment retStr = null;

        try {
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("part", "snippet");


            CommentThread commentThread = new CommentThread();

            CommentThreadSnippet snippet = new CommentThreadSnippet();
            snippet.setVideoId(videoId);
            snippet.setChannelId(Constant.CHANNEL_ID);

            Comment topLevelComment = new Comment();

            CommentSnippet commentSnippet = new CommentSnippet();
            commentSnippet.setTextOriginal(comment);

            topLevelComment.setSnippet(commentSnippet);
            snippet.setTopLevelComment(topLevelComment);
            commentThread.setSnippet(snippet);

            YouTube.CommentThreads.Insert commentThreadsInsertRequest = youTube.commentThreads().insert(parameters.get("part").toString(), commentThread);

            CommentThread response = commentThreadsInsertRequest.execute();
            retStr = response.getSnippet().getTopLevelComment();
            //Log.d("Commm",response.toString());
        }catch (GoogleJsonResponseException e) {
            e.printStackTrace();
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
        } catch (UserRecoverableAuthIOException e) {
            activity.startActivityForResult(e.getIntent(), VideoActivity.REQUEST_AUTHORIZATION);
        }catch (Throwable t) {
            t.printStackTrace();
        }

        return retStr;



    }


}
