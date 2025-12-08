package com.example.spotify.Service;

import static androidx.media.AudioManagerCompat.requestAudioFocus;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ServiceInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.spotify.R;
import com.example.spotify.models.Music;
import com.example.spotify.viewModels.MusicViewModel;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class MusicService extends Service {

    private MediaPlayer mediaPlayer;
    private MediaSessionCompat mediaSession;
    private int currentPosition = 0;
    private ArrayList<Music> playlist = new ArrayList<>();
    private String currentUrl = "";
    private List<Music> lsu = new ArrayList<>();
    private AudioManager audioManager;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable mUpdatePlaybackStateRunnable;
    private AudioFocusRequest focusRequest;
    private static final String CHANNEL_ID = "music_channel";
    private AudioManager.OnAudioFocusChangeListener focusChangeListener;

    private int currentIndex ;
    private boolean x = false;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(mp -> {
            NextSong();
        });
        mUpdatePlaybackStateRunnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    updatePlaybackState(); // Gọi hàm cập nhật

                    // Lặp lại sau mỗi 1 giây
                    mHandler.postDelayed(this, 1000);
                }
            }
        };
        MusicServiceHelper.setPlayer(mediaPlayer);
        mediaSession = new MediaSessionCompat(this, "MusicService");
        mediaSession.setActive(true);
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                // Code khi hệ thống yêu cầu "Play"
                Intent resumeIntent = new Intent(MusicService.this, MusicService.class);
                resumeIntent.setAction("RESUME");
                startService(resumeIntent);
            }

            @Override
            public void onPause() {
                // Code khi hệ thống yêu cầu "Pause"
                Intent pauseIntent = new Intent(MusicService.this, MusicService.class);
                pauseIntent.setAction("PAUSE");
                startService(pauseIntent);
            }

            @Override
            public void onSkipToNext() {
                // Code khi hệ thống yêu cầu "Next"
                Intent nextIntent = new Intent(MusicService.this, MusicService.class);
                nextIntent.setAction("NEXT");
                startService(nextIntent);
            }

            @Override
            public void onSkipToPrevious() {
                // Code khi hệ thống yêu cầu "Previous"
                Intent prevIntent = new Intent(MusicService.this, MusicService.class);
                prevIntent.setAction("PREVIOUS");
                startService(prevIntent);
            }
        });
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        createNotificationChannel();
        focusChangeListener = focusChange -> {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    // ✅ Lấy lại quyền phát nhạc
                    if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                        mediaPlayer.setVolume(1.0f, 1.0f);
                        Intent resumeIntent = new Intent(MusicService.this, MusicService.class);
                        resumeIntent.setAction("RESUME");
                        startService(resumeIntent);
                        MusicServiceHelper.setPhat(true);
                    }
                    break;

                case AudioManager.AUDIOFOCUS_LOSS:
                    // ❌ Mất hoàn toàn quyền -> dừng phát
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        Intent pauseIntent = new Intent(MusicService.this, MusicService.class);
                        pauseIntent.setAction("PAUSE");
                        startService(pauseIntent);
                        MusicServiceHelper.setPhat(false);
                    }
                    break;

                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    // ⚠️ Mất tạm thời (cuộc gọi, thông báo dài)
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        Intent pauseIntent = new Intent(MusicService.this, MusicService.class);
                        pauseIntent.setAction("PAUSE");
                        startService(pauseIntent);
                        MusicServiceHelper.setPhat(false);
                    }
                    break;

                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    // 🦆 Giảm âm lượng khi app khác phát âm thanh nhẹ
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.setVolume(0.3f, 0.3f);
                        MusicServiceHelper.setPhat(true);
                    }
                    break;

            }


        };

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            AudioAttributes playbackAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();

            focusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(playbackAttributes)
                    .setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener(focusChangeListener)
                    .build();
        }





