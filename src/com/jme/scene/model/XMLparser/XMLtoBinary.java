package com.jme.scene.model.XMLparser;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

/**
 * Started Date: Jun 23, 2004<br><br>
 * This class can read a XML file and convert it into jME binary format.
 *
 * @author Jack Lindamood
 */
public class XMLtoBinary {
    private DataOutputStream myOut;

    /**
     * Converts an XML file to jME binary.  The syntax is: "XMLtoBinary file.xml out.jme"
     * @param args 2 strings.  The first is the XML file to read, the second is the place to place its output.
     */
    public static void main(String[] args) {
        if (args.length!=2){
            System.err.println("Correct way to use is: <FormatFile> <jmeoutputfile>");
            System.err.println("For example: runner.xml runner.jme");
            return;
        }
        File inFile=new File(args[0]);
        File outFile=new File(args[1]);
        if (!inFile.canRead()){
            System.err.println("Cannot read input file " + inFile);
            return;
        }
        try {
            System.out.println("Converting file " + inFile + " to " + outFile);
            new XMLtoBinary().sendXMLtoBinary(new FileInputStream(inFile),new FileOutputStream(outFile));
        } catch (IOException e) {
            System.err.println("Unable to convert:" + e);
            return;
        }
        System.out.println("Conversion complete!");
    }

    /**
     * Creates a new XMl -> Binary object.
     */
    public XMLtoBinary(){

    }

    /**
     * The only function a user needs to call.  It converts an XML file to jME's binary format
     * @param XmlStream A stream representing the XML file to convert
     * @param binFile The stream to save the XML's jME binary equivalent
     */
    public void sendXMLtoBinary(InputStream XmlStream,OutputStream binFile){
        myOut=new DataOutputStream(binFile);
        SAXParserFactory factory=SAXParserFactory.newInstance();
        factory.setValidating(true);
        try {
            SAXParser parser=factory.newSAXParser();
            parser.parse(XmlStream,new SAXConverter());
        } catch (IOException e) {
            throw new RuntimeException("Unable to do IO correctly:" + e.getMessage());
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Serious parser configuration error:" + e.getMessage());
        } catch (SAXParseException e) {
            throw new RuntimeException(e.toString() +'\n' + "Line: " +e.getLineNumber() + '\n' + "Column: " + e.getColumnNumber());
        }catch (SAXException e) {
            throw new RuntimeException("Unknown sax error: " + e.getMessage());
        }
    }

    /**
     * This class reads XML files in a SAX manner.
     */
    private class SAXConverter extends DefaultHandler{
        private static final boolean DEBUG = false;

        public void startDocument() throws SAXException {
            try {
                myOut.writeLong(BinaryFormatConstants.BEGIN_FILE);
            } catch (IOException e) {
                throw new SAXException("Unable to write header:" + e);
            }
        }

        public void endDocument() throws SAXException {
            try {
                myOut.writeByte(BinaryFormatConstants.END_FILE);
                myOut.close();
            } catch (IOException e) {
                throw new SAXException("Unable to close binFile outStream:"+e);
            }
        }

        public void startElement(String uri,String localName,String qName, Attributes atts) throws SAXException{
            if (DEBUG) System.out.println("startElement:" + qName);
            try {
                myOut.writeByte(BinaryFormatConstants.BEGIN_TAG);
                myOut.writeUTF(qName);
                myOut.writeByte(atts.getLength());
                for (int i=0;i<atts.getLength();i++){
                    String currentAtt=atts.getQName(i);
                    myOut.writeUTF(currentAtt);
                    processWrite(qName,currentAtt,atts.getValue(i));
                }
            } catch (IOException e) {
                throw new SAXException("Unable to write start element: " + qName + " " + e);
            }
        }

        /**
         * Takes a tag's name, an attribute's name, and the value of the attribute and writes it in the
         * appropriate manner.  For example, (&lt vertex data="blarg">)
         * @param qName Name of tag (example "vertex" )
         * @param att Name of attribute (example "data" )
         * @param value Name of the value of the attribute (example "blarg" )
         * @throws IOException
         */
        private void processWrite(String qName,String att, String value) throws IOException {
            if (DEBUG) System.out.println("processWrite qName=" + qName + "**att=" + att + "**value="+ value+"**");
            if ("data".equals(att)){
                if ("vertex".equals(qName) || "normal".equals(qName) || "origvertex".equals(qName)|| "orignormal".equals(qName)){
                    writeVector3fArray(value);
                } else if ("color".equals(qName)){
                    writeColorArray(value);
                } else if ("texturecoords".equals(qName)){
                    writeVector2fArray(value);
                } else if ("index".equals(qName) || "jointindex".equals(qName)){
                    writeIntArray(value);
                } else{
                    writeString(value);
                }
            } else if ("loc".equals(att) || "dir".equals(att) || "scale".equals(att) || "translation".equals(att) || "trans".equals(att) || "localvec".equals(att)){
                writeVec3f(value);
            } else if ("rotation".equals(att) || "rot".equals(att) || "localrot".equals(att)){
                writeQuat(value);
            } else if ("alpha".equals(att) || "shiny".equals(att) || "time".equals(att)){
                writeFloat(value);
            } else if ("fconstant".equals(att) || "flinear".equals(att) || "fquadratic".equals(att) || "fangle".equals(att) || "fexponent".equals(att)){
                writeFloat(value);
            } else if ("ambient".equals(att) || "diffuse".equals(att) || "emissive".equals(att) || "specular".equals(att)){
                writeColor(value);
            } else if ("URL".equals(att)){
                writeURL(value);
            } else if ("isattenuate".equals(att)){
                writeBoolean(value);
            } else if ("numJoints".equals(att) || "index".equals(att) || "parentindex".equals(att)){
                writeInt(value);
            } else if ("v3farray".equals(att)){
                writeVector3fArray(value);
            } else if ("quatarray".equals(att)){
                writeQuatArray(value);
            } else if ("q3norm".equals(att)){
                writeShortArray(value);
            } else if ("q3vert".equals(att)){
                writeByteArray(value);
            }
                writeString(value);
        }
    }


