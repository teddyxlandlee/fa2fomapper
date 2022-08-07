package xland.mcmodbridge.fa2fomapper.api;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MappingContextProviders {
    private static final List<MappingContextProvider> PROVIDERS = new ArrayList<>();

    public static boolean addProvider(Class<? extends MappingContextProvider> cls) {
        MappingContextProvider provider;
        try {
            provider = (MappingContextProvider) MethodHandles.lookup()
                    .findConstructor(cls, MethodType.methodType(Void.class))
                    .invoke();
        } catch (Throwable e) {
            return false;
        }
        return PROVIDERS.add(provider);
    }

    public static List<MappingContextProvider> getProviders() {
        return Collections.unmodifiableList(PROVIDERS);
    }
}
