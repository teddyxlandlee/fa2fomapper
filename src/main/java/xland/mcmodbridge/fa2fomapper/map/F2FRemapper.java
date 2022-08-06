package xland.mcmodbridge.fa2fomapper.map;

import org.objectweb.asm.commons.Remapper;
import xland.mcmodbridge.fa2fomapper.api.Mapping;

public class F2FRemapper extends Remapper {
    private final Mapping mapping;

    public F2FRemapper(Mapping mapping) {
        this.mapping = mapping;
    }

    @Override
    public String mapMethodName(String owner, String name, String descriptor) {
        return mapping.getMethods().getOrDefault(Mapping.NodeElement.of(owner, name, descriptor), name);
    }

    @Override
    public String mapFieldName(String owner, String name, String descriptor) {
        return mapping.getFields().getOrDefault(Mapping.NodeElement.of(owner, name, descriptor), name);
    }

    @Override
    public String map(String internalName) {
        return mapping.mapClass(internalName);
    }
}
