package com.example.myappvideo2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ScreenshotFragment extends Fragment {

    private Button pictureBtn;
    private Button sendPictureBtn;
    private Bitmap myBitmap;
    private ImageView myImg;
    private ImageView serverImg;
    private TextView tvServer;
    private String ip="";
    private String log="";
    private String pas="";

    private ClientConnectClass serverClass = new ClientConnectClass();
    private Socket mSocket;
    private BufferedReader input;

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

    @Override
    public void onResume() {
        super.onResume();
        myImg.setImageBitmap(myBitmap);
    }

    @Override
    public void onPause() {
        super.onPause();
        myBitmap = ((BitmapDrawable)myImg.getDrawable()).getBitmap();
        serverClass.closeConnection();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v=inflater.inflate(R.layout.fragment_screenshot, container, false);
        pictureBtn = (Button) v.findViewById(R.id.picture);
        myImg=(ImageView)v.findViewById(R.id.myImg);

        sendPictureBtn=(Button)v.findViewById(R.id.sendPicture);
        serverImg=(ImageView)v.findViewById(R.id.serverImg);
        tvServer=(TextView)v.findViewById(R.id.tvServer);
        tvServer.setVisibility(View.INVISIBLE);

        if(myBitmap==null)
            sendPictureBtn.setVisibility(View.INVISIBLE);
        else
            sendPictureBtn.setVisibility(View.VISIBLE);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pictureBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new ScreenshotFragment.DownloadImageTask(myImg).execute("http://"+ip+"/axis-cgi/bitmap/image.bmp?resolution=800x600");
            }
        });
        sendPictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String picture64=getBase64ImageString(myBitmap);
                if (!picture64.equalsIgnoreCase("")){
                    String test = picture64+"| Тест отправки картинки";
                    final byte[] data = test.getBytes();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                serverClass = new ClientConnectClass();
                                serverClass.openConnection();
                                mSocket=serverClass.mySocket;
                                serverClass.sendData(data);
                                //Socket socket = mSocket;
                                input = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try{
                                        String line;
                                        StringBuilder map = new StringBuilder();
                                        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
                                        String date = " "+df.format(Calendar.getInstance().getTime())+" ";
                                        try {
                                            while ((line = input.readLine()) != null){
                                                ///УСЛОВИЕ ДЛЯ ОСТАНОВКИ ЧТЕНИЯ СТРОК ИЗ СОКЕТА (ЭТО из даты, которая добавляется в конце для ответа
                                                if(line.contains(date)){
                                                    int index = line.indexOf(' ');
                                                    String buf = line.substring(0,index);
                                                    map.append(buf);
                                                    break;
                                                }else{
                                                    map.append(line);
                                                }
                                            }
                                        } catch (SocketException e) {
                                            e.printStackTrace();
                                        }
                                        String message = map.toString();
                                        int leftindex = message.lastIndexOf(' ');
                                        message =  message.substring(leftindex+1);
                                        byte[] decodeBytes = Base64.decode(message, Base64.DEFAULT | Base64.NO_WRAP);

                                        final Bitmap res = BitmapFactory.decodeByteArray(decodeBytes, 0, decodeBytes.length);
                                        if(message!=null){
                                            serverImg.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    serverImg.setImageBitmap(res);
                                                    if(res!=null) {
                                                        Toast.makeText(getContext(), "Фото с сервера получено", Toast.LENGTH_SHORT).show();
                                                        tvServer.setVisibility(View.VISIBLE);
                                                    }else {
                                                        Toast.makeText(getContext(), "Фото с сервера не устанавливается. Попробуйте еще раз", Toast.LENGTH_SHORT).show();
                                                        tvServer.setVisibility(View.INVISIBLE);
                                                    }
                                                }
                                            });
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                            serverClass.closeConnection();
                                    }
                                }).start();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
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
            if(result==null) {
                Toast.makeText(getContext(), "Изображение не получено. Проверьте логин, пароль и IP-адрес камеры", Toast.LENGTH_LONG).show();
                sendPictureBtn.setVisibility(View.INVISIBLE);
            }else {
                Image.setImageBitmap(result);
                myBitmap=result;
                sendPictureBtn.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Изображение получено", Toast.LENGTH_LONG).show();
            }
        }
    }
    public String getBase64ImageString(Bitmap photo) {
        String imgString="";
        if(photo != null) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 25, outputStream);
            byte[] profileImage = outputStream.toByteArray();
            imgString = Base64.encodeToString(profileImage, Base64.DEFAULT);
        }else{
            Toast.makeText(getContext(),"Сделайте сначала снимок",Toast.LENGTH_SHORT).show();
        }
        return imgString;
    }
}
