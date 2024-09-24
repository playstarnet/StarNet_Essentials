package com.playstarnet.essentials.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StaticValues {
    public static int shopIterationNum = 0;
    public static boolean shopScreenWasFilled = false;
    public static List<String> friends = new ArrayList<>(List.of("b51b17a8d16a486c88ac9c5dadb097d4"));
    public static boolean friendsCheck = false;
    public static Map<String, String> users = new ConcurrentHashMap<>(Map.of(
            "OliviaTheVampire", "b344687bec74479a95401aa8ccb13e92",
            "DB2O", "b51b17a8d16a486c88ac9c5dadb097d4"
    ));
    public static List<String> devs = new ArrayList<>(List.of("b344687bec74479a95401aa8ccb13e92"));
    public static List<String> teamMembers = new ArrayList<>();
    public static List<String> translators = new ArrayList<>();
}
