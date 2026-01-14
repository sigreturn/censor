package me.sigreturn.censor.natives.censor;

public class LibcensorCensor implements Censor {
    public static final CensorFactory FACTORY = LibcensorCensor::new;

    @Override
    public String censor(final String input) {
        return NativeLibcensor.censor(input);
    }
}
