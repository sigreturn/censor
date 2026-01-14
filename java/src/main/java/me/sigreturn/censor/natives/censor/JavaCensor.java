package me.sigreturn.censor.natives.censor;

public class JavaCensor implements Censor {
    public static final CensorFactory FACTORY = JavaCensor::new;

    @Override
    public String censor(final String input) {
        // This path is currently a fallback for unsupported architectures, do nothing
        // for now.
        return input;
    }
}
