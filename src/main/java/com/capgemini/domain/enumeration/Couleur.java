package com.capgemini.domain.enumeration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * The Couleur enumeration.
 */
public enum Couleur {
    AUTRE, BLANC, BLOND, BRUN, CHATAIN, ROUX;

    private static final List<Couleur> VALUES =
        Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    public static Couleur randomCouleur()  {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }
}
