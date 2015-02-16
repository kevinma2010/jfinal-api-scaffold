package com.mlongbo.jfinal.common;

import org.dom4j.CDATA;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
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
public class XmlProperty {

    private static final Logger Log = LoggerFactory.getLogger(XmlProperty.class);
    
    private static long lastModified = 0L;
    private static final Object lock = new Object();
    private File file;
    private Document document;

    private Map<String,String> propertyCache = new HashMap<String, String>();


    /**
     * Creates a new XMLPropertiesTest object.
     *
     * @param fileName the full path the file that properties should be read from
     *                 and written to.
     * @throws java.io.IOException if an error occurs loading the properties.
     */
    public XmlProperty(String fileName) throws IOException {
        this(new File(fileName));
    }
    
    /**
     * Creates a new XMLPropertiesTest object.
     *
     * @param file the file that properties should be read from and written to.
     * @throws java.io.IOException if an error occurs loading the properties.
     */
    public XmlProperty(File file) throws IOException {
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
            propertyCache.clear();
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

    /**
     * 检查文件是否有更新*
     */
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

    /**
     * Returns the value of the specified property.
     *
     * @param name the name of the property to get.
     * @return the value of the specified property.
     */
    public synchronized String getProperty(String name) {
        String value = propertyCache.get(name);
        if (value != null) {
            return value;
        }

        String[] propName = parsePropertyName(name);
        // Search for this property by traversing down the XML heirarchy.
        Element element = document.getRootElement();
        for (String aPropName : propName) {
            element = element.element(aPropName);
            if (element == null) {
                // This node doesn't match this part of the property name which
                // indicates this property doesn't exist so return null.
                return null;
            }
        }
        // At this point, we found a matching property, so return its value.
        // Empty strings are returned as null.
        value = element.getTextTrim();
        if ("".equals(value)) {
            return null;
        }
        else {
            // Add to cache so that getting property next time is fast.
            propertyCache.put(name, value);
            return value;
        }
    }
    /**
     * Return all values who's path matches the given property
     * name as a String array, or an empty array if the if there
     * are no children. This allows you to retrieve several values
     * with the same property name. For example, consider the
     * XML file entry:
     * <pre>
     * &lt;foo&gt;
     *     &lt;bar&gt;
     *         &lt;prop&gt;some value&lt;/prop&gt;
     *         &lt;prop&gt;other value&lt;/prop&gt;
     *         &lt;prop&gt;last value&lt;/prop&gt;
     *     &lt;/bar&gt;
     * &lt;/foo&gt;
     * </pre>
     * If you call getProperties("foo.bar.prop") will return a string array containing
     * {"some value", "other value", "last value"}.
     *
     * @param name the name of the property to retrieve
     * @return all child property values for the given node name.
     */
    public String[] getProperties(String name) {
        String[] propName = parsePropertyName(name);
        // Search for this property by traversing down the XML heirarchy,
        // stopping one short.
        Element element = document.getRootElement();
        for (int i = 0; i < propName.length - 1; i++) {
            element = element.element(propName[i]);
            if (element == null) {
                // This node doesn't match this part of the property name which
                // indicates this property doesn't exist so return empty array.
                return new String[]{};
            }
        }
        // We found matching property, return names of children.
        Iterator iter = element.elementIterator(propName[propName.length - 1]);
        List<String> props = new ArrayList<String>();
        String value;
        while (iter.hasNext()) {
            // Empty strings are skipped.
            value = ((Element)iter.next()).getTextTrim();
            if (!"".equals(value)) {
                props.add(value);
            }
        }
        String[] childrenNames = new String[props.size()];
        return props.toArray(childrenNames);
    }

    /**
     * Return all values who's path matches the given property
     * name as a String array, or an empty array if the if there
     * are no children. This allows you to retrieve several values
     * with the same property name. For example, consider the
     * XML file entry:
     * <pre>
     * &lt;foo&gt;
     *     &lt;bar&gt;
     *         &lt;prop&gt;some value&lt;/prop&gt;
     *         &lt;prop&gt;other value&lt;/prop&gt;
     *         &lt;prop&gt;last value&lt;/prop&gt;
     *     &lt;/bar&gt;
     * &lt;/foo&gt;
     * </pre>
     * If you call getProperties("foo.bar.prop") will return a string array containing
     * {"some value", "other value", "last value"}.
     *
     * @param name the name of the property to retrieve
     * @return all child property values for the given node name.
     */
    public Iterator getChildProperties(String name) {
        String[] propName = parsePropertyName(name);
        // Search for this property by traversing down the XML heirarchy,
        // stopping one short.
        Element element = document.getRootElement();
        for (int i = 0; i < propName.length - 1; i++) {
            element = element.element(propName[i]);
            if (element == null) {
                // This node doesn't match this part of the property name which
                // indicates this property doesn't exist so return empty array.
                return Collections.EMPTY_LIST.iterator();
            }
        }
        // We found matching property, return values of the children.
        Iterator iter = element.elementIterator(propName[propName.length - 1]);
        ArrayList<String> props = new ArrayList<String>();
        while (iter.hasNext()) {
            props.add(((Element)iter.next()).getText());
        }
        return props.iterator();
    }

    /**
     * Returns the value of the attribute of the given property name or <tt>null</tt>
     * if it doesn't exist. Note, this
     *
     * @param name the property name to lookup - ie, "foo.bar"
     * @param attribute the name of the attribute, ie "id"
     * @return the value of the attribute of the given property or <tt>null</tt> if
     *      it doesn't exist.
     */
    public String getAttribute(String name, String attribute) {
        if (name == null || attribute == null) {
            return null;
        }
        String[] propName = parsePropertyName(name);
        // Search for this property by traversing down the XML heirarchy.
        Element element = document.getRootElement();
        for (String child : propName) {
            element = element.element(child);
            if (element == null) {
                // This node doesn't match this part of the property name which
                // indicates this property doesn't exist so return empty array.
                break;
            }
        }
        if (element != null) {
            // Get its attribute values
            return element.attributeValue(attribute);
        }
        return null;
    }


    /**
     * Return all children property names of a parent property as a String array,
     * or an empty array if the if there are no children. For example, given
     * the properties <tt>X.Y.A</tt>, <tt>X.Y.B</tt>, and <tt>X.Y.C</tt>, then
     * the child properties of <tt>X.Y</tt> are <tt>A</tt>, <tt>B</tt>, and
     * <tt>C</tt>.
     *
     * @param parent the name of the parent property.
     * @return all child property values for the given parent.
     */
    public String[] getChildrenProperties(String parent) {
        String[] propName = parsePropertyName(parent);
        // Search for this property by traversing down the XML heirarchy.
        Element element = document.getRootElement();
        for (String aPropName : propName) {
            element = element.element(aPropName);
            if (element == null) {
                // This node doesn't match this part of the property name which
                // indicates this property doesn't exist so return empty array.
                return new String[]{};
            }
        }
        // We found matching property, return names of children.
        List children = element.elements();
        int childCount = children.size();
        String[] childrenNames = new String[childCount];
        for (int i = 0; i < childCount; i++) {
            childrenNames[i] = ((Element)children.get(i)).getName();
        }
        return childrenNames;
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
    
    public void destroy() {
        document = null;
        file = null;
        propertyCache.clear();
        propertyCache = null;
        
    }
}
