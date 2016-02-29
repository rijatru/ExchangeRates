package com.ricardo.exchangerates.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.ricardo.exchangerates.BR;

/**
 * Created by ricardo on 2/28/16.
 */
public class Rates extends BaseObservable {

    private double gbpValue = 0;
    private double eurValue = 0;
    private double jpyValue = 0;
    private double brlValue = 0;

    private double gbp = 0;
    private double eur = 0;
    private double jpy = 0;
    private double brl = 0;

    public Rates() {

    }

    public void setValues(double gbpValue, double eurValue, double jpyValue, double brlValue) {

        this.gbpValue = gbpValue;
        this.eurValue = eurValue;
        this.jpyValue = jpyValue;
        this.brlValue = brlValue;
    }

    @Bindable
    public String getGbp() {

        return String.valueOf(this.gbp);
    }

    @Bindable
    public String getEur() {

        return String.valueOf(this.eur);
    }

    @Bindable
    public String getJpy() {

        return String.valueOf(this.jpy);
    }

    @Bindable
    public String getBrl() {

        return String.valueOf(this.brl);
    }

    public String getGbpValue() {

        return String.valueOf(this.gbpValue);
    }

    public String getEurValue() {

        return String.valueOf(this.eurValue);
    }

    public String getJpyValue() {

        return String.valueOf(this.jpyValue);
    }

    public String getBrlValue() {

        return String.valueOf(this.brlValue);
    }

    public void setGbp(Double gbp) {

        this.gbp = gbp;
        notifyPropertyChanged(BR.gbp);
    }

    public void setEur(Double eur) {

        this.eur = eur;
        notifyPropertyChanged(BR.eur);
    }

    public void setJpy(Double jpy) {

        this.jpy = jpy;
        notifyPropertyChanged(BR.jpy);
    }

    public void setBrl(Double brl) {

        this.brl = brl;
        notifyPropertyChanged(BR.brl);
    }
}
