package io.github.humbleui.skija.impl;

import java.lang.ref.Cleaner;
import java.util.Objects;

public final class Cleanable {
    private static final Cleaner cleanerInstance = Cleaner.create();

    public static Cleanable register(Object obj, Runnable action) {
        Objects.requireNonNull(obj);
        Objects.requireNonNull(action);
        return new Cleanable(cleanerInstance.register(obj, action));
    }

    private final Cleaner.Cleanable key;

    private Cleanable(Cleaner.Cleanable key) {
        this.key = key;
    }

    public void clean() {
        if (this.key != null) {
            this.key.clean();
        }
    }
}
