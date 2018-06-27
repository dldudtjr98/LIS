package com.dldud.riceapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.kakao.usermgmt.response.model.UserProfile;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class UserProfileSettingActivity extends BaseActivity {


    private ImageView setImage;
    private EditText name;
    static String userId;

    private ProgressDialog dialog = null;
    final int PICK_FROM_ALBUM = 123;
    int serverResponseCode = 124;
    final String insertUrlPath = "http://52.78.18.156/public/UserData_Insert.php";
    private String uploadFilePath;
    private String uploadFileName;

    private String folderPath;

    final String urlPath = "http://52.78.18.156/public/user_db.php";
    private String picturefilename;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_setting);

        folderPath = Environment.getExternalStorageDirectory().getAbsolutePath();

        createFolder();

        NetworkUtil.setNetworkPolicy();
        Intent intent = getIntent();
        userId = intent.getStringExtra("userProfile");
        Button setFin = (Button)findViewById(R.id.setFinBtn);
        setImage = (ImageView)findViewById(R.id.imageSet);
        name = (EditText)findViewById(R.id.Name);

        setImage.setOnClickListener(setting);
        setFin.setOnClickListener(finish);

        try {
            TaskUser taskUser = new TaskUser();
            String JSONString = taskUser.execute(urlPath).get();
            taskUser.jsonParser(JSONString);

            String[] link_id = taskUser.link_id.toArray(new String[taskUser.link_id.size()]);
            for(int i=0;i<link_id.length;i++){
                String val = link_id[i];
                if(val.contains(userId)){
                    redirectMainActivity();
                    finish();
                }
            }
        }catch(InterruptedException e){
            e.printStackTrace();
        }catch(ExecutionException e){
            e.printStackTrace();
        }


    }

    View.OnClickListener setting = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            doTakeAlbumAction();
        }
    };
    View.OnClickListener finish = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String nickNameValue;
            nickNameValue = name.getText().toString();
            dialog = ProgressDialog.show(UserProfileSettingActivity.this,"","유저 등록 중입니다...",true);

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
                PHPRequestLogIn request = new PHPRequestLogIn(insertUrlPath);
                String result = request.PhPtest(userId,uploadFileName,nickNameValue);
                if (result.equals("1")) {
                    Toast.makeText(getApplication(), "전송완료", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplication(), "전송불가", Toast.LENGTH_SHORT).show();
                }
            }catch(MalformedURLException e){
                e.printStackTrace();
            }

            redirectMainActivity();
            finish();
        }
    };
/*
    private void getRealPathFromURI(Uri data) {
        String result;
        Cursor cursor = getContentResolver().query(data, null, null, null, null);

        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = data.getPath();

        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            uploadFileName = result.substring(result.lastIndexOf("/")+1);
            uploadFilePath = result.replace(uploadFileName, "");
            cursor.close();
        }
    }
*/

    public void doTakeAlbumAction(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,PICK_FROM_ALBUM);
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == PICK_FROM_ALBUM)
        {
            if(resultCode == Activity.RESULT_OK){
                picturefilename = "lis_profile_" + GetRandName();
                String strFilePath = "/storage/emulated/0/LIS/"+ picturefilename + ".jpg";
                File fileCacheItem = new File(strFilePath);
                OutputStream out = null;
                try{

                    Uri imgUri;
                    String result;
                    imgUri = data.getData();

                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(imgUri, filePath, null, null, null);
                    cursor.moveToFirst();
                    String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
                    BitmapFactory.Options options =new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath,options);
                    bitmap = ExifUtils.rotateBitmap(imagePath,bitmap);
                    setImage.setImageBitmap(bitmap);

                    fileCacheItem.createNewFile();
                    out = new FileOutputStream(fileCacheItem);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);

                    uploadFileName = picturefilename+".jpg";
                    uploadFilePath = "storage/emulated/0/LIS/";
                    cursor.close();
                    /*
                    getRealPathFromURI(data.getData());
                    Bitmap img = MediaStore.Images.Media.getBitmap(getContentResolver(),data.getData());
                    setImage.setImageBitmap(img);
                    */
                } catch (Exception e){
                    e.printStackTrace();
                }
                finally {
                    try{
                        out.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void createFolder(){
        String title = "/LIS";
        folderPath += title;
        File cameraDir = new File(folderPath);
        cameraDir.mkdirs();
    }

    public static String GetRandName(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
        String getTime = sdf.format(date);
        int RandNum = (int)(Math.random()*100000);
        String getNum = String.valueOf(RandNum);
        String GetName = getTime + getNum;

        return GetName;
    }

    private void redirectMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    public int uploadFile(String sourceFileUri) {

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
                    + uploadFilePath + "" + uploadFileName);

            runOnUiThread(new Runnable() {
                public void run() {
                }
            });
            return 0;
        } else {
            try {
                String upLoadServerUri = "http://52.78.18.156/public/UploadToServer.php";
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


                            Toast.makeText(UserProfileSettingActivity.this, "File Upload Complete.",
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

                        Toast.makeText(UserProfileSettingActivity.this, "MalformedURLException",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {
                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {

                        Toast.makeText(UserProfileSettingActivity.this, "Got Exception : see logcat ",
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


}