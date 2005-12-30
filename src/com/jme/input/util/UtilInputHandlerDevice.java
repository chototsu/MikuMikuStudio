package com.jme.input.util;

import com.jme.input.InputHandler;
import com.jme.input.InputHandlerDevice;
import com.jme.input.action.InputAction;

import java.util.ArrayList;

/**
 * 
 */
class UtilInputHandlerDevice extends InputHandlerDevice {

    public static final String DEVICE_UTIL = "Synthetic Input Device";

    public UtilInputHandlerDevice() {
        super(DEVICE_UTIL);
    }

    protected void createTriggers(InputAction action, int axisIndex, int buttonIndex, boolean allowRepeats,
                                  InputHandler inputHandler) {
        if (axisIndex != InputHandler.AXIS_NONE) {
            if (axisIndex != InputHandler.AXIS_ALL) {
                SyntheticAxis axis = (SyntheticAxis) axes.get(axisIndex);
                axis.createTrigger(inputHandler, action, allowRepeats);
            } else {
                for (int i = axes.size() - 1; i >= 0; i--) {
                    SyntheticAxis axis = (SyntheticAxis) axes.get(i);
                    axis.createTrigger(inputHandler, action, allowRepeats);
                }
            }
        }
        if (buttonIndex != InputHandler.BUTTON_NONE) {
            if (buttonIndex != InputHandler.BUTTON_ALL) {
                SyntheticButton button = (SyntheticButton) buttons.get(buttonIndex);
                button.createTrigger(inputHandler, action, allowRepeats);
            } else {
                for (int i = buttons.size() - 1; i >= 0; i--) {
                    SyntheticButton button = (SyntheticButton) buttons.get(i);
                    button.createTrigger(inputHandler, action, allowRepeats);
                }
            }
        }
    }

    private static UtilInputHandlerDevice instance;

    /**
     * @return only instance of UtilInputHandlerDevice
     */
    static UtilInputHandlerDevice get() {
        if (instance == null) {
            instance = new UtilInputHandlerDevice();
            InputHandler.addDevice(instance);
        }
        return instance;
    }

    private ArrayList axes = new ArrayList();

    void addAxis( SyntheticAxis axis) {
        int index = axes.size();
        axes.add(axis);
        axis.setIndex(index);
    }

    void removeAxis( SyntheticAxis axis) {
        if (axes.get(axis.getIndex()) == axis) {
            axes.set(axis.getIndex(), null);
        }
    }

    private ArrayList buttons = new ArrayList();

    void addButton( SyntheticButton button) {
        int index = buttons.size();
        buttons.add(button);
        button.setIndex(index);
    }

    void removeButton( SyntheticButton button) {
        if (buttons.get(button.getIndex()) == button) {
            buttons.set(button.getIndex(), null);
        }
    }
}
