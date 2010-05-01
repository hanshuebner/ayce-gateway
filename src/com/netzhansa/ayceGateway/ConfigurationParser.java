package com.netzhansa.ayceGateway;

import javax.xml.stream.*;
import javax.xml.transform.stax.StAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

import java.io.*;

public class ConfigurationParser {
    
    static int getIntegerAttributeValue(XMLStreamReader parser, String attributeName) {
        return Integer.parseInt(parser.getAttributeValue("", attributeName));
    }
    
    static String configurationSchemaFilename = "ayce-gateway-configuration.xsd";
    
    static void validateConfigurationFile(String fileName) throws XMLStreamException, SAXException, IOException {
        InputStream inputStream = new FileInputStream(fileName);
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader parser = factory.createXMLStreamReader(inputStream);
        
        // Set up schema validator
        SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        Schema schemaGrammar = schemaFactory.newSchema(new File(configurationSchemaFilename));
        Validator schemaValidator = schemaGrammar.newValidator();
        schemaValidator.validate(new StAXSource(parser));
    }
    
    // TODO: Read from String, validate before parsing
    static public LEDMatrix parseConfigurationFile(String fileName) throws XMLStreamException, SAXException, IOException
    {
        validateConfigurationFile(fileName);
        InputStream inputStream = new FileInputStream(fileName);
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader parser = factory.createXMLStreamReader(inputStream);

        LEDMatrix world = null;
        for (int event = parser.next(); event != XMLStreamConstants.END_DOCUMENT; event = parser.next()) {
            switch (event) {
            case XMLStreamConstants.START_ELEMENT:
                if (parser.getLocalName() == "matrix") {
                    world = new LEDMatrix(getIntegerAttributeValue(parser, "width"),
                                          getIntegerAttributeValue(parser, "height"));
                }
                if (parser.getLocalName() == "led") {
                    int universe = getIntegerAttributeValue(parser, "universe");
                    int address = getIntegerAttributeValue(parser, "address");
                    int x = getIntegerAttributeValue(parser, "x");
                    int y = getIntegerAttributeValue(parser, "y");
                    // TODO: validate x/y/address range
                    LED led = world.getLED(x, y);
                    led.setUniverse(universe);
                    led.setAddress(address);
                }
                break;
            }
        }
        parser.close();
        
        return world;
    }
    public static void main(String[] args)
    {
        try {
            String fileName = args[0];
            LEDMatrix world = parseConfigurationFile(fileName);
            
            System.out.println("Configuration read, width " + world.getWidth() + " height " + world.getHeight());
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (XMLStreamException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