    private void writeShortArray(String data) throws IOException {
            myOut.writeByte(BinaryFormatConstants.DATA_SHORTARRAY);
            if (data==null || data.length()==0) {
                myOut.writeInt(0);
                return;
            }
            String [] information=removeDoubleWhiteSpaces(data).trim().split(" ");
            if (information.length==1 && "".equals(information[0])){
                myOut.writeInt(0);
                return;
            }
            myOut.writeInt(information.length);
            for (int i=0;i<information.length;i++){
                myOut.writeShort(Short.parseShort(information[i]));
            }
        }

        private void writeByteArray(String data) throws IOException {
            myOut.writeByte(BinaryFormatConstants.DATA_BYTEARRAY);
            if (data==null || data.length()==0) {
                myOut.writeInt(0);
                return;
            }
            String [] information=removeDoubleWhiteSpaces(data).trim().split(" ");
            if (information.length==1 && "".equals(information[0])){
                myOut.writeInt(0);
                return;
            }
            myOut.writeInt(information.length);
            for (int i=0;i<information.length;i++){
                myOut.writeShort(Byte.parseByte(information[i]));
            }
        }


        private void writeQuatArray(String data) throws IOException {
            myOut.writeByte(BinaryFormatConstants.DATA_QUATARRAY);
            if (data==null || data.length()==0){
                myOut.writeInt(0);
                return;
            }
            String [] information=removeDoubleWhiteSpaces(data).trim().split(" ");
            if (information.length==1 && "".equals(information[0])){
                myOut.writeInt(0);
                return;
            }
            if (information.length%4!=0){
                throw new IOException("Quat length not modulus of 4: " + information.length);
            }
            myOut.writeInt(information.length/4);
            for (int i=0;i<information.length/4;i++){
                myOut.writeFloat(Float.parseFloat(information[i*3+0]));
                myOut.writeFloat(Float.parseFloat(information[i*3+1]));
                myOut.writeFloat(Float.parseFloat(information[i*3+2]));
                myOut.writeFloat(Float.parseFloat(information[i*3+3]));
            }
        }

        private void writeBoolean(String value) throws IOException {
            boolean toWrite;
            if ("true".equals(value))
                toWrite=true;
            else if ("false".equals(value))
                toWrite=false;
            else
                throw new IOException("Parameter must be true or false, not " + value);
            myOut.writeByte(BinaryFormatConstants.DATA_BOOLEAN);
            myOut.writeBoolean(toWrite);
        }

        private void writeInt(String value) throws IOException {
            myOut.write(BinaryFormatConstants.DATA_INT);
            myOut.writeInt(Integer.parseInt(value));
        }

        private void writeURL(String value) throws IOException {
            myOut.writeByte(BinaryFormatConstants.DATA_URL);
            myOut.writeUTF(value);
        }

        private void writeColor(String value) throws IOException{
            myOut.writeByte(BinaryFormatConstants.DATA_COLOR);
            String [] split=value.trim().split(" ");
            if (split.length!=4)
                throw new IOException("ilformated Color:" + value);
            myOut.writeFloat(Float.parseFloat(split[0]));
            myOut.writeFloat(Float.parseFloat(split[1]));
            myOut.writeFloat(Float.parseFloat(split[2]));
            myOut.writeFloat(Float.parseFloat(split[3]));
        }

        private void writeFloat(String value) throws IOException {
            myOut.writeByte(BinaryFormatConstants.DATA_FLOAT);
            myOut.writeFloat(Float.parseFloat(value));
        }

        private void writeQuat(String value) throws IOException {
            myOut.writeByte(BinaryFormatConstants.DATA_QUAT);
            String [] split=value.trim().split(" ");
            if (split.length!=4)
                throw new IOException("ilformated Quat:" + value);
            myOut.writeFloat(Float.parseFloat(split[0]));
            myOut.writeFloat(Float.parseFloat(split[1]));
            myOut.writeFloat(Float.parseFloat(split[2]));
            myOut.writeFloat(Float.parseFloat(split[3]));
        }

