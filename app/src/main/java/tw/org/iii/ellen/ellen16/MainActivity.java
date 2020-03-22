package tw.org.iii.ellen.ellen16;
// Volley裡利用泛型!!!
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {
    private TextView mesg ;
    private ImageView img ;
    private String[] permissions =
            {Manifest.permission.WRITE_EXTERNAL_STORAGE} ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    permissions,
                    123);
        }
        else{
            init() ;
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            init() ;
        }
    }

    private void init(){
        mesg = findViewById(R.id.mesg) ;
        img = findViewById(R.id.img) ;
    }

    public void test1(View view){
        mesg.setText("");
        StringRequest request = new StringRequest(
                Request.Method.GET,
                "https://www.iii.org.tw",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("ellen",response) ;
                        mesg.setText(response) ;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) ;

        MainApp .queue.add(request) ;
    }

    public void test2(View view){
        //JSON資料
        StringRequest request = new StringRequest(
                Request.Method.GET,
                "https://data.coa.gov.tw/Service/OpenData/ODwsv/ODwsvTravelFood.aspx",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //mesg.setText(response) ;
                        parseJSON(response) ;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) ;

        MainApp .queue.add(request) ;
    }

    private void parseJSON(String json) {
        mesg.setText("");
        try {
            JSONArray root = new JSONArray(json);

            for(int i = 0; i < root.length(); i++){
                JSONObject row = root.getJSONObject(i) ;
                mesg.append(row.getString("Name") +":"+ row.getString("Address")+"\n");
            }
        }catch (Exception e){
            Log.v("ellen",e.toString()) ;
        }
    }

    public void test3(View view){
        ImageRequest request = new ImageRequest(
                "https://www.fourpaws.com/-/media/images/fourpaws-na/us/articles/dog-playtime-927x388.jpg",
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        img.setImageBitmap(response) ;
                    }
                },
                0, 0,
                Bitmap.Config.ARGB_8888,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //可null
                    }
                }
        ) ;

        MainApp.queue.add(request) ;
    }

    public void test4(View view){
        JsonArrayRequest request = new JsonArrayRequest(
                "https://data.coa.gov.tw/Service/OpenData/ODwsv/ODwsvTravelFood.aspx",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //直接拿到JSONArray
                        parseJSON2(response) ;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) ;
        MainApp.queue.add(request) ;
    }

    private void parseJSON2(JSONArray root){
        mesg.setText("");
        try {
            for(int i = 0; i < root.length(); i++){
                JSONObject row = root.getJSONObject(i) ;
                mesg.append(row.getString("Name") +":"+ row.getString("Address")+"\n");
            }
        }catch (Exception e){
            Log.v("ellen",e.toString()) ;
        }
    }

    public void test5(View view){
        InputStreamRequest request = new InputStreamRequest(
                Request.Method.GET,
                "https://pdfmyurl.com/?url=https://shopping.pchome.com.tw/",
                null,
                new Response.Listener<byte[]>() {
                    @Override
                    public void onResponse(byte[] response) {
                        savePDF(response) ;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("ellen",error.toString()) ;
                    }
                }

        );
        request.setRetryPolicy(new DefaultRetryPolicy(
                20*1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )) ;

        MainApp.queue.add(request) ;

    }

    private void savePDF(byte[] data){
        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) ;
        File saveFile = new File(downloadDir, "pchome.pdf") ;
        try {
            BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(saveFile)) ;
            bout.write(data) ;
            bout.flush() ;
            bout.close() ;
            Toast.makeText(this,"save OK",Toast.LENGTH_SHORT).show() ;
        }catch (Exception e){
            Log.v("ellen",e.toString()) ;
        }

    }
}
