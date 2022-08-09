package xland.mcmodbridge.fa2fomapper.api;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Maps;
import org.objectweb.asm.Type;

import javax.annotation.Nonnull;
import java.util.*;

public class Mapping {
    private final BiMap<Type, Type> classes;
    private final Map<NodeElement, String> fields;
    private final Map<NodeElement, String> methods;

    Mapping(BiMap<Type, Type> classes, Map<NodeElement, String> fields, Map<NodeElement, String> methods) {
        this.classes = classes;
        this.fields = fields;
        this.methods = methods;
    }

    public static class NodeElement {
        final Type owner;
        final String name;
        final Type desc;

        NodeElement(Type owner, String name, Type desc) {
            this.owner = owner;
            this.name = name;
            this.desc = desc;
        }

        public static NodeElement of(String owner, String name, String desc) {
            return new NodeElement(objType(owner),
                    name, Type.getType(desc));
        }

        public Type getOwner() {
            return owner;
        }

        public String getName() {
            return name;
        }

        public Type getDesc() {
            return desc;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NodeElement that = (NodeElement) o;
            return Objects.equals(owner, that.owner) && Objects.equals(name, that.name) && Objects.equals(desc, that.desc);
        }

        @Override
        public int hashCode() {
            return Objects.hash(owner, name, desc);
        }

        @Override
        public String toString() {
            return owner + "." + name + ':' + desc;
        }
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        public Builder addClass(String klass, String mapped) {
            classes.put(objType(klass), objType(mapped));
            return this;
        }

        public Builder addField(String klass, String field, String mapped, String desc) {
            fields.put(NodeElement.of(klass, field, desc), mapped);
            return this;
        }

        public Builder addMethod(String klass, String method, String mapped, String desc) {
            methods.put(NodeElement.of(klass, method, desc), mapped);
            return this;
        }

        public ElementsContainer withClass(String klass, String mapped) {
            classes.put(objType(klass), objType(mapped));
            return new ElementsContainer(klass);
        }

        public class ElementsContainer {
            private final String owner;

            ElementsContainer(String owner) {
                this.owner = owner;
            }

            public ElementsContainer ofField(String name, String mapped, String desc) {
                fields.put(NodeElement.of(owner, name, desc), mapped);
                return this;
            }

            public ElementsContainer ofMethod(String name, String mapped, String desc) {
                methods.put(NodeElement.of(owner, name, desc), mapped);
                return this;
            }

            public Builder done() {
                return Builder.this;
            }

            public ElementsContainer withClass(String klass, String mapped) {
                return Builder.this.withClass(klass, mapped);
            }

            public Builder addClass(String klass, String mapped) {
                return Builder.this.addClass(klass, mapped);
            }

            public Mapping build() {
                return Builder.this.build();
            }
        }

        Builder() {}

        public Mapping build() {
            return new Mapping(classes, fields, methods);
        }

        private final BiMap<Type, Type> classes = HashBiMap.create();
        final Map<NodeElement, String> fields = new HashMap<>();
        final Map<NodeElement, String> methods = new HashMap<>();
    }

    static Type objType(String s) {
        return Type.getType('L' + s + ';');
    }

    public BiMap<Type, Type> getClasses() {
        return Maps.unmodifiableBiMap(classes);
    }

    public Map<NodeElement, String> getFields() {
        return Collections.unmodifiableMap(fields);
    }

    public Map<NodeElement, String> getMethods() {
        return Collections.unmodifiableMap(methods);
    }

    @Nonnull
    public String mapClass(String name) {
        final Type type1;
        if (name.startsWith("[")) {
            type1 = Type.getType(name);
        } else
            type1 = objType(name);

        return mapType(type1).getInternalName();
    }

    public Type mapType(Type type) {
        switch (type.getSort()) {
            case Type.ARRAY:
            final int dimensions = type.getDimensions();
            Type elementType = type.getElementType();
            elementType = mapType(elementType);

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < dimensions; i++) sb.append('[');
            sb.append(elementType.getDescriptor());
            return Type.getType(sb.toString());

            case Type.OBJECT:
            return classes.getOrDefault(type, type);

            default:
            return type;
        }
    }

    public Type mapMethodType(Type type) {
        Type returnType = type.getReturnType();
        returnType = mapType(returnType);
        final Type[] types = Arrays.stream(type.getArgumentTypes())
                .map(this::mapType)
                .toArray(Type[]::new);
        return Type.getMethodType(returnType, types);
    }

    @Nonnull
    public NodeElement mapField(NodeElement element) {
        Type owner = mapType(element.owner);
        String name = fields.getOrDefault(element, element.name);
        Type desc = mapType(element.desc);
        return new NodeElement(owner, name, desc);
    }

    @Nonnull
    public NodeElement mapMethod(NodeElement element) {
        Type owner = mapType(element.owner);
        String name = methods.getOrDefault(element, element.name);
        Type desc = mapMethodType(element.desc);
        if (desc == null) desc = element.desc;
        return new NodeElement(owner, name, desc);
    }

    public static Mapping empty() {
        return new Mapping(ImmutableBiMap.of(), Collections.emptyMap(), Collections.emptyMap());
    }

    @Override
    public String toString() {
        return "Mapping{" +
                "classes=" + classes +
                ", fields=" + fields +
                ", methods=" + methods +
                '}';
    }
}
