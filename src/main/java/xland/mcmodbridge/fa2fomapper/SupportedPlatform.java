package xland.mcmodbridge.fa2fomapper;

import javax.annotation.Nullable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Arrays;
import java.util.logging.Logger;

public enum SupportedPlatform {
    FORGE_116(32, 37, "forge16"),
    FORGE_117(37, Integer.MAX_VALUE, "forge17"),
    OTHERS,
    ;
    private final int minInclusive, maxExclusive;
    private final boolean isForge;
    private final String id;

    SupportedPlatform(int minInclusive, int maxExclusive, String id) {
        this.minInclusive = minInclusive;
        this.maxExclusive = maxExclusive;
        this.isForge = true;
        this.id = id;
    }

    SupportedPlatform() {
        this.minInclusive = Integer.MIN_VALUE;
        this.maxExclusive = Integer.MAX_VALUE;
        this.isForge = false;
        this.id = "";
    }

    public boolean isForge() {
        return isForge;
    }

    public boolean includes(int version) {
        return minInclusive <= version && version < maxExclusive;
    }

    public static SupportedPlatform current() {
        if (current == null) {
            synchronized (SupportedPlatform.class) {
                if (current == null) {
                    final String forgeVersion = VersionGetter.getForgeVersion();
                    if (forgeVersion != null) {
                        int version;
                        try {
                            version = Integer.parseInt(forgeVersion.split("\\.", 2)[0]);
                            current = Arrays.stream(SupportedPlatform.values())
                                    .filter(SupportedPlatform::isForge)
                                    .filter(p -> p.includes(version))
                                    .findFirst()
                                    .orElseGet(SupportedPlatform::others);
                        } catch (NumberFormatException e) {
                            current = others();
                        }
                    } else {
                        current = others();
                    }
                }
            }
        }
        return current;
    }

    public String getId() {
        return id;
    }

    private static SupportedPlatform others() {
        return OTHERS;
    }

    private static volatile SupportedPlatform current;

    private static class VersionGetter {
        private static @Nullable String getForgeVersion() {
            try {
                Class<?> clazz = Class.forName("net.minecraftforge.fml.loading.StringSubstitutor");
                MethodHandle mh = MethodHandles.lookup().findStatic(clazz, "replace",
                        MethodType.fromMethodDescriptorString("(Ljava/lang/String;Lnet/minecraftforge/fml/loading/moddiscovery/ModFile;)Ljava/lang/String;",
                                VersionGetter.class.getClassLoader()));
                return (String) mh.invoke("global.forgeVersion", null);
            } catch (ClassNotFoundException e) {
                return null;
            } catch (NoSuchMethodException | IllegalAccessException e) {
                Logger.getLogger("fa2fomapper.VersionGetter").warning(() ->
                        "Can't get forge version: " + e);
                return null;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }
}
