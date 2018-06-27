package com.dldud.riceapp;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;
import com.squareup.picasso.RequestHandler;

import java.io.IOException;

/**
 * Created by dldud on 2018-04-10.
 */

public class VideoRequestHandler extends RequestHandler {
    public String SCHEME_VIDEO = "video";
    @Override
    public boolean canHandleRequest(Request data) {
        String scheme = data.uri.getScheme();
        return(SCHEME_VIDEO.equals(scheme));
    }

    @Override
    public Result load(Request data, int networkPolicy) throws IOException {
        Bitmap bm = ThumbnailUtils.createVideoThumbnail(data.uri.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
        return new Result(bm, Picasso.LoadedFrom.DISK);
    }
}
