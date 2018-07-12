package com.dldud.riceapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import static com.dldud.riceapp.UserProfileSettingActivity.userId;

public class DialogReplyActivity extends AppCompatActivity {

    String PageIdx, replyString, userString, myString, getReplyUserString, ReplyUserProfile, ReplyUserName, ReplyUserId, feedThumbnail;
    RecyclerView recyclerView;
    String imgUrl = "http://52.78.18.156/data/riceapp/";
    private String replyInsertUrlPath = "http://52.78.18.156/public/Comment_insert.php";
    private ArrayList<ReplyItemData> rData = new ArrayList<>();
    private LinearLayoutManager replyLayoutManager;
    private CustomReplyAdapter rAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dialog_reply);
        this.setFinishOnTouchOutside(false);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        ImageView imageView = (ImageView)findViewById(R.id.dialogBannerImage);

        final EditText replyText = (EditText)findViewById(R.id.replyText);
        Button sendText = (Button)findViewById(R.id.sendBtn);

        //Feed Idx
        Intent intent = getIntent();
        PageIdx = intent.getStringExtra("position");
        recyclerView = (RecyclerView)findViewById(R.id.dialogReply);


        try{
            Task task = new Task();
            myString = task.execute("http://52.78.18.156/public/ping_db.php").get();

            task.jsonParser(myString);

            String[] idx = task.idx.toArray(new String[task.idx.size()]);
            String[] thumbnail = task.thumbnail.toArray(new String[task.thumbnail.size()]);

            int length = idx.length;

            for(int i = 0 ; i < length ; i++){
                if(PageIdx.equals(idx[i])){
                    feedThumbnail = thumbnail[i];
                    break;
                }
            }

        }catch(InterruptedException e){
            e.printStackTrace();
        }catch(ExecutionException e){
            e.printStackTrace();
        }

        Glide.with(this)
                .load(imgUrl + feedThumbnail)
                .apply(new RequestOptions().fitCenter())
                .apply(new RequestOptions().placeholder(R.drawable.loading_img))
                .into(imageView);


        sendText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replyLayoutManager = new LinearLayoutManager(getApplicationContext());
                replyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(replyLayoutManager);
                rAdapter = new CustomReplyAdapter(getApplicationContext(), rData);
                recyclerView.setAdapter(rAdapter);
                rAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(rAdapter.getItemCount()-1);
                try {
                    TaskUser taskUser = new TaskUser();
                    String idx;
                    String replyContent;
                    PHPRequestReply request = new PHPRequestReply(replyInsertUrlPath);

                    replyContent = replyText.getText().toString();

                    userString = taskUser.execute("http://52.78.18.156/public/user_db.php").get();
                    taskUser.jsonParser(userString);

                    String[] userIdx = taskUser.idx.toArray(new String[taskUser.idx.size()]);
                    String[] userLink_id = taskUser.link_id.toArray(new String[taskUser.link_id.size()]);
                    String[] user_profile = taskUser.profile.toArray(new String[taskUser.profile.size()]);
                    String[] user_name = taskUser.nickname.toArray(new String[taskUser.nickname.size()]);

                    int userLinearNum = userIdx.length;

                    for (int i = 0; i < userLinearNum; i++) {
                        String val = userLink_id[i];
                        if (val.contains(userId)) {
                            getReplyUserString = userIdx[i];
                            ReplyUserProfile = user_profile[i];
                            ReplyUserName = user_name[i];
                            ReplyUserId = userLink_id[i];
                        }
                    }

                    if (replyContent.equals("")) {
                        Toast.makeText(getApplicationContext(), "댓글을 입력 후 확인버튼을 눌러주세요", Toast.LENGTH_LONG).show();
                    } else {
                        ReplyItemData rItem = new ReplyItemData();
                        idx = PageIdx;

                        long now = System.currentTimeMillis();
                        Date date = new Date(now);
                        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
                        String formatDate = sdfNow.format(date);

                        request.PhPtest(getReplyUserString, idx, replyContent, formatDate,ReplyUserId);
                        Toast.makeText(getApplicationContext(), "댓글을 남겼습니다", Toast.LENGTH_LONG).show();

                        //keyboard gone
                        InputMethodManager mgr = (InputMethodManager) getApplicationContext().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                        mgr.hideSoftInputFromWindow(replyText.getWindowToken(), 0);

                        //Edittext initialize
                        replyText.setText("");
                        replyText.setHint("댓글을 입력해주세요");

                        rItem.strReplyContent = replyContent;
                        rItem.strReplyUserImage = ReplyUserProfile;
                        rItem.strReplyUserName = ReplyUserName;
                        rItem.strReplyUserId = ReplyUserId;

                        rData.add(rItem);
                        rAdapter = new CustomReplyAdapter(getApplicationContext(), rData);
                        recyclerView.setAdapter(rAdapter);
                        rAdapter.notifyDataSetChanged();
                        //scroll GOTO Last
                        recyclerView.scrollToPosition(rAdapter.getItemCount()-1);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e){
                    e.printStackTrace();
                }
            }
        });

        try {
            TaskReply taskReply = new TaskReply();
            TaskUser taskUser = new TaskUser();
            replyString = taskReply.execute("http://52.78.18.156/public/comment_db.php").get();
            userString = taskUser.execute("http://52.78.18.156/public/user_db.php").get();
            taskUser.jsonParser(userString);
            taskReply.jsonParser(replyString);
            String idx;

            idx = PageIdx;

            String[] replyIdx = taskReply.idx.toArray(new String[taskReply.idx.size()]);
            String[] replyUser = taskReply.user_idx.toArray(new String[taskReply.user_idx.size()]);
            String[] replyPing = taskReply.ping_idx.toArray(new String[taskReply.ping_idx.size()]);
            String[] replyContent = taskReply.content.toArray(new String[taskReply.content.size()]);

            String[] nickname = taskUser.nickname.toArray(new String[taskUser.nickname.size()]);
            String[] profile = taskUser.profile.toArray(new String[taskUser.profile.size()]);
            String[] userIdx = taskUser.idx.toArray(new String[taskUser.idx.size()]);
            String[] userId = taskUser.link_id.toArray(new String[taskUser.link_id.size()]);

            int replyNum = replyIdx.length;
            int userLinearNum = userIdx.length;

            for (int i = 0; i < replyNum; i++) {
                String val = replyPing[i];
                if (val.contains(idx)) {
                    ReplyItemData rItem = new ReplyItemData();

                    String strUserId;
                    strUserId = replyUser[i];
                    rItem.strReplyContent = replyContent[i];

                    for (int j = 0; j < userLinearNum; j++) {
                        String val1 = userIdx[j];
                        if (val1.contains(strUserId)) {
                            rItem.strReplyUserImage = imgUrl + profile[j];
                            rItem.strReplyUserName = nickname[j];
                            rItem.strReplyUserId = userId[j];
                        }
                    }
                    rData.add(rItem);
                }
            }
            replyLayoutManager = new LinearLayoutManager(this);
            replyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(replyLayoutManager);
            rAdapter = new CustomReplyAdapter(this, rData);
            recyclerView.setAdapter(rAdapter);
            rAdapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(rAdapter.getItemCount()-1);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


    }
}
