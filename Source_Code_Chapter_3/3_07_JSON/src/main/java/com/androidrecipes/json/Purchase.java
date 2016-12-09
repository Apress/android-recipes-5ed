package com.androidrecipes.json;

import android.annotation.SuppressLint;
import android.support.annotation.Nullable;

import com.squareup.moshi.Json;

import java.util.Date;
import java.util.UUID;

@SuppressWarnings("WeakerAccess")
public class Purchase {
    @Json(name = "purchase_id")
    public UUID id;
    public Date timestamp;
    @Json(name = "product_name")
    public String productName;
    @Json(name = "product_id")
    public String productId;
    @Json(name = "delivery_address")
    public Address deliveryAddress;
    public Integer units;
    @Json(name = "purchase_amount")
    public Long amount;
    public Currency currency;


    @SuppressWarnings("WeakerAccess")
    public static class Address {
        public String name;
        public String street;
        public String zip;
        public String city;
        public String state;
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
