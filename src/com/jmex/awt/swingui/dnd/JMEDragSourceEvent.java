package com.jmex.awt.swingui.dnd;

import java.awt.Point;

/**
 * @author Galun
 */
public class JMEDragSourceEvent {

    private Point point;
    private int action;
    private boolean dropSuccess;

    public JMEDragSourceEvent( Point point, int action ) {
        this.point = point;
        this.action = action;
    }

    public JMEDragSourceEvent( Point point, int action, boolean success ) {
        this.point = point;
        this.action = action;
        this.dropSuccess = success;
    }

    public Point getPoint() {
        return point;
    }

    public int getAction() {
        return action;
    }

    protected void setDropSuccess( boolean success ) {
        dropSuccess = success;
    }

    public boolean getDropSuccess() {
        return dropSuccess;
    }
}
