package xland.mctestmod.fa2fomapper.test;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.ClassRemapper;
import xland.mcmodbridge.fa2fomapper.api.Mapping;
import xland.mcmodbridge.fa2fomapper.api.tiny.TinyUtils;
import xland.mcmodbridge.fa2fomapper.map.F2FClassRemapper;
import xland.mcmodbridge.fa2fomapper.map.F2FRemapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MappingTest {
    Mapping mapping = TinyUtils.read(new BufferedReader(new StringReader(TinyMappingTest.MAPPING)),
            "base", "forge17");

    @Test
    public void test() throws IOException {
        final Path path = Paths.get(
                "/tmp/input-463fc9ae-a779-413c-8f16-a9d5d518a0c2.class");
        if (!Files.exists(path)) return;
        ClassReader input = new ClassReader(Files.newInputStream(path));
        ClassWriter out = new ClassWriter(3);
        ClassRemapper remapper = new F2FClassRemapper(out, new F2FRemapper(mapping));
        input.accept(remapper, 8);

        try (OutputStream os = Files.newOutputStream(Paths.get(
                "/tmp/export-463fc9ae-a779-413c-8f16-a9d5d518a0c2.class"))) {
            os.write(out.toByteArray());
        }
    }
}