//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setContentTitle("Đang phát: " + playlist.get(currentIndex))
//                .setContentText(playlist.get(currentIndex).getTenNgheSi())
//                .setSmallIcon(R.drawable.logo)
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo))
//                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//                .setStyle(new androidx.media.NotificationCompat.MediaStyle()
//                        .setMediaSession(mediaSession.getSessionToken())
//                        .setShowActionsInCompactView(0, 1, 2))
//                .addAction(new NotificationCompat.Action(
//                        R.drawable.ic_previous, "Trước", prevPendingIntent))
//                .addAction(new NotificationCompat.Action(
//                        R.drawable.ic_pause, "Tạm dừng", pausePendingIntent))
//                .addAction(new NotificationCompat.Action(
//                        R.drawable.ic_next, "Tiếp", nextPendingIntent));
//
//        startForeground(1, builder.build());

    }
    private void updatePlaybackState() {
        if (mediaPlayer == null || mediaSession == null) {
            return;
        }

        // Lấy trạng thái hiện tại
        long position = mediaPlayer.isPlaying() ? mediaPlayer.getCurrentPosition() : currentPosition;
        int state = mediaPlayer.isPlaying() ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED;

        // Xây dựng PlaybackState
        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_SEEK_TO // <-- BẮT BUỘC ĐỂ HIỆN SEEKBAR
                )
                // Cập nhật trạng thái, vị trí, và tốc độ phát
                .setState(state, position, 1.0f);

        // Gửi thông tin mới cho MediaSession
        mediaSession.setPlaybackState(stateBuilder.build());
    }
    private Notification showNotification(Bitmap albumArt, Music ms) {
        Intent playIntent = new Intent(this, MusicService.class);
        playIntent.setAction("PLAY");
        PendingIntent playPendingIntent = PendingIntent.getService(
                this, 0, playIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Intent pauseIntent = new Intent(this, MusicService.class);
        pauseIntent.setAction("PAUSE");
        PendingIntent pausePendingIntent = PendingIntent.getService(
                this, 0, pauseIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Intent nextIntent = new Intent(this, MusicService.class);
        nextIntent.setAction("NEXT");
        PendingIntent nextPendingIntent = PendingIntent.getService(
                this, 0, nextIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        Intent resumeIntent = new Intent(this, MusicService.class);
        resumeIntent.setAction("RESUME");
        PendingIntent resumePendingIntent = PendingIntent.getService(
                this, 0, resumeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        Intent prevIntent = new Intent(this, MusicService.class);
        prevIntent.setAction("PREVIOUS");
        PendingIntent prevPendingIntent = PendingIntent.getService(
                this, 0, prevIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );



        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(ms.getTenBaiHat())
                .setContentText(ms.getTenNgheSi())
                .setSmallIcon(R.drawable.tb_spotify)
                .setLargeIcon(albumArt)
                .setOnlyAlertOnce(true)
                .setShowWhen(false)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .addAction(R.drawable.tb_previous, "Trước", prevPendingIntent)
                .addAction(
                        mediaPlayer.isPlaying() ? R.drawable.tb_pause : R.drawable.tb_play, // icon thay đổi
                        mediaPlayer.isPlaying() ? "Tạm dừng" : "Phát",
                        mediaPlayer.isPlaying() ? pausePendingIntent : resumePendingIntent
                )
                .addAction(R.drawable.tb_next, "Tiếp", nextPendingIntent)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2)
                        .setMediaSession(mediaSession.getSessionToken()) // 👈 THÊM DÒNG NÀY
                );
        Notification notification = builder.build();
        return notification;
    }

    private void NextSong() {
        MusicViewModel.ktra.postValue(1);
        if (currentIndex >= playlist.size()) {
            currentIndex = 0;
        }
           MusicServiceHelper.setCurrentSong(playlist.get(currentIndex));

           if(currentIndex>0)
           {
               if (currentIndex > 0) {
                   // Lấy bài hát trước đó
                   Music prevSong = playlist.get(currentIndex - 1);

                   // SỬA: Dùng vòng for ngược để xóa phần tử an toàn
                   // (Không được dùng for-each để remove)
                   for (int i = lsu.size() - 1; i >= 0; i--) {
                       Music m = lsu.get(i);
                       if (m.getTenBaiHat().equals(prevSong.getTenBaiHat())
                               && m.getTenNgheSi().equals(prevSong.getTenNgheSi())) {
                           lsu.remove(i);
                           // Đã tìm thấy và xóa, có thể break nếu chỉ muốn xóa 1 bài trùng
                           break;
                       }
                   }
                   // Thêm bài vào lịch sử
                   lsu.add(prevSong);
               }
           }

            playNewSong(playlist.get(currentIndex));
            currentIndex++;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();

            if ("PLAY".equals(action)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    audioManager.requestAudioFocus(focusRequest);
                }
//                String url = intent.getStringExtra("url");
//                String tenBai = intent.getStringExtra("tenbai");
//                String tacGia= intent.getStringExtra("tacgia");
//                String anh = intent.getStringExtra("anh");
                Music ms = (Music) intent.getSerializableExtra("music");
                if (ms.getDuongDan() != null && !ms.getDuongDan().equals(currentUrl)) {
                    MusicViewModel.ktra.postValue(0);
                    playNewSong(ms);

                    lsu.add(ms);
                    saveCurrentSong(ms.getTenBaiHat(),ms.getTenNgheSi(),ms.getAnh(),ms.getDuongDan());
                } else if (!mediaPlayer.isPlaying()) {
                    MusicServiceHelper.setPhat(true);
                    mediaPlayer.start();
                    if(lsu.size()==0)
                    {
                        lsu.add(ms);
                        MusicViewModel.ktra.postValue(0);
                    } else {
                        for(Music m : lsu)
                        {
                            if(m.getTenBaiHat().equals(ms.getTenBaiHat())&&m.getTenNgheSi().equals(ms.getTenNgheSi()))
                            {

                                lsu.remove(m);
                            }

                        }
                        lsu.add(ms);
                    }
                    saveCurrentSong(ms.getTenBaiHat(),ms.getTenNgheSi(),ms.getAnh(),ms.getDuongDan());
                    MusicServiceHelper.setPlaying(true);
                }

            } else if ("PAUSE".equals(action)) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    currentPosition = mediaPlayer.getCurrentPosition();
                    new Thread(() -> {
                        Bitmap albumArt = getBitmapFromURL( MusicServiceHelper.getCurrentSong().getValue().getAnh());
                        Notification updated = showNotification(albumArt,  MusicServiceHelper.getCurrentSong().getValue());
                        NotificationManager nm = getSystemService(NotificationManager.class);
                        nm.notify(1, updated); // 🔹 Cập nhật notification sau
                    }).start();
                    updatePlaybackState();
                    MusicServiceHelper.setPhat(false);
                    MusicServiceHelper.setPlaying(false);
                }

            } else if ("RESUME".equals(action)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    audioManager.requestAudioFocus(focusRequest);
                }
                mediaPlayer.seekTo(currentPosition);
                if(!MusicServiceHelper.isPlaying()&&mediaPlayer!=null)
                {
                    mediaPlayer.start();
                    MusicServiceHelper.setPlaying(true);
                    new Thread(() -> {
                        Bitmap albumArt = getBitmapFromURL( MusicServiceHelper.getCurrentSong().getValue().getAnh());
                        Notification updated = showNotification(albumArt,  MusicServiceHelper.getCurrentSong().getValue());
                        NotificationManager nm = getSystemService(NotificationManager.class);
                        nm.notify(1, updated); // 🔹 Cập nhật notification sau
                    }).start();
                    updatePlaybackState();
                    MusicServiceHelper.setPhat(true);
                }

            } else if ("STOP".equals(action)) {
                stopSelf();
                MusicServiceHelper.setPlaying(false);
            }else if ("PLAYLIST".equals(action)) {
                playlist = (ArrayList<Music>) intent.getSerializableExtra("playlist");
            }else if ("NEXT".equals(action)) {
                mediaPlayer.reset();

                NextSong();
            } else if ("PREVIOUS".equals(action)) {
                if(lsu.size()>0)
                {
                    mediaPlayer.reset();
                    previous();
                }

            }

        }

        return START_STICKY; // Giữ service chạy nền
    }
    private boolean requestFocus() {
        int result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            result = audioManager.requestAudioFocus(focusRequest);
        } else {
            result = audioManager.requestAudioFocus(
                    focusChangeListener,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN
            );
        }

        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }
    private void previous() {
        int vt = lsu.size() - 1;

        if (lsu.isEmpty()) {
            MusicViewModel.ktra.postValue(0);
            return;
        }
        MusicServiceHelper.setCurrentSong(lsu.get(vt));
        playNewSong(lsu.get(vt));
        if (lsu.size()==1) {
            MusicViewModel.ktra.postValue(0);
        } else {

            MusicViewModel.ktra.postValue(1);
        }
        lsu.remove(vt);
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "Music Playback";
            String description = "Channel for music playback controls";
            int importance = android.app.NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            android.app.NotificationManager notificationManager = getSystemService(android.app.NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private void playNewSong(Music ms) {
        try {
            if (!requestFocus()) {
                Log.d("AudioFocus", "Không được cấp Audio Focus");
                return;
            }
           MusicViewModel.setIsPreparing(true);
            MusicServiceHelper.setPhat(true);
            currentUrl = ms.getDuongDan();
            mediaPlayer.reset();
            MusicServiceHelper.setCurrentSong(ms);
            MusicServiceHelper.setPlaying(true);
            mediaPlayer.setDataSource(ms.getDuongDan());

            // 🔹 Gọi startForeground sớm với ảnh mặc định
            Bitmap defaultArt = BitmapFactory.decodeResource(getResources(), R.drawable.spotify);
            Notification initial = showNotification(defaultArt, ms);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startForeground(1, initial, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);
            } else {
                startForeground(1, initial);
            }

            mediaPlayer.setOnPreparedListener(mp -> {
                mediaPlayer.start();
                MusicViewModel.setIsPreparing(false);
                MediaMetadataCompat metadata = new MediaMetadataCompat.Builder()
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, ms.getTenBaiHat())
                        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, ms.getTenNgheSi())
                        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, mp.getDuration()) // <-- DÒNG NÀY QUAN TRỌNG
                        .build();
                mediaSession.setMetadata(metadata);
                // 2. Cập nhật trạng thái và BẮT ĐẦU chạy handler
                updatePlaybackState();
                mHandler.post(mUpdatePlaybackStateRunnable);
                new Thread(() -> {
                    Bitmap albumArt = getBitmapFromURL(ms.getAnh());
                    Notification updated = showNotification(albumArt, ms);
                    NotificationManager nm = getSystemService(NotificationManager.class);
                    nm.notify(1, updated); // 🔹 Cập nhật notification sau
                }).start();
            });

            mediaPlayer.prepareAsync();

        } catch (Exception e) {
            Log.e("MusicService", "Lỗi phát nhạc: " + e.getMessage(), e);
        }
    }
    private Bitmap getBitmapFromURL(String src) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            input.close();
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return BitmapFactory.decodeResource(getResources(), R.drawable.spotify);
        }
    }

    private void saveCurrentSong(String tenBai, String tacGia, String anh, String url) {
        SharedPreferences prefs = getSharedPreferences("music_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("tenBaiHat",tenBai);
        editor.putString("tacGia", tacGia);
        editor.putString("anh",anh);
        editor.putString("url",url);
        editor.putString("phatnhac","yes");
        editor.apply();
    }



    private void abandonFocus() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            audioManager.abandonAudioFocusRequest(focusRequest);
        } else {
            audioManager.abandonAudioFocus(focusChangeListener);
        }
    }
//    @Override
//    public void onDestroy() {
//        if (mediaPlayer != null) {
//            if (mediaPlayer.isPlaying()) {
//                mediaPlayer.stop();
//            }
//            mediaPlayer.release();
//            mediaPlayer = null;
//        }
//        super.onDestroy();
//    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHandler != null && mUpdatePlaybackStateRunnable != null) {
            // 1. DỪNG Handler cập nhật seekbar (Rất quan trọng!)
            mHandler.removeCallbacks(mUpdatePlaybackStateRunnable);
        }

        if (mediaSession != null) {
            // 2. Giải phóng MediaSession
            mediaSession.setActive(false); // Báo không active nữa
            mediaSession.release();
        }

        if (mediaPlayer != null) {
            // 3. Giải phóng Player
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop(); // Nên stop() trước khi release
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }

        // 4. Từ bỏ quyền phát nhạc
        abandonFocus();

        MusicServiceHelper.setPhat(false);
    }
}
