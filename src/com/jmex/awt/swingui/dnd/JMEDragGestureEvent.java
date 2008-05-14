package com.jmex.awt.swingui.dnd;

import java.awt.Point;
import java.awt.event.MouseEvent;

/**
 * @author Galun
 */
public class JMEDragGestureEvent {

    private JMEMouseDragGestureRecognizer recognizer;
    private int action;
    private Point point;
    private MouseEvent event;

    public JMEDragGestureEvent( JMEMouseDragGestureRecognizer recognizer, int action, Point point, MouseEvent event ) {
        this.recognizer = recognizer;
        this.action = action;
        this.point = point;
        this.event = event;
    }

    public JMEMouseDragGestureRecognizer getRecognizer() {
        return recognizer;
    }

    public int getAction() {
        return action;
    }

    public MouseEvent getEvent() {
        return event;
    }

    public Point getPoint() {
        return point;
    }
}
