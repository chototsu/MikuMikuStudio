/*
 * Copyright (c) 2010-2014, Kazuhiko Kobayashi
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package projectkyoto.mmd.file;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by kobayasi on 2014/05/21.
 */
public class KeywordManager {
    static class Keyword {
        byte[] buf;
        int length;
        int hash;

        public Keyword() {
        }

        public Keyword(byte[] buf, int length) {
            this.buf = buf;
            this.length = length;
            updateHash();
        }

        public void set(byte[] buf, int length) {
            this.buf = buf;
            this.length = length;
            updateHash();
        }
        private void updateHash() {
            int result = 1;
            for (int i=0;i<length;i++) {
                byte element = buf[i];
                result = 31 * result + element;
            }
            hash = result;
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Keyword keyword = (Keyword) o;

            if (length != keyword.length) return false;
            for(int i=0;i<length;i++) {
                if (buf[i] != keyword.buf[i]) {
                    return false;
                }
            }

            return true;
        }

        @Override
        protected Keyword clone() throws CloneNotSupportedException {
            byte[] buf2 = Arrays.copyOf(buf, length);
            Keyword newKeyword = new Keyword(buf2, length);
            return newKeyword;
        }
    }
    public static class KeyValue {
        public int id;
        public String value;

        public KeyValue(String value) {
            this.id = getNextId();
            this.value = value;
        }

        @Override
        public String toString() {
            return "KeyValue{" +
                    "id=" + id +
                    ", value='" + value + '\'' +
                    '}';
        }
    }
    private static int nextId = 0;
    private static synchronized int getNextId() {
        return nextId++;
    }

    private static HashMap<Keyword, KeyValue> map = new HashMap<Keyword, KeyValue>();
    private static ArrayList<KeyValue> keyValueList = new ArrayList<KeyValue>();
    private static Keyword keyword = new Keyword();
    public static synchronized KeyValue getKeyword(byte[] buf, int length) throws IOException{
        try {
            keyword.set(buf, length);
            KeyValue value = map.get(keyword);
            if (value == null) {
                value = new KeyValue((new String(buf,0, length,"MS932")).intern());
                map.put(keyword.clone(), value);
                keyValueList.add(value);
            }
            return value;
        } catch(CloneNotSupportedException ex) {
            throw new IOException("clone error", ex);
        }
    }
    public static synchronized KeyValue getKeyValue(int id) {
        return keyValueList.get(id);
    }
    public static synchronized String[] getKeywords() {
        String[] array = new String[map.size()];
        map.values().toArray(array);
        return array;
    }
}
