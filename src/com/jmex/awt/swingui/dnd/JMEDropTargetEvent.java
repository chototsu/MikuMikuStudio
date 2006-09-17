package com.jmex.awt.swingui.dnd;

import java.awt.Point;
import java.awt.datatransfer.Transferable;

/**
 * @author Galun
 */
public class JMEDropTargetEvent {

    private Point point;
    private int action;
    private boolean accepted;
    private boolean completed;
    private JMEDragAndDrop dnd;

    JMEDropTargetEvent( Point point, int action, JMEDragAndDrop dnd ) {
        this.point = point;
        this.action = action;
        this.dnd = dnd;
    }

    public Point getPoint() {
        return point;
    }

    public int getAction() {
        return action;
    }

    public void acceptDrop( int action ) {
        accepted = true;
    }

    public void dropComplete( boolean success ) {
        completed = success;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public Transferable getTransferable() {
        return dnd.getTransferable();
    }

    public boolean isCompleted() {
        return completed;
    }
}
