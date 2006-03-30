/*Copyright*/
package com.jme.input.util;

import com.jme.input.ActionTrigger;
import com.jme.input.InputHandler;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;

/**
 * trigger for simulating button
 */
class SyntheticTrigger extends ActionTrigger {
    private SyntheticTriggerContainer container;
    private boolean permanentlyActiveIfRepeats;

    public SyntheticTrigger( SyntheticTriggerContainer container, InputHandler handler, InputAction action,
                             boolean allowRepeats, boolean permanentlyActiveIfRepeats ) {
        super( handler, container.getName(), action, allowRepeats );
        this.container = container;
        container.add( this );
        this.permanentlyActiveIfRepeats = permanentlyActiveIfRepeats;
        if ( permanentlyActiveIfRepeats ) {
            if ( allowRepeats ) {
                activate();
            }
        }
        infos[0] = new TriggerInfo();
    }

    private int count;
    private TriggerInfo[] infos = new TriggerInfo[1];

    protected int getActionInvocationCount() {
        return count;
    }

    protected void remove() {
        super.remove();
        container.remove( this );
    }

    public void performAction( InputActionEvent event ) {
        super.performAction( event );
        if ( !allowRepeats ) {
            count = 0;
        }
    }

    protected void putTriggerInfo( InputActionEvent event, int invocationIndex ) {
        super.putTriggerInfo( event, invocationIndex );
        event.setTriggerIndex( container.getIndex() );
        TriggerInfo info = infos[invocationIndex];
        event.setTriggerCharacter( info.character );
        event.setTriggerPressed( info.pressed );
        event.setTriggerDelta( info.delta );
        event.setTriggerPosition( info.position );
        event.setTriggerData( info.data );
    }

    protected final String getDeviceName() {
        return UtilInputHandlerDevice.DEVICE_UTIL;
    }

    public void checkActivation( char character, int buttonIndex, float position, float delta, boolean pressed, Object data ) {
        if ( buttonIndex == container.getIndex() ) {
            TriggerInfo info;
            if ( allowRepeats ) {
                info = infos[0];
                count = 1;
            }
            else {
                if ( count >= infos.length ) {
                    TriggerInfo[] oldInfos = infos;
                    infos = new TriggerInfo[ count + 3 ];
                    System.arraycopy( oldInfos, 0, infos, 0, oldInfos.length );
                    for ( int i = oldInfos.length; i < infos.length; i++ ) {
                        infos[i] = new TriggerInfo();
                    }
                }
                info = infos[count];
                count++;
            }
            info.character = character;
            info.position = position;
            info.delta = delta;
            info.pressed = pressed;
            info.data = data;
            if ( !allowRepeats ) {
                activate();
            } else if ( !permanentlyActiveIfRepeats ) {
                if ( pressed ) {
                    activate();
                }
                else {
                    deactivate();
                }
            }
        }
    }
}

/*
 * $log$
 */

