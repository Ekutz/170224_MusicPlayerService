package com.jspark.android.musicplayerservice;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;

import static com.jspark.android.musicplayerservice.App.audio;

public class PlayActivity extends AppCompatActivity {

    List<Music> music;
    ViewPager vp;

    ImageButton btnRew, btnPlay, btnFF;
    int position = 0;
    SeekBar sbar;
    TextView duration, time;

    private static final int PLAY = 0;
    private static final int PAUSE = 1;
    private static final int STOP = 2;

    private static int playStatus = STOP;

    private class Timer extends Thread {

        @Override
        public void run() {
            while (playStatus < STOP) {
                if(audio!=null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                sbar.setProgress(audio.getCurrentPosition());
                                time.setText(convertTime(audio.getCurrentPosition()));
                                if (time.getText().toString().equals(duration.getText().toString())) {
                                    next();
                                }
                            } catch (Exception e) {

                            }
                        }
                    });
                }
                try {
                    Thread.sleep(1000);
                } catch(InterruptedException e) {

                }
            }
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        vp = (ViewPager)findViewById(R.id.viewPager);
        btnRew = (ImageButton)findViewById(R.id.btnRewind);
        btnPlay = (ImageButton)findViewById(R.id.btnPlay);
        btnFF = (ImageButton)findViewById(R.id.btnForwardF);
        sbar = (SeekBar)findViewById(R.id.seekBar);
        duration = (TextView)findViewById(R.id.txtDu);
        time = (TextView)findViewById(R.id.spendTime);

        btnRew.setOnClickListener(clickListener);
        btnPlay.setOnClickListener(clickListener);
        btnFF.setOnClickListener(clickListener);

        music = DataLoader.load(this);

        setViewPager();

        setSeekBar();

        setItem();
    }

    private void setItem() {
        // 특정 페이지 호출
        Intent i = getIntent();
        if(i!=null) {
            Bundle bundle = i.getExtras();
            position = bundle.getInt("position");
            vp.setCurrentItem(position);

            if(position==0) {
                playerInit();
                controllerInit();
                timerInit();
                play();
            }
        }
    }

    private void setViewPager() {
        CustomPagerAdapter adapter = new CustomPagerAdapter(music, PlayActivity.this);

        vp.setAdapter(adapter);

        // ViewPager 페이지가 변하는 것을 감지하는 리스너
        vp.addOnPageChangeListener(pageListener);
        vp.setPageTransformer(false, pageTransformer);
    }

    ViewPager.OnPageChangeListener pageListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            PlayActivity.this.position = position;
            init();
            playerInit();
            controllerInit();
            timerInit();
            playStatus=STOP;
            play();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    ViewPager.PageTransformer pageTransformer = new ViewPager.PageTransformer() {
        @Override
        public void transformPage(View page, float position) {
            float normalizedposition = Math.abs( 1 - Math.abs(position) );

            page.setAlpha(normalizedposition);  //View의 투명도 조절
            page.setScaleX(normalizedposition/2 + 0.5f); //View의 x축 크기조절
            page.setScaleY(normalizedposition/2 + 0.5f); //View의 y축 크기조절
            page.setRotationY(position * 80); //View의 Y축(세로축) 회전 각도
        }
    };

    private void setSeekBar() {
        // SeekBar의 변화를 감지하는 리스너
        sbar.setOnSeekBarChangeListener(sbarListener);
    }

    SeekBar.OnSeekBarChangeListener sbarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            if(audio!=null&&b==true) // b = 사용자가 터치 했는지의 유무
            audio.seekTo(i);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnRewind :
                    preview();
                    break;
                case R.id.btnPlay:
                    play();
                    break;
                case R.id.btnForwardF:
                    next();
                    break;
            }
        }
    };

    private void play() {
        switch(playStatus) {
            case STOP :
                playStop();
                break;
            case PLAY :
                playPlay();
                break;
            case PAUSE :
                playPause();
                break;
        }
    }

    private void playStop() {
        audio.start();
        btnPlay.setImageResource(android.R.drawable.ic_media_pause);
        playStatus=PLAY;
        Thread thread = new Timer();
        thread.start();
    }

    private void playPlay() {
        audio.pause();
        playStatus = PAUSE;
        btnPlay.setImageResource(android.R.drawable.ic_media_play);
    }

    private void playPause() {
        audio.start();
        playStatus = PLAY;
        btnPlay.setImageResource(android.R.drawable.ic_media_pause);
    }

    private void preview() {
        if(position>0)position -= 1;
        vp.setCurrentItem(position);
    }

    private void next() {
        if(position<music.size())position += 1;
        vp.setCurrentItem(position);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(audio!=null) {
            audio.release();
        }
        playStatus = STOP;
    }

    private void init() {
        if(audio!=null) {
            audio.release(); // 뷰페이저로 이동할 경우 미디어 해제하고 실행한다
        }
    }

    private void playerInit() {
        Uri uri = music.get(position).uri;
        audio = MediaPlayer.create(this, uri);
        audio.setLooping(false);
    }

    private void controllerInit() {
        sbar.setMax(audio.getDuration());
    }

    private void timerInit() {
        duration.setText(convertTime(music.get(position).length));
        time.setText("00:00");
    }

    private String convertTime(long time) {
        return String.format("%02d", time / 1000 / 60) + ":" + String.format("%02d", (time / 1000 - ((int) time / 1000 / 60) * 60));
    }


}
