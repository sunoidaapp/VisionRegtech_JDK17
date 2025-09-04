package com.vision.wb;

//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
//public class sample {
//
//	public static void main(String[] args) {
//        // Sample list with duplicate values
//        List<String> items = Arrays.asList("apple", "banana", "apple", "orange", "banana", "grape", "kiwi");
//
//        // Filter the list to include only distinct values and store in an array
//        String[] distinctItemsArray = items.stream()
//                .distinct()
//                .toArray(String[]::new);
//
//        // Print the filtered array with distinct values
//        System.out.println(Arrays.toString(distinctItemsArray));
//    }
//}
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

class Item {
    private String name;
    private int sno;

    public Item(String name, int sno) {
        this.name = name;
        this.sno = sno;
    }

    public String getName() {
        return name;
    }

    public int getSno() {
        return sno;
    }

    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", sno=" + sno +
                '}';
    }
}

public class sample {
    public static void main(String[] args) {
        // Sample list with duplicate values
        List<Item> items = Arrays.asList(
                new Item("apple", 1),
                new Item("banana", 2),
                new Item("apple", 3)
               // new Item("orange", 4),
               // new Item("banana", 5),
               // new Item("grape", 6),
               // new Item("kiwi", 7)
        );

        // Create a set to keep track of seen names
        Set<String> seenNames = new LinkedHashSet<>();

        // Filter the list to include only distinct values based on the name
        Item[] distinctItemsArray = items.stream()
                .filter(item -> seenNames.add(item.getName()))
                .toArray(Item[]::new);

        // Print the filtered array with distinct values
        System.out.println(Arrays.toString(distinctItemsArray));
    }
}
