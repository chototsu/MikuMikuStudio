package com.jmex.font3d;

public interface TextFactory {
    /**
     * Method for creating new Text-objects.
     * 
     * @param text
     *            the text that should be visualized.
     * @param size
     *            the size of the text generated.
     * @param flags
     *            Can be Font.BOLD, Font.ITALIC, Font.PLAIN
     * @return the new text object.
     */
    JmeText createText(String text, float size, int flags);
}
