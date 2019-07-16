package com.example.myappvideo2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ScreenshotFragment extends Fragment {

    Button pictureBtn;
    ImageView myImg;
    String ip="";
    String log="";
    String pas="";

    public ScreenshotFragment(String i, String u, String p) {
        ip=i;
        log=u;
        pas=p;
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
        View v=inflater.inflate(R.layout.fragment_screenshot, container, false);
        pictureBtn = (Button) v.findViewById(R.id.picture);
        myImg=(ImageView)v.findViewById(R.id.myImg);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pictureBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new ScreenshotFragment.DownloadImageTask(myImg).execute("http://"+ip+"/axis-cgi/bitmap/image.bmp");
            }
        });
    }

    class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView Image;

        public DownloadImageTask(ImageView Image) {
            this.Image = Image;
        }

        protected Bitmap doInBackground(String... urls) {
            String urlString = urls[0];
            Bitmap img = null;
            HttpURLConnection conn=null;
            try {
                URL url = new URL(urlString);
                conn = (HttpURLConnection) url.openConnection();
                if(!log.equalsIgnoreCase(""))
                    conn.setRequestProperty("Authorization", "Basic " + Base64.encodeToString((log+":"+pas).getBytes(), Base64.NO_WRAP));
                InputStream in = conn.getInputStream();
                img = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }
            return img;
        }

        protected void onPostExecute(Bitmap result) {
            Image.setImageBitmap(result);
        }
    }
}
