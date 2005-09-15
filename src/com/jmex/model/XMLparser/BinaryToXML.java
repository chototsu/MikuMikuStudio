/*
 * Copyright (c) 2003-2005 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jmex.model.XMLparser;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

/**
 * Started Date: Jun 23, 2004<br><br>
 *
 * This class converts jME's binary format to human readable XML.
 *
 * @author Jack Lindamood
 */
public class BinaryToXML {
    private DataInputStream myIn;
    private Writer XMLFile;
    private short tabCount;
    
    /**
     * <code>currentLine</code> contains the String that will be finally written to the XML file via writeLine.
     */
    private StringBuffer currentLine;
    private static final boolean DEBUG = false;


    /**
     * Converts jME binary to XML.  The syntax is: "BinaryToXML file.jme out.xml"
     * @param args 2 strings.  The first is the jME binary file to read, the second is the place to place its output.
     */
    public static void main(String[] args) {
        if (args.length!=2){
            System.err.println("Correct way to use is: <FormatFile> <XMLoutputout>");
            System.err.println("For example: runner.jme runner.xml");
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
            new BinaryToXML().sendBinarytoXML(new FileInputStream(inFile),new FileWriter(outFile));
        } catch (IOException e) {
            System.err.println("Unable to convert:" + e);
            return;
        }
        System.out.println("Conversion complete!");
    }


    /**
     * The only function a user needs to use.  Pass in an inputstream that represents jME's binary format,
     * and <code>BinaryToXML.sendBinarytoXML</code> will write the stream to XML format.
     * @param binFile The input stream of the jME file.
     * @param XML Where to write the XML format too.
     * @throws IOException If unable to read or write either file.
     */
    public void sendBinarytoXML (InputStream binFile,Writer XML) throws IOException {
        myIn=new DataInputStream(binFile);
        XMLFile=XML;
        tabCount=0;
        currentLine=new StringBuffer();
        readHeader();
        while (readPart());
        XML.close();
    }

    /**
     * Reads a block from jME's binary format.
     * @return True if it is valid to read another block, false if END_FILE block is reached
     * @throws IOException If anything funny goes on with writting.
     */
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
            throw new IOException("Unknown flag read: " + flag);
        }
    }

    /**
     * Reads a datablock, by first reading in its type.  The type defines how the rest of the block will be read
     * @throws IOException If anything wierd goes on while reading
     */
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
            case BinaryFormatConstants.DATA_BOOLEAN:
                currentLine.append(myIn.readBoolean());
                break;
            case BinaryFormatConstants.DATA_QUATARRAY:
                readQuatArray();
                break;
            case BinaryFormatConstants.DATA_BYTEARRAY:
                readByteArray();
                break;
            case BinaryFormatConstants.DATA_SHORTARRAY:
                readShortArray();
                break;
            case BinaryFormatConstants.DATA_V2F:
                readVec2f();
                break;
            case BinaryFormatConstants.DATA_MATRIX3:
                readMatrix3f();
                break;
            default:
                throw new IOException("Unknown data type:" + type);
        }
    }

    private void readMatrix3f() throws IOException {
        for (int i=0;i<9;i++){
            currentLine.append(myIn.readFloat());
            if (i!=8) currentLine.append(' ');
        }
    }

    private void readShortArray() throws IOException {
        int length=myIn.readInt();
        for (int i=0;i<length;i++){
            currentLine.append(myIn.readShort());
            if (i!=length-1) currentLine.append(' ');
        }
    }

    private void readByteArray() throws IOException {
        int length=myIn.readInt();
        for (int i=0;i<length;i++){
            currentLine.append(myIn.readByte());
            if (i!=length-1) currentLine.append(' ');
        }
    }

    private void readQuatArray() throws IOException {
        int length=myIn.readInt();
        if (DEBUG) System.out.println("reading Quat[].length=" + length);
        for (int i=0;i<length;i++)
            for (int j=0;j<4;j++){
                currentLine.append(Float.toString(myIn.readFloat()));
                if (i!=length-1 || j!=3) currentLine.append(' ');
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

    private void readVec2f() throws IOException {
        currentLine.append(myIn.readFloat()).append(' ').append(myIn.readFloat());
    }


    private void readIntArray() throws IOException{
        int length=myIn.readInt();
        if (DEBUG) System.out.println("reading int[].length=" + length);
        for (int i=0;i<length;i++){
            currentLine.append(Integer.toString(myIn.readInt()));
            if (i!=length-1) currentLine.append(' ');
        }
    }

    private void readVec2fArray() throws IOException {
        int length=myIn.readInt();
        if (DEBUG) System.out.println("reading Vec2f[].length=" + length);
        for (int i=0;i<length;i++)
            for (int j=0;j<2;j++){
                currentLine.append(Float.toString(myIn.readFloat()));
                if (i!=length-1 || j!=1) currentLine.append(' ');
            }
    }

    private void readColorArray() throws IOException {
        int length=myIn.readInt();
        if (DEBUG) System.out.println("reading ColorRGBA.length=" + length);
        for (int i=0;i<length;i++)
            for (int j=0;j<4;j++){
                currentLine.append(Float.toString(myIn.readFloat()));
                if (i!=length-1 || j!=3) currentLine.append(' ');
            }
    }

    private void readVec3fArray() throws IOException {
        int length=myIn.readInt();
        if (DEBUG) System.out.println("reading Vec3f[].length=" + length);
        for (int i=0;i<length;i++){
            for (int j=0;j<3;j++){
                currentLine.append(Float.toString(myIn.readFloat()));
                if (i!=length-1 || j!=2) currentLine.append(' ');
            }
        }
    }

    /**
     * Reads the file header.  Throws an IOException if the header doesn't match.
     * @throws IOException
     */
    private void readHeader() throws IOException {
        if (BinaryFormatConstants.BEGIN_FILE!=myIn.readLong()){
            throw new IOException("Header data doesn't match");
        }
    }

    /**
     * Takes currentLine, adds tabs to the begining, and writes it to the XMLFile.
     * @throws IOException
     */
    private void writeLine() throws IOException {
        for (int i=0;i<tabCount;i++)
            XMLFile.write('\t');
        if (DEBUG) System.out.println("PRINTED LINE:" + currentLine + "***");
        currentLine.append('\n');
        XMLFile.write(currentLine.toString());
        currentLine.setLength(0);
    }
}