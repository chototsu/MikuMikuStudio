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

    protected void createTriggers(InputAction action, int axisIndex, int button, boolean allowRepeats,
                                  InputHandler inputHandler) {
        if (axisIndex != InputHandler.AXIS_NONE) {
            if (axisIndex != InputHandler.AXIS_ALL) {
                TwoButtonAxis axis = (TwoButtonAxis) axes.get(axisIndex);
                axis.new AxisTrigger(inputHandler, axis.getName(), action, allowRepeats);
            } else {
                for (int i = axes.size() - 1; i >= 0; i--) {
                    TwoButtonAxis axis = (TwoButtonAxis) axes.get(i);
                    axis.new AxisTrigger(inputHandler, axis.getName(), action, allowRepeats);
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

    void addAxis(TwoButtonAxis axis) {
        int index = axes.size();
        axes.add(axis);
        axis.setIndex(index);
    }

    void removeAxis(TwoButtonAxis axis) {
        if (axes.get(axis.getIndex()) == axis) {
            axes.set(axis.getIndex(), null);
        }
    }
}
