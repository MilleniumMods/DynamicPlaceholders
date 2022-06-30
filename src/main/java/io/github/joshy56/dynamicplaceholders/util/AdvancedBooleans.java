package io.github.joshy56.dynamicplaceholders.util;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 28/6/2022.
 */
public final class AdvancedBooleans {

    public static boolean xor(boolean optionOne, boolean optionTwo) {
        return (!optionOne || !optionTwo) && (optionOne || optionTwo);
    }
}
