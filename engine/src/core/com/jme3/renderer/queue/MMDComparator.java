/*
 * Copyright (c) 2010-2014, Kazuhiko Kobayashi
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jme3.renderer.queue;

import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import projectkyoto.jme3.mmd.PMDGeometry;

import java.util.logging.Logger;

/**
 * Created by kobayasi on 2014/05/14.
 */
public class MMDComparator implements GeometryComparator{
    private static final Logger logger = Logger.getLogger(MMDComparator.class.getName());
    @Override
    public void setCamera(Camera cam) {

    }
    private int calcDistance(Geometry geom) {
        if (geom instanceof PMDGeometry) {
            return ((PMDGeometry) geom).getMaterialNo();
        } else {
            throw new RuntimeException("Geometry is not a PMDGeometry.");
        }
    }

    @Override
    public int compare(Geometry o1, Geometry o2) {
        int dist1 = calcDistance(o1);
        int dist2 = calcDistance(o2);

        if (dist1 == dist2) {
            return 0;
        } else if (dist1 < dist2) {
            return -1;
        } else {
            return 1;
        }
    }
}
