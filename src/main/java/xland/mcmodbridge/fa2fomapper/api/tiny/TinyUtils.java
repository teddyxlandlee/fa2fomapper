package xland.mcmodbridge.fa2fomapper.api.tiny;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;
import xland.mcmodbridge.fa2fomapper.api.Mapping;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

public class TinyUtils {
    private static final Logger LOGGER = LogManager.getLogger("TinyUtils");

    public static Mapping read(BufferedReader reader, String from, String to) {
        if (Objects.equals(from, to)) return Mapping.empty();

        try {
            String headerLine = reader.readLine();

            if (headerLine == null)
                throw new EOFException();
            else if (headerLine.startsWith("v1\t")) {
                return readV1(reader, from, to, headerLine);
            } else {
                LOGGER.fatal("Unsupported mapping header: `" + headerLine + '`');
            }
        } catch (IOException e) {
            LOGGER.fatal("Can't read mapping: " + e);
        }
        return Mapping.empty();
    }

    public static Mapping readV1(BufferedReader reader, String from, String to,
                                 String headerLine) {
        List<String> headerList = Arrays.asList(headerLine.split("\t"));
        if (headerList.size() < 2) {
            LOGGER.fatal("Invalid mapping is provided - header too short");
            return Mapping.empty();
        }
        int fromIndex = headerList.indexOf(from) - 1;
        int toIndex = headerList.indexOf(to) - 1;

        if (fromIndex < 0 || toIndex < 0) {
            LOGGER.fatal("Invalid mapping is provided - can't find required namespace");
            return Mapping.empty();
        } else if (fromIndex == toIndex) {
            return Mapping.empty();
        }

        Mapping.Builder builder = Mapping.builder();

        //Map<String, String> preMap = fromIndex != 0 ? new HashMap<>() : null;
        @Nullable Mapping.Builder preMap = fromIndex != 0 ? Mapping.builder() : null;
        String line;

        List<Consumer<Mapping>> delayedTasks = new ArrayList<>();

        try {
            while ((line = reader.readLine()) != null) {
                String[] splitLine = line.split("\t");
                if (splitLine.length < 2) continue;
                String type = splitLine[0];

                if ("CLASS".equals(type)) {
                    builder.addClass(splitLine[1 + fromIndex], splitLine[1 + toIndex]);
                    if (preMap != null) preMap.addClass(splitLine[1], splitLine[1 + fromIndex]);
                } else {
                    boolean isMethod;

                    if ("FIELD".equals(type)) {
                        isMethod = false;
                    } else if ("METHOD".equals(type)) {
                        isMethod = true;
                    } else {
                        continue;
                    }

                    String owner = splitLine[1];
                    String name = splitLine[3 + fromIndex];
                    String desc = splitLine[2];
                    String nameTo = splitLine[3 + toIndex];

                    if (preMap == null) {
                        if (isMethod)
                            builder.addMethod(owner, name, nameTo, desc);
                        else
                            builder.addField(owner, name, nameTo, desc);
                    } else {
                        delayedTasks.add(mapping -> {
                            if (isMethod)
                                builder.addMethod(mapping.mapClass(owner),
                                        name, nameTo,
                                        mapping.mapMethodType(Type.getType(desc)).getDescriptor());
                            else
                                builder.addField(mapping.mapClass(owner),
                                        name, nameTo,
                                        mapping.mapMethodType(Type.getType(desc)).getDescriptor());
                        });
                    }
                }
            } // done `while`
            if (preMap != null) {
                final Mapping preMapReal = preMap.build();
                delayedTasks.forEach(c -> c.accept(preMapReal));
            }
        } catch (IOException e) {
            LOGGER.fatal("Can't read mapping: " + e);
            return Mapping.empty();
        }
        return builder.build();
    }
}
