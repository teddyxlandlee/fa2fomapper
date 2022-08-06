package xland.mcmodbridge.fa2fomapper.map;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.MethodRemapper;
import org.objectweb.asm.commons.Remapper;

public class F2FClassRemapper extends ClassRemapper {
    public F2FClassRemapper(ClassVisitor classVisitor, Remapper remapper) {
        super(Opcodes.ASM7, classVisitor, remapper);
    }

    @Override
    protected MethodVisitor createMethodRemapper(MethodVisitor methodVisitor) {
        return new F2FMethodRemapper(methodVisitor, remapper);
    }

    private static class F2FMethodRemapper extends MethodRemapper {
        protected F2FMethodRemapper(MethodVisitor methodVisitor, Remapper remapper) {
            super(Opcodes.ASM7, methodVisitor, remapper);
        }

        @Override
        public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
            Handle lambda = fromLambdaFactory(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
            name = lambda != null ?
                    remapper.mapMethodName(lambda.getOwner(), lambda.getName(), lambda.getDesc()) :
                    remapper.mapInvokeDynamicMethodName(name, descriptor);  // by default: unchanged
            for (int i = 0; i < bootstrapMethodArguments.length; i++) {
                bootstrapMethodArguments[i] = remapper.mapValue(bootstrapMethodArguments[i]);
            }

            mv.visitInvokeDynamicInsn(name,
                    remapper.mapMethodDesc(descriptor),
                    ((Handle) remapper.mapValue(bootstrapMethodHandle)),
                    bootstrapMethodArguments);
        }

        private static Handle fromLambdaFactory(String name, String desc, Handle bsm, Object... bsmArgs) {
            if (!isLambdaFactory(bsm)) return null;
            return new Handle(Opcodes.H_INVOKEINTERFACE, desc.substring(desc.lastIndexOf(')') + 2, desc.length() - 1), name, ((Type) bsmArgs[0]).getDescriptor(), true);
        }

        private static boolean isLambdaFactory(Handle bsm) {
            final String name = bsm.getName();
            final String desc = bsm.getDesc();
            return bsm.getTag() == Opcodes.H_INVOKESTATIC
                    && !bsm.isInterface()
                    && "java/lang/invoke/LambdaMetafactory".equals(bsm.getOwner())
                    && ("metafactory".equals(name)
                        //&& bsm.getDesc().equals()
                        && "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;"
                    .equals(desc)
                        || "altMetafactory".equals(name)
                        && "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite;"
                    .equals(desc)
                    );
        }
    }
}
