package io.github.manoelcampos.accessors;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AutoClassAccessorsMojoTest {
    /**
     * Test if the AutoClassAccessorsMojo.accessorsPlugin.version() is equal to the defined in the pom.xml
     */
    @Test
    void pluginVersionExecutedByByteBuddyIsTheSameAsDefinedInThePomXmlFile() {
        final var mojo = new AutoClassAccessorsMojo();
        final String currentVersion = readVersionFromPomXml();
        assertEquals(currentVersion, mojo.accessorsPlugin.version());
    }

    /**
     * {@return the current plugin version from the pom.xml file}
     */
    private String readVersionFromPomXml() {
        final var factory = DocumentBuilderFactory.newInstance();
        try {
            final var docBuilder = factory.newDocumentBuilder();
            final var doc = docBuilder.parse(new File("pom.xml"));
            return doc.getElementsByTagName("version").item(0).getTextContent();
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }
}
