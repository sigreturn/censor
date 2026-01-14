package me.sigreturn.censor.natives;

public class NativeSetupException extends RuntimeException {

    public NativeSetupException() {
    }

    public NativeSetupException(final String message) {
        super(message);
    }

    public NativeSetupException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public NativeSetupException(final Throwable cause) {
        super(cause);
    }

    public NativeSetupException(final String message, final Throwable cause, boolean enableSuppression,
                                boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
