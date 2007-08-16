package com.jme.scene.state;

import com.jme.scene.batch.GeomBatch;

/**
 * Logic responsible for transfering data from a batch to a shader before rendering
 * 
 * @author MrCoder
 */
public interface GLSLShaderDataLogic {
    /**
     * Responsible for transfering data from a batch to a shader before rendering
     * @param shader Shader to update with new data(setUniform/setAttribute)
     * @param batch batch to retrieve data from
     */
    void applyData(GLSLShaderObjectsState shader, GeomBatch batch);
}
