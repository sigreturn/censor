package me.sigreturn.censor.natives.util;

import me.sigreturn.censor.natives.NativeSetupException;
import me.sigreturn.censor.natives.censor.CensorFactory;
import me.sigreturn.censor.natives.censor.JavaCensor;
import me.sigreturn.censor.natives.censor.LibcensorCensor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class Natives {
    private Natives() {
        throw new AssertionError();
    }

    private static Runnable copyAndLoadNative(final String path) {
        return () -> {
            try {
                final InputStream nativeLib = Natives.class.getResourceAsStream(path);
                if (nativeLib == null) {
                    throw new IllegalStateException("Native library " + path + " not found.");
                }

                final Path tempFile = createTemporaryNativeFilename(path.substring(path.lastIndexOf('.')));
                Files.copy(nativeLib, tempFile, StandardCopyOption.REPLACE_EXISTING);
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    try {
                        Files.deleteIfExists(tempFile);
                    } catch (final IOException ignored) {
                        // Well, it doesn't matter...
                    }
                }));

                try {
                    System.load(tempFile.toAbsolutePath().toString());
                } catch (final UnsatisfiedLinkError e) {
                    throw new NativeSetupException("Unable to load native " + tempFile.toAbsolutePath(), e);
                }
            } catch (final IOException e) {
                throw new NativeSetupException("Unable to copy natives", e);
            }
        };
    }

    private static Path createTemporaryNativeFilename(String ext) throws IOException {
        final String temporaryFolderPath = System.getProperty("censor.natives-tmpdir");

        if (temporaryFolderPath != null) {
            return Files.createTempFile(Path.of(temporaryFolderPath), "native-", ext);
        } else {
            return Files.createTempFile("native-", ext);
        }
    }

    public static final NativeCodeLoader<CensorFactory> censor = new NativeCodeLoader<>(
            List.of(
                    new NativeCodeLoader.Variant<>(NativeConstraints.LINUX_X86_64,
                            copyAndLoadNative("/linux_x86_64/libcensor.so"),
                            "libcensor (Linux x86_64)",
                            LibcensorCensor.FACTORY),

                    new NativeCodeLoader.Variant<>(NativeConstraints.WINDOWS_X86_64,
                            copyAndLoadNative("/windows_x86_64/censor.dll"),
                            "libcensor (Windows x86_64)",
                            LibcensorCensor.FACTORY),

                    new NativeCodeLoader.Variant<>(NativeCodeLoader.ALWAYS, () -> {
                    }, "Java", JavaCensor.FACTORY)
            )
    );
}
