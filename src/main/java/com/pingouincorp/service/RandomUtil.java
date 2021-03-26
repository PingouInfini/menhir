package com.pingouincorp.service;

import org.apache.commons.lang3.RandomStringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

/**
 * Utility class for generating random Strings.
 */
public final class RandomUtil {

    private static final int DEF_COUNT = 20;

    private static final String ALPHA_NUM = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*_=+-/";

    private RandomUtil() {
    }

    /**
     * Generate a password.
     *
     * @return the generated password
     */
    public static String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(DEF_COUNT);
    }

    /**
     * Generate an activation key.
     *
     * @return the generated activation key
     */
    public static String generateActivationKey() {
        return RandomStringUtils.randomNumeric(DEF_COUNT);
    }

    /**
     * Generate a reset key.
     *
     * @return the generated reset key
     */
    public static String generateResetKey() {
        return RandomStringUtils.randomNumeric(DEF_COUNT);
    }

    /**
     * Génération d'un String selon la taille spécifiée en paramètre
     *
     * @param length
     * @return chaine de caratères aléatoires  de la taille désirée
     */
    public static String generateRandomString(final int length) {
        String result = "";
        for (int i = 0; i < length; i++) {
            result += ALPHA_NUM.charAt(new Random().nextInt(ALPHA_NUM.length()));
        }
        return result;
    }


    /**
     * Génération d'un entier entre 0 et 100
     *
     * @return entier [0-100]
     */
    public static int generateRandomInt() {
        return generateRandomInt(0, 100);
    }

    /**
     * Génération d'un entier entre les bornes spécifiées
     *
     * @param valeurMin
     * @param valeurMax
     * @return entier [valeurMin-valeurMax]
     */
    public static int generateRandomInt(final int valeurMin, final int valeurMax) {
        return valeurMin + (int) Math.round(Math.random() * (valeurMax-valeurMin));
    }

    /**
     * Renvoie un entier en fonction des chances spécifiées
     * ie: generateRandomIntByPercentage(0.5,0.3,0.2) => 0:50%, 1:30%, 2:20%
     * <p>
     * le total des chances ne doit pas spécifiquement etre égal à 0:
     * ie: generateRandomIntByPercentage(0.2,0.2,0.1) => 0:40%, 1:40%, 2:20%
     *
     * @param chanceByInt
     * @return
     */
    public static int generateRandomIntByPercentage(final double... chanceByInt) {
        int returnedValue = 0;
        double cumulChance = 0;
        double maxValueRandomDouble = 0;
        for (final double chance : chanceByInt) {
            maxValueRandomDouble += chance;
        }

        final double randomDouble = generateRandomDouble(0, maxValueRandomDouble);

        for (final double chance : chanceByInt) {
            cumulChance += chance;
            if (cumulChance < randomDouble) {
                returnedValue++;
            } else
                break;
        }
        return returnedValue;
    }

    /**
     * Génération d'un décimal entre 0 et 100
     *
     * @return décimal [0-100]
     */
    public static double generateRandomDouble() {
        return generateRandomDouble(0, 100);
    }

    /**
     * Génération d'un décimal entre les bornes spécifiées
     *
     * @param valeurMin
     * @param valeurMax
     * @return décimal [valeurMin-valeurMax]
     */
    public static double generateRandomDouble(final double valeurMin, final double valeurMax) {
        return new Random().nextFloat() * (valeurMax - valeurMin) + valeurMin;
    }

    public static double generateRandomDouble(final double valeurMin, final double valeurMax, int nbdigit) {
        return roundAvoid((new Random().nextFloat() * (valeurMax - valeurMin) + valeurMin), nbdigit);
    }

    public static double roundAvoid(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

    /**
     * Génération d'un boolean aléatoire (50% de chance de true)
     *
     * @return boolean
     */
    public static boolean generateRandomBoolean() {
        return generateRandomBoolean(0.5);
    }

    /**
     * Génération d'un boolean aléatoire (50% de chance de true)
     *
     * @param chanceOfTrue décimal devant être compris entre [0-1]
     * @return
     */
    public static boolean generateRandomBoolean(final double chanceOfTrue) {
        return (Math.random() < chanceOfTrue);
    }

    /**
     * Génération d'une date aléatoire au format "dd/MM/yyyy hh:mm:ss:SSS"
     * entre le 01/01/1900 et le 31/12/2018
     *
     * @return Date
     */
    public static Date generateRandomDate() {
        final Random random = new Random();
        final int minDay = (int) LocalDate.of(1000, 1, 1).toEpochDay();
        final int maxDay = (int) LocalDate.of(2020, 12, 31).toEpochDay();
        final long randomDay = (long) (minDay + random.nextInt((maxDay - minDay)));

        return Date.from(LocalDate.ofEpochDay(randomDay).
            atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Génération d'une date aléatoire au format "dd/MM/yyyy hh:mm:ss:SSS"
     * entre le 01/01/1900 et le 31/12/2018
     *
     * @return String représentant une date au format "dd/MM/yyyy hh:mm:ss:SSS"
     */
    public static String generateRandomDateToString() {
        final TimeZone tz = TimeZone.getTimeZone("UTC");
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(tz);
        return df.format(generateRandomDate());
    }

    public static String generateRandomDateToString(String simpleDateFormat) {
        final TimeZone tz = TimeZone.getTimeZone("UTC");
        final DateFormat df = new SimpleDateFormat(simpleDateFormat);
        df.setTimeZone(tz);
        return df.format(generateRandomDate());
    }

    public static String castDateToString(final Date date) {
        final TimeZone tz = TimeZone.getTimeZone("UTC");
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(tz);
        return df.format(date);
    }
}
