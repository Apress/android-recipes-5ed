package com.androidrecipes.simplexml;

import android.annotation.SuppressLint;
import android.support.annotation.Nullable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.convert.Convert;

import java.util.Date;
import java.util.UUID;

@SuppressWarnings("WeakerAccess")
@Root(name = "purchase")
public class Purchase {
    @Attribute
    public UUID id;
    @Element()
    public Date timestamp;
    @Element
    public String productName;
    @Element
    public String productId;
    @Element
    public Address deliveryAddress;
    @Element
    public Integer units;
    @Element
    public Long amount;
    @Element
    public Currency currency;


    @SuppressWarnings("WeakerAccess")
    public static class Address {
        @Element
        public String name;
        @Element
        public String street;
        @Element
        public String zip;
        @Element
        public String city;
        @Element
        public String state;
        @Element
        public String country;

        @Override
        public String toString() {
            return String.format("%s\n%s\n%s %s\n%s\n%s", name, street, city, zip, street, country);
        }
    }

    @SuppressWarnings("WeakerAccess")
    public enum Currency {
        Euro("€", true), Dollars("$", true), BritishPounds("£", true), SwedishKrona("SEK", false);

        private final String sign;
        private final boolean prefix;

        Currency(String sign, boolean prefix) {
            this.sign = sign;
            this.prefix = prefix;
        }

        @Nullable
        public static Currency fromSign(String sign) {
            for (Currency currency : values()) {
                if (currency.sign.equals(sign)) {
                    return currency;
                }
            }
            return null;
        }

        public String getSign() {
            return sign;
        }

        @SuppressLint("DefaultLocale")
        public String toString(Long amount) {
            long characteristicPart = amount / 100;
            long fractionalPart = amount % 100;

            if (prefix) {
                return String.format("%s %d,%d", sign, characteristicPart, fractionalPart);
            } else {
                return String.format("%d,%d %s", characteristicPart, fractionalPart, sign);
            }
        }
    }

}
