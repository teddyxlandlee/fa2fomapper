package xland.mcmodbridge.fa2fomapper;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.tree.ClassNode;
import xland.mcmodbridge.fa2fomapper.api.Mapping;
import xland.mcmodbridge.fa2fomapper.api.MappingContextProvider;
import xland.mcmodbridge.fa2fomapper.map.F2FClassRemapper;
import xland.mcmodbridge.fa2fomapper.map.F2FRemapper;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.logging.Logger;

public class MapperTransformer implements ITransformer<ClassNode> {
    private static final Logger LOGGER = Logger.getLogger("MapperTransformer");
    private ServiceLoader<MappingContextProvider> providers;
    MapperTransformer() {

    }

    private Mapping getMapping(String originalClassName) {
        for (MappingContextProvider provider : providers) {
            if (provider.remappedClasses().contains(originalClassName)) {
                return provider.getMapping(SupportedPlatform.current().getId());
            }
        }
        LOGGER.warning("Accessing invalid class: " + originalClassName);
        return Mapping.empty();
    }

    @Nonnull
    @Override
    public ClassNode transform(@Nonnull ClassNode input, @Nonnull ITransformerVotingContext context) {
        Remapper remapper = new F2FRemapper(getMapping(input.name));
        ClassNode output = new ClassNode();
        ClassRemapper cr = new F2FClassRemapper(output, remapper);
        input.accept(cr);
        return output;
    }

    @Nonnull
    @Override
    public TransformerVoteResult castVote(@Nonnull ITransformerVotingContext context) {
        return TransformerVoteResult.YES;
    }

    @Nonnull
    @Override
    public Set<Target> targets() {
        final HashSet<Target> targets = new HashSet<>();
        initProviders();
        LOGGER.info(() -> "Providers: " + Iterables.size(providers));
        for (MappingContextProvider provider : providers) {
            for (String cls : provider.remappedClasses())
                targets.add(Target.targetClass(cls));
        }
        return targets;
    }

    private void initProviders() {
        if (providers == null) {
            providers = (ServiceLoader.load(MappingContextProvider.class));
            LOGGER.info("Initialing MapperTransformer");
        }
    }
}
