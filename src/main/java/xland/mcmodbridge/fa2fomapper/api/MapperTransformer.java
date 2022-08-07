package xland.mcmodbridge.fa2fomapper.api;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.tree.ClassNode;
import xland.mcmodbridge.fa2fomapper.SupportedPlatform;
import xland.mcmodbridge.fa2fomapper.map.F2FClassRemapper;
import xland.mcmodbridge.fa2fomapper.map.F2FRemapper;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

public class MapperTransformer implements ITransformer<ClassNode> {
    private static final Logger LOGGER = LogManager.getLogger("MapperTransformer");
    private final MappingContextProvider provider;

    public MapperTransformer(MappingContextProvider provider) {
        this.provider = provider;
    }

    private Mapping getMapping(String originalClassName) {
        if (provider.remappedClasses().contains(originalClassName)) {
            return provider.getMapping(SupportedPlatform.current().getId());
        }
        LOGGER.warn("Accessing invalid class: " + originalClassName);
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
        for (String cls : provider.remappedClasses())
            targets.add(Target.targetClass(cls));
        return targets;
    }
}
