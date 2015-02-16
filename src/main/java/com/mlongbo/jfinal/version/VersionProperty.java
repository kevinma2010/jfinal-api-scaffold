package com.mlongbo.jfinal.version;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * @author malongbo
 */
public class VersionProperty {

    private static final Logger Log = LoggerFactory.getLogger(VersionProperty.class);
    private static long lastModified = 0L;
    private static final Object lock = new Object();
    private File file;
    private Document document;

    private Map<String,Version> nowVersion = new HashMap<String, Version>();

    /**
     * Creates a new XMLPropertiesTest object.
     *
     * @param fileName the full path the file that properties should be read from
     *                 and written to.
     * @throws java.io.IOException if an error occurs loading the properties.
     */
    public VersionProperty(String fileName) throws IOException {
        this(new File(fileName));
    }

   /* *//**
     * Loads XML properties from a stream.
     *
     * @param in the input stream of XML.
     * @throws java.io.IOException if an exception occurs when reading the stream.
     *//*
    public VersionProperty(InputStream in) throws IOException {
        if (in != null) {
            Reader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            buildDoc(reader);
        }
    }*/

    /**
     * Creates a new XMLPropertiesTest object.
     *
     * @param file the file that properties should be read from and written to.
     * @throws java.io.IOException if an error occurs loading the properties.
     */
    public VersionProperty(File file) throws IOException {
        this.file = file;
        if (!file.exists()) {
            // Attempt to recover from this error case by seeing if the
            // tmp file exists. It's possible that the rename of the
            // tmp file failed the last time Jive was running,
            // but that it exists now.
            File tempFile;
            tempFile = new File(file.getParentFile(), file.getName() + ".tmp");
            if (tempFile.exists()) {
                Log.error("WARNING: " + file.getName() + " was not found, but temp file from " +
                        "previous write operation was. Attempting automatic recovery." +
                        " Please check file for data consistency.");
                tempFile.renameTo(file);
            }
            // There isn't a possible way to recover from the file not
            // being there, so throw an error.
            else {
                throw new FileNotFoundException("XML properties file does not exist: "
                        + file.getName());
            }
        }
        // Check read and write privs.
        if (!file.canRead()) {
            throw new IOException("XML properties file must be readable: " + file.getName());
        }
        if (!file.canWrite()) {
            throw new IOException("XML properties file must be writable: " + file.getName());
        }

        FileReader reader = new FileReader(file);
        lastModified = file.lastModified();
        buildDoc(reader);
    }

    /**
     * Builds the document XML model up based the given reader of XML data.
     * @param in the input stream used to build the xml document
     * @throws java.io.IOException thrown when an error occurs reading the input stream.
     */
    private void buildDoc(Reader in) throws IOException {
        try {
            SAXReader xmlReader = new SAXReader();
            xmlReader.setEncoding("UTF-8");
            document = xmlReader.read(in);
            buildNowVersion();
        }
        catch (Exception e) {
            Log.error("Error reading XML properties", e);
            throw new IOException(e.getMessage());
        }
        finally {
            if (in != null) {
                in.close();
            }
        }
    }

