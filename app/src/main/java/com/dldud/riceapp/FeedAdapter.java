package com.dldud.riceapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.kakao.kakaolink.AppActionBuilder;
import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.LinkObject;
import com.kakao.message.template.LocationTemplate;
import com.kakao.message.template.SocialObject;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.KakaoParameterException;
import com.kakao.util.helper.log.Logger;
import com.squareup.picasso.Picasso;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import android.os.Handler;
import java.util.logging.LogRecord;

import static com.dldud.riceapp.MainActivity.navigation;
import static com.dldud.riceapp.UserProfileSettingActivity.userId;

/**
 * Created by dldud on 2018-05-03.
 */

public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context context;

    private ArrayList<ReplyItemData> rData;

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private KakaoLink kakaoLink;
    private KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder;
    private ReplyAdapter rAdapter;
    private String getLikeUserString;
    private String replyString;
    private String userString;
    private String likeString;
    private String imgUrl = "http://52.78.18.156/data/riceapp/";
    private String likeInsertUrlPath = "http://52.78.18.156/public/Ping_like_insert.php";
    private String replyInsertUrlPath = "http://52.78.18.156/public/Comment_insert.php";

    private ArrayList<ItemData> items;

    private boolean isMoreLoading = false;
    private int visibleThreshold = 1;
    private double latitude,longitude;

    private OnLoadMoreListener onLoadMoreListener;
    private LinearLayoutManager layoutManager;
    private LinearLayoutManager replyLayoutManager;

    private boolean isShowMap = true;

    public boolean isShowMap() {
        return isShowMap;
    }

    public void setShowMap(boolean showMap) {
        isShowMap = showMap;
    }



    public interface OnLoadMoreListener{
        void onLoadMore();
    }

    public FeedAdapter(Context context, OnLoadMoreListener onLoadMoreListener){
        this.onLoadMoreListener = onLoadMoreListener;
        this.context = context;
        items = new ArrayList<>();
    }

    public void setLinearLayoutManager(LinearLayoutManager linearLayoutManager){
        this.layoutManager=linearLayoutManager;
    }

    public void setRecyclerView(RecyclerView mView){
        mView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int firstVisibleItem, visibleItemCount, totalItemCount, lastVisibleItem;
            visibleItemCount = recyclerView.getChildCount();
            totalItemCount = layoutManager.getItemCount();
            firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
            lastVisibleItem = layoutManager.findLastVisibleItemPosition();

            if (!isMoreLoading && (totalItemCount - visibleItemCount)<= (firstVisibleItem + visibleThreshold)) {
                if (onLoadMoreListener != null) {
                    onLoadMoreListener.onLoadMore();
                }
                isMoreLoading = true;
            }
            }
        });
    }

    @Override
    public int getItemViewType(int position){
        return items.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    public void add(ItemData data){
        items.add(data);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        if (viewType == VIEW_ITEM) {
            return new CardViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false));
        } else {
            return new ProgressViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progress, parent, false));
        }
    }

    public void addAll(List<ItemData> lst){
        items.clear();
        items.addAll(lst);
        notifyDataSetChanged();
    }

    public void addItemMore(List<ItemData> lst){
        items.addAll(lst);
        notifyItemRangeChanged(0,items.size());
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof CardViewHolder) {

            NetworkUtil.setNetworkPolicy();
            final RecyclerView recyclerView;
            final ItemData item = items.get(position);
            rData = new ArrayList<>();

            final ConstraintLayout pictureBack;
            final ConstraintLayout videoBack;
            final WebView wv;
            final ImageView detailImage;
            final Button vidClose, imgClose;

            videoBack = (ConstraintLayout)((Activity)context).findViewById(R.id.videoOn);
            pictureBack = (ConstraintLayout)((Activity)context).findViewById(R.id.imageOn);
            recyclerView = (RecyclerView) ((Activity) context).findViewById(R.id.dynamicLayout);
            detailImage = (ImageView) ((Activity) context).findViewById(R.id.detailImage);
            wv = (WebView) ((Activity) context).findViewById(R.id.seeDetailView);
            vidClose = (Button) ((Activity) context).findViewById(R.id.wvCloseBtn);
            imgClose = (Button) ((Activity) context).findViewById(R.id.imgCloseBtn);

            String likeCnt = String.valueOf(item.getPingLike());
            String pingCnt = String.valueOf(item.getPingReply());

            ((CardViewHolder) holder).oTextLike.setText(item.getStrLike());
            ((CardViewHolder) holder).oTextShare.setText(item.getStrShare());
            ((CardViewHolder) holder).oTextReply.setText(item.getStrReply());
            ((CardViewHolder) holder).oTextUserId.setText(item.getStrUserName());
            ((CardViewHolder) holder).oTextContent.setText(item.getStrContent());
            ((CardViewHolder) holder).oTextLikeCnt.setText(likeCnt);
            ((CardViewHolder) holder).oTextReplyCnt.setText(pingCnt);
            ((CardViewHolder) holder).oTextDistance.setText(item.getStrDistance());

            ((CardViewHolder) holder).oTextTime.setText(item.getStrTime());

            ((CardViewHolder) holder).reply.setVisibility(View.GONE);
            ((CardViewHolder) holder).oFeedMap.setVisibility(View.GONE);
            ((CardViewHolder) holder).oMapContainer.setVisibility(View.GONE);



            if(videoBack.getVisibility() == View.VISIBLE) {
                recyclerView.setNestedScrollingEnabled(false);
            }

            if(pictureBack.getVisibility() == View.VISIBLE) {
                recyclerView.setNestedScrollingEnabled(false);
            }


            ((CardViewHolder) holder).replyView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                @Override
                public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                    int action = e.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_MOVE:
                            rv.getParent().requestDisallowInterceptTouchEvent(true);
                            break;
                    }
                    return false;
                }

                @Override
                public void onTouchEvent(RecyclerView rv, MotionEvent e) {

                }

                @Override
                public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

                }
            });
            ((CardViewHolder) holder).oTextLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String idx;
                        boolean isExistLike = false;
                        TaskLike taskLike = new TaskLike();
                        TaskUser taskUser = new TaskUser();
                        PHPRequestLike request = new PHPRequestLike(likeInsertUrlPath);

                        idx = item.getStrIdx();

                        userString = taskUser.execute("http://52.78.18.156/public/user_db.php").get();
                        likeString = taskLike.execute("http://52.78.18.156/public/ping_like_db.php").get();

                        taskLike.jsonParser(likeString);
                        taskUser.jsonParser(userString);

                        String[] userIdx = taskUser.idx.toArray(new String[taskUser.idx.size()]);
                        String[] userLink_id = taskUser.link_id.toArray(new String[taskUser.link_id.size()]);

                        String[] pingIdx = taskLike.idx.toArray(new String[taskLike.idx.size()]);
                        String[] pingUserIdx = taskLike.user_idx.toArray(new String[taskLike.user_idx.size()]);
                        String[] pingPingIdx = taskLike.ping_idx.toArray(new String[taskLike.ping_idx.size()]);

                        int userLinearNum = userIdx.length;
                        int pingLinearNum = pingIdx.length;

                        for (int i = 0; i < userLinearNum; i++) {
                            String val = userLink_id[i];
                            if (val.contains(userId)) {
                                getLikeUserString = userIdx[i];
                            }
                        }

                        for (int j = 0; j < pingLinearNum; j++) {
                            String s1 = pingUserIdx[j];
                            String s2 = pingPingIdx[j];
                            if (s2.contains(idx) && s1.contains(getLikeUserString)) {
                                Toast.makeText(context, "이미 좋아요를 누른 게시물입니다", Toast.LENGTH_LONG).show();
                                isExistLike = true;
                                break;
                            }
                        }
                        if (!isExistLike) {
                            request.PhPtest(getLikeUserString, idx);
                            ((CardViewHolder) holder).oTextLike.setTypeface(null, Typeface.BOLD);
                            ((CardViewHolder) holder).oTextLike.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
                            int getCnt = item.getPingLike();
                            getCnt++;
                            ((CardViewHolder) holder).oTextLikeCnt.setText(String.valueOf(getCnt));
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            });

            ((CardViewHolder) holder).oTextShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(context, "공유기능은 아직 지원하지 않습니다", Toast.LENGTH_LONG).show();
                    MapReverseGeoCoder reverseGeoCoder = null;
                    try {
                        String contentText;
                        if(item.getStrContent() == null) {
                            contentText = " ";
                        } else{
                            contentText =item.getStrContent();
                        }


                        LocationTemplate params = LocationTemplate.newBuilder("성남시 분당구 판교역로 235",
                                ContentObject.newBuilder("여기는 고정텍스트 (UI필요)",
                                        item.getStrThumbnailImage(),
                                        LinkObject.newBuilder()
                                                .setWebUrl("https://developers.kakao.com")
                                                .setMobileWebUrl("https://developers.kakao.com")
                                                .build())
                                        .setDescrption(contentText).build())
                                .setSocial(SocialObject.newBuilder().setLikeCount(item.getPingLike())
                                        .setCommentCount(item.getPingReply())
                                        .setSharedCount(333).build())
                                .setAddressTitle("카카오 판교오피스")
                                .build();

                        KakaoLinkService.getInstance().sendDefault(context, params, new ResponseCallback<KakaoLinkResponse>() {
                            @Override
                            public void onFailure(ErrorResult errorResult) {
                                Logger.e(errorResult.toString());
                            }

                            @Override
                            public void onSuccess(KakaoLinkResponse result) {

                            }



                        });



                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

            ((CardViewHolder) holder).oTextReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String position = item.getStrIdx();

                    Intent intent = new Intent(view.getContext(),FeedDetailActivity.class);
                    intent.putExtra("position",position);
                    intent.putExtra("page","2");
                    view.getContext().startActivity(intent);
                }
            });

            ((CardViewHolder)holder).oButtonMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String position = item.getStrIdx();

                    Intent intent = new Intent(view.getContext(),FeedDetailActivity.class);
                    intent.putExtra("position",position);
                    intent.putExtra("page","3");
                    view.getContext().startActivity(intent);
                }
            });


            ((CardViewHolder) holder).oImageBanner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.getStrVideo().substring(item.getStrVideo().length() - 3, item.getStrVideo().length()).equals("mp4")) {
                        String name = item.getStrVideo();
                        String type = "video/mp4";

                        videoBack.setVisibility(View.VISIBLE);

                        pictureBack.setVisibility(View.GONE);

                        wv.loadUrl("http://52.78.18.156/public/playVideo.php?video=" + name + "&type=" + type);

                        ((CardViewHolder) holder).reply.setVisibility(View.GONE);
                        ((CardViewHolder) holder).oTextReply.setTypeface(null, Typeface.NORMAL);
                    } else {
                        pictureBack.setVisibility(View.VISIBLE);

                        videoBack.setVisibility(View.GONE);
                        wv.setVisibility(View.GONE);

                        Picasso.with(detailImage.getContext())
                                .load(item.getStrVideo())
                                .into(detailImage);

                        ((CardViewHolder) holder).reply.setVisibility(View.GONE);
                        ((CardViewHolder) holder).oTextReply.setTypeface(null, Typeface.NORMAL);
                    }
                }
            });

            vidClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    videoBack.setVisibility(View.GONE);
                }
            });

            imgClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pictureBack.setVisibility(View.GONE);
                }
            });

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    if (dy > 0 && ((CardViewHolder) holder).oFeedMap.getVisibility() == View.VISIBLE) {
                        // Scrolling up
                        ((CardViewHolder) holder).oFeedMap.setVisibility(View.GONE);
                        ((CardViewHolder) holder).oMapContainer.setVisibility(View.GONE);
                        ((CardViewHolder) holder).oMap.setVisibility(View.GONE);

                    } else if (dy < 0 && ((CardViewHolder) holder).oFeedMap.getVisibility() == View.VISIBLE) {
                        // Scrolling down
                        ((CardViewHolder) holder).oFeedMap.setVisibility(View.GONE);
                        ((CardViewHolder) holder).oMapContainer.setVisibility(View.GONE);
                        ((CardViewHolder) holder).oMap.setVisibility(View.GONE);

                    } else if (dy > 0) {
                    } else if (dy < 0) {
                    }
                }
            });


            Glide.with(context)
                    .load(item.getStrUserImage())
                    .apply(new RequestOptions().circleCropTransform())
                    .into(((CardViewHolder)holder).oImageUser);

            ((CardViewHolder) holder).oImageUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navigation.setTag(item.strUserId);
                    navigation.setSelectedItemId(R.id.navigation_myProfile);
                }
            });

            Glide.with(context)
                    .load(item.getStrThumbnailImage())
                    .apply(new RequestOptions().fitCenter())
                    .apply(new RequestOptions().placeholder(R.drawable.loading_img))
                    .into(((CardViewHolder)holder).oImageBanner);

