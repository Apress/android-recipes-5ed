package com.androidrecipes.json;

import android.app.Activity;
import android.databinding.BindingConversion;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.androidrecipes.json.databinding.MainBinding;
import com.squareup.moshi.FromJson;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.ToJson;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import okio.Okio;
import okio.Source;

public class JsonActivity extends Activity {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Moshi moshi;
    private MainBinding binding;

    @BindingConversion
    public static CharSequence convertFromDate(Date date) {
        return DATE_FORMAT.format(date);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.main);

        moshi = new Moshi.Builder()
                .add(new UuidAdapter())
                .add(new CurrencyAdapter())
                .add(new DateAdapter())
                .build();

        JsonAdapter<Purchase> jsonAdapter = moshi.adapter(Purchase.class);

// Convert from JSON
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.purchase);
            Source source = Okio.source(inputStream);
            Purchase purchase = jsonAdapter.fromJson(Okio.buffer(source));
            binding.setPurchase(purchase);

            // Convert back to JSON
            String json = jsonAdapter.toJson(purchase);
            binding.setRawJson(json);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private class UuidAdapter {

        @ToJson
        String toJson(UUID uuid) {
            return uuid.toString();
        }

        @FromJson
        UUID fromJson(String json) {
            return UUID.fromString(json);
        }

    }

    private class DateAdapter {

        @ToJson
        String toJson(Date date) {
            return DATE_FORMAT.format(date);
        }

        @FromJson
        Date fromJson(String json) {
            try {
                return DATE_FORMAT.parse(json);
            } catch (ParseException e) {
                return null;
            }
        }
    }

    private class CurrencyAdapter {
        @ToJson
        String toJson(Purchase.Currency currency) {
            return currency.getSign();
        }

        @FromJson
        Purchase.Currency fromJson(String json) {
            return Purchase.Currency.fromSign(json);
        }
    }
}
