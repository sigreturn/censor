package me.sigreturn.censor.natives.util;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public final class NativeCodeLoader<T> implements Supplier<T> {
    private final Variant<T> selected;

    NativeCodeLoader(final List<Variant<T>> variants) {
        this.selected = getVariant(variants);
    }

    @Override
    public T get() {
        return selected.constructed;
    }

    private static <T> Variant<T> getVariant(final List<Variant<T>> variants) {
        for (final Variant<T> variant : variants) {
            final T got = variant.get();

            if (got == null) {
                continue;
            }

            return variant;
        }

        throw new IllegalArgumentException("Can't find any suitable variants");
    }

    public String getLoadedVariant() {
        return selected.name;
    }

    static class Variant<T> {
        private Status status;
        private final Runnable setup;
        private final String name;
        private final Supplier<T> object;
        private T constructed;

        Variant(final BooleanSupplier possiblyAvailable, final Runnable setup, final String name, final T object) {
            this(possiblyAvailable, setup, name, () -> object);
        }

        Variant(final BooleanSupplier possiblyAvailable, final Runnable setup, final String name, final Supplier<T> object) {
            this.status =
                    possiblyAvailable.getAsBoolean() ? Status.POSSIBLY_AVAILABLE : Status.NOT_AVAILABLE;
            this.setup = setup;
            this.name = name;
            this.object = object;
        }

        public @Nullable T get() {
            if (status == Status.NOT_AVAILABLE || status == Status.SETUP_FAILURE) {
                return null;
            }

            // Make sure setup happens only once
            if (status == Status.POSSIBLY_AVAILABLE) {
                try {
                    setup.run();
                    constructed = object.get();
                    status = Status.SETUP;
                } catch (Exception e) {
                    status = Status.SETUP_FAILURE;
                    return null;
                }
            }

            return constructed;
        }
    }

    private enum Status {
        NOT_AVAILABLE,
        POSSIBLY_AVAILABLE,
        SETUP,
        SETUP_FAILURE
    }

    static final BooleanSupplier ALWAYS = () -> true;
}
