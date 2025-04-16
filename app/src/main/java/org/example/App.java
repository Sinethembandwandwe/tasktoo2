package org.example;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class App {
    public static void main(String[] args) {
        try {
            // Ask user what fields they want
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter fields to display (comma-separated, e.g. name,country,list):");
            String input = "name,country"; // TEMP: remove this later

            List<String> selectedFields = Arrays.stream(input.split(","))
                                                .map(String::trim)
                                                .collect(Collectors.toList());

            // Parse XML file
            File file = new File("src/main/resources/data.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList recordList = doc.getElementsByTagName("record");

            for (int i = 0; i < recordList.getLength(); i++) {
                Node node = recordList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element record = (Element) node;

                    for (String field : selectedFields) {
                        String value = getTagValue(field, record);
                        System.out.println(capitalize(field) + ": " + value);
                    }
                    System.out.println("------------");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getTagValue(String tag, Element element) {
        NodeList nlList = element.getElementsByTagName(tag);
        if (nlList != null && nlList.getLength() > 0) {
            NodeList subList = nlList.item(0).getChildNodes();

            if (subList != null && subList.getLength() > 0) {
                return subList.item(0).getNodeValue();
            }
        }
        return "N/A";
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
