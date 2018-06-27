package com.dldud.riceapp;

import net.daum.mf.map.api.MapReverseGeoCoder;

public class FeedLikeItemData {
    String strIdx;
    String strLike;
    String strShare;
    String strReply;
    String strUserId;
    String strUserImage;
    String strThumbnailImage;
    String strVideo;
    String strUserName;
    String strPingLike;
    String strPingReply;
    String strContent;
    String strTitile;
    String strTime;
    int likeCnt;
    boolean bActiveMap = false;
    double douLatitude;
    double douLongitude;

    private int iMarkerIndex = -1;
    private String strAddress = null;
    private MapReverseGeoCoder reverseGeoCoder = null;

    public boolean getActiveMap()
    {
        return bActiveMap;
    }

    public void setbActivemap(boolean b)
    {
        bActiveMap = b;
    }

    int getiMarkerIndex() {
        return iMarkerIndex;
    }

    void setiMarkerIndex(int iMarkerIndex) {
        this.iMarkerIndex = iMarkerIndex;
    }

    String getStrAddress() {
        return strAddress;
    }

    void setStrAddress(String strAddress) {
        this.strAddress = strAddress;
    }

    void setReverseGeoCoder(MapReverseGeoCoder reverseGeoCoder) {
        this.reverseGeoCoder = reverseGeoCoder;
    }

    MapReverseGeoCoder getReverseGeoCoder() {
        return reverseGeoCoder;
    }

    //Getters and Setters
    String getStrIdx() {
        return strIdx;
    }

    public void setStrIdx(String strIdx) {
        this.strIdx = strIdx;
    }

    String getStrTitile() {
        return strTitile;
    }

    public void setStrTitile(String strTitile) {
        this.strIdx = strTitile;
    }

    String getStrTime() {
        return strTime;
    }

    public void setStrTime(String strTime) {
        this.strTime = strTime;
    }

    String getStrLike() {
        return strLike;
    }

    public void setStrLike(String strLike) {
        this.strLike = strLike;
    }

    String getStrShare() {
        return strShare;
    }

    public void setStrShare(String strShare) {
        this.strShare = strShare;
    }

    String getStrReply() {
        return strReply;
    }

    public void setStrReply(String strReply) {
        this.strReply = strReply;
    }

    public String getStrUserId() {
        return strShare;
    }

    public void setStrUserId(String strUserId) {
        this.strUserId = strUserId;
    }

    String getStrUserImage() {
        return strUserImage;
    }

    public void setStrUserImage(String strUserImage) {
        this.strUserImage = strUserImage;
    }

    String getStrThumbnailImage() {
        return strThumbnailImage;
    }

    public void setStrThumbnailImage(String strThumbnailImage) {
        this.strThumbnailImage = strThumbnailImage;
    }

    String getStrVideo() {
        return strVideo;
    }

    public void setStrVideo(String strVideo) {
        this.strVideo = strVideo;
    }

    String getStrUserName() {
        return strUserName;
    }

    public void setStrUserName(String strUserName){
        this.strUserName = strUserName;
    }

    String getStrPingReply(){
        return strPingReply;
    }

    public void setStrPingReply(String strPingReply){
        this.strPingReply = strPingReply;
    }

    String getStrPingLike(){
        return strPingLike;
    }

    public void setStrPingLike(String strPingLike){
        this.strPingLike = strPingLike;
    }

    String getStrContent(){
        return strContent;
    }

    public void setStrContent(String strContent){
        this.strContent = strContent;
    }

    double getDouLatitude(){
        return  douLatitude;
    }

    public void setDouLatitude(double douLatitude) {
        this.douLatitude = douLatitude;
    }

    double getDouLongitude(){
        return douLongitude;
    }

    public void setDouLongitude(double douLongitude) {
        this.douLongitude = douLongitude;
    }
}
