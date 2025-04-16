package org.example;

import com.google.gson.Gson;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.InputStream;
import java.util.*;

public class App {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter fields to display (comma-separated, e.g. name,country,list):");
        String input = scanner.nextLine();
        String[] selectedFields = input.split(",");

        InputStream inputStream = App.class.getClassLoader().getResourceAsStream("data.xml");
        if (inputStream == null) {
            throw new IllegalArgumentException("Could not find data.xml");
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(inputStream);
        doc.getDocumentElement().normalize();

        NodeList nodeList = doc.getElementsByTagName("record");

        List<Map<String, String>> outputList = new ArrayList<>();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            Map<String, String> jsonMap = new LinkedHashMap<>();
            for (String field : selectedFields) {
                field = field.trim();
                Node fieldNode = element.getElementsByTagName(field).item(0);
                if (fieldNode != null) {
                    jsonMap.put(field, fieldNode.getTextContent());
                } else {
                    jsonMap.put(field, "N/A");
                }
            }
            outputList.add(jsonMap);
        }

        Gson gson = new Gson();
        System.out.println(gson.toJson(outputList));
    }
}
