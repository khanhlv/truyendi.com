package com.ttruyen.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class StringUtil {

    public static String stripAccents(String input) {
        String str = StringUtils.trim(input).toLowerCase();

        str = StringUtils.stripAccents(str);
        str = str.replaceAll("đ", "d");
        str = str.replaceAll("[^\\p{Alpha}\\p{Digit}]+", " ");

        return str;
    }

    public static String stripAccents(String input, CharSequence character) {
        String str = StringUtils.trim(input).toLowerCase();

        str = StringUtils.stripAccents(str);
        str = str.replaceAll("đ", "d");
        str = str.replaceAll("[^\\p{Alpha}\\p{Digit}]+", character.toString());
        str = str.replaceAll("-$", "");
        str = str.replaceAll("^-", "");
        return str;
    }

    public static void main(String[] args) throws IOException {
        String ab = "Chương 93";
        System.out.println(ab.split(":").length);

        InputStream in = StringUtil.class.getClassLoader().getResourceAsStream("config.properties");

        Properties properties = new Properties();
        properties.load(new InputStreamReader(in));

        System.out.println(properties.getProperty("dataSource.user"));
    }
}
