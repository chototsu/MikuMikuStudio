package com.jmex.awt.swingui.dnd;

/**
 * @author Galun
 */
public interface JMEDropTargetListener {

    /**
     * a transferable is dragged and enters the geometry of an object
     *
     * @param e the event describing what happened
     */
    public void dragEnter( JMEDropTargetEvent e );

    /**
     * a transferable is dragged and leaves the geometry of an object
     *
     * @param e the event describing what happened
     */
    public void dragExit( JMEDropTargetEvent e );

	/**
	 * a transferable is dragged over the geometry of an object
	 * @param e the event describing what happened
	 */
	public void dragOver(JMEDropTargetEvent e);

	/**
     * a drag operation ends
     *
     * @param e the event describing what happened
     */
    public void drop(JMEDropTargetEvent e);
}
