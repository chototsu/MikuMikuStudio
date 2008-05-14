package com.jmex.awt.swingui.dnd;

/**
 * @author Galun
 */
public interface JMEDragSourceListener {

    /**
     * a transferable is dragged and enters the geometry of an object
     *
     * @param e the event describing what happened
     */
    public void dragEnter( JMEDragSourceEvent e );

    /**
     * a transferable is dragged and leaves the geometry of an object
     *
     * @param e the event describing what happened
     */
    public void dragExit( JMEDragSourceEvent e );

    /**
     * a drag operation ends
     *
     * @param e the event describing what happened
     */
    public void dragDropEnd(JMEDragSourceEvent e);
}
