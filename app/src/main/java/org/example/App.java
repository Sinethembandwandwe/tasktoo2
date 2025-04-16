package org.example;

import com.google.gson.Gson;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.util.*;

public class App {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter fields to display (comma-separated, e.g. name,country,list):");

            if (!scanner.hasNextLine()) {
                System.out.println("No input received. Exiting.");
                return;
            }

            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("No fields entered. Exiting.");
                return;
            }

            List<String> requestedFields = Arrays.asList(input.split("\\s*,\\s*"));

            InputStream inputStream = App.class.getClassLoader().getResourceAsStream("data.xml");
            if (inputStream == null) {
                System.out.println("data.xml not found.");
                return;
            }

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            UserHandler handler = new UserHandler(requestedFields);
            saxParser.parse(inputStream, handler);

            Gson gson = new Gson();
            String jsonOutput = gson.toJson(handler.getResults());
            System.out.println(jsonOutput);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class UserHandler extends DefaultHandler {
        private final List<String> fields;
        private final List<Map<String, String>> results = new ArrayList<>();
        private Map<String, String> currentEntry = null;
        private StringBuilder currentValue = new StringBuilder();
        private String currentField = null;

        public UserHandler(List<String> fields) {
            this.fields = fields;
        }

        public List<Map<String, String>> getResults() {
            return results;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if ("record".equalsIgnoreCase(qName)) {
                currentEntry = new HashMap<>();
            } else if (currentEntry != null && fields.contains(qName)) {
                currentField = qName;
                currentValue.setLength(0); // reset
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (currentField != null) {
                currentValue.append(ch, start, length);
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if ("record".equalsIgnoreCase(qName)) {
                results.add(currentEntry);
                currentEntry = null;
            } else if (currentField != null && currentField.equals(qName)) {
                currentEntry.put(currentField, currentValue.toString().trim());
                currentField = null;
            }
        }
    }
}
