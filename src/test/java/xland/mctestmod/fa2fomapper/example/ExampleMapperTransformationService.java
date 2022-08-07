package xland.mctestmod.fa2fomapper.example;

import xland.mcmodbridge.fa2fomapper.api.AbstractMapperTransformationService;
import xland.mcmodbridge.fa2fomapper.api.MappingContextProvider;
import xland.mcmodbridge.fa2fomapper.api.SimpleMappingContextProvider;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Collections;

public class ExampleMapperTransformationService extends AbstractMapperTransformationService {
    @Override
    public String mapperName() {
        return "fa2fomapper-example-3";
    }

    @Override
    public MappingContextProvider mappingContext() {
        return new SimpleMappingContextProvider(Collections.singleton(
                "xland/mctestmod/fa2fomapper/example/TheTransformedClass")) {
            @Override
            protected BufferedReader mappingReader() {
                return new BufferedReader(new StringReader(
                        "v1\tbase\tforge17\n" +
                  "CLASS\txland/mctestmod/fa2fomapper/example/TheTransformedClass\tcom/example/Done"));
            }
        };
    }
}
