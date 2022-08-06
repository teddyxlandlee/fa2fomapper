package xland.mcmodbridge.fa2fomapper.api;

import xland.mcmodbridge.fa2fomapper.api.tiny.TinyUtils;

import java.io.BufferedReader;
import java.util.Collection;

public abstract class SimpleMappingContextProvider implements MappingContextProvider {
    protected final Collection<String> remappedClasses;

    protected SimpleMappingContextProvider(Collection<String> remappedClasses) {
        this.remappedClasses = remappedClasses;
    }

    @Override
    public Collection<String> remappedClasses() {
        return remappedClasses;
    }

    protected abstract BufferedReader mappingReader();

    @Override
    public Mapping getMapping(String supportedVersionId) {
        if ("".equals(supportedVersionId)) return Mapping.empty();
        return TinyUtils.read(mappingReader(), "base", supportedVersionId);
    }
}
