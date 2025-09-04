package com.vision.wb;

import org.json.JSONArray;
import org.json.JSONObject;

public class JsonConversion {
    public static void main(String[] args) {
        // Create a JSONObject to hold the main structure
        JSONObject jsonObject = new JSONObject();

        // Add variables and values to the main structure
        jsonObject.put("variable1", "value1");
        jsonObject.put("variable2", "value2");

        // Create a JSONArray to hold linked list objects
        JSONArray linkedList = new JSONArray();

        // Create a JSONObject for the first linked object
        JSONObject linkedObject1 = new JSONObject();
        linkedObject1.put("linkedVariable1", "linkedValue1");
        linkedObject1.put("linkedVariable2", "linkedValue2");

        // Create a JSONObject for the second linked object
        JSONObject linkedObject2 = new JSONObject();
        linkedObject2.put("linkedVariable1", "linkedValue3");
        linkedObject2.put("linkedVariable2", "linkedValue4");

        // Add the linked objects to the linked list
        linkedList.put(linkedObject1);
        linkedList.put(linkedObject2);

        // Add the linked list to the main structure
        jsonObject.put("linkedList", linkedList);

        // Print the JSON object
        System.out.println(jsonObject.toString(4));
    }
}
