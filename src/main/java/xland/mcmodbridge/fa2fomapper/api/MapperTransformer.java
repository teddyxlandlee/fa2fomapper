package xland.mcmodbridge.fa2fomapper.api;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.tree.ClassNode;
import xland.mcmodbridge.fa2fomapper.SupportedPlatform;
import xland.mcmodbridge.fa2fomapper.map.F2FClassRemapper;
import xland.mcmodbridge.fa2fomapper.map.F2FRemapper;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

        if ("true".equals(System.getProperty("fa2fomapper.export"))) {
            new Thread(() -> {
                LOGGER.info("Transformed {}", output.name);
                final Path path = Paths.get(".fa2fomapper", output.name + ".class");
                try {
                    Files.createDirectories(path.getParent());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                ClassWriter cw = new ClassWriter(3);
                output.accept(cw);
                try(OutputStream os = Files.newOutputStream(path)) {
                    os.write(cw.toByteArray());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                LOGGER.warn("Dumped {} (was {}) into {}", output.name, input.name, path.toAbsolutePath());
            }, "fa2fomapper-dumpclass").start();
        }
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
