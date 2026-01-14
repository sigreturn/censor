package me.sigreturn.censor.natives.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;
import java.util.function.BooleanSupplier;

public class NativeConstraints {
    private static final boolean NATIVES_ENABLED = !Boolean.getBoolean("censor.natives-disabled");
    private static final boolean IS_AMD64;
    private static final boolean IS_AARCH64;
    private static final boolean CAN_GET_MEMORYADDRESS;
    private static final boolean IS_LINUX;
    private static final boolean IS_MUSL_LIBC;

    static {
        final ByteBuf test = Unpooled.directBuffer();
        try {
            CAN_GET_MEMORYADDRESS = test.hasMemoryAddress();
        } finally {
            test.release();
        }

        String osArch = System.getProperty("os.arch", "");
        IS_AMD64 = osArch.equals("amd64") || osArch.equals("x86_64");
        IS_AARCH64 = osArch.equals("aarch64") || osArch.equals("arm64");

        IS_LINUX = System.getProperty("os.name", "").equalsIgnoreCase("Linux");

        // Determine if we're using musl libc by invoking `ldd --version`.
        if (IS_LINUX) {
            boolean isMusl;
            try {
                final Process process = new ProcessBuilder("ldd", "--version")
                        .redirectErrorStream(true)
                        .start();
                process.waitFor();
                try (var reader = process.getInputStream()) {
                    final byte[] outputRaw = reader.readAllBytes();
                    String output = new String(outputRaw, StandardCharsets.UTF_8);
                    isMusl = output.contains("musl");
                }
            } catch (final Exception e) {
                isMusl = false;
            }
            IS_MUSL_LIBC = isMusl;
        } else {
            IS_MUSL_LIBC = false;
        }
    }

    static final BooleanSupplier NATIVE_BASE = () -> NATIVES_ENABLED && CAN_GET_MEMORYADDRESS;

    static final BooleanSupplier LINUX_X86_64 = () -> NATIVE_BASE.getAsBoolean()
            && IS_LINUX && IS_AMD64 && !IS_MUSL_LIBC;

    static final BooleanSupplier LINUX_X86_64_MUSL = () -> NATIVE_BASE.getAsBoolean()
            && IS_LINUX && IS_AMD64 && IS_MUSL_LIBC;

    static final BooleanSupplier LINUX_AARCH64 = () -> NATIVE_BASE.getAsBoolean()
            && IS_LINUX && IS_AARCH64 && !IS_MUSL_LIBC;

    static final BooleanSupplier LINUX_AARCH64_MUSL = () -> NATIVE_BASE.getAsBoolean()
            && IS_LINUX && IS_AARCH64 && IS_MUSL_LIBC;
}
