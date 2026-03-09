package FarmEngine;

import Farm.Crops.*;
import Farm.Culture;
import Farm.Farms;

import java.io.FileWriter;
import java.io.PrintWriter;

import Farm.Plot;

import java.io.*;
import java.util.Scanner;

public class SaveSystem {
    private static final String FILE_PATH = "saves/save.txt";

    public static void saves(Farms farms){
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            writer.println(farms.getMoney());

            String[] types = {"Wheat", "Tomato", "Carrot", "Potato", "Kiwi", "Strawberry", "Corn", "Pumpkin"};
            for(String type : types) {
                writer.println(farms.getInventory().getQuantity(type + "_Seed"));
                writer.println(farms.getInventory().getQuantity(type + "_Crop"));
            }

            for (int i = 0; i < farms.getNbLINES(); i++) {
                for (int j = 0; j < farms.getNbCOLMUNS(); j++) {
                    Plot p = farms.getField()[i][j];
                    if (p.isEmpty()) {
                        writer.println("EMPTY");
                    } else {
                        String name = p.getActualCulture().getName().toUpperCase();
                        double time = p.getActualCulture().getTimeSec();
                        writer.println(name + "|" + time);
                    }
                }
            }
            System.out.println("Partie Sauvegardée dans " + FILE_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void load(Farms farms) {
        try {
            File file = new File("saves/save.txt");
            Scanner scanner = new Scanner(file);

            if (scanner.hasNextLine()) {
                farms.setMoney(Double.parseDouble(scanner.nextLine()));
            }

            String[] types = {"Wheat", "Tomato", "Carrot", "Potato", "Kiwi", "Strawberry", "Corn", "Pumpkin"};
            for(String type : types) {
                if(scanner.hasNextLine()) farms.getInventory().add(type + "_Seed", Integer.parseInt(scanner.nextLine()));
                if(scanner.hasNextLine()) farms.getInventory().add(type + "_Crop", Integer.parseInt(scanner.nextLine()));
            }


            for (int i = 0; i < farms.getNbLINES(); i++) {
                for (int j = 0; j < farms.getNbCOLMUNS(); j++) {
                    if (scanner.hasNextLine()) {

                        String line = scanner.nextLine();

                        if (!line.equals("EMPTY")) {

                            String[] parts = line.split("\\|");
                            String type = parts[0];
                            double savedTime = Double.parseDouble(parts[1]);

                            Culture c = switch (type) {
                                case "WHEAT" -> new Wheat();
                                case "CARROT" -> new Carrot();
                                case "POTATO" -> new Potato();
                                case "TOMATO" -> new Tomato();
                                case "KIWI" -> new Kiwi();
                                case "STRAWBERRY" -> new Strawberry();
                                case "CORN" -> new Corn();
                                case "PUMPKIN" -> new Pumpkin();
                                default -> null;
                            };

                            if (c != null) {
                                c.setTimeSec(savedTime);
                                farms.getField()[i][j].planting(c);
                            }
                        }
                    }
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("No Saves Founds");
        }
    }
}



