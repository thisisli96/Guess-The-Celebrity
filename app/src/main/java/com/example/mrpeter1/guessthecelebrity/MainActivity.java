package com.example.mrpeter1.guessthecelebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebURLs = new ArrayList<String>();
    ArrayList<String> celebNames = new ArrayList<String>();
    int chosenCeleb = 0 ;
    ImageView imageView;

    String[] answers = new  String[4]; // step 8 // apa gunanya ?
    int locationOfCorrectAnswer = 0;

    Button button0, button1, button2, button3;

    public  void celebChosen (View view){ // step 9
        if (view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer))){
            Toast.makeText(getApplicationContext(),"Correct!",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(),"Wrong it was "+ celebNames.get(chosenCeleb),Toast.LENGTH_SHORT).show();

        }
    }

    public class imageDownloader extends AsyncTask<String, Void, Bitmap>{ // step 5


        @Override
        protected Bitmap doInBackground(String... urls) {
           try {
               URL url = new URL(urls[0]);
               HttpURLConnection connection =(HttpURLConnection) url.openConnection();
               connection.connect();
               InputStream inputStem = connection.getInputStream();
               Bitmap myBitmap = BitmapFactory.decodeStream(inputStem);
               return myBitmap;

           }catch (Exception e){
               e.printStackTrace();
               return  null;
           }
        }
    }

    public  class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try {

                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1 )
                {
                    char current = (char) data;
                    result += current;
                    data = reader.read();

                }
                 return  result;
            } catch (Exception e) {
                e.printStackTrace();
                return "failed";
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);

        DownloadTask task = new DownloadTask();
        String result = null;

        try{ // STEP DUA
            result = task.execute("http://www.posh24.se/kandisar").get();
            Log.i("Contents of URL", result);
            String[] splitResult = result.split("<div class=\"listedArticles\">");  //bisa
         // String[] splitResult = result.split("<div class=\"channelListEntry\">");

            Pattern p = Pattern.compile("img src=\"(.*?)\""); // untuk dapat namanya saja dari inspect elemen http://www.posh24.se/kandisar
            Matcher m = p.matcher(splitResult[0]);
            while (m.find()) {
                celebURLs.add(m.group(1)); // step 3
               //System.out.println(m.group(1));

            }

                p = Pattern.compile("alt=\"(.*?)\"");  // untuk dapat namanya saja dari inspect elemen http://www.posh24.se/kandisar
          m = p.matcher(splitResult[0]);
            while (m.find()) {
                celebNames.add(m.group(1)); // step 3
                // System.out.println(m.group(1));
            }

            Random rand = new Random();
            chosenCeleb = rand.nextInt(celebURLs.size()); // step 6

            imageDownloader imageTask = new imageDownloader();

            Bitmap celebImage = imageTask.execute(celebURLs.get(chosenCeleb)).get(); // step 6 dan mengambil atau mengubah jadi file gambar sesuai dengan chosenceleb

            imageView.setImageBitmap(celebImage);// menampilkan di xml gambar yang di dapatkan oleh celeb image

            locationOfCorrectAnswer = rand.nextInt(4); // step 8 mengset 4 kemungkinan jawaban
            // dibawahnya membuat variabel untuk lokasi jawaban yang  tidak benar
            int incorrectAnswerLocation;
            for (int i=0; i < 4 ; i++){ // step 8 perintah untuk mencocokan jawaban yang benar atau salah

                if (i == locationOfCorrectAnswer){
                    answers[i] = celebNames.get(chosenCeleb);
                } else {

                    incorrectAnswerLocation = rand.nextInt(celebURLs.size());

                    while (incorrectAnswerLocation == chosenCeleb){
                        incorrectAnswerLocation = rand.nextInt(celebURLs.size());
                    }

                    answers[i] =celebNames.get(incorrectAnswerLocation);
                }
            }

            button0.setText(answers[0]); //step 7
            button1.setText(answers[1]);
            button2.setText(answers[2]);
            button3.setText(answers[3]);


        }catch (Exception e){
            e.printStackTrace();
        }



    }
}


//step pertama download script sebuah web ke android
// step kedua mengambil nama dan gambar pada script tsb
// step ketiga memasukkan data nama dan image ke dalam array
// step 4 mengacak array nama image untuk di tampilkan
// step 5 mendownload image
// step 6 memilih secara random url yang telah di download
// step 7 menampilkan nama di button untuk dijadika pilahan ganda
// step 8 mengset salah satu dari button ada jawaban yg benar
// step 9 set onclick selebchosen settelah itu add tag ke button untuk membandingkan jawaban yang di pilih