        private void writeVec3f(String value) throws IOException {
            myOut.writeByte(BinaryFormatConstants.DATA_V3F);
            String [] split=value.trim().split(" ");
            if (split.length!=3)
                throw new IOException("ilformated Vector3f:" + value);
            myOut.writeFloat(Float.parseFloat(split[0]));
            myOut.writeFloat(Float.parseFloat(split[1]));
            myOut.writeFloat(Float.parseFloat(split[2]));
        }

        private void writeString(String value) throws IOException {
            myOut.writeByte(BinaryFormatConstants.DATA_STRING);
            myOut.writeUTF(value);
        }

        private void writeIntArray(String data) throws IOException {
            myOut.writeByte(BinaryFormatConstants.DATA_INTARRAY);
            if (data==null || data.length()==0) {
                myOut.writeInt(0);
                return;
            }
            String [] information=removeDoubleWhiteSpaces(data).trim().split(" ");
            if (information.length==1 && "".equals(information[0])){
                myOut.writeInt(0);
                return;
            }
            myOut.writeInt(information.length);
            for (int i=0;i<information.length;i++){
                myOut.writeInt(Integer.parseInt(information[i]));
            }
        }

        private void writeVector2fArray(String data) throws IOException {
            myOut.writeByte(BinaryFormatConstants.DATA_V2FARRAY);
            if (data==null || data.length()==0){
                myOut.writeInt(0);
                return;
            }
            String [] information=removeDoubleWhiteSpaces(data).trim().split(" ");
            if (information.length==1 && "".equals(information[0])){
                myOut.writeInt(0);
                return;
            }
            if (information.length%2!=0){
                throw new IOException("Vector2f length not modulus of 2: " + information.length);
            }
            myOut.writeInt(information.length/2);
            for (int i=0;i<information.length/2;i++){
                myOut.writeFloat(Float.parseFloat(information[i*2+0]));
                myOut.writeFloat(Float.parseFloat(information[i*2+1]));
            }
        }

        public void writeColorArray(String data) throws IOException {
            myOut.writeByte(BinaryFormatConstants.DATA_COLORARRAY);
            if (data == null || data.length()==0){
                myOut.writeInt(0);
                return;
            }
            String [] information=removeDoubleWhiteSpaces(data).trim().split(" ");
            if (information.length==1 && "".equals(information[0])){
                myOut.writeInt(0);
                return;
            }
            if (information.length%4!=0){
                throw new IOException("Color length not modulus of 4: " + information.length);
            }
            myOut.writeInt(information.length/4);
            for (int i=0;i<information.length/4;i++){
                myOut.writeFloat(Float.parseFloat(information[i*4+0]));
                myOut.writeFloat(Float.parseFloat(information[i*4+1]));
                myOut.writeFloat(Float.parseFloat(information[i*4+2]));
                myOut.writeFloat(Float.parseFloat(information[i*4+3]));
            }
        }


        private void writeVector3fArray(String data) throws IOException{
            myOut.writeByte(BinaryFormatConstants.DATA_V3FARRAY);
            if (data==null || data.length()==0){
                myOut.writeInt(0);
                return;
            }
            String [] information=removeDoubleWhiteSpaces(data).trim().split(" ");
            if (information.length==1 && "".equals(information[0])){
                myOut.writeInt(0);
                return;
            }
            if (information.length%3!=0){
                throw new IOException("Vector3f length not modulus of 3: " + information.length);
            }
            myOut.writeInt(information.length/3);
            for (int i=0;i<information.length/3;i++){
                myOut.writeFloat(Float.parseFloat(information[i*3+0]));
                myOut.writeFloat(Float.parseFloat(information[i*3+1]));
                myOut.writeFloat(Float.parseFloat(information[i*3+2]));
            }
        }
        /**
         * Whenever two or more whitespaces are next to each other, they are removed and replaced by ' '.
         * @param data The string to remove from
         * @return A new, removed string
         */
        private String removeDoubleWhiteSpaces(String data) {
            StringBuffer toReturn=new StringBuffer();
            boolean whiteSpaceFlag=false;
            for (int i=0;i<data.length();i++){
                if (Character.isWhitespace(data.charAt(i))){
                    if (!whiteSpaceFlag && toReturn.length()!=0) toReturn.append(' ');
                    whiteSpaceFlag = true;
                } else{
                    toReturn.append(data.charAt(i));
                    whiteSpaceFlag=false;
                }
            }
            return toReturn.toString();
        }


        public void endElement(String uri,String localName, String qName) throws SAXException{
            try {
                if (DEBUG) System.out.println("endElement:" + qName);
                myOut.writeByte(BinaryFormatConstants.END_TAG);
                myOut.writeUTF(qName);
            } catch (IOException e) {
                throw new SAXException("Unable to write end element: " + qName + " " + e);
            }

        }
    }
}