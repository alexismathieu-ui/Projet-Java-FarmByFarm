package FarmEngine;

import Farm.*;
import Farm.Animal.Chicken;
import Farm.Animal.Cow;
import Farm.Animal.Pig;
import Farm.Animal.Sheep;
import Farm.Crops.*;
import Farm.Enclosure.Enclosure;
import Farm.Enclosure.EnclosureManager;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.*;
import java.util.Scanner;

public class SaveSystem {

    private static String getFilePath(int slot){
        return "saves/save" + slot + ".txt";
    }

    public static void saves(Farms farms, int slot){
        String path = getFilePath(slot);
        try (PrintWriter writer = new PrintWriter(new FileWriter(path))) {

            writer.println(farms.getMoney());
            writer.println(farms.getLevel());
            writer.println(farms.getCurrentXP());
            writer.println(farms.getNextLevelXP());
            writer.println(farms.getUnlockedPlotsCount());

            String[] types = {"Wheat","Tomato","Carrot","Potato","Lemon","Strawberry","Corn","Pineapple","Egg","Truff","Milk","Wool"};
            for(String type : types) {
                writer.println(farms.getInventory().getQuantity(type + "_Seed"));
                writer.println(farms.getInventory().getQuantity(type + "_Crop"));
            }

            for (int i = 0; i < farms.getNbLINES(); i++) {
                for (int j = 0; j < farms.getNbCOLMUNS(); j++) {
                    Plot p = farms.getField()[i][j];
                    writer.println(p.isLocked());
                    if (p.isEmpty()) {
                        writer.println("EMPTY");
                    } else {
                        writer.println(p.getActualCulture().getName().toUpperCase() + "|" + p.getActualCulture().getTimeSec());
                    }
                }
            }

            writer.println("ANIMALS_STARTS");
            for(Animals a : farms.getMyAnimals()){
                writer.println(a.getSpecies() + "|" + a.isHungry() + "|" + a.hasProduced());
            }
            writer.println("ANIMALS_END");

            writer.println("ENCLOSURES_START");
            EnclosureManager mgr = farms.getEnclosureManager();
            if (mgr != null) {
                writer.println(mgr.getEnclosures().size());
                for (Enclosure enc : mgr.getEnclosures()) {
                    writer.println(enc.getId() + "|" + enc.getName() + "|" + enc.getMaxCapacity());
                    writer.println(enc.getAnimals().size());
                    for (Animals a : enc.getAnimals()) {
                        int idx = farms.getMyAnimals().indexOf(a);
                        writer.println(idx);
                    }
                }
            } else {
                writer.println(0);
            }
            writer.println("ENCLOSURES_END");

            writer.println("QUESTS_DATA");
            writer.println(farms.getNextQuestTime());
            writer.println(farms.getActiveQuests().size());
            for (Quest q : farms.getActiveQuests()) {
                writer.println(q.getTargetItem() + "|" + q.getAmountNeeded() + "|" + q.getRewardMoney() + "|" + q.getRewardXP());
            }

            System.out.println("Partie Sauvegardée dans " + path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void load(Farms farms, int slot) {
        File file = new File(getFilePath(slot));
        if (!file.exists()) return;

        try (Scanner scanner = new Scanner(file)) {
            if (scanner.hasNextLine()) farms.setMoney(Double.parseDouble(scanner.nextLine()));
            if (scanner.hasNextLine()) farms.setLevel(Integer.parseInt(scanner.nextLine()));
            if (scanner.hasNextLine()) farms.setCurrentXP(Double.parseDouble(scanner.nextLine()));
            if (scanner.hasNextLine()) farms.setNextLevelXP(Double.parseDouble(scanner.nextLine()));
            if (scanner.hasNextLine()) {
                int count = Integer.parseInt(scanner.nextLine());
                for(int i = 0; i < count; i++) farms.incrementUnlockedPlots();
            }

            String[] types = {"Wheat","Tomato","Carrot","Potato","Lemon","Strawberry","Corn","Pineapple","Egg","Truff","Milk","Wool"};
            for(String type : types) {
                if(scanner.hasNextLine()) farms.getInventory().add(type + "_Seed", Integer.parseInt(scanner.nextLine()));
                if(scanner.hasNextLine()) farms.getInventory().add(type + "_Crop", Integer.parseInt(scanner.nextLine()));
            }

            for (int i = 0; i < farms.getNbLINES(); i++) {
                for (int j = 0; j < farms.getNbCOLMUNS(); j++) {
                    if (!scanner.hasNextLine()) continue;
                    farms.getField()[i][j].setLocked(Boolean.parseBoolean(scanner.nextLine()));
                    String line = scanner.nextLine();
                    if (!line.equals("EMPTY")) {
                        String[] parts = line.split("\\|");
                        Culture c = switch (parts[0]) {
                            case "WHEAT"      -> new Wheat();
                            case "CARROT"     -> new Carrot();
                            case "POTATO"     -> new Potato();
                            case "TOMATO"     -> new Tomato();
                            case "LEMON"      -> new Lemon();
                            case "STRAWBERRY" -> new Strawberry();
                            case "CORN"       -> new Corn();
                            case "PINEAPPLE"  -> new Pineapple();
                            default -> null;
                        };
                        if (c != null) {
                            double t = Double.parseDouble(parts[1]);
                            c.setTimeSec(t); c.setTimeLeft(t);
                            farms.getField()[i][j].planting(c);
                        }
                    }
                }
            }

            while(scanner.hasNextLine()){
                String line = scanner.nextLine();
                if (line.equals("ANIMALS_STARTS")) continue;
                if (line.equals("ANIMALS_END")) break;
                String[] parts = line.split("\\|");
                Animals a = switch (parts[0]){
                    case "Chicken" -> new Chicken();
                    case "Sheep"   -> new Sheep();
                    case "Cow"     -> new Cow();
                    case "Pig"     -> new Pig();
                    default        -> null;
                };
                if (a != null){
                    a.setHungry(Boolean.parseBoolean(parts[1]));
                    a.setProduced(Boolean.parseBoolean(parts[2]));
                    farms.addAnimals(a);
                }
            }

            EnclosureManager mgr = new EnclosureManager(false);
            farms.setEnclosureManager(mgr);

            while(scanner.hasNextLine()){
                String line = scanner.nextLine();
                if (line.equals("ENCLOSURES_START")) {
                    int enclCount = Integer.parseInt(scanner.nextLine());
                    for (int e = 0; e < enclCount; e++) {
                        String header = scanner.nextLine(); // id|name|capacity
                        String[] hp = header.split("\\|");
                        Enclosure enc = mgr.addEnclosureWithId(
                            Integer.parseInt(hp[0]), hp[1], Integer.parseInt(hp[2])
                        );
                        int animalCount = Integer.parseInt(scanner.nextLine());
                        for (int k = 0; k < animalCount; k++) {
                            int idx = Integer.parseInt(scanner.nextLine());
                            if (idx >= 0 && idx < farms.getMyAnimals().size()) {
                                enc.addAnimal(farms.getMyAnimals().get(idx));
                            }
                        }
                    }
                    continue;
                }
                if (line.equals("ENCLOSURES_END")) break;
            }

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.equals("QUESTS_DATA")) {
                    if (scanner.hasNextLine()) {
                        long savedNextTime = Long.parseLong(scanner.nextLine());
                        long now = System.currentTimeMillis();
                        if (savedNextTime != 0 && now >= savedNextTime) {
                            farms.setNextQuestTime(0);
                            farms.generalQuests();
                            break;
                        } else {
                            farms.setNextQuestTime(savedNextTime);
                        }
                    }
                    if (scanner.hasNextLine()) {
                        int questCount = Integer.parseInt(scanner.nextLine());
                        farms.getActiveQuests().clear();
                        for (int i = 0; i < questCount; i++) {
                            if (!scanner.hasNextLine()) continue;
                            String[] qp = scanner.nextLine().split("\\|");
                            if(qp.length < 4) continue;
                            farms.getActiveQuests().add(new Quest(qp[0], Integer.parseInt(qp[1]),
                                Double.parseDouble(qp[2]), Integer.parseInt(qp[3])));
                        }
                    }
                }
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("No Saves Found");
        }
    }

    public static String getSaveSummary(int slot) {
        File file = new File("saves/save" + slot + ".txt");
        if (!file.exists()) return "Nouvelle Partie";
        try (Scanner scanner = new Scanner(file)) {
            if (scanner.hasNextLine()) {
                double money = Double.parseDouble(scanner.nextLine());
                int level = Integer.parseInt(scanner.nextLine());
                return "Niveau " + level + " — " + (int)money + " $";
            }
        } catch (Exception e) { return "Sauvegarde corrompue"; }
        return "Nouvelle Partie";
    }
}
