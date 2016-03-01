package com.ricardo.exchangerates;

import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import com.ricardo.exchangerates.components.AppComponent;
import com.ricardo.exchangerates.components.DaggerAppComponent;
import com.ricardo.exchangerates.components.DaggerHttpRequesterComponent;
import com.ricardo.exchangerates.components.HttpRequesterComponent;
import com.ricardo.exchangerates.databinding.ActivityMainBinding;
import com.ricardo.exchangerates.model.HttpRequester;
import com.ricardo.exchangerates.model.Rates;
import com.ricardo.exchangerates.modules.AppModule;
import com.ricardo.exchangerates.modules.HttpRequesterModule;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;

public class MainActivity extends AppCompatActivity {

    public final static String[] labels = new String[]{"GBP", "EUR", "JPY", "BRL"};

    @Inject
    HttpRequester httpRequester;

    @Inject
    SharedPreferences sharedPrefs;

    @Bind(R.id.chart)
    ColumnChartView barChart;

    Rates rates;

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rates = new Rates();

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setRates(rates);

        ButterKnife.bind(this);

        inject();

        setTextListeners();

        checkForSavedResult();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (httpRequester != null) {

            httpRequester.cancelAll();
        }
    }

    void inject() {

        AppComponent appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(App.getAppInstance(this)))
                .build();

        sharedPrefs = appComponent.provideAppPreferences();

        HttpRequesterComponent httpRequesterComponent = DaggerHttpRequesterComponent.builder()
                .httpRequesterModule(new HttpRequesterModule())
                .build();

        httpRequester = httpRequesterComponent.provideHttpRequester();
    }

    void setTextListeners() {

        binding.inputNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() < 10) setNewValues(s.toString());

            }

        });
    }

    void checkForSavedResult() {

        String result = (getFromPreferences(Constants.RESULT).length() > 0) ? getFromPreferences(Constants.RESULT) : "";

        if (result.length() > 0) {

            if (isWeekend()) {

                if (hasInfoFromLastFriday(result)) {

                    fillData(result);

                } else {

                    getExchangeRates();
                }

            } else {

                if (isSameDate(result)) {

                    fillData(result);

                } else {

                    getExchangeRates();
                }
            }

        } else {

            getExchangeRates();
        }
    }

    boolean hasInfoFromLastFriday(String result) {

        try {

            String date = new JSONObject(result).getString(Constants.DATE);

            int savedDay = Integer.valueOf(date.length() > 2 ? date.substring(date.length() - 2) : date);

            Calendar calendar = Calendar.getInstance();

            int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

            if (currentDay - savedDay > 2) { // Not last Friday

                return false;
            }

        } catch (JSONException ex) {

            Log.d("App", "JSONException " + ex.getMessage());
        }

        return true;
    }

    void getExchangeRates() {

        httpRequester.get(this, Constants.FIXER_URL, new HttpRequester.Listener() {
            @Override
            public void onDataRetrieved(String result) {

                saveToPreferences(Constants.RESULT, result);

                saveToPreferences(Constants.DAY_OF_THE_WEEK, getDayOfTheWeek());

                fillData(result);
            }
        });
    }

    boolean isWeekend() {

        Calendar calendar = Calendar.getInstance();

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY;
    }

    boolean isSameDate(String result) {

        String currentDate = getCurrentDate();

        try {

            String savedDate = new JSONObject(result).getString(Constants.DATE);

            if (savedDate.equals(currentDate)) return true;

        } catch (JSONException ex) {

            Log.d("App", "JSONException: " + ex.getMessage());
        }

        return false;
    }

    String getCurrentDate() {

        Calendar calendar = Calendar.getInstance();

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);

        String monthString = String.valueOf(month);
        String dayString = String.valueOf(day);

        if (month < 10) monthString = "0" + monthString;
        if (day < 10) dayString = "0" + dayString;

        return year + "-" + monthString + "-" + dayString;
    }

    String getDayOfTheWeek() {

        Calendar calandar = Calendar.getInstance();

        return String.valueOf(calandar.get(Calendar.DAY_OF_WEEK));
    }

    void saveToPreferences(String key, String data) {

        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(key, data);
        editor.apply();
    }

    String getFromPreferences(String key) {

        return sharedPrefs.getString(key, "");
    }

    void fillData(String result) {

        try {

            JSONObject ratesJson = new JSONObject(result).getJSONObject(Constants.RATES);

            rates.setValues(
                    ratesJson.optDouble(Constants.GBP, 0),
                    ratesJson.optDouble(Constants.EUR, 0),
                    ratesJson.optDouble(Constants.JPY, 0),
                    ratesJson.optDouble(Constants.BRL, 0));

            rates.setGbp(round(ratesJson.optDouble(Constants.GBP, 0), 2));
            rates.setEur(round(ratesJson.optDouble(Constants.EUR, 0), 2));
            rates.setJpy(round(ratesJson.optDouble(Constants.JPY, 0), 2));
            rates.setBrl(round(ratesJson.optDouble(Constants.BRL, 0), 2));

            setChart();

        } catch (JSONException ex) {

            Log.d("App", "JSONException: " + ex.getMessage());
        }
    }

    void setNewValues(String newValue) {

        if (newValue.length() > 0) {

            rates.setGbp(round(Double.parseDouble(rates.getGbpValue()) * Double.parseDouble(newValue), 2));
            rates.setEur(round(Double.parseDouble(rates.getEurValue()) * Double.parseDouble(newValue), 2));
            rates.setJpy(round(Double.parseDouble(rates.getJpyValue()) * Double.parseDouble(newValue), 2));
            rates.setBrl(round(Double.parseDouble(rates.getBrlValue()) * Double.parseDouble(newValue), 2));

        } else {

            newValue = "1";

            rates.setGbp(round(Double.parseDouble(rates.getGbpValue()) * Double.parseDouble(newValue), 2));
            rates.setEur(round(Double.parseDouble(rates.getEurValue()) * Double.parseDouble(newValue), 2));
            rates.setJpy(round(Double.parseDouble(rates.getJpyValue()) * Double.parseDouble(newValue), 2));
            rates.setBrl(round(Double.parseDouble(rates.getBrlValue()) * Double.parseDouble(newValue), 2));
        }
    }


    public static double round(double value, int places) {

        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    void setChart() {

        ColumnChartData data;

        barChart.setZoomEnabled(false);

        List<Column> columns = new ArrayList<>();
        List<SubcolumnValue> values;

        values = new ArrayList<>();
        values.add(new SubcolumnValue(0f, ChartUtils.pickColor()));
        columns.add(new Column(values).setHasLabels(true).setHasLabelsOnlyForSelected(false));

        values = new ArrayList<>();
        values.add(new SubcolumnValue(0f, ChartUtils.pickColor()));
        columns.add(new Column(values).setHasLabels(true).setHasLabelsOnlyForSelected(false));

        values = new ArrayList<>();
        values.add(new SubcolumnValue(0f, ChartUtils.pickColor()));
        columns.add(new Column(values).setHasLabels(true).setHasLabelsOnlyForSelected(false));

        values = new ArrayList<>();
        values.add(new SubcolumnValue(0f, ChartUtils.pickColor()));
        columns.add(new Column(values).setHasLabels(true).setHasLabelsOnlyForSelected(false));

        Axis axisY = new Axis().setHasLines(true);

        axisY.setName(getString(R.string.usd_label));

        List<AxisValue> axisValues = new ArrayList<>();

        for (int i = 0; i < labels.length; i++) {

            axisValues.add(new AxisValue(i).setLabel(labels[i]));
        }

        data = new ColumnChartData(columns);

        data.setAxisXBottom(new Axis(axisValues).setHasLines(false));
        data.setAxisYLeft(axisY);

        barChart.setColumnChartData(data);

        data.getColumns().get(0).getValues().get(0).setTarget(Float.parseFloat(rates.getGbpValue()) * Float.parseFloat("1"));
        data.getColumns().get(1).getValues().get(0).setTarget(Float.parseFloat(rates.getEurValue()) * Float.parseFloat("1"));
        data.getColumns().get(2).getValues().get(0).setTarget(Float.parseFloat(rates.getJpyValue()) * Float.parseFloat("1"));
        data.getColumns().get(3).getValues().get(0).setTarget(Float.parseFloat(rates.getBrlValue()) * Float.parseFloat("1"));

        barChart.startDataAnimation();
    }
}
