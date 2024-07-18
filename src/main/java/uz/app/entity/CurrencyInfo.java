package uz.app.entity;

import com.google.gson.annotations.SerializedName;

public class CurrencyInfo {
    @SerializedName("Ccy")
    private String Ccy;
    @SerializedName("CcyNm_EN")
    private String CcyNmEN;
    @SerializedName("CcyNm_RU")
    private String CcyNmRU;
    @SerializedName("CcyNm_UZ")
    private String CcyNmUZ;
    @SerializedName("CcyNm_UZC")
    private String CcyNmUZC;
    @SerializedName("Code")
    private String Code;
    @SerializedName("Date")
    private String Date;
    @SerializedName("Diff")
    private String Diff;
    @SerializedName("id")
    private Long Id;
    @SerializedName("Nominal")
    private String Nominal;
    @SerializedName("Rate")
    private String Rate;


    public String getCcy() {
        return Ccy;
    }

    public void setCcy(String ccy) {
        Ccy = ccy;
    }

    public String getCcyNmEN() {
        return CcyNmEN;
    }

    public void setCcyNmEN(String ccyNmEN) {
        CcyNmEN = ccyNmEN;
    }

    public String getCcyNmRU() {
        return CcyNmRU;
    }

    public void setCcyNmRU(String ccyNmRU) {
        CcyNmRU = ccyNmRU;
    }

    public String getCcyNmUZ() {
        return CcyNmUZ;
    }

    public void setCcyNmUZ(String ccyNmUZ) {
        CcyNmUZ = ccyNmUZ;
    }

    public String getCcyNmUZC() {
        return CcyNmUZC;
    }

    public void setCcyNmUZC(String ccyNmUZC) {
        CcyNmUZC = ccyNmUZC;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getDiff() {
        return Diff;
    }

    public void setDiff(String diff) {
        Diff = diff;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getNominal() {
        return Nominal;
    }

    public void setNominal(String nominal) {
        Nominal = nominal;
    }

    public String getRate() {
        return Rate;
    }

    public void setRate(String rate) {
        Rate = rate;
    }

    @Override
    public String toString() {
        return "CurrencyInfo{" +
                "Ccy='" + Ccy + '\'' +
                ", CcyNmEN='" + CcyNmEN + '\'' +
                ", CcyNmRU='" + CcyNmRU + '\'' +
                ", CcyNmUZ='" + CcyNmUZ + '\'' +
                ", CcyNmUZC='" + CcyNmUZC + '\'' +
                ", Code='" + Code + '\'' +
                ", Date='" + Date + '\'' +
                ", Diff='" + Diff + '\'' +
                ", Id=" + Id +
                ", Nominal='" + Nominal + '\'' +
                ", Rate='" + Rate + '\'' +
                '}';
    }
}
