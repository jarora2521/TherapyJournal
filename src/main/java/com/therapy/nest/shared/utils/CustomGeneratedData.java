package com.therapy.nest.shared.utils;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.RandomBasedGenerator;
import org.apache.commons.lang3.RandomStringUtils;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;


public final class CustomGeneratedData {

    private static final Random RANDOM = new SecureRandom();
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static String GenerateUniqueID() {
        UUID uuid = Generators.timeBasedGenerator().generate();
        return uuid.toString().replace("-", "");
    }

    public static String generateClientId() {
        UUID uuid = Generators.timeBasedGenerator().generate();
        return uuid.toString();
    }

    public static String generateClientSecret() {
        RandomBasedGenerator uuid = Generators.randomBasedGenerator();
        return uuid.toString();
    }

    public static String generateClientSalt() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public static Long GenerateCertificateId() {
        return System.currentTimeMillis();
    }

    public static long getTimeStamp() {
        return new Date().getTime();
    }

    public static LocalDate stringToLocalDate(String formDate) {
        return LocalDate.parse(formDate);
    }

    public static LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static LocalDateTime convertToLocalDateTimeViaMilisecond(Date dateToConvert) {
        return Instant.ofEpochMilli(dateToConvert.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static LocalDateTime getLocalDate(long valueToConvert) {
        Date currentDate = new Date(valueToConvert);
        return currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static LocalDate stringToDate(String dateToConvert) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return LocalDate.parse(dateToConvert, formatter);
    }

    public static LocalDate stringToDateFormatA(String dateToConvert) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(dateToConvert, formatter);
    }

    public static LocalDate stringToLocalDateFormat(String dateToConvert) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDate.parse(dateToConvert, formatter);
    }

    public static LocalDate stringToDateFormat(String dateToConvert) throws ParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[dd-MM-yyyy][yyyy-MM-dd]");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        LocalDate receivedDate = LocalDate.parse(dateToConvert, formatter);
        String returnDateStr = receivedDate.format(formatter2);

        return LocalDate.parse(returnDateStr, formatter2);
    }

    public static LocalDateTime getLocalDateTimeFromString(String dateTime) {
        String format = "yyyy-MM-dd HH:mm:ss.SSS";
        //Expected String Format >>> "2022-07-19T21:00:00.000Z" Or "2022-07-19T21:00:00.000"

        if (dateTime.indexOf("T") != -1) {
            dateTime = dateTime.replace('T', ' ');
        }
        if (dateTime.indexOf("0Z") != -1) {
            dateTime = dateTime.replaceAll("0Z", "0");
        }

        return getLocalDateTimeFromString(format, dateTime);
    }

    public static List<?> convertObjectToList(Object obj) {
        List<?> list = new ArrayList<Object>();
        if (obj.getClass().isArray()) {
            list = Arrays.asList((Object[]) obj);
        } else if (obj instanceof Collection) {
            list = new ArrayList<Object>((Collection<?>) obj);
        }
        return list;
    }

    public static boolean isObjectHashMap(Object source) {
        try {
            HashMap<String, Object> result = (HashMap<String, Object>) source;
            return true;
        } catch (ClassCastException e) {
        }

        return false;
    }

    public static boolean isObjectString(Object source) {
        try {
            String result = (String) source;
            return true;
        } catch (ClassCastException e) {
        }

        return false;
    }

    public static String generatePassword(int length) {
        StringBuilder returnValue = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return new String(returnValue);
    }

    public static String getPassword() {
        return RandomStringUtils.random(8, true, true);
    }

    public static String harshMethod(String string) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(string.getBytes());

        byte[] byteData = md.digest();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xFF) + 256, 16).substring(1));
        }

        return sb.toString();
    }

    // Parsing DateTime String in "yyyy-MM-dd HH:mm:ss.SSS" Format
    public static LocalDateTime getLocalDateTimeFromString(String format, String dateTime) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        LocalDateTime newDateTime = LocalDateTime.parse(dateTime, formatter);

        return newDateTime;
    }
}
