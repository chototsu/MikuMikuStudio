/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the Mojo Monkey Coding, jME, jMonkey Engine, nor the
 * names of its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
/*
 * Created on 14 juin 2004
 */
package com.jme.sound;

import java.net.URL;
import java.util.Hashtable;

/**
 * @author Arman
 *
 */
public class SoundPool {

    private static Hashtable table = new Hashtable();

    private static IBuffer[][] programs;

    private static float[] programDuration;

    public static IBuffer getBuffer(String fileName) {
        IBuffer loaded = null;
        if (!table.containsKey(fileName)) {
            loaded = SoundAPIController.getSoundSystem().loadBuffer(fileName);
            table.put(fileName, loaded);
        }

        return loaded;
    }

    public static IBuffer getBuffer(URL url) {
        IBuffer loaded = null;
        if (!table.containsKey(url.getFile())) {
            loaded = SoundAPIController.getSoundSystem().loadBuffer(url);

            table.put(url.getFile(), loaded);
        } else {
          loaded = (IBuffer)table.get(url.getFile());
        }

        return loaded;
    }

    public static int compile(URL[] url) {
        IBuffer[] tmp = new IBuffer[url.length];
        for (int a = 0; a < tmp.length; a++) {
            tmp[a] = getBuffer(url[a]);
        }
        return compile(tmp);
    }

    public static int compile(String[] files) {
        IBuffer[] tmp = new IBuffer[files.length];
        for (int a = 0; a < tmp.length; a++) {
            tmp[a] = getBuffer(files[a]);
        }
        return compile(tmp);
    }

    public static int compile(IBuffer[] sequence) {
        if (programs == null) {
            programs = new IBuffer[1][];
            programs[0] = sequence;
            programDuration=new float[1];
            for(int a=0; a<sequence.length; a++)
                programDuration[0]+=sequence[a].getDuration();
            return 0;

        } else {
            float[] durationTmp=new float[programs.length + 1];
            System.arraycopy(programDuration, 0, durationTmp, 0, programDuration.length);

            IBuffer[][] tmp = new IBuffer[programs.length + 1][];
            for (int a = 0; a < programs.length; a++) {
                tmp[a] = programs[a];
            }
            for (int a = 0; a < sequence.length; a++) {
                durationTmp[programDuration.length]+=sequence[a].getDuration();
            }
            tmp[programs.length] = sequence;
            programs = tmp;
            programDuration=durationTmp;
        }
        return programs.length - 1;
    }

    public static IBuffer[] getProgram(int programNumber) {
        return programs[programNumber];
    }

    public static float getProgramDuration(int programNumber){
        return programDuration[programNumber];
    }

}
