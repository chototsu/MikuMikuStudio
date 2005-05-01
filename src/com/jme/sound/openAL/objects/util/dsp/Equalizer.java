package com.jme.sound.openAL.objects.util.dsp;

import java.util.ArrayList;
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
