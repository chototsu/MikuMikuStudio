/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.projectkyoto.mms.asset.manifest;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author kobayasi
 */
public class ProductNode implements Serializable{
    Map<String, FileNode> fileMap = new HashMap<String, FileNode>();
    String name;
    String developerName;
    String description;
    String language;
    public ProductNode() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDeveloperName() {
        return developerName;
    }

    public void setDeveloperName(String developerName) {
        this.developerName = developerName;
    }

    public Map<String, FileNode> getFileMap() {
        return fileMap;
    }

    public void setFileMap(Map<String, FileNode> fileMap) {
        this.fileMap = fileMap;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ProductNode{" + "fileMap=" + fileMap + ", name=" + name + ", developerName=" + developerName + ", description=" + description + ", language=" + language + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ProductNode other = (ProductNode) obj;
        if (this.fileMap != other.fileMap && (this.fileMap == null || !this.fileMap.equals(other.fileMap))) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.developerName == null) ? (other.developerName != null) : !this.developerName.equals(other.developerName)) {
            return false;
        }
        if ((this.description == null) ? (other.description != null) : !this.description.equals(other.description)) {
            return false;
        }
        if ((this.language == null) ? (other.language != null) : !this.language.equals(other.language)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }
    
}