/*
            if (item.getStrTitile().equals("shopping")) {
                ((CardViewHolder) holder).oFilterImage.setImageResource(R.drawable.shopping_off);
            } else if (item.getStrTitile().equals("food")) {
                ((CardViewHolder) holder).oFilterImage.setImageResource(R.drawable.food_off);
            } else if (item.getStrTitile().equals("animal")) {
                ((CardViewHolder) holder).oFilterImage.setImageResource(R.drawable.animal_off);
            } else if (item.getStrTitile().equals("alcohol")) {
                ((CardViewHolder) holder).oFilterImage.setImageResource(R.drawable.alcohol_off);
            } else if (item.getStrTitile().equals("cafe")) {
                ((CardViewHolder) holder).oFilterImage.setImageResource(R.drawable.cafe_off);
            } else {
                ((CardViewHolder) holder).oFilterImage.setImageResource(R.drawable.anything_off);
            }
*/
        }
    }

    public void setMoreLoading(boolean isMoreLoading) {
        this.isMoreLoading=isMoreLoading;
    }

    public ItemData getItem(int position) {
        return items.get(position);
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setProgressMore(final boolean isProgress) {
        if (isProgress) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    items.add(null);
                    notifyItemInserted(items.size() - 1);
                }
            });
        } else {
            items.remove(items.size() - 1);
            notifyItemRemoved(items.size());
        }
    }

    /*
   View Holders
   _________________________________________________________________________________________________
    */

    static class CardViewHolder extends RecyclerView.ViewHolder {
        private TextView oTextLike;
        private TextView oTextShare;
        private TextView oTextReply;
        private TextView oTextUserId;
        private TextView oTextContent;
        private TextView oTextLikeCnt;
        private Button oButtonMap;
        private ImageView oImageBanner;
        private ImageView oImageUser;
        private ImageView oFilterImage;
        private TextView oTextReplyCnt;
        private TextView oTextTime;
        private FrameLayout oFeedMap;
        private ViewGroup oMapContainer;
        private MapView oMap;
        private RelativeLayout reply;
        private Button replyBtn;
        private RecyclerView replyView;
        private TextView oTextDistance;

        CardViewHolder(View v) {
            super(v);

            oImageBanner = (ImageView) v.findViewById(R.id.bannerImage);
            oImageUser = (ImageView) v.findViewById(R.id.userImage);
            oButtonMap = (Button) v.findViewById(R.id.mapButton);
            oTextLike = (TextView) v.findViewById(R.id.likeText);
            oTextShare = (TextView) v.findViewById(R.id.shareText);
            oTextReply = (TextView) v.findViewById(R.id.replyContent);
            oTextUserId = (TextView) v.findViewById(R.id.userIdText);
            oTextContent = (TextView) v.findViewById(R.id.contentText);
            oTextLikeCnt = (TextView) v.findViewById(R.id.likeCnt);
            oTextReplyCnt = (TextView) v.findViewById(R.id.replyCnt);
            oTextTime = (TextView) v.findViewById(R.id.timeTxt);
            oFeedMap = (FrameLayout) v.findViewById(R.id.mapFrame);
            oMapContainer = (ViewGroup) v.findViewById(R.id.mapFeed);
            reply = (RelativeLayout) v.findViewById(R.id.replyLayout);
            replyView = (RecyclerView) v.findViewById(R.id.replyCard);
            replyBtn = (Button) v.findViewById(R.id.replyBtn);
            oTextDistance = (TextView)v.findViewById(R.id.distance);

        }

    }

    static class ProgressViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar pBar;

        ProgressViewHolder(View v) {
            super(v);
            pBar = (ProgressBar) v.findViewById(R.id.pBar);
        }
    }
}
