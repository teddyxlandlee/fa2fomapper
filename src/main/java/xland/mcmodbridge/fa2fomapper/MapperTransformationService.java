package xland.mcmodbridge.fa2fomapper;

import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import xland.mcmodbridge.fa2fomapper.api.MapperTransformer;
import xland.mcmodbridge.fa2fomapper.api.Mapping;
import xland.mcmodbridge.fa2fomapper.api.MappingContextProvider;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class MapperTransformationService implements ITransformationService {
    @Nonnull
    @Override
    public String name() {
        return "fa2fomapper";
    }

    @Override
    public void initialize(@Nonnull IEnvironment env) {

    }

    @Override
    public void beginScanning(@Nonnull IEnvironment env) {

    }

    @Override
    public void onLoad(@Nonnull IEnvironment env, @Nonnull Set<String> set) {

    }

    @Nonnull
    @Override
    @SuppressWarnings("rawtypes")
    public List<ITransformer> transformers() {
        return Collections.singletonList(new MapperTransformer(new MappingContextProvider() {
            @Override
            public Collection<String> remappedClasses() {
                return Collections.emptySet();
            }

            @Override
            public Mapping getMapping(String supportedVersionId) {
                return Mapping.empty();
            }
        }));
    }
}
