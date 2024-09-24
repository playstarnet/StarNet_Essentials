package com.playstarnet.essentials.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StaticValues {
    public static int shopIterationNum = 0;
    public static boolean shopScreenWasFilled = false;
    public static List<String> friends = new ArrayList<>();
    public static boolean friendsCheck = false;
    public static Map<String, String> users = Map.of(
            "b344687bec74479a95401aa8ccb13e92", "OliviaTheVampire",
            "b51b17a8d16a486c88ac9c5dadb097d4", "DB2O",
            "2a2b396a59df43e08ec1e71339d383cd", "J2yden",
            "d6f5957ac93c4145aab0471504fe524e", "selenodot"
    );
    public static List<String> devs = new ArrayList<>(List.of("b344687bec74479a95401aa8ccb13e92"));
    public static List<String> teamMembers = new ArrayList<>();
    public static List<String> translators = new ArrayList<>();
}
