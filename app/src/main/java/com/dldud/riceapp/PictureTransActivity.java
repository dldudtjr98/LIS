package com.dldud.riceapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import static com.dldud.riceapp.CameraFragment.picturefilename;
import static com.dldud.riceapp.UserProfileSettingActivity.userId;

public class PictureTransActivity extends AppCompatActivity {

    int serverResponseCode = 125;
    String upLoadServerUri = null;
    final String insertUrlPath = "http://52.78.18.156/public/Data_insert.php";
    final String urlPath = "http://52.78.18.156/public/ping_db.php";
    final String uploadFilePath = "storage/emulated/0/LIS/";
    final String uploadFileName = picturefilename + ".jpg";
    String filter;
    RadioGroup rg;
    String picContent;
    EditText setText;
    MapView mapViewFix;
    CheckBox picCheck;
    String UserId;

    ProgressDialog dialog = null;

    double latitude, longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_trans);

        NetworkUtil.setNetworkPolicy();

        setText = (EditText)findViewById(R.id.picContent);
        picCheck = (CheckBox)findViewById(R.id.picAnonCheck);

        rg = (RadioGroup)findViewById(R.id.filterPicGroup);

        Button cancel = (Button) findViewById(R.id.picCancelBtn);
        cancel.setOnClickListener(cancelTrans);

        Button ok = (Button)findViewById(R.id.picSendBtn);
        ok.setOnClickListener(sendTrans);

        mapViewFix = new MapView(this);
        final ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.picMapFix);
        mapViewContainer.addView(mapViewFix);

        upLoadServerUri = "http://52.78.18.156/public/UploadToServer.php";

        GPSInfo gps = new GPSInfo(PictureTransActivity.this);
        //GPS 사용유무
        if(gps.isGetLocation()){
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        } else {
            gps.showSettingsAlert();
        }

        mapViewFix.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(latitude, longitude),1, true);

    }


    View.OnClickListener cancelTrans = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            onBackPressed();
            finish();
        }
    };

    View.OnClickListener sendTrans = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            MapPoint.GeoCoordinate mapPointGeo = mapViewFix.getMapCenterPoint().getMapPointGeoCoord();
            latitude = mapPointGeo.latitude;
            longitude = mapPointGeo.longitude;

            int id = rg.getCheckedRadioButtonId();
            RadioButton rb = (RadioButton)findViewById(id);
            filter = rb.getResources().getResourceEntryName(rb.getId());

            picContent = setText.getText().toString();

            dialog = ProgressDialog.show(PictureTransActivity.this,"","파일을 업로드 중입니다...",true);

            new Thread(new Runnable(){
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                    uploadFile(uploadFilePath + "" + uploadFileName);
                }
            }).start();

            try {
                Task task = new Task();
                String locationLong= Double.toString(longitude);
                String locationLat = Double.toString(latitude);
                String JSONString = task.execute(urlPath).get();
                task.jsonParser(JSONString);
                if(picCheck.isChecked()) {
                    UserId = "0";
                } else {
                    UserId = userId;
                }

                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
                String formatDate = sdfNow.format(date);

                PHPRequest request = new PHPRequest(insertUrlPath);
                String result = request.PhPtest(UserId,filter ,uploadFileName, uploadFileName, picContent, locationLat, locationLong, formatDate);
                if(result.equals("1")){
                    Toast.makeText(getApplication(),"전송완료",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplication(),"전송불가",Toast.LENGTH_SHORT).show();
                }

                mapViewFix.setVisibility(View.GONE);

            }catch(InterruptedException e){
                e.printStackTrace();
            }catch(ExecutionException e){
                e.printStackTrace();
            }catch(MalformedURLException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(PictureTransActivity.this, MainActivity.class);
            intent.putExtra("fragmentNumber",1);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    };


    public int uploadFile(String sourceFileUri){

        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {
            dialog.dismiss();
            Log.e("uploadFile", "Source File not exist :"
                    +uploadFilePath + "" + uploadFileName);

            runOnUiThread(new Runnable() {
                public void run() {
                }
            });
            return 0;
        } else {
            try {
                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);
                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if (serverResponseCode == 200) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
                                    + uploadFileName;

                            Toast.makeText(PictureTransActivity.this, "File Upload Complete.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                //close the streams //

                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {
                dialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {

                        Toast.makeText(PictureTransActivity.this, "MalformedURLException",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {
                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {

                        Toast.makeText(PictureTransActivity.this, "Got Exception : see logcat ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Exception", "Exception : "
                        + e.getMessage(), e);

            }
            dialog.dismiss();
            return serverResponseCode;
        }
    }
    @Override
    protected void onDestroy() {
        Log.d("dialog", "called onDestroy");

        super.onDestroy();
        if(dialog != null) {
            dialog.dismiss();
        }
    }
}
