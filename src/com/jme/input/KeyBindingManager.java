/*
 * Copyright (c) 2003-2009 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jme.input;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <code>KeyBindingManager</code> maintains a list of command and
 * key pairs. A key is denoted as an int and corresponds to <code>KeyInput</code>'s
 * constants. A call to <code>isValidCommand</code> with an associated command will return
 * true if they key associated with the command is pressed. This allows for pairings with
 * <code>InputAction</code> how is also associated with a command, to be paired allowing
 * for actions to be performed within the game based on user input.
 *
 * @see com.jme.input.KeyInput
 * @author Mark Powell
 * @version $Id: KeyBindingManager.java,v 1.17 2006/05/12 21:16:17 nca Exp $
 */
public class KeyBindingManager {
	//singleton instance
	private static KeyBindingManager instance = null;

	//key mappings
	private HashMap<String, ArrayList<KeyCodes>> keyMap;

        private boolean[] restrictKey = new boolean[256];

	/**
	 * Private constructor is called by the getInstance method.
	 * It initializes the keyMap.
	 */
	private KeyBindingManager() {
		keyMap = new HashMap<String, ArrayList<KeyCodes>>();
    }
	
	/**
	 * <code>set</code> sets the command to the given keycode overriding
     * any previous keycodes previously set for the same command.
	 * @param command the command to set.
	 * @param keyCode the key to set to the command.
	 */
	public void set(String command, int keyCode) {
        ArrayList<KeyCodes> keyList = new ArrayList<KeyCodes>();
        KeyCodes key = new KeyCodes();
        key.keys = new int[1];
        key.keys[0] = keyCode;
        keyList.add(key);
        keyMap.put(command, keyList);
	}

    /**
     * <code>set</code> sets the command to the given list of keycodes
     * overriding any previous keycodes previously set for the same command.
     *
     * @param command the command to set.
     * @param keyCode the list of keys to set to the command.
     */
    public void set(String command, int[] keyCode) {
        ArrayList<KeyCodes> keyList = new ArrayList<KeyCodes>();
        KeyCodes key = new KeyCodes();
        key.keys = keyCode;
        keyList.add(key);
        keyMap.put(command, keyList);
    }

    /**
     * <code>add</code> adds a keycode to a command that already exists. This
     * will not override the previous keycode, but add to it. Allowing the
     * two keys to perform the same command.
     * @param command the command to add to.
     * @param keyCode the key to add to the command.
     */
    public void add(String command, int keyCode) {
        ArrayList<KeyCodes> list = keyMap.get(command);
        if(null == list) {
            set( command, keyCode );
            return;
        }

        KeyCodes key = new KeyCodes();
        key.keys = new int[1];
        key.keys[0] = keyCode;
        list.add(key);
    }

    /**
     * <code>add</code> adds a list of keycodes corresponding to a
     * command. All the keys defined in the array must be pressed for
     * the command to be valid.
     * @param command the command to assign to the keys.
     * @param keyCode the array of keys that must be pressed.
     */
    public void add(String command, int[] keyCode) {
        ArrayList<KeyCodes> list = keyMap.get(command);
        if(null == list) {
            set( command, keyCode );
            return;
        }

        KeyCodes key = new KeyCodes();
        key.keys = keyCode;
        list.add(key);
    }

    /**
     * <code>get</code> retrieves the key(s) for a given command. An array
     * of ints are returned, where all ints would be required for the command
     * to be executed. For example: int[] = {KEY_1, KEY_2} would require
     * both 1 and 2 pressed at the same time.
     * @param command the requested key map
     * @return the key map for the command.
     */
	public int[] get(String command, int index) {
        return keyMap.get(command).get(index).keys;
   }

   /**
    * <code>isValidCommand</code> determines if a command is executable in
    * the current state of the keyboard. That is, is a valid key pressed to
    * execute the requested command.
    * @param command the command to check.
    * @return true if the command should be executed, false otherwise.
    */
   public boolean isValidCommand(String command) {
     return isValidCommand(command, true);
   }

    /**
     * <code>isValidCommand</code> determines if a command is executable in
     * the current state of the keyboard. That is, is a valid key pressed to
     * execute the requested command.
     * @param command the command to check.
     * @param allowRepeats allow repetitious key presses.
     * @return true if the command should be executed, false otherwise.
     */
    public boolean isValidCommand(String command, boolean allowRepeats) {
        ArrayList<KeyCodes> keyList = keyMap.get(command);
        if(null == keyList) {
            return false;
        }
        if ( keyList.isEmpty() )
        {
            return true; //is this desired? (it was the previous behaviour)
        }

        for(int i = 0, max = keyList.size(); i < max; i++) {
            int[] keycodes = keyList.get(i).keys;
            boolean value = true;

            for(int j = 0; value && j < keycodes.length; j++) {
              if (allowRepeats)
                value = KeyInput.get().isKeyDown(keycodes[j]);
              else
                value = getStickyKey(keycodes[j]);
            }

            if (value) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Returns true if a key is down and wasn't down last call.
     * If a key is down and not restricted, the key is set as restricted and true is returned.
     * If a key is down and restricted, false is returned.
     * If a key is not down and is restricted, the restriction is cleared.
     * @param key The key to test
     * @return True if the key is a fresh key input.
     */
    private boolean getStickyKey(int key) {
        if (!restrictKey[key] && KeyInput.get().isKeyDown(key)) {
            restrictKey[key] = true;
            return true;
        } else if (!KeyInput.get().isKeyDown(key) && restrictKey[key])
            restrictKey[key] = false;
        return false;
    }

    /**
     * <code>remove</code> deletes a key map from the list.
     * @param command the key map to delete.
     */
	public void remove(String command) {
		keyMap.remove(command);
	}
	
	/**
     * <code>removeAll</code> deletes all key mappings from the list.
     */
    public void removeAll() {
        keyMap.clear();
    }

    /**
     * <code>getInstance</code> gets the static singleton instance of
     * the manager.
     * @return the instance of the key binding manager.
     */
	public static KeyBindingManager getKeyBindingManager() {
		if(null == instance) {
			instance = new KeyBindingManager();
		}

		return instance;
	}

    /**
     * <code>KeyCodes</code> defines a list of one or more keys for
     * a given key command. During key press comparisons, a logical and
     * will be used to insure all keys are pressed for which ever command
     * this uses.
     */
    public class KeyCodes {
        public int[] keys;
    }
}