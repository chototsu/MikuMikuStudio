package com.jmex.awt.swingui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.event.SwingPropertyChangeSupport;

import com.jme.input.util.SyntheticButton;
import com.jme.input.InputHandler;
import com.jme.input.action.InputAction;

/**
 * This class is used to execute an action in jMEs update thread instead of the Swing thread.
 * @see #performAction(com.jme.input.action.InputActionEvent)
 */
public abstract class JMEAction extends InputAction implements Action, ActionListener {
    public JMEAction( String name, InputHandler inputHandler ) {
        button = new ActionButton( name );
        inputHandler.addAction( this, button.getDeviceName(), button.getIndex(), InputHandler.AXIS_NONE, false );
        delegate = new AbstractAction( name ) {
            public synchronized void addPropertyChangeListener( PropertyChangeListener listener ) {
                if (changeSupport == null) {
                    changeSupport = new SwingPropertyChangeSupport( JMEAction.this );
                }
                super.addPropertyChangeListener( listener );
            }

            public void actionPerformed( ActionEvent e ) {
            }
        };
    }

    private final ActionButton button;

    public SyntheticButton getButton() {
        return button;
    }

    private static class ActionButton extends SyntheticButton {

        public ActionButton( String name ) {
            super( name );
        }

        protected void trigger( float delta, char character, float value, boolean pressed, Object data ) {
            super.trigger( delta, character, value, pressed, data );
        }
    }

    private final Action delegate;

    public final void actionPerformed( ActionEvent e ) {
        button.trigger( 0, '\0', 0, true, null );
    }

    public final void addPropertyChangeListener( PropertyChangeListener listener ) {
        delegate.addPropertyChangeListener( listener );
    }

    public final Object getValue( String key ) {
        return delegate.getValue( key );
    }

    public boolean isEnabled() {
        return delegate.isEnabled();
    }

    public final void putValue( String key, Object value ) {
        delegate.putValue( key, value );
    }

    public final void removePropertyChangeListener( PropertyChangeListener listener ) {
        delegate.removePropertyChangeListener( listener );
    }

    public void setEnabled( boolean b ) {
        delegate.setEnabled( b );
    }
}
