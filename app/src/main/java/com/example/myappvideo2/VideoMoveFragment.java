package com.example.myappvideo2;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Layout;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class VideoMoveFragment extends Fragment {

    private VideoView myVideo;
    private ProgressBar videoProgressBar;
    private int horProgress;
    private int vertProgress;
    private Button upBtn;
    private Button leftBtn;
    private Button rightBtn;
    private Button downBtn;
    private RelativeLayout l2;
    private LinearLayout l3;
    private LinearLayout l4;
    private MediaController mc;
    private CheckBox checkMoveAccelerometer;
    private String ip;
    private String log;
    private String pas;
    private Boolean PTZ;

    private Accelerometer accelerometer;

    private SeekBar seekZoom;
    private Button zoomLeftBtn;
    private Button zoomRightBtn;

    private TextView tvFocus;
    private SeekBar seekFocus;
    private Button focusLeftBtn;
    private Button focusRightBtn;

    private TextView tvIris;
    private SeekBar seekIris;
    private Button irisLeftBtn;
    private Button irisRightBtn;

    private SeekBar.OnSeekBarChangeListener sbListenerZFI;

    private SeekBar seekPan;
    private SeekBar seekTilt;
    private int vertNow=7;
    private int horNow=16;
    private String URL;

    public VideoMoveFragment(String i, String u, String p, Boolean ptz) {
        ip=i;
        log=u;
        pas=p;
        PTZ=ptz;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v=inflater.inflate(R.layout.fragment_video_move, container, false);
        myVideo = (VideoView) v.findViewById(R.id.myVideo);
        videoProgressBar=(ProgressBar)v.findViewById((R.id.video_progressbar));
        upBtn = (Button) v.findViewById(R.id.up);
        leftBtn = (Button) v.findViewById(R.id.left);
        rightBtn = (Button) v.findViewById(R.id.right);
        downBtn = (Button) v.findViewById(R.id.down);
        l2=(RelativeLayout) v.findViewById(R.id.layuot2);
        l3=(LinearLayout) v.findViewById(R.id.layuot3);
        l4=(LinearLayout) v.findViewById(R.id.layuot4);
        checkMoveAccelerometer=(CheckBox) v.findViewById(R.id.checkAcc);

        seekZoom=(SeekBar)v.findViewById(R.id.seekHorZoom);
        zoomLeftBtn = (Button) v.findViewById(R.id.leftZoom);
        zoomRightBtn = (Button) v.findViewById(R.id.rightZoom);

        tvFocus=(TextView)v.findViewById(R.id.txFocus);
        seekFocus=(SeekBar)v.findViewById(R.id.seekHorFocus);
        focusLeftBtn = (Button) v.findViewById(R.id.leftFocus);
        focusRightBtn = (Button) v.findViewById(R.id.rightFocus);

        tvIris=(TextView)v.findViewById(R.id.txIris);
        seekIris=(SeekBar)v.findViewById(R.id.seekHorIris);
        irisLeftBtn = (Button) v.findViewById(R.id.leftIris);
        irisRightBtn = (Button) v.findViewById(R.id.rightIris);

        seekPan=(SeekBar)v.findViewById(R.id.seekHorMove);
        seekTilt=(SeekBar)v.findViewById(R.id.seekVertMove);

        if(log.equalsIgnoreCase("")&&pas.equalsIgnoreCase(""))
            URL=ip;
        else
            URL=log+":"+pas+"@"+ip;

        if(PTZ){
            tvFocus.setVisibility(View.VISIBLE);
            seekFocus.setVisibility(View.VISIBLE);
            focusLeftBtn.setVisibility(View.VISIBLE);
            focusRightBtn.setVisibility(View.VISIBLE);

            tvIris.setVisibility(View.VISIBLE);
            seekIris.setVisibility(View.VISIBLE);
            irisLeftBtn.setVisibility(View.VISIBLE);
            irisRightBtn.setVisibility(View.VISIBLE);

            if(savedInstanceState==null){
                seekPan.setProgress(29/2);
                seekPan.setMax(29);
                seekTilt.setProgress(5);
                seekTilt.setMax(10);
                vertProgress=10;
                horProgress=29;
				vertNow=5;
				horNow=29/2;
            }
        }else{
            tvFocus.setVisibility(View.INVISIBLE);
            seekFocus.setVisibility(View.INVISIBLE);
            focusLeftBtn.setVisibility(View.INVISIBLE);
            focusRightBtn.setVisibility(View.INVISIBLE);

            tvIris.setVisibility(View.INVISIBLE);
            seekIris.setVisibility(View.INVISIBLE);
            irisLeftBtn.setVisibility(View.INVISIBLE);
            irisRightBtn.setVisibility(View.INVISIBLE);

            if(savedInstanceState==null){
                seekPan.setProgress(0);
                seekPan.setMax(0);
                seekTilt.setProgress(0);
                seekTilt.setMax(0);
                vertProgress=0;
                horProgress=0;
				vertNow=0;
				horNow=0;
            }
        }



        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        upBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rightChange(seekTilt,1);
            }
        });
        downBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leftChange(seekTilt,1);
            }
        });
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leftChange(seekPan,1);
            }
        });
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rightChange(seekPan,1);
            }
        });
        seekPan.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if(!changeMove) {
                    int change = progress - horNow;
                    if (change > 0)
                        for (int i = 0; i < change; i++)
                            new SendTask().execute("http://" + ip + "/axis-cgi/com/ptz.cgi?move=right");
                    else if (change < 0)
                        for (int i = change; i < 0; i++)
                            new SendTask().execute("http://" + ip + "/axis-cgi/com/ptz.cgi?move=left");
                    horNow = progress;
                }
                changeMove=false;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekTilt.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if(!changeMove) {
                    int change = progress - vertNow;
                    if (change > 0)
                        for (int i = 0; i < change; i++)
                            new SendTask().execute("http://" + ip + "/axis-cgi/com/ptz.cgi?move=up");
                    else if (change < 0)
                        for (int i = change; i < 0; i++)
                            new SendTask().execute("http://" + ip + "/axis-cgi/com/ptz.cgi?move=down");
                    vertNow = progress;
                }
                changeMove=false;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        checkMoveAccelerometer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    l3.setVisibility(View.INVISIBLE);
                    l4.setVisibility(View.INVISIBLE);
                    LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(getActivity().getWindow().getDecorView().getMeasuredWidth(), LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.gravity = Gravity.CENTER;
                    l2.setLayoutParams(lp);
                    accelerometer=new Accelerometer(getContext());
                    accelerometer.setListener(new Accelerometer.Listener() {
                        @Override
                        public void onTranslation(float tx, float ty, float tz) {
                            if (tx > 1.0f) {
                                leftChange(seekPan,1);
                            } else if (tx < -1.0f) {
                                rightChange(seekPan,1);
                            }

                            if (ty > 1.0f) {
                                leftChange(seekTilt,1);
                            } else if (ty < -1.0f) {
                                rightChange(seekTilt,1);
                            }
                        }
                    });
                    accelerometer.register();
                }else{
                    l3.setVisibility(View.VISIBLE);
                    l4.setVisibility(View.VISIBLE);
                    LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(getActivity().getWindow().getDecorView().getMeasuredWidth()-40, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.gravity = Gravity.CENTER;
                    l2.setLayoutParams(lp);
                    accelerometer.unregister();
                }
            }
        });
        zoomLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leftChange(seekZoom, 5);
            }
        });
        zoomRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rightChange(seekZoom, 5);
            }
        });
        seekZoom.setOnSeekBarChangeListener(setZFIListener("http://"+ip+"/axis-cgi/com/ptz.cgi?zoom="));
        focusLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leftChange(seekFocus, 5);
            }
        });
        focusRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rightChange(seekFocus, 5);
            }
        });
        seekFocus.setOnSeekBarChangeListener(setZFIListener("http://"+ip+"/axis-cgi/com/ptz.cgi?focus="));
        irisLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leftChange(seekIris, 5);
            }
        });
        irisRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rightChange(seekIris,5);
            }
        });
        seekIris.setOnSeekBarChangeListener(setZFIListener("http://"+ip+"/axis-cgi/com/ptz.cgi?iris="));
    }
    @Override
    public void onResume(){
        super.onResume();
        mc = new MediaController(getContext());
        myVideo.setMediaController(mc);
        if(PTZ)
            myVideo.setVideoURI(Uri.parse("rtsp://"+URL+"/mpeg4/media.amp"));
        else
            myVideo.setVideoURI(Uri.parse("rtsp://"+URL+"/axis-media/media.amp"));

        myVideo.requestFocus();
        myVideo.start();
        videoProgressBar.setVisibility(View.VISIBLE);
        myVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i1) {
                        videoProgressBar.setVisibility(View.GONE);
                        mediaPlayer.start();
                    }
                });
            }
        });


        if(checkMoveAccelerometer.isChecked())
            accelerometer.register();
    }

    @Override
    public void onPause(){
        super.onPause();
        if(checkMoveAccelerometer.isChecked())
            accelerometer.unregister();
    }

    private void leftChange(SeekBar sb, int kol){
        int change=sb.getProgress()-kol;
        if(change<0)
            change=0;
        sb.setProgress(change);
    }
    private void rightChange(SeekBar sb, int kol){
        int change=sb.getProgress()+kol;
        if(change>sb.getMax())
            change=sb.getMax();
        sb.setProgress(change);
    }

    private SeekBar.OnSeekBarChangeListener setZFIListener(final String s){
         sbListenerZFI = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                int change;
                if(progress==0)
                    change=1;
                else
                    change=progress*200-1;
                new SendTask().execute(s+change);
                if(seekBar.getId()==R.id.seekHorZoom)
                    changeMoveSeekBar(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
         return sbListenerZFI;
    }

    boolean changeMove=false;
    private void changeMoveSeekBar(int progress) {
        int pr;
        if(PTZ){
            changeMove=true;
            seekPan.setMax(29+425*progress*2/100);
            changeMove=true;
            seekTilt.setMax(10+150*progress*2/100);
            if(horProgress!=0)
                pr=seekPan.getMax()*seekPan.getProgress()/horProgress;
            else
                pr=1;
            changeMove=true;
            seekPan.setProgress(pr);
            if(vertProgress!=0)
                pr=seekTilt.getMax()*seekTilt.getProgress()/vertProgress;
            else
                pr=1;
            changeMove=true;
            seekTilt.setProgress(pr);
        }else{
            changeMove=true;
            if(progress<=10){
                seekPan.setMax((int)(progress*0.34+1));
                changeMove=true;
                seekTilt.setMax((int)(progress*0.34+1));
            }else if(progress<=15){
                seekPan.setMax((int)(progress*0.54));
                changeMove=true;
                seekTilt.setMax((int)(progress*0.4));
            }else if(progress<=30){
                seekPan.setMax((int)(progress*0.6));
                changeMove=true;
                seekTilt.setMax((int)(progress*0.8));
            }else if(progress<=35){
                seekPan.setMax((int)(progress*0.8));
                changeMove=true;
                seekTilt.setMax((int)(progress*0.95));
            }else if(progress<=40){
                seekPan.setMax((int)(progress*1));
                changeMove=true;
                seekTilt.setMax((int)(progress*1.5));
            }else if(progress<=45){
                seekPan.setMax((int)(progress*2.5));
                changeMove=true;
                seekTilt.setMax((int)(progress*4));
            }else if(progress<=50){
                seekPan.setMax((int)(progress*3.5));
                changeMove=true;
                seekTilt.setMax((int)(progress*6));
            }
            if(horProgress!=0)
                pr=seekPan.getMax()*seekPan.getProgress()/horProgress;
            else
                pr=0;
            changeMove=true;
            seekPan.setProgress(pr);
            if(vertProgress!=0)
                pr=seekTilt.getMax()*seekTilt.getProgress()/vertProgress;
            else
                pr=0;
            changeMove=true;
            seekTilt.setProgress(pr);
        }
        horProgress=seekPan.getMax();
        vertProgress=seekTilt.getMax();
        changeMove=false;
    }


    class SendTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... uri) {
            String urlString=uri[0];
            String responseString;
            HttpURLConnection conn=null;
            try {
                URL url = new URL(urlString);
                conn = (HttpURLConnection) url.openConnection();
                if(!log.equalsIgnoreCase("")) {
                    conn.setRequestProperty("Authorization", "Basic " + Base64.encodeToString((log + ":" + pas).getBytes(), Base64.NO_WRAP));
                }
                int code=conn.getResponseCode();
                if( code>=200 && code<400){
                    responseString = "OK";
                }
                else {
                    responseString = "FAILED";
                }
            } catch (IOException e) {
                responseString = "ERROR";
            } finally {
                conn.disconnect();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equalsIgnoreCase("ERROR"))
                Toast.makeText(getContext(), "Ошибка при соединении. Проверьте параметры камеры", Toast.LENGTH_SHORT).show();
            else if(result.equalsIgnoreCase("FAILED"))
                Toast.makeText(getContext(), "Ошибка при соединении. Проверьте параметры сети", Toast.LENGTH_SHORT).show();
        }
    }
}
