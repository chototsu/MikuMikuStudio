package com.jmex.awt.swingui.dnd;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.jme.util.LoggingSystem;
import com.jmex.awt.swingui.JMEDesktop;

/**
 * Drag and Drop support for {@link JMEDesktop} see {@link jmetest.awt.swingui.dnd.TestJMEDragAndDrop} for an example.
 * @author Galun
 */
public class JMEDragAndDrop {

    private static Logger log = LoggingSystem.getLogger();
    private static JMEDragAndDrop instance;
    private JMEDragGestureEvent dge;
    private Transferable transferable;
    private JMEDragSourceListener dragSourceListener;
    private JMEDropTargetListener dropTargetListener;
    private boolean dndInProgress;
    private JMEDesktop desktop;
    private JPanel dragPanel;

    public JMEDragAndDrop( JMEDesktop desktop ) {
        dndInProgress = false;
        setDesktop( desktop );
    }

    private void setDesktop( JMEDesktop desktop ) {
        desktop.setDragAndDropSupport( this );
        this.desktop = desktop;
    }

    public Transferable getTransferable() {
        return transferable;
    }

    public boolean isDragPanel( Component c ) {
        return c != null && c == dragPanel;
    }

    public void startDrag( JMEDragGestureEvent dge, ImageIcon icon, Transferable transferable, JMEDragSourceListener listener ) throws JMEDndException {
        if ( dndInProgress ) {
            throw new JMEDndException( "drag and drop in progress" );
        }
        this.dge = dge;
        this.transferable = transferable;
        dragSourceListener = listener;
        dragPanel = new JPanel();
        dragPanel.setLayout( new BorderLayout() );
        dragPanel.setBorder( new EmptyBorder( 0, 0, 0, 0 ) );
        JLabel label = new JLabel( icon );
        label.setName( "dragLabel" );
        label.setSize( new Dimension( icon.getIconWidth(), icon.getIconHeight() ) );
        dragPanel.add( label );
        dragPanel.setPreferredSize( label.getSize() );
        dragPanel.setSize( label.getSize() );
        dragPanel.setVisible( true );
        dragPanel.validate();
        desktop.getJDesktop().add( dragPanel, Integer.MAX_VALUE );
        desktop.getJDesktop().setComponentZOrder( dragPanel, 0 );
        dndInProgress = true;
    }

    public boolean isDragging() {
        return dndInProgress;
    }

    public void doDrag( MouseEvent event ) {
        Point p = SwingUtilities.convertPoint( (Component) event.getSource(), event.getX(), event.getY(), desktop.getJDesktop() );
        p.x -= 16;
        p.y -= 16;
        dragPanel.setLocation( p );
    }

    public void doDrop( MouseEvent e ) {
        boolean dropSuccess = false;
        if ( dropTargetListener != null ) {
            JMEDropTargetEvent dte = new JMEDropTargetEvent( e.getPoint(), dge.getAction(), this );
            dropTargetListener.drop( dte );
            dropSuccess = dte.isCompleted();
        }
//		log.info("dropping, dropTargetListener=" + dropTargetListener);
        dragPanel.setVisible( false );
        desktop.getJDesktop().remove( dragPanel );
        dragSourceListener.dragDropEnd( new JMEDragSourceEvent( e.getPoint(), dge.getAction(), dropSuccess ) );
        dndInProgress = false;
        dropTargetListener = null;
    }

    public void mouseEntered( MouseEvent e ) {
//		try {
//			Component c = (Component)e.getSource();
//		log.info("source=" + c.getParent().getName() + "." + c.getName());
//		} catch (Exception ex) {}
        if ( e.getSource() instanceof JMEDropTargetListener ) {
            dropTargetListener = (JMEDropTargetListener) e.getSource();
            ( (JMEDropTargetListener) e.getSource() ).dragEnter( new JMEDropTargetEvent( e.getPoint(), dge.getAction(), this ) );
        }
        dragSourceListener.dragEnter( new JMEDragSourceEvent( e.getPoint(), dge.getAction() ) );
    }

    public void mouseExited( MouseEvent e ) {
        if ( e.getSource() instanceof JMEDropTargetListener ) {
            dropTargetListener = null;
            ( (JMEDropTargetListener) e.getSource() ).dragExit( new JMEDropTargetEvent( e.getPoint(), dge.getAction(), this ) );
        }
        dragSourceListener.dragExit( new JMEDragSourceEvent( e.getPoint(), dge.getAction() ) );
    }

    public static ImageIcon createTextIcon( JComponent c, String text ) {
        Font font = c.getFont();
        int mx = 2;
        int my = 1;
        int w = c.getFontMetrics( font ).stringWidth( text ) + mx * 2;
        int h = c.getFontMetrics( font ).getHeight() + my * 2;
        BufferedImage bi = new BufferedImage( w, h, BufferedImage.TYPE_4BYTE_ABGR );
        Graphics g = bi.getGraphics();
        g.setColor( Color.black );
        g.fillRect( 0, 0, w, h );
        g.setFont( font );
        g.setColor( Color.yellow );
        g.drawString( text, mx, h - my );
        log.info( "created a text image for " + text + ": " + bi.toString() );
//		try {
//			ImageIO.write(bi, "png", new File("/tmp/text.png"));
//		} catch (Exception ex) {}
        return new ImageIcon( bi );
    }

    public JMEDropTargetListener getDropTargetListener() {
        return dropTargetListener;
    }
}
