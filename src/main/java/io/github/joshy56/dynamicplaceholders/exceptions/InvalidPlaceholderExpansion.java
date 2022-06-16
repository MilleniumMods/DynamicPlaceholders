package io.github.joshy56.dynamicplaceholders.exceptions;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 15/6/2022.
 */
public class InvalidPlaceholderExpansion extends Exception {
    public InvalidPlaceholderExpansion() {
        super("Invalid placeholder expansion");
    }

    public InvalidPlaceholderExpansion(String message) {
        super(message);
    }

    public InvalidPlaceholderExpansion(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidPlaceholderExpansion(Throwable cause) {
        super("Invalid placeholder expansion", cause);
    }
}
