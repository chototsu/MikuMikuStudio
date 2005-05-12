/*
 * Created on Aug 23, 2004
 *
 */
package com.jmex.ui;

import java.util.Iterator;
import java.util.Vector;

import com.jme.input.InputHandler;
import com.jme.math.Vector3f;

/**
 * @author schustej
 *
 */
public abstract class UIActiveObject extends UIObject {

    public static final int DRAW_DOWN = 8;
    public static final int DRAW_OVER = 16;
    
    /*
     * Default set of states for active components
     */
    public static final int UP = 0;
    public static final int OVER = 1;
    public static final int DOWN = 2;
    public static final int SELECTED = 3;
    
    /*
     * Starting state, this may never change depending upon implementation
     */
    protected int _state = UP;
    
    /*
     * The Actions to trigger when UI response is needed
     */
    protected Vector _actions = null;
    
    /*
     * If using mouse and keyboard response
     */
    protected InputHandler _inputHandler = null;

    /*
     * Used with mouse response
     */
    protected UIActiveArea _hitArea = null;
    
    public UIActiveObject( String name, int x, int y, int width, int height, InputHandler inputHandler, UIColorScheme scheme, int flags) {
        super( name, x, y, width, height, scheme, flags);
        
        _inputHandler = inputHandler;
        
        _actions = new Vector();
        
        if( _inputHandler != null) {
            _hitArea = new UIActiveArea( name+"ActiveArea", _x, _y, _width, _height, _inputHandler);
            attachChild( _hitArea);
        }
    }
    
    /**
     * Add an inputaction that will be fired when
     * the control changes state
     * @param action
     */
    public void addAction( UIInputAction action) {
        _actions.add( action);
    }

    /**
     * removes a given input action
     * @param action
     */
    public void removeAction( UIInputAction action) {
        _actions.remove( action);
    }

    /**
     * When called, all registered actions will be fired
     * using 'this' object as the reference from where
     * the fire was called.
     *
     */
    protected void fireActions() {
        Iterator actionIter = _actions.iterator();
		while (actionIter.hasNext()) {
			((UIInputAction) actionIter.next()).performAction( this);
		}
    }
    
    /**
     * Checks the hitarea for the mouse location
     * @return
     */
    protected boolean hitTest() {
        return _hitArea.hitTest();
    }

    /**
     * returns the current state of the control
     * @return
     */
    public int getState() {
        return _state;
    }

    /**
     * Places the center of the control at the given screen location
     * @return
     */
    public void centerAt(int x, int y) {
        this.localTranslation.set(x, y, 0);

        _x = (int) (x - _width / 2);
        _y = (int) (y - _height / 2);

        _hitArea._x = _x;
        _hitArea._y = _y;
    }

    /**
     * Moves the quad around the screen, properly setting local params at the
     * same time.
     */
    public void setLocation(int x, int y) {
      _x = x;
      _y = y;
      setLocalTranslation(new Vector3f(_x + _width / 2, _y + _height / 2,
                                       0.0f));
      _hitArea._x = _x;
      _hitArea._y = _y;
    }
    
    public void setAltBorderColors() {
        
        if( usingStdBorder()) {
            _leftborder.setColor( 0, _scheme._borderdarkcolor);
            _leftborder.setColor( 1, _scheme._borderdarkcolor);
            
            _topborder.setColor( 0, _scheme._borderdarkcolor);
            _topborder.setColor( 1, _scheme._borderdarkcolor);
            
            _rightborder.setColor( 0, _scheme._borderlightcolor);
            _rightborder.setColor( 1, _scheme._borderlightcolor);
            
            _bottomborder.setColor( 0, _scheme._borderlightcolor);
            _bottomborder.setColor( 1, _scheme._borderlightcolor);
        } else if( usingInverseBorder()) {
            _leftborder.setColor( 0, _scheme._borderlightcolor);
            _leftborder.setColor( 1, _scheme._borderlightcolor);
            
            _topborder.setColor( 0, _scheme._borderlightcolor);
            _topborder.setColor( 1, _scheme._borderlightcolor);
            
            _rightborder.setColor( 0, _scheme._borderdarkcolor);
            _rightborder.setColor( 1, _scheme._borderdarkcolor);
            
            _bottomborder.setColor( 0, _scheme._borderdarkcolor);
            _bottomborder.setColor( 1, _scheme._borderdarkcolor);
        }
        _leftborder.updateColorBuffer();
        _topborder.updateColorBuffer();
        _rightborder.updateColorBuffer();
        _bottomborder.updateColorBuffer();
    }
    
    public void setBaseBorderColors() {
        if( usingStdBorder()) {
            _leftborder.setColor( 0, _scheme._borderlightcolor);
            _leftborder.setColor( 1, _scheme._borderlightcolor);
            
            _topborder.setColor( 0, _scheme._borderlightcolor);
            _topborder.setColor( 1, _scheme._borderlightcolor);
            
            _rightborder.setColor( 0, _scheme._borderdarkcolor);
            _rightborder.setColor( 1, _scheme._borderdarkcolor);
            
            _bottomborder.setColor( 0, _scheme._borderdarkcolor);
            _bottomborder.setColor( 1, _scheme._borderdarkcolor);
        } else if( usingInverseBorder()) {
            _leftborder.setColor( 0, _scheme._borderdarkcolor);
            _leftborder.setColor( 1, _scheme._borderdarkcolor);
            
            _topborder.setColor( 0, _scheme._borderdarkcolor);
            _topborder.setColor( 1, _scheme._borderdarkcolor);
            
            _rightborder.setColor( 0, _scheme._borderlightcolor);
            _rightborder.setColor( 1, _scheme._borderlightcolor);
            
            _bottomborder.setColor( 0, _scheme._borderlightcolor);
            _bottomborder.setColor( 1, _scheme._borderlightcolor);
        }
        _leftborder.updateColorBuffer();
        _topborder.updateColorBuffer();
        _rightborder.updateColorBuffer();
        _bottomborder.updateColorBuffer();
    }
    
    public void setHighlightColors() {
        _quad.setColor( 0, _scheme._highlightbackgroundcolor);
        _quad.setColor( 1, _scheme._highlightbackgroundcolor);
        _quad.setColor( 2, _scheme._highlightbackgroundcolor);
        _quad.setColor( 3, _scheme._highlightbackgroundcolor);
        
        _quad.updateColorBuffer();
    }
    
    public void setBaseColors() {
        _quad.setColor( 0, _scheme._backgroundcolor);
        _quad.setColor( 1, _scheme._backgroundcolor);
        _quad.setColor( 2, _scheme._backgroundcolor);
        _quad.setColor( 3, _scheme._backgroundcolor);
        
        _quad.updateColorBuffer();
    }
    
    /**
     * Abstract, must be implemented in the extentions
     * @return
     */
    public abstract boolean update( float time);

}
