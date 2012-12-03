/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.projectkyoto.mms.asset.manifest;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author kobayasi
 */
public class Manifest implements Serializable{
    Map<String, ProductNode> productMap = new HashMap<String, ProductNode>();
    String version;
    int serialNo;
    public Manifest() {
    }

    public Map<String, ProductNode> getProductMap() {
        return productMap;
    }

    public void setProductMap(Map<String, ProductNode> assetsMap) {
        this.productMap = assetsMap;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Manifest other = (Manifest) obj;
        if (this.productMap != other.productMap && (this.productMap == null || !this.productMap.equals(other.productMap))) {
            return false;
        }
        if ((this.version == null) ? (other.version != null) : !this.version.equals(other.version)) {
            return false;
        }
        if (this.serialNo != other.serialNo) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + (this.version != null ? this.version.hashCode() : 0);
        hash = 23 * hash + this.serialNo;
        return hash;
    }

    @Override
    public String toString() {
        return "Manifest{" + "productMap=" + productMap + ", version=" + version + ", serialNo=" + serialNo + '}';
    }


    public int getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(int serialNo) {
        this.serialNo = serialNo;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
    
}
