package com.doku.sdkocov2.model;

/**
 * Created by zaki on 2/25/16.
 */
public class CCItem {
    String linkID;
    String cardName;
    String cardZipCode;
    String cardNumberEncrypt;
    String cardCountry;
    String cardCity;
    String cardEmail;
    String cardExpiryDateEncrypt;
    String cardPhone;
    String cardNoMasked;
    String typeCard;
    boolean choosenCard;

    public CCItem(String linkID, String cardName, String cardZipCode, String cardNumberEncrypt, String cardCountry, String cardCity, String cardEmail,
                  String cardExpiryDateEncrypt, String cardPhone, String cardNoMasked, String typeCard) {
        super();

        this.linkID = linkID;
        this.cardName = cardName;
        this.cardZipCode = cardZipCode;
        this.cardNumberEncrypt = cardNumberEncrypt;
        this.cardCountry = cardCountry;
        this.cardCity = cardCity;
        this.cardEmail = cardEmail;
        this.cardExpiryDateEncrypt = cardExpiryDateEncrypt;
        this.cardPhone = cardPhone;
        this.cardNoMasked = cardNoMasked;
        this.typeCard = typeCard;
    }

    public CCItem(String linkID, String cardName, String cardNumberEncrypt, String cardEmail,
                  String cardExpiryDateEncrypt, String cardNoMasked, String typeCard) {
        super();
        this.linkID = linkID;
        this.cardName = cardName;
        this.cardNumberEncrypt = cardNumberEncrypt;
        this.cardEmail = cardEmail;
        this.cardExpiryDateEncrypt = cardExpiryDateEncrypt;
        this.cardNoMasked = cardNoMasked;
        this.typeCard = typeCard;
    }

    public String getTypeCard() {
        return typeCard;
    }

    public void setTypeCard(String typeCard) {
        this.typeCard = typeCard;
    }

    public boolean isChoosenCard() {
        return choosenCard;
    }

    public void setChoosenCard(boolean choosenCard) {
        this.choosenCard = choosenCard;
    }


    public String getLinkID() {
        return linkID;
    }

    public void setLinkID(String linkID) {
        this.linkID = linkID;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getCardZipCode() {
        return cardZipCode;
    }

    public void setCardZipCode(String cardZipCode) {
        this.cardZipCode = cardZipCode;
    }

    public String getCardNumberEncrypt() {
        return cardNumberEncrypt;
    }

    public void setCardNumberEncrypt(String cardNumberEncrypt) {
        this.cardNumberEncrypt = cardNumberEncrypt;
    }

    public String getCardCountry() {
        return cardCountry;
    }

    public void setCardCountry(String cardCountry) {
        this.cardCountry = cardCountry;
    }

    public String getCardCity() {
        return cardCity;
    }

    public void setCardCity(String cardCity) {
        this.cardCity = cardCity;
    }

    public String getCardEmail() {
        return cardEmail;
    }

    public void setCardEmail(String cardEmail) {
        this.cardEmail = cardEmail;
    }

    public String getCardExpiryDateEncrypt() {
        return cardExpiryDateEncrypt;
    }

    public void setCardExpiryDateEncrypt(String cardExpiryDateEncrypt) {
        this.cardExpiryDateEncrypt = cardExpiryDateEncrypt;
    }

    public String getCardPhone() {
        return cardPhone;
    }

    public void setCardPhone(String cardPhone) {
        this.cardPhone = cardPhone;
    }

    public String getCardNoMasked() {
        return cardNoMasked;
    }

    public void setCardNoMasked(String cardNoMasked) {
        this.cardNoMasked = cardNoMasked;
    }


}
