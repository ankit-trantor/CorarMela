package com.cpsdbd.corarmela;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cpsdbd.corarmela.Adapter.CommentAdapter;
import com.cpsdbd.corarmela.Utility.DollUtil;
import com.cpsdbd.corarmela.Utility.FileUtils;
import com.cpsdbd.corarmela.Utility.MyYoutubeHelper;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.cpsdbd.corarmela.Utility.Constant;
import com.cpsdbd.corarmela.Utility.MyUtils;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.Comment;
import com.google.api.services.youtube.model.VideoStatistics;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class VideoActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener,
        YouTubePlayer.PlaybackEventListener,EasyPermissions.PermissionCallbacks{
    private static final int RECOVERY_REQUEST = 1;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION=1000;

    public static final int REQUEST_ACCOUNT_PICKER = 1000;
    public static final int REQUEST_AUTHORIZATION = 1001;
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    public static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private YouTubePlayerView youTubeView;

    private String itemId;
    private String playListId;
    private String videoTitle;
    private String videoDescription;

    private TextView tvLike,tvDislike,tvView,tvCommentCount,tvVideoTitle,tvVideoDescription;
    private EditText etComment;


    private YouTubePlayer player;
    private ImageButton btnDownload;

    private RecyclerView rvComments;
    private CommentAdapter adapter;
    private List<Comment> commentList;

    private List<Integer> itags;

    private int commentCounter;

    private Observable<VideoStatistics> statObservable;
    private Observable<String> rateObservable;

    Observable<List<Comment>> commentObserVable;

    private GoogleAccountCredential mCredential;
    private DollUtil dollUtil;
    private static final String[] SCOPES = { YouTubeScopes.YOUTUBE_READONLY,YouTubeScopes.YOUTUBE, YouTubeScopes.YOUTUBE_FORCE_SSL,YouTubeScopes.YOUTUBEPARTNER};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        commentList = new ArrayList<>();
        adapter = new CommentAdapter(this,commentList);

        dollUtil = new DollUtil(this);


        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                this, Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());


        getResultsFromApi();



    }

    private void initView(){
        tvLike = (TextView) findViewById(R.id.like);
        tvDislike = (TextView) findViewById(R.id.dislike);
        tvView = (TextView) findViewById(R.id.view);
        tvCommentCount = (TextView) findViewById(R.id.tv_comment_count);
        tvVideoTitle = (TextView) findViewById(R.id.video_title);
        tvVideoDescription = (TextView) findViewById(R.id.video_description);
        etComment = (EditText) findViewById(R.id.et_comment);

        // Set Text
        tvVideoTitle.setText(videoTitle);
        //tvVideoDescription.setText(videoDescription);
    }

    private void getResultsFromApi() {
        if (! dollUtil.isGooglePlayServicesAvailable()) {
            //acquireGooglePlayServices();
            dollUtil.acquireGooglePlayServices(REQUEST_GOOGLE_PLAY_SERVICES);
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        }else {

            // Initia Lixe
            itags = new ArrayList<>();

            btnDownload = (ImageButton) findViewById(R.id.download);

            MyUtils.colorStatusBar(this);

            itemId = getIntent().getStringExtra(Constant.RESOURCE_ID);
            playListId = getIntent().getStringExtra(Constant.PLAYLIST_ID);
            videoTitle = getIntent().getStringExtra(Constant.TITLE);
            videoDescription = getIntent().getStringExtra(Constant.DESCRIPTION);



            youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
            youTubeView.initialize(Constant.WEB_API_KEY, this);

            rvComments = (RecyclerView) findViewById(R.id.rv_comments);
            rvComments.setLayoutManager(new LinearLayoutManager(this));
            rvComments.setAdapter(adapter);

            initView();

            getStatistics();

            getAllComments();

        }
    }

    private void getAllComments() {
        final MyYoutubeHelper helper = MyYoutubeHelper.getInstance(this,mCredential);
        commentObserVable  = Observable.create(new Observable.OnSubscribe<List<Comment>>() {
            @Override
            public void call(Subscriber<? super List<Comment>> subscriber) {
                try {
                    List<Comment> data = helper.getAllComments(itemId);
                    subscriber.onNext(data); // Emit the contents of the URL
                    subscriber.onCompleted(); // Nothing more to emit
                } catch (Exception e) {
                    subscriber.onError(e); // In case there are network errors
                }
            }
        });

        commentObserVable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Comment>>() {
                    @Override
                    public void call(List<Comment> comments) {
                        for (Comment x: comments){
                            adapter.addComment(x);
                        }
                    }
                });

    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, android.Manifest.permission.GET_ACCOUNTS)) {
            String accountName = this.getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    android.Manifest.permission.GET_ACCOUNTS);
        }
    }



    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Toast.makeText(this, "This app requires Google Play Services. Please install " +
                            "Google Play Services on your device and relaunch this app.", Toast.LENGTH_LONG).show();
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;

            case RECOVERY_REQUEST:

                // Retry initialization if user performed a recovery action
                getYouTubePlayerProvider().initialize(Constant.WEB_API_KEY, this);

                break;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this,MyBackgroundService.class);
        stopService(intent);
    }



    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean b) {
        if (!b) {
            this.player = player;
            player.setPlaybackEventListener(this);
            player.loadVideo(itemId); // Plays https://www.youtube.com/watch?v=fhWaJi1Hsfo

            // Rate the Video
            //rateVideo();
        }

    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this, RECOVERY_REQUEST).show();
        }

    }


    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return youTubeView;
    }

    @Override
    public void onPlaying() {

    }

    @Override
    public void onPaused() {

    }

    @Override
    public void onStopped() {

    }

    @Override
    public void onBuffering(boolean b) {

    }

    @Override
    public void onSeekTo(int i) {

    }

    public void downloadVideo(View view) {
        /*Log.d("YYYY",itemId);
        mExtractor.extract(itemId).enqueue(mExtractionCallback);*/

        writeDataOnExternalStorage();

    }

    @AfterPermissionGranted(WRITE_EXTERNAL_STORAGE_PERMISSION)
    private void writeDataOnExternalStorage() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            // ...
            //createDirectory(playListId,videoTitle);
            downloadFile();

        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_external_storage),
                    WRITE_EXTERNAL_STORAGE_PERMISSION, perms);
        }
    }


    private void downloadFile(){
        String videoUrl = Constant.getVideoUrl(itemId);


        new YouTubeExtractor(this) {
            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                if (ytFiles == null) {

                    return;
                   /* int itag = 22;
                    String downloadUrl = ytFiles.get(itag).getUrl();

                    Log.d("UUU",downloadUrl);*/

                }

                // Iterate over itags
                for (int i = 0, itag; i < ytFiles.size(); i++) {
                    itag = ytFiles.keyAt(i);
                    itags.add(itag);
                    // ytFile represents one file with its url and meta data
                    YtFile ytFile = ytFiles.get(itag);

                    // Just add videos in a decent format => height -1 = audio
                    if (ytFile.getFormat().getHeight() == -1 || ytFile.getFormat().getHeight() >= 360) {
                        //addButtonToMainLayout(vMeta.getTitle(), ytFile);


                        /*String downloadUrl = ytFiles.get(itag).getUrl();

                        Log.d("UUU",downloadUrl);*/
                    }
                }

                int finalItag= Collections.min(itags);

                String downloadUrl = ytFiles.get(finalItag).getUrl();

                String fileName="";
                if (videoTitle.length() > 55) {
                    fileName = videoTitle.substring(0, 55) + "." + ytFiles.get(finalItag).getFormat().getExt();
                } else {
                    fileName = videoTitle + "." + ytFiles.get(finalItag).getFormat().getExt();
                }

                File file = new File(Environment.getExternalStorageDirectory().getPath()+"/"+getString(R.string.app_name)+"/"+playListId);


                if(!FileUtils.isFileExist(file,fileName)){
                    saveFile(downloadUrl,"/"+getString(R.string.app_name)+"/"+playListId,fileName);
                }else{
                    Toast.makeText(VideoActivity.this, "File Already Downloaded", Toast.LENGTH_SHORT).show();
                }


                //saveFile(downloadUrl,"/"+getString(R.string.app_name)+"/"+playListId,fileName);


            }
        }.extract(videoUrl, true, true);



       //DownloadManager.Request request = new DownloadManager.Request(resultUri);


        /*request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        //File file= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        request.setDestinationInExternalPublicDir(path,title);

        Long reference = manager.enqueue(request);*/

    }

    private void saveFile(String downloadedUrl,String path,String name){

        DownloadManager manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

        Uri uri = Uri.parse(downloadedUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle("Downloading Files");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(path,name);
        //request.setDestinationInExternalFilesDir(this)
        manager.enqueue(request);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    public void getStaistics(View view) {

    }


    private void getStatistics(){
        statObservable = Observable.create(new Observable.OnSubscribe<VideoStatistics>() {
            @Override
            public void call(Subscriber<? super VideoStatistics> subscriber) {
                try {
                    VideoStatistics data = MyYoutubeHelper.getInstance(VideoActivity.this,mCredential).getStatistics(itemId);
                    subscriber.onNext(data); // Emit the contents of the URL
                    subscriber.onCompleted(); // Nothing more to emit
                } catch (Exception e) {
                    subscriber.onError(e); // In case there are network errors
                }
            }
        });

        statObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<VideoStatistics>() {
                    @Override
                    public void call(VideoStatistics videoStatistics) {
                        tvLike.setText(videoStatistics.getLikeCount()+"");
                        tvDislike.setText(videoStatistics.getDislikeCount()+"");
                        tvView.setText(videoStatistics.getViewCount()+" Views");

                        commentCounter = videoStatistics.getCommentCount().intValue();
                        tvCommentCount.setText(commentCounter+" Comments");

                    }
                });
    }

    public void insertComment(View view) {
        String commentText = etComment.getText().toString().trim();

        if(!TextUtils.isEmpty(commentText)){
            etComment.setText("");
            MyUtils.hideKey(view);
            insertComment(commentText,itemId);
        }

    }

    private void insertComment(final String comment, final String videoId){
        final MyYoutubeHelper helper = MyYoutubeHelper.getInstance(this,mCredential);
        Observable<Comment> insertcommentObservable = Observable.create(new Observable.OnSubscribe<Comment>() {
            @Override
            public void call(Subscriber<? super Comment> subscriber) {
                try {
                    Comment data = helper.insertComment(comment,videoId);
                    subscriber.onNext(data); // Emit the contents of the URL
                    subscriber.onCompleted(); // Nothing more to emit
                } catch (Exception e) {
                    subscriber.onError(e); // In case there are network errors
                }
            }
        });

        insertcommentObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
               .subscribe(new Action1<Comment>() {
                   @Override
                   public void call(Comment comment) {
                       adapter.addCommentatTop(comment);
                       rvComments.scrollToPosition(0);
                       // Increse Comment Count
                       commentCounter++;
                       // Update Ui
                       updateComment();

                   }
               });
    }

    private void updateComment() {
        tvCommentCount.setText(commentCounter+" Comments");
    }

    public void shareVideo(View view) {
        shareVideo(videoTitle,itemId);
    }


    private void shareVideo(String title, String videoId){
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, title);
        share.putExtra(Intent.EXTRA_TEXT, "https://www.youtube.com/watch?v="+videoId);

        startActivity(Intent.createChooser(share, "Share link!"));
    }
}
