package com.androidrecipes.simplexml;

import android.app.Activity;
import android.databinding.BindingConversion;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.androidrecipes.xmlpull.databinding.MainBinding;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.transform.RegistryMatcher;
import org.simpleframework.xml.transform.Transform;

import java.io.InputStream;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class SimpleXmlActivity extends Activity {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private MainBinding binding;

    @BindingConversion
    public static CharSequence convertFromDate(Date date) {
        return DATE_FORMAT.format(date);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.main);

RegistryMatcher registryMatcher = new RegistryMatcher();
registryMatcher.bind(UUID.class, new UuidConverter());
registryMatcher.bind(Date.class, new DateConverter());
registryMatcher.bind(Purchase.Currency.class, new CurrencyConverter());

Serializer serializer = new Persister(registryMatcher);

try {
    InputStream inputStream = getResources().openRawResource(R.raw.purchase);
    Purchase purchase = serializer.read(Purchase.class, inputStream);
    binding.setPurchase(purchase);

    StringWriter stringWriter = new StringWriter();
    serializer.write(purchase, stringWriter);
    binding.setRawXml(stringWriter.toString());
} catch (Exception e) {
    e.printStackTrace();
}
    }

    public static class UuidConverter implements Transform<UUID> {
        @Override
        public UUID read(String value) throws Exception {
            return UUID.fromString(value);
        }

        @Override
        public String write(UUID value) throws Exception {
            return value.toString();
        }

    }

    public static class DateConverter implements Transform<Date> {
        @Override
        public Date read(String value) throws Exception {
            return DATE_FORMAT.parse(value);
        }

        @Override
        public String write(Date value) throws Exception {
            return DATE_FORMAT.format(value);
        }
    }

    public static class CurrencyConverter implements Transform<Purchase.Currency> {
        @Override
        public Purchase.Currency read(String value) throws Exception {
            return Purchase.Currency.fromSign(value);
        }

        @Override
        public String write(Purchase.Currency value) throws Exception {
            return value.getSign();
        }
    }
}
