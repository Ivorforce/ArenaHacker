package game;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * So here's the deal.
 * Some library, I think slick2d, adds the natives to the jar.
 * That's nice because we don't need to download them from elsewhere.
 * It's also annoying because lwjgl cannot load them from inside the jar.
 * <p>
 * So we extract them to the folder they come in here. Luckily, we can be
 * reasonably sure the jar is inside a suitable folder because of the readme
 * and launch scripts.
 */
public class LWJGLNativesHelper {
    public static final String[] nativeFilenames = {
        "jinput-dx8_64.dll",
        "jinput-dx8.dll",
        "jinput-raw_64.dll",
        "jinput-raw.dll",
        "lwjgl.dll",
        "lwjgl64.dll",
        "OpenAL32.dll",
        "OpenAL64.dll",

        "liblwjgl.so",
        "liblwjgl64.so",
        "libopenal.so",
        "libopenal64.so",

        "libjinput-osx.jnilib",
        "liblwjgl.dylib",
        "openal.dylib",

        "libjinput-linux.so",
        "libjinput-linux64.so",
        "liblwjgl.so",
        "liblwjgl64.so",
        "libopenal.so",
        "libopenal64.so",
    };

    public static final Path nativesDir = Path.of("lwjgl-natives").toAbsolutePath();

    public static void copyOutsideAndLoad() {
        try {
            Files.createDirectory(nativesDir);
        }
        catch (FileAlreadyExistsException ignored) {}
        catch (IOException e) {
                throw new RuntimeException(e);
            }

        for (String nativeFilename : nativeFilenames) {
            InputStream stream = LWJGLNativesHelper.class.getClassLoader().getResourceAsStream(nativeFilename);
            assert stream != null;

            try {
                Files.copy(stream, nativesDir.resolve(nativeFilename));
            }
            catch (FileAlreadyExistsException ignored) {} catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        System.setProperty("org.lwjgl.librarypath", nativesDir.toString());
    }
}
