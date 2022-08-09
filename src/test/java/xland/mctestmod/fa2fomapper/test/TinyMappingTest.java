package xland.mctestmod.fa2fomapper.test;

import org.junit.jupiter.api.Test;
import xland.mcmodbridge.fa2fomapper.api.Mapping;
import xland.mcmodbridge.fa2fomapper.api.tiny.TinyUtils;

import java.io.BufferedReader;
import java.io.StringReader;

public class TinyMappingTest {
    @Test
    public void test() {
        Mapping mapping = TinyUtils.read(new BufferedReader(new StringReader(MAPPING)),
                "base", "forge17");
        //String type = mapping.mapClass("net/minecraft/class_1078");
        System.out.println(mapping);
        System.out.println("=====================================");
        System.out.println(mapping.mapMethod(Mapping.NodeElement.of(
                "net/minecraft/class_1078", /*getOrDefault*/"method_4679", "(Ljava/lang/String;)Ljava/lang/String;")));
    }

    static final String MAPPING = "v1\tbase\tforge16\tforge17\n" +
            "CLASS\tnet/minecraft/class_2960\tnet/minecraft/util/ResourceLocation\tnet/minecraft/resources/ResourceLocation\n" +
            "METHOD\tnet/minecraft/class_2960\t()Ljava/lang/String;\tmethod_12836\tfunc_110624_b\tm_135827_\n" +
            "METHOD\tnet/minecraft/class_2960\t()Ljava/lang/String;\tmethod_12832\tfunc_110623_a\tm_135815_\n" +
            "CLASS\tnet/minecraft/class_1078\tnet/minecraft/client/resources/ClientLanguageMap\tnet/minecraft/client/resources/language/ClientLanguage\n" +
            "FIELD\tnet/minecraft/class_1078\tLjava/util/Map;\tfield_5330\tfield_239495_c_\tf_118910_\n" +
            "METHOD\tnet/minecraft/class_1078\t(Ljava/lang/String;)Ljava/lang/String;\tmethod_4679\tfunc_230503_a_\tm_6834_\n";
}