    private void reCheck() {
        if (lastModified < file.lastModified()) {
            synchronized (lock) {
                if (lastModified < file.lastModified()) {
                    lastModified = file.lastModified();
                    try {
                        buildDoc(new FileReader(file));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    private void buildNowVersion() {
        nowVersion.put(ClientType.ANDROID.getType(),loadNowVersion(ClientType.ANDROID));
        nowVersion.put(ClientType.IPHONE.getType(),loadNowVersion(ClientType.IPHONE));
    }

    private Version loadNowVersion(ClientType type) {
        Element clientElement = null;
        List<Version> versions = null;
        String nowVersion = "";
        switch (type) {
            case ANDROID:
                clientElement = getElement("client.android");
                break;
            case IPHONE:
                clientElement = getElement("client.iphone");
                break;
            default:
                return null;
        }
        nowVersion = clientElement.attributeValue("default");
        versions = new ArrayList<Version>();
        List elements = clientElement.elements();

        if (elements == null) {
            return null;
        }

        Version versionTmp = null;
        for (Object ele : elements) {
            Element versionEle = (Element) ele;
            String version = versionEle.elementText("version");
            if (version == null || !nowVersion.equalsIgnoreCase(version)) {
                continue;
            }

            String url = versionEle.elementText("url");
            String message = versionEle.elementText("message");

            versionTmp = new Version();
            versionTmp.setMessage(message);
            versionTmp.setUrl(url);
            versionTmp.setVersion(version);

            return versionTmp;
        }
        return null;
    }

    /**
     * Returns an array representation of the given Jive property. Jive
     * properties are always in the format "prop.name.is.this" which would be
     * represented as an array of four Strings.
     *
     * @param name the name of the Jive property.
     * @return an array representation of the given Jive property.
     */
    private String[] parsePropertyName(String name) {
        List<String> propName = new ArrayList<String>(5);
        // Use a StringTokenizer to tokenize the property name.
        StringTokenizer tokenizer = new StringTokenizer(name, ".");
        while (tokenizer.hasMoreTokens()) {
            propName.add(tokenizer.nextToken());
        }
        return propName.toArray(new String[propName.size()]);
    }

    public void addVersion(Version version) {
        Element clientElement = null;
        ClientType type = version.getType();
        switch (type) {
            case ANDROID:
                clientElement = getElement("client.android");
                break;
            case IPHONE:
                clientElement = getElement("client.iphone");
                break;
            default:
                return;
        }

        Element entry = clientElement.addElement("entry");
        Element element;

        element = entry.addElement("version");
        element.setText(version.getVersion());

        element = entry.addElement("url");
        element.setText(version.getUrl());

        element = entry.addElement("message");
        element.setText(version.getMessage());

        saveProperties();
    }

    public void deleteVersion(String version, ClientType type) {
        Element clientElement = null;
        switch (type) {
            case ANDROID:
                clientElement = getElement("client.android");
                break;
            case IPHONE:
                clientElement = getElement("client.iphone");
                break;
            default:
                return;
        }

        List elements = clientElement.elements();

        if (elements == null) {
            return;
        }
        Element element = null;
        for (Object ele : elements) {
            Element versionEle = (Element) ele;
            String versionTmp = versionEle.elementText("version");

            if (version.equalsIgnoreCase(versionTmp)) {
                element = versionEle;
                break;
            }
        }

        if (element != null) {
            elements.remove(element);
            saveProperties();
        }
    }

    public void setVersion(ClientType type, String version) {
        Element clientElement = null;
        switch (type) {
            case ANDROID:
                clientElement = getElement("client.android");
                break;
            case IPHONE:
                clientElement = getElement("client.iphone");
                break;
            default:
                return;
        }

        Attribute attribute = clientElement.attribute("default");

        if (attribute != null) {
            attribute.setValue(version);
            saveProperties();
        }
    }

    public Version getNowVersion(ClientType type) {
        reCheck();
        return nowVersion.get(type.getType());
    }

    public List<Version> getVersions(ClientType type) {
        Element clientElement = null;
        List<Version> versions = null;
        switch (type) {
            case ANDROID:
                clientElement = getElement("client.android");
                break;
            case IPHONE:
                clientElement = getElement("client.iphone");
                break;
            default:
                return null;
        }

        versions = new ArrayList<Version>();
        List elements = clientElement.elements();

        if (elements == null) {
            return versions;
        }

        Version versionTmp = null;
        for (Object ele : elements) {
            Element versionEle = (Element) ele;
            String version = versionEle.elementText("version");
            String url = versionEle.elementText("url");
            String message = versionEle.elementText("message");

            versionTmp = new Version();
            versionTmp.setMessage(message);
            versionTmp.setUrl(url);
            versionTmp.setVersion(version);

            versions.add(versionTmp);
        }
        return versions;
    }

    private Element getElement(String name) {
        String[] propName = parsePropertyName(name);
        // Search for this property by traversing down the XML heirarchy.
        Element element = document.getRootElement();
        for (int i = 0; i < propName.length; i++) {
            element = element.element(propName[i]);
            // Can't find the property so return.
            if (element == null) {
                break;
            }
        }
        return element;
    }

    /**
     * Saves the properties to disk as an XML document. A temporary file is
     * used during the writing process for maximum safety.
     */
    private synchronized void saveProperties() {
        boolean error = false;
        // Write data out to a temporary file first.
        File tempFile = null;
        Writer writer = null;
        try {
            tempFile = new File(file.getParentFile(), file.getName() + ".tmp");
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile), "UTF-8"));
            OutputFormat prettyPrinter = OutputFormat.createPrettyPrint();
            XMLWriter xmlWriter = new XMLWriter(writer, prettyPrinter);
            xmlWriter.write(document);
        }
        catch (Exception e) {
            Log.error(e.getMessage(), e);
            // There were errors so abort replacing the old property file.
            error = true;
        }
        finally {
            if (writer != null) {
                try {
                    writer.close();
                }
                catch (IOException e1) {
                    Log.error(e1.getMessage(), e1);
                    error = true;
                }
            }
        }

        // No errors occured, so delete the main file.
        if (!error) {
            // Delete the old file so we can replace it.
            if (!file.delete()) {
                Log.error("Error deleting property file: " + file.getAbsolutePath());
                return;
            }
            // Copy new contents to the file.
            try {
                copy(tempFile, file);
            }
            catch (Exception e) {
                Log.error(e.getMessage(), e);
                // There were errors so abort replacing the old property file.
                error = true;
            }
            // If no errors, delete the temp file.
            if (!error) {
                tempFile.delete();
            }
        }
    }

    /**
     * Copies the inFile to the outFile.
     *
     * @param inFile  The file to copy from
     * @param outFile The file to copy to
     * @throws java.io.IOException If there was a problem making the copy
     */
    private static void copy(File inFile, File outFile) throws IOException {
        FileInputStream fin = null;
        FileOutputStream fout = null;
        try {
            fin = new FileInputStream(inFile);
            fout = new FileOutputStream(outFile);
            copy(fin, fout);
        }
        finally {
            try {
                if (fin != null) fin.close();
            }
            catch (IOException e) {
                // do nothing
            }
            try {
                if (fout != null) fout.close();
            }
            catch (IOException e) {
                // do nothing
            }
        }
    }

    /**
     * Copies data from an input stream to an output stream
     *
     * @param in the stream to copy data from.
     * @param out the stream to copy data to.
     * @throws java.io.IOException if there's trouble during the copy.
     */
    private static void copy(InputStream in, OutputStream out) throws IOException {
        // Do not allow other threads to intrude on streams during copy.
        synchronized (in) {
            synchronized (out) {
                byte[] buffer = new byte[256];
                while (true) {
                    int bytesRead = in.read(buffer);
                    if (bytesRead == -1) break;
                    out.write(buffer, 0, bytesRead);
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            VersionProperty property = new VersionProperty("version.xml");

            List<Version> androidVersions = property.getVersions(ClientType.ANDROID);
            System.out.println(androidVersions.size());

            Version nowVersion = property.getNowVersion(ClientType.ANDROID);
            System.out.println(nowVersion.getVersion());

            Version add = new Version();
            add.setVersion("1.1.1.1");
            add.setUrl("www.baidu.com");
            add.setMessage("baidu");
            add.setType(ClientType.ANDROID);
            property.addVersion(add);

            property.deleteVersion("1.1.1.1", ClientType.ANDROID);

            property.setVersion(ClientType.ANDROID,"0.1.0");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
