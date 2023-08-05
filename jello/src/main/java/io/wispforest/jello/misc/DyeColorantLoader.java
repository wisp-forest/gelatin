package io.wispforest.jello.misc;

import com.google.gson.*;
import io.wispforest.gelatin.common.util.VersatileLogger;
import io.wispforest.gelatin.dye_entries.variants.DyeableVariantManager;
import io.wispforest.gelatin.dye_registry.DyeColorant;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import io.wispforest.jello.Jello;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.block.MapColor;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DyeColorantLoader {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final VersatileLogger LOGGER = new VersatileLogger("DyeColorantLoader");

    public static Map<Identifier, ColorData> loadedColorData = new LinkedHashMap<>();

    public static Map<String, List<String>> rawConversionData = new LinkedHashMap<>();
    public static Map<String, String> tempConversionMapLookup = new HashMap<>();

    //--------------------

    private static final Map<String, String> conversionMapLookup = new HashMap<>();

    public static void loadFromJson() {
        LOGGER.restartTimer();

        Map<Integer, Path> versions = new Int2ObjectArrayMap<>();

        Stream<Path> files;

        Optional<ModContainer> possibleContainer = FabricLoader.getInstance().getModContainer("jello");

        Optional<Path> folderPath;

        if(possibleContainer.isPresent()){
            ModContainer container = possibleContainer.get();

            folderPath = container.findPath("data/jello/other/");

            if(folderPath.isPresent()){
                List<Path> result;

                try {
                    result = Files.walk(folderPath.get())
                            .filter(Files::isRegularFile)
                            .collect(Collectors.toList());
                } catch (IOException e){
                    LOGGER.failMessage(e.toString());

                    throw new IllegalStateException("The attempted to gather all revisions of the Jello Color Database has gone wrong!");
                }

                files = result.stream();
            } else {
                throw new IllegalStateException("The Required Folder path for the Jello Color Database could not be located, something has gone very wrong!");
            }
        } else {
            throw new IllegalStateException("Could not locate the Jello Mod container from fabric, something has gone very wrong!");
        }

        files.forEach(uri -> {
            String fileName = uri.getFileName().toString();

            String[] nameSplit = fileName.split("colorDatabase_rev");

            try {
                Integer revisionNumber = Integer.parseInt(nameSplit[1].replace(".json", ""));

                if(versions.containsKey(revisionNumber)){
                    LOGGER.failMessage("Seems that there is another colorDatabase Revision with the same number and such will be ignored!");
                } else {
                    versions.put(revisionNumber, uri);
                }
            } catch (NumberFormatException e){
                LOGGER.failMessage("A ColorDatabase Reversion number was not able to be parsed and such will be ignored!");
                LOGGER.failMessage(fileName);
            }
        });

        List<Path> versionsOrdered = new ArrayList<>();

        for (Map.Entry<Integer, Path> entry : versions.entrySet()) {
            Integer integer = entry.getKey();
            Path path = entry.getValue();

            if(integer >= versionsOrdered.size()){
                versionsOrdered.add(path);
            } else {
                versionsOrdered.add(integer, path);
            }
        }

        int doubleDuplicateEntries = 0;

        try {
            if(versionsOrdered.isEmpty()) versionsOrdered.add(folderPath.get().resolve("data/jello/other/colorDatabase_rev0.json"));

            JsonObject databaseJson = GSON.fromJson(new InputStreamReader(Files.newInputStream(versionsOrdered.get(versionsOrdered.size() - 1))), JsonObject.class);

            for (JsonElement color : databaseJson.getAsJsonArray("colors")) {
                ColorData data = GSON.fromJson(color, ColorData.class);

                Identifier colorId = data.getColorId();

                if (DyeColorantRegistry.DYE_COLOR.containsId(new Identifier(colorId.getPath()))){
                    doubleDuplicateEntries++;

                    continue;
                }

                loadedColorData.put(colorId, data);

                DyeColorant currentDyeColor = DyeColorantRegistry.registerDyeColor(colorId, MapColor.CLEAR, data.colorInDecimal());
                DyeableVariantManager.createVariantContainer(currentDyeColor);
            }

            LOGGER.stopTimerPrint("It seems that the registry filling took ");
            LOGGER.infoMessage("Total amount of registered dyes from json are " + DyeColorantRegistry.DYE_COLOR.size());

            LOGGER.restartTimer();

            ListIterator<Path> fileIterator = versionsOrdered.listIterator(versionsOrdered.size());

            while (fileIterator.hasPrevious()){
                Path file = fileIterator.previous();

                databaseJson = GSON.fromJson(new InputStreamReader(Files.newInputStream(file)), JsonObject.class);

                if(!databaseJson.has("conversionData")) continue;

                JsonObject conversionJson = databaseJson.getAsJsonObject("conversionData");

                for (Map.Entry<String, JsonElement> entry : conversionJson.entrySet()) {
                    JsonArray array = entry.getValue().getAsJsonArray();

                    List<String> values = new ArrayList<>();

                    array.forEach(e -> values.add(e.getAsString()));

                    String outputKey = entry.getKey();

                    if(tempConversionMapLookup.containsKey(outputKey)){ //Since we have found that this is an input key somewhere, we will add it and the corresponding values
                        String newOutputKey = tempConversionMapLookup.get(outputKey);

                        values.add(outputKey);

                        rawConversionData.get(newOutputKey).addAll(values);

                        values.forEach(inputKey -> tempConversionMapLookup.put(inputKey, newOutputKey));

                        tempConversionMapLookup.put(outputKey, newOutputKey);
                    } else { //Make conversion mapping for unique conversion
                        values.forEach(inputKey -> tempConversionMapLookup.put(inputKey, outputKey));

                        rawConversionData.put(outputKey, values);
                    }
                }
            }

            rawConversionData.forEach((output, inputs) -> inputs.forEach(input -> conversionMapLookup.put(input, output)));

            tempConversionMapLookup.clear();

            LOGGER.stopTimerPrint("It seems that the Conversion Mapping creation took ");
            LOGGER.infoMessage("Total amount of Mappings from json are " + conversionMapLookup.size());

        } catch (JsonSyntaxException | JsonIOException e) {
            LOGGER.failMessage("Something has gone with the json to Dye Registry method!");
            e.printStackTrace();
        } catch (IOException | NullPointerException e){
            LOGGER.failMessage("Something has gone with the file loading for extra dye colors!");
            e.printStackTrace();
        }

        int duplicateEntries = ColorData.similarNames.values().stream()
                .map(colorData -> {
                    return colorData.stream()
                            .filter(colorData1 -> colorData1.getColorId().getPath().contains("2"))
                            .toList();
                })
                .mapToInt(List::size)
                .sum();

        LOGGER.setDebugCheck(() -> FabricLoader.getInstance().isDevelopmentEnvironment())
                .infoMessage("There are {} duplicate entries within the color database!", (duplicateEntries - doubleDuplicateEntries));
    }

    public static JsonObject saveToJson(Map<Identifier, ColorData> colorData, Map<String, List<String>> conversionData){
        if(!FabricLoader.getInstance().isDevelopmentEnvironment()) throw new IllegalStateException("YOU CAN NOT DO THIS, STOP IT!!!!!!!!");

        JsonObject newDatabaseValue = new JsonObject();

        newDatabaseValue.addProperty("#comment", "Data was taken from https://chir.ag/projects/ntc/ntc.js, created by Chirag Mehta, under the Creative Commons License: Attribution 2.5 http://creativecommons.org/licenses/by/2.5/");
        newDatabaseValue.addProperty("#changes", "This JSON file is adapted with Identifier Safe Name for easy loading and also is now a JSON");

        JsonArray colors = new JsonArray();

        newDatabaseValue.add("colors", colors);

        colorData.forEach((identifier, color) -> colors.add(GSON.fromJson(GSON.toJson(color), JsonObject.class)));

        JsonObject conversionJson = new JsonObject();

        newDatabaseValue.add("conversionData", conversionJson);

        conversionData.forEach((key, values) -> {
            JsonArray array = new JsonArray();

            values.forEach(array::add);

            conversionJson.add(key, array);
        });

        return newDatabaseValue;
    }

    public static void saveNewVersion(Map<Identifier, ColorData> colorData, Map<String, List<String>> conversionData){
        if(!FabricLoader.getInstance().isDevelopmentEnvironment()) return;

        URL file = DyeColorantRegistry.class.getClassLoader().getResource("data/jello/other/colorDatabase_rev0.json");

        String root = file.getPath().split("run")[0];

        Path path = Path.of(root.substring(1, root.length()).replace("%20", " ")).resolve("jello/src/main/resources/data/jello/other/");

        File newFile = path.resolve("colorDatabase_rev2.json").toFile();

        LOGGER.infoMessage(path.toString());

        JsonObject object = saveToJson(colorData, conversionData);

        if(newFile.exists()) newFile.delete();

        try {
            newFile.createNewFile();
        } catch (IOException e){
            LOGGER.failMessage("Could not create new File for new Color Database");
            LOGGER.failMessage(e.toString());

            return;
        }

        try {
            FileWriter writer = new FileWriter(newFile);

            writer.write(GSON.toJson(object));

            writer.close();
        } catch (IOException e){
            LOGGER.failMessage("Could not write new Color Database");
            LOGGER.failMessage(e.toString());

            return;
        }

        LOGGER.infoMessage("A new Revision for the Database has been made!");
    }

    public static Identifier remapId(Identifier id){
        String path = id.getPath();

        //LOGGER.restartTimer();

        /*
         * Two options can be taken:
         *    1. Find what block variant it is and then get the color using such
         *    2. Iterate though the keys of the conversion map and check if the path has the given color
         */
        String color = null;
        // Root 1
//        {
//            DyeableItemVariant variant = DyeableItemVariant.getVariantFromItem(id);
//
//            if(!variant.isSuchAVariant(id, true)) return null;
//
//            String[] pathParts = path.split("_");
//
//            StringBuilder stringBuilder = new StringBuilder();
//            for (int i = 0; i < pathParts.length - variant.wordCount; i++) {
//                stringBuilder.append(pathParts[i]);
//
//                if (i < pathParts.length - variant.wordCount - 1) stringBuilder.append("_");
//            }
//
//            color = stringBuilder.toString();
//
//            LOGGER.stopTimerPrint("Root 1 took about ");
//        }

        //Root 2
        {
            List<String> validColors = new ArrayList<>();

            for (String inputColor : DyeColorantLoader.conversionMapLookup.keySet()) {
                if(path.contains(inputColor)) validColors.add(inputColor);
            }

            int length = 0;

            for (String validColor : validColors) {
                if(validColor.length() > length){
                    length = validColor.length();

                    color = validColor;
                }
            }

            //LOGGER.stopTimerPrint("Root 2 took about ");
        }

        return (color == null)
                ? id
                : Jello.id(path.replace(color, DyeColorantLoader.conversionMapLookup.get(color)));
    }

    public static final class ColorData {

        public static final Map<String, List<ColorData>> similarNames = new HashMap<>();

        private final String hexValue;
        private final String colorName;
        private final String identifierSafeName;

        private transient Integer colorValue;
        private transient Identifier colorId;

        public ColorData(String hexValue, String colorName, String identifierSafeName) {
            this.hexValue = hexValue;
            this.colorName = colorName;
            this.identifierSafeName = identifierSafeName;
        }

        public String hexValue() {
            return hexValue;
        }

        public String colorName() {
            return colorName;
        }

        public String identifierSafeName() {
            return identifierSafeName;
        }

        public int colorInDecimal(){
            if (colorValue == null) {
                try {
                    colorValue = Integer.parseInt(hexValue, 16);
                } catch (NumberFormatException e) {
                    LOGGER.failMessage("Seems that hexValue is somehow invalid and was not parsed, setting such to black! [hexValue: {}]", hexValue);
                    LOGGER.failMessage(e.toString());

                    colorValue = 0;
                }
            }

            return colorValue;
        }

        public Identifier getColorId(){
            if(colorId == null){
                colorId = Jello.id(identifierSafeName);

                if (DyeColorantRegistry.DYE_COLOR.containsId(colorId)){
                    similarNames.computeIfAbsent(colorId.getPath(), s -> new ArrayList<>(List.of(loadedColorData.get(colorId))))
                            .add(this);

                    colorId = Jello.id(colorId.getPath() + "_2");
                }
            }

            return colorId;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (ColorData) obj;
            return Objects.equals(this.hexValue, that.hexValue) &&
                    Objects.equals(this.colorName, that.colorName) &&
                    Objects.equals(this.identifierSafeName, that.identifierSafeName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(hexValue, colorName, identifierSafeName);
        }

        @Override
        public String toString() {
            return "ColorDataJson[" +
                    "hexValue=" + hexValue + ", " +
                    "colorName=" + colorName + ", " +
                    "identifierSafeName=" + identifierSafeName + ']';
        }
    }
}
