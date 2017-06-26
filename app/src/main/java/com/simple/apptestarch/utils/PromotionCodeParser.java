package com.simple.apptestarch.utils;

/**
 * Created by mrsimple on 26/6/17.
 */
public class PromotionCodeParser {

    private String mReferrer = "";

    /**
     * 从 Appsflyer那里获取 referrer
     *
     * @return
     */
    public String getReferrer() {
        return mReferrer;
    }

    public String parsePromotionCode() {
        String referrer = getReferrer() ;
        String promKey = "promote_code";
        if (referrer == null || referrer.equalsIgnoreCase("") || !referrer.contains(promKey)) {
            return "";
        }
        String[] rawData = referrer.split("&");
        if ( rawData == null ) {
            return "";
        }
        String promCode = "";
        for (int i = 0; i < rawData.length; i++) {
            String rawItem = rawData[i];
            if (rawItem.startsWith(promKey)) {
                String[] pcodeArray = rawItem.split("=");
                if ( pcodeArray.length == 2 ) {
                    promCode = pcodeArray[1] ;
                }
                break;
            }
        }
        return promCode;
    }
}
