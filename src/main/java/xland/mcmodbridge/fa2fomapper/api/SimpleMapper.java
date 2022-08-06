package xland.mcmodbridge.fa2fomapper.api;

import com.google.common.collect.BiMap;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * A simple mapper that only transforms JVM-required things.
 * Signatures, inner classes and other data will not be transformed.
 * <br>
 * This means that reflective options may have risk querying.
 */
@Deprecated
public class SimpleMapper {
    private final Mapping mapping;

    public SimpleMapper(Mapping mapping) {
        this.mapping = mapping;
    }

    public ClassNode transform(ClassNode node) {
        node.name = mapClass(node.name);
        node.superName = mapClass(node.superName);
        if (node.interfaces != null)
            node.interfaces = node.interfaces.stream().map(this::mapClass)
                .collect(Collectors.toList());
        mapAnnotations(node.visibleAnnotations);
        mapAnnotations(node.invisibleAnnotations);
        mapAnnotations(node.visibleTypeAnnotations);
        mapAnnotations(node.invisibleTypeAnnotations);
        node.nestHostClass = mapClass(node.nestHostClass);
        if (node.nestMembers != null)
            node.nestMembers = node.nestMembers.stream().map(this::mapClass)
                    .collect(Collectors.toList());
        node.fields.forEach(f -> {
            final Mapping.NodeElement e = mapField(Mapping.NodeElement.of(node.name, f.name, f.desc));
            f.name = e.getName();
            f.desc = e.getDesc().getDescriptor();
            mapAnnotations(f.visibleAnnotations);
            mapAnnotations(f.invisibleAnnotations);
            mapAnnotations(f.visibleTypeAnnotations);
            mapAnnotations(f.invisibleTypeAnnotations);
        });
        node.methods.forEach(m -> {
            final Mapping.NodeElement e = mapMethod(Mapping.NodeElement.of(node.name, m.name, m.desc));
            m.name = e.getName();
            m.desc = e.getDesc().getDescriptor();
            m.exceptions = m.exceptions.stream().map(this::mapClass)
                    .collect(Collectors.toList());
            mapAnnotations(m.visibleAnnotations);
            mapAnnotations(m.invisibleAnnotations);
            mapAnnotations(m.visibleTypeAnnotations);
            mapAnnotations(m.invisibleTypeAnnotations);
            mapAnnotations(m.visibleLocalVariableAnnotations);
            mapAnnotations(m.invisibleLocalVariableAnnotations);
            mapObject(m.annotationDefault, o2 -> m.annotationDefault = o2);
            for (List<AnnotationNode> a : m.visibleParameterAnnotations)
                mapAnnotations(a);
            for (List<AnnotationNode> a : m.invisibleParameterAnnotations)
                mapAnnotations(a);
            m.localVariables.forEach(v -> v.desc = mapFieldType(v.desc));
            m.tryCatchBlocks.forEach(b -> {
                if (b.type != null)
                    b.type = mapClass(b.type);
                mapAnnotations(b.visibleTypeAnnotations);
                mapAnnotations(b.invisibleTypeAnnotations);
            });
        });
        //TODO
        throw new UnsupportedOperationException("TODO");
    }

    private void mapAnnotations(List<? extends AnnotationNode> annotations) {
        annotations.forEach(this::mapAnnotation);
    }

    private void mapInstructions(InsnList instructions) {
        for (AbstractInsnNode node : instructions) {

        }
    }

    private void mapAnnotation(AnnotationNode node) {
        node.desc = mapFieldType(node.desc);
        if (node.values == null) return;
        mapAnnotationArgs(node.values);
    }

    private void mapObject(Object o, Consumer<Object> c) {
        if (o instanceof Type) {
            final Type mapped = getMapping().getClasses().get(((Type) o));
            if (mapped != null) c.accept(mapped);
        } else if (o instanceof String[]) {
            final String[] s = ((String[]) o);
            Type t = Type.getType(s[0]);
            final Mapping.NodeElement e = mapField(new Mapping.NodeElement(t, s[1], t));
            s[0] = e.getOwner().getDescriptor();
            s[1] = e.getName();
        } else if (o instanceof AnnotationNode) {
            mapAnnotation(((AnnotationNode) o));
        }
    }

    private void mapAnnotationArgs(@Nonnull List<Object> list) {
        final int size = list.size();
        for (int i = 0; i < size; i++) {
            final Object o = list.get(i);
            int finalI = i;
            mapObject(o, k -> list.set(finalI, k));
        }
    }

    public Mapping getMapping() {
        return mapping;
    }

    public String mapClass(String s) {
        return getMapping().mapClass(s);
    }

    public String mapFieldType(String s) {
        final BiMap<Type, Type> classes = getMapping().getClasses();
        final Type type = classes.get(Type.getType(s));
        if (type == null) return s;
        return type.getDescriptor();
    }

    public Mapping.NodeElement mapField(Mapping.NodeElement e) {
        return getMapping().mapField(e);
    }

    public Mapping.NodeElement mapMethod(Mapping.NodeElement e) {
        return getMapping().mapMethod(e);
    }
}
