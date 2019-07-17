package com.example.myappvideo2;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class VideoMoveFragment extends Fragment {

    VideoView myVideo;
    Button upBtn;
    Button leftBtn;
    Button rightBtn;
    Button downBtn;
    MediaController mc;
    CheckBox checkMoveAccelerometer;
    String ip="";
    String log="";
    String pas="";
    Boolean PTZ;

    Accelerometer accelerometer;

    SeekBar seekZoom;
    Button zoomLeftBtn;
    Button zoomRightBtn;

    SeekBar seekFocus;
    Button focusLeftBtn;
    Button focusRightBtn;

    SeekBar seekIris;
    Button irisLeftBtn;
    Button irisRightBtn;

    SeekBar.OnSeekBarChangeListener sbListenerZFI;

    SeekBar seekPan;
    SeekBar seekTilt;
    int vertNow=50;
    int horNow=50;

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
        upBtn = (Button) v.findViewById(R.id.up);
        leftBtn = (Button) v.findViewById(R.id.left);
        rightBtn = (Button) v.findViewById(R.id.right);
        downBtn = (Button) v.findViewById(R.id.down);
        checkMoveAccelerometer=(CheckBox) v.findViewById(R.id.checkAcc);

        seekZoom=(SeekBar)v.findViewById(R.id.seekHorZoom);
        zoomLeftBtn = (Button) v.findViewById(R.id.leftZoom);
        zoomRightBtn = (Button) v.findViewById(R.id.rightZoom);

        seekFocus=(SeekBar)v.findViewById(R.id.seekHorFocus);
        focusLeftBtn = (Button) v.findViewById(R.id.leftFocus);
        focusRightBtn = (Button) v.findViewById(R.id.rightFocus);

        seekIris=(SeekBar)v.findViewById(R.id.seekHorIris);
        irisLeftBtn = (Button) v.findViewById(R.id.leftIris);
        irisRightBtn = (Button) v.findViewById(R.id.rightIris);

        seekPan=(SeekBar)v.findViewById(R.id.seekHorMove);
        seekTilt=(SeekBar)v.findViewById(R.id.seekVertMove);

        String URL=null;
        if(log.equalsIgnoreCase("")&&pas.equalsIgnoreCase(""))
            URL=ip;
        else
            URL=log+":"+pas+"@"+ip;
        mc = new MediaController(getContext());
        if(PTZ)
            myVideo.setVideoURI(Uri.parse("rtsp://"+URL+"/mpeg4/media.amp"));
        else
            myVideo.setVideoURI(Uri.parse("rtsp://"+URL+"/axis-media/media.amp"));
        myVideo.setMediaController(mc);
        myVideo.requestFocus();
        myVideo.start();

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
                int change=progress-vertNow;
                if(change>0)
                    for(int i=0; i<change; i++)
                        new SendTask().execute("http://"+ip+"/axis-cgi/com/ptz.cgi?move=right");
                else if(change<0)
                    for(int i=change; i<0; i++)
                        new SendTask().execute("http://"+ip+"/axis-cgi/com/ptz.cgi?move=left");
                vertNow=progress;
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
                int change=progress-horNow;
                if(change>0)
                    for(int i=0; i<change; i++)
                        new SendTask().execute("http://"+ip+"/axis-cgi/com/ptz.cgi?move=up");
                else if(change<0)
                    for(int i=change; i<0; i++)
                        new SendTask().execute("http://"+ip+"/axis-cgi/com/ptz.cgi?move=down");
                horNow=progress;
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
                    upBtn.setVisibility(View.INVISIBLE);
                    leftBtn.setVisibility(View.INVISIBLE);
                    rightBtn.setVisibility(View.INVISIBLE);
                    downBtn.setVisibility(View.INVISIBLE);
                    seekPan.setEnabled(false);
                    seekTilt.setEnabled(false);
                    accelerometer=new Accelerometer(getContext());
                    accelerometer.setListener(new Accelerometer.Listener() {
                        @Override
                        public void onTranslation(float tx, float ty, float tz) {
                            if (tx > 1.0f) {
                                new SendTask().execute("http://"+ip+"/axis-cgi/com/ptz.cgi?move=left");
                                leftChange(seekPan,1);
                            } else if (tx < -1.0f) {
                                new SendTask().execute("http://"+ip+"/axis-cgi/com/ptz.cgi?move=right");
                                rightChange(seekPan,1);
                            }

                            if (ty > 1.0f) {
                                new SendTask().execute("http://"+ip+"/axis-cgi/com/ptz.cgi?move=down");
                                leftChange(seekTilt,1);
                            } else if (ty < -1.0f) {
                                new SendTask().execute("http://"+ip+"/axis-cgi/com/ptz.cgi?move=up");
                                rightChange(seekTilt,1);
                            }
                        }
                    });
                    accelerometer.register();
                }else{
                    upBtn.setVisibility(View.VISIBLE);
                    leftBtn.setVisibility(View.VISIBLE);
                    rightBtn.setVisibility(View.VISIBLE);
                    downBtn.setVisibility(View.VISIBLE);
                    seekPan.setEnabled(true);
                    seekTilt.setEnabled(true);
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



    class SendTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... uri) {
            String urlString=uri[0];
            String responseString="OK";
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
