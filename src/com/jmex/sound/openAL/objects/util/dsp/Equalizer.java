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

package com.jmex.sound.openAL.objects.util.dsp;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Arman
 */
public class Equalizer {

    private int[]  frequencies;
    private Map filterMap=new HashMap();
    private double minDB;
    private double maxDB;
    private double range;
    
    
    /**
     * Creates an Equalizer with the given frequencies.
     * The frequencies are in Hz
     * For best results use an array like {50, 200, 800, 3200, 12800}
     * It is possible to use an array like {50, 100, 200, 400, 800, 1600, 3200, 6400, 12800}
     * but this will result to performance failure
     * @param frequencies the equalized frequencies
     * @param minDB the minimum DB gain for this equalizer
     * @param maxDB the maximum DB gain for this equalizer
     */
    public Equalizer(int[] frequencies, double minDB, double maxDB){
        this.frequencies=frequencies;
        this.minDB=minDB;
        this.maxDB=maxDB;
        range=maxDB-minDB;
       
    }

    public int[] getFrequencies() {
        return frequencies;
    }
    
    
    public  void addFilter(int streamIdent, Filter filter){
        filterMap.put(new Integer(streamIdent), filter);
    }
    
    protected Filter getFilter(int streamIdent){
        Iterator it=filterMap.keySet().iterator();
        while(it.hasNext()){
            Integer i=(Integer)it.next();
            if(i.intValue()==streamIdent){
                return (Filter)filterMap.get(i);
            }
        }
        return null;
    }
    
    public void setDBValue(int streamNumber, int frequency, double value){
        if(value<minDB){
            value=minDB;
        }
        if(value>maxDB){
            value=maxDB;
        }
        Filter filter=getFilter(streamNumber);
        if(filter !=null){
            int[] frequencies=filter.frequencies;
            for(int a=0; a<frequencies.length; a++){
                if(frequencies[a]==frequency){
                    double adjust=Math.pow(10, value/20);
                    adjust=adjust>=1.0 ? adjust : -adjust;
                    filter.setAdjustement(a, adjust);
                }
            }
        }
    }
    
}
