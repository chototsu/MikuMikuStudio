package com.jme.scene.model.XMLparser;

import com.jme.system.JmeException;

import java.io.InputStream;
import java.io.Writer;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Started Date: Jun 23, 2004<br><br>
 *
 * This class converts jME's binary format to human readable XML
 *
 * @author Jack Lindamood
 */
public class BinaryToXML {
    DataInputStream myIn;
    Writer XMLFile;
    short tabCount;
    StringBuffer currentLine;
    private static final boolean DEBUG = false;

    public BinaryToXML(){

    }

    /**
     * The only function a user needs to use.  Pass in an inputstream that represents jME's binary format,
     * and <code>BinaryToXML.sendBinarytoXML</code> will write the stream to XML format.
     * @param binFile The input stream of the jME file
     * @param XML Where to write the XML format too.
     */
    public void sendBinarytoXML (InputStream binFile,Writer XML){
        myIn=new DataInputStream(binFile);
        XMLFile=XML;
        tabCount=0;
        currentLine=new StringBuffer();
        try {
            readHeader();
            while (readPart());
            XML.close();
        } catch (IOException e) {
            throw new JmeException("Error trying to read from binFile: " + e.getMessage());
        }
    }

    private boolean readPart() throws IOException{
        byte flag=myIn.readByte();
        if (flag==BinaryFormatConstants.BEGIN_TAG){
            String currentTag=myIn.readUTF();
            if (DEBUG) System.out.println("curTag:" + currentTag+"***");
            currentLine.append('<').append(currentTag).append(' ');
            int numTags=myIn.readByte();
            for (int i=0;i<numTags;i++){
                String currentAttrib=myIn.readUTF();
                if (DEBUG) System.out.println("curAttrib:" + currentAttrib + "***");
                currentLine.append(currentAttrib).append("=\"");
                readData();
                currentLine.append("\" ");
            }
            currentLine.append('>');
            writeLine();

            tabCount++;
            return true;
        } else if (flag==BinaryFormatConstants.END_TAG){
            tabCount--;
            currentLine.append("</").append(myIn.readUTF()).append('>');
            writeLine();

            return true;
        }else if (flag==BinaryFormatConstants.END_FILE){
            return false;
        }else{
            throw new JmeException("Unknown flag read: " + flag);
        }
    }

    private void readData() throws IOException {
        byte type=myIn.readByte();
        switch (type){
            case BinaryFormatConstants.DATA_STRING:
                currentLine.append(myIn.readUTF());
                break;
            case BinaryFormatConstants.DATA_COLORARRAY:
                readColorArray();
                break;
            case BinaryFormatConstants.DATA_INTARRAY:
                readIntArray();
                break;
            case BinaryFormatConstants.DATA_V2FARRAY:
                readVec2fArray();
                break;
            case BinaryFormatConstants.DATA_V3FARRAY:
                readVec3fArray();
                break;
            case BinaryFormatConstants.DATA_FLOAT:
                currentLine.append(myIn.readFloat());
                break;
            case BinaryFormatConstants.DATA_QUAT:
                readQuat();
                break;
            case BinaryFormatConstants.DATA_COLOR:
                readColor();
                break;
            case BinaryFormatConstants.DATA_URL:
                currentLine.append(myIn.readUTF());
                break;
            case BinaryFormatConstants.DATA_V3F:
                readVec3f();
                break;
            case BinaryFormatConstants.DATA_INT:
                currentLine.append(myIn.readInt());
                break;
            default:
                throw new IOException("Unknown data type:" + type);
        }
    }

    private void readColor() throws IOException {
        currentLine.append(myIn.readFloat()).append(' ').append(myIn.readFloat());
        currentLine.append(' ').append(myIn.readFloat()).append(' ').append(myIn.readFloat());
    }

    private void readQuat() throws IOException {
        currentLine.append(myIn.readFloat()).append(' ').append(myIn.readFloat());
        currentLine.append(' ').append(myIn.readFloat()).append(' ').append(myIn.readFloat());
    }

    private void readVec3f() throws IOException {
        currentLine.append(myIn.readFloat()).append(' ').append(myIn.readFloat());
        currentLine.append(' ').append(myIn.readFloat());
    }


    private void readIntArray() throws IOException{
        short length=myIn.readShort();
        for (int i=0;i<length;i++){
            currentLine.append(Integer.toString(myIn.readInt()));
            if (i!=length-1) currentLine.append(' ');
        }
    }

    private void readVec2fArray() throws IOException {
        short length=myIn.readShort();
        for (int i=0;i<length;i++)
            for (int j=0;j<2;j++){
                currentLine.append(Float.toString(myIn.readFloat()));
                if (i!=length-1 || j!=1) currentLine.append(' ');
            }
    }

    private void readColorArray() throws IOException {
        short length=myIn.readShort();
        for (int i=0;i<length;i++)
            for (int j=0;j<4;j++){
                currentLine.append(Float.toString(myIn.readFloat()));
                if (i!=length-1 || j!=3) currentLine.append(' ');
            }
    }

    private void readVec3fArray() throws IOException {
        short length=myIn.readShort();
        for (int i=0;i<length;i++){
            for (int j=0;j<3;j++){
                currentLine.append(Float.toString(myIn.readFloat()));
                if (i!=length-1 || j!=2) currentLine.append(' ');
            }
        }
    }

    private void readHeader() throws IOException {
        if (BinaryFormatConstants.BEGIN_FILE!=myIn.readLong()){
            throw new JmeException("Header data doesn't match");
        }
    }

    private void writeLine() throws IOException {
        for (int i=0;i<tabCount;i++)
            XMLFile.write('\t');
        if (DEBUG) System.out.println("PRINTED LINE:" + currentLine + "***");
        currentLine.append('\n');
        XMLFile.write(currentLine.toString());
        currentLine.setLength(0);
    }
}