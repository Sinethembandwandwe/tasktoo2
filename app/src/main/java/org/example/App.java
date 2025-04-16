package org.example;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.Gson;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.io.InputStream;
import java.util.*;

public class App {
    public static void main(String[] args) {
        // Available XML fields
        List<String> validFields = List.of("name", "postalZip", "region", "country", "address", "list");
        String[] selectedFields;

        Scanner scanner = new Scanner(System.in);

        // Step 4: Input validation loop
        while (true) {
            System.out.println("Enter fields to display (comma-separated, e.g. name,country,list):");
            if (!scanner.hasNextLine()) {
                System.out.println("❌ No input found. Exiting.");
                return;
            }
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("❌ Input cannot be empty. Please try again.");
                continue;
            }

            selectedFields = input.split(",");
            boolean allValid = true;

            for (int i = 0; i < selectedFields.length; i++) {
                selectedFields[i] = selectedFields[i].trim();
                if (!validFields.contains(selectedFields[i])) {
                    System.out.println("❌ Invalid field: " + selectedFields[i]);
                    allValid = false;
                }
            }

            if (allValid) break;
            else System.out.println("Please enter only valid fields.\n");
        }

        try {
            // Load data.xml from resources
            InputStream inputStream = App.class.getClassLoader().getResourceAsStream("data.xml");
            if (inputStream == null) {
                throw new IllegalArgumentException("❌ data.xml not found in resources folder.");
            }

            // Parse XML file
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(inputStream);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("record");
            JsonArray jsonArray = new JsonArray();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    JsonObject jsonObject = new JsonObject();

                    for (String field : selectedFields) {
                        NodeList fieldNodes = element.getElementsByTagName(field);
                        if (fieldNodes.getLength() > 0) {
                            jsonObject.addProperty(field, fieldNodes.item(0).getTextContent());
                        } else {
                            jsonObject.addProperty(field, ""); // Leave blank if field missing
                        }
                    }

                    jsonArray.add(jsonObject);
                }
            }

            // Convert to JSON and print
            Gson gson = new Gson();
            String jsonOutput = gson.toJson(jsonArray);
            System.out.println("\n✅ JSON Output:");
            System.out.println(jsonOutput);

        } catch (Exception e) {
            System.out.println("❌ Error processing XML: " + e.getMessage());
        }
    }
}
