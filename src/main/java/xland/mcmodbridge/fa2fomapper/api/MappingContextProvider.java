package xland.mcmodbridge.fa2fomapper.api;

import java.util.Collection;

public interface MappingContextProvider {
    Collection<String> remappedClasses();

    /**
     * @param supportedVersionId can be one of: <br>
     * {@code "forge16", "forge17", ""}
     */
    Mapping getMapping(String supportedVersionId);
}
