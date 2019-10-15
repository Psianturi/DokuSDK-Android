package com.doku.sdkocov2.model;

/**
 * Created by zaki on 3/17/16.
 */
public class PromoItem {

    String promoID;
    String promoName;
    String promoAmount;
    String promoDateStart;
    String promoDateEnd;

    public PromoItem(String promoID, String promoName, String promoAmount, String promoDateStart, String promoDateEnd) {
        super();

        this.promoID = promoID;
        this.promoName = promoName;
        this.promoAmount = promoAmount;
        this.promoDateStart = promoDateStart;
        this.promoDateEnd = promoDateEnd;
    }

    public String toString() {
        return promoName + "( " + promoAmount + " )";
    }

    public String getPromoDateEnd() {
        return promoDateEnd;
    }

    public void setPromoDateEnd(String promoDateEnd) {
        this.promoDateEnd = promoDateEnd;
    }

    public String getPromoID() {
        return promoID;
    }

    public void setPromoID(String promoID) {
        this.promoID = promoID;
    }

    public String getPromoName() {
        return promoName;
    }

    public void setPromoName(String promoName) {
        this.promoName = promoName;
    }

    public String getPromoAmount() {
        return promoAmount;
    }

    public void setPromoAmount(String promoAmount) {
        this.promoAmount = promoAmount;
    }

    public String getPromoDateStart() {
        return promoDateStart;
    }

    public void setPromoDateStart(String promoDateStart) {
        this.promoDateStart = promoDateStart;
    }


}
