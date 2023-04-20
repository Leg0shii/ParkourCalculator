package de.legoshi.parkourcalculator.file;

import de.legoshi.parkourcalculator.parkour.simulator.PlayerTickInformation;
import de.legoshi.parkourcalculator.parkour.tick.InputTick;
import de.legoshi.parkourcalculator.util.BlockColors;
import de.legoshi.parkourcalculator.util.Vec3;
import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CSVUtils {

    public static void saveTicksToCSV(List<InputData> inputTicks, String filePath) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath))) {
            // Write the header
            writer.write("X,Y,Z,YAW,PITCH,ANGLE_X,ANGLE_Y,W,A,S,D,SPRINT,SNEAK,JUMP,LMB,RMB,VEL_X,VEL_Y,VEL_Z");
            writer.newLine();

            // Write the content
            for (InputData tick : inputTicks) {
                InputTick iTick = tick.getInputTick();
                Vec3 p = tick.getPosition();
                Vec3 v = tick.getVelocity();
                writer.write(String.format(Locale.US, "%.15f,%.15f,%.15f,%.5f,%.5f,%.1f,%.1f,%b,%b,%b,%b,%b,%b,%b,%b,%b,%.15f,%.15f,%.15f",
                        p.x, p.y, p.z, iTick.YAW, 90.0, iTick.YAW, 0.0, iTick.W, iTick.A, iTick.S, iTick.D, iTick.SPRINT, iTick.SNEAK, iTick.JUMP, false, false, v.x, v.y, v.z));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<InputData> loadTicksFromCSV(String filePath) {
        List<InputData> inputTicks = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            // Skip the header
            reader.readLine();

            // Read the content
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                InputData inputData = new InputData();
                InputTick tick = new InputTick(
                    Boolean.parseBoolean(tokens[7]),
                    Boolean.parseBoolean(tokens[8]),
                    Boolean.parseBoolean(tokens[9]),
                    Boolean.parseBoolean(tokens[10]),
                    Boolean.parseBoolean(tokens[13]), // sprint 13
                    Boolean.parseBoolean(tokens[11]), // sneak 11
                    Boolean.parseBoolean(tokens[12]), // jump 12
                    Float.parseFloat(tokens[3])
                );

                Vec3 position = new Vec3(Double.parseDouble(tokens[0]), Double.parseDouble(tokens[1]), Double.parseDouble(tokens[2]));
                Vec3 velocity = new Vec3(Double.parseDouble(tokens[16]), Double.parseDouble(tokens[17]), Double.parseDouble(tokens[18]));

                inputData.setInputTick(tick);
                inputData.setPosition(position);
                inputData.setVelocity(velocity);
                inputTicks.add(inputData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return inputTicks;
    }

    public static void saveBlocksToCSV(List<BlockData> blockDatas, String filePath) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath))) {
            // Write the header
            writer.write("X,Y,Z,TYPE,TIER,COLOR,TOP,BOTTOM,NORTH,EAST,SOUTH,WEST");
            writer.newLine();

            // Write the content
            for (BlockData b : blockDatas) {
                writer.write(String.format(Locale.US, "%.1f,%.1f,%.1f,%s,%d,%s,%b,%b,%b,%b,%b,%b",
                        b.blockType, b.pos.x, b.pos.y, b.pos.z, b.tier, b.color.toString(), b.TOP, b.BOTTOM, b.NORTH, b.EAST, b.SOUTH, b.WEST));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<List<BlockData>> loadBlocksFromCSV(String filePath) {
        List<List<BlockData>> lists = new ArrayList<>();
        List<BlockData> solidBlocks = new ArrayList<>();
        List<BlockData> transparentBlocks = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                BlockData blockData = new BlockData(
                        tokens[3],
                        new Vec3(
                                Double.parseDouble(tokens[0]),
                                Double.parseDouble(tokens[1]),
                                Double.parseDouble(tokens[2])
                        ),
                        Integer.parseInt(tokens[4])+1,
                        BlockColors.parse(tokens[5]),
                        Boolean.parseBoolean(tokens[6]),
                        Boolean.parseBoolean(tokens[7]),
                        Boolean.parseBoolean(tokens[8]),
                        Boolean.parseBoolean(tokens[9]),
                        Boolean.parseBoolean(tokens[10]),
                        Boolean.parseBoolean(tokens[11])
                );
                if (blockData.color.getOpacity() == 1.0) solidBlocks.add(blockData);
                else transparentBlocks.add(blockData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        lists.add(solidBlocks);
        lists.add(transparentBlocks);

        return lists;
    }

}