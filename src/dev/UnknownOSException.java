package dev;

public class UnknownOSException extends Exception {
    public UnknownOSException() {
        super("Your operating system is likely unsupported. If you believe otherwise, please raise an issue on github.");
    }
}
