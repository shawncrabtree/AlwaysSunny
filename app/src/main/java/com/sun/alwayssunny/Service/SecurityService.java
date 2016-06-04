package com.sun.alwayssunny.Service;

import android.os.Bundle;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Brett on 6/3/2016.
 */
public class SecurityService {

    /*
     * encryptLatLong()
     * encrypts the user's location data to send to the cloud server.
     */
    public static String encryptLatLong(Double lat, Double lng) throws JSONException, GeneralSecurityException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("lat", lat);
        jsonObject.put("lng", lng);

        String toEncrypt = jsonObject.toString();

        String encoded = Base64.encodeToString(toEncrypt.getBytes(), Base64.DEFAULT);

        return encoded;
    }
}
