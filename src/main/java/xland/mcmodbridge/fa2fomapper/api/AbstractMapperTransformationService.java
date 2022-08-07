package xland.mcmodbridge.fa2fomapper.api;

import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformer;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class AbstractMapperTransformationService
        implements cpw.mods.modlauncher.api.ITransformationService {
    public abstract String mapperName();
    public abstract MappingContextProvider mappingContext();

    /////////////////////////////////////////////

    @Nonnull
    @Override
    public String name() {
        return "fa2fomapper-" + mapperName();
    }

    @Override
    public void initialize(@Nonnull IEnvironment env) { }

    @Override
    public void beginScanning(@Nonnull IEnvironment env) { }

    @Override
    public void onLoad(@Nonnull IEnvironment env, @Nonnull Set<String> set) { }

    @Nonnull
    @Override
    @SuppressWarnings("rawtypes")
    public List<ITransformer> transformers() {
        return Collections.singletonList(new MapperTransformer(mappingContext()));
    }
}
