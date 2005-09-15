/*
 * Copyright (c) 2003-2005 jMonkeyEngine
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

package com.jme.util;

import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import com.jme.image.Image;

/**
 * 
 * <code>DDSLoader</code> is an image loader that reads in a DirectX DDS file.
 * Currently only supports DXT1, DXT3 and DXT5 - RGB support will be added
 * later.
 * 
 * @author Gareth Jenkins-Jones
 * @version $Id: DDSLoader.java,v 1.2 2005-09-15 17:12:57 renanse Exp $
 */
public final class DDSLoader {
    private DDSLoader() {
    }

    public static Image loadImage(InputStream fis) throws IOException {
        DDSReader reader = new DDSReader(fis);
        reader.loadHeader();
        ByteBuffer data = reader.readData();

        return new Image(reader.pixelFormat_, reader.width_, reader.height_,
            data, reader.sizes_ );
    }

    /**
     * DDS reader
     * 
     * @author Gareth
     */
    public static class DDSReader {
        private static final int DDSD_MANDATORY = 0x1007;
        private static final int DDSD_MIPMAPCOUNT = 0x20000;
        private static final int DDSD_LINEARSIZE = 0x80000;
        private static final int DDSD_DEPTH = 0x800000;

        private static final int DDPF_ALPHAPIXELS = 0x1;
        private static final int DDPF_FOURCC = 0x4;
        private static final int DDPF_RGB = 0x40;

        private static final int DDSCAPS_COMPLEX = 0x8;
        private static final int DDSCAPS_TEXTURE = 0x1000;
        private static final int DDSCAPS_MIPMAP = 0x400000;

        private static final int DDSCAPS2_CUBEMAP = 0x200;
        private static final int DDSCAPS2_VOLUME = 0x200000;

        private static final int PF_DXT1 = 0x31545844;
        private static final int PF_DXT3 = 0x33545844;
        private static final int PF_DXT5 = 0x35545844;

        private static final double LOG2 = Math.log(2);

        private int width_;
        private int height_;
        private int flags_;
        private int pitchOrSize_;
        private int mipMapCount_;
        private int caps1_;
        private int caps2_;

        private boolean compressed_;
        private int pixelFormat_;
        private int bpp_;
        private int[] sizes_;

        private InputStream stream_;
        private DataInput in_;

        public DDSReader(InputStream in) {
            in_ = new LittleEndien(in);
            stream_ = in;
        }

        public void loadHeader() throws IOException {
            if (in_.readInt() != 0x20534444 || in_.readInt() != 124) {
                throw new IOException("Not a DDS file");
            }

            flags_ = in_.readInt();

            if (!is(flags_, DDSD_MANDATORY)) {
                throw new IOException("Mandatory flags missing");
            }
            if (is(flags_, DDSD_DEPTH)) {
                throw new IOException("Depth not supported");
            }

            height_ = in_.readInt();
            width_ = in_.readInt();
            pitchOrSize_ = in_.readInt();
            in_.skipBytes(4);
            mipMapCount_ = in_.readInt();
            in_.skipBytes(44);
            readPixelFormat();
            caps1_ = in_.readInt();
            caps2_ = in_.readInt();
            in_.skipBytes(12);

            if (!is(caps1_, DDSCAPS_TEXTURE)) {
                throw new IOException("File is not a texture");
            }
            if (is(caps2_, DDSCAPS2_CUBEMAP)) {
                throw new IOException("Cubemaps not supported");
            }
            if (is(caps2_, DDSCAPS2_VOLUME)) {
                throw new IOException("Volume textures not supported");
            }

            int expectedMipmaps = 1 + (int) Math.ceil(Math.log(Math.max(
                    height_, width_)) / LOG2);

            if (is(caps1_, DDSCAPS_MIPMAP)) {
                if (!is(flags_, DDSD_MIPMAPCOUNT)) {
                    mipMapCount_ = expectedMipmaps;
                } else if (mipMapCount_ != expectedMipmaps) {
                    throw new IOException("Got " + mipMapCount_
                            + "mipmaps, expected" + expectedMipmaps);
                }
            } else {
                mipMapCount_ = 1;
            }

            loadSizes();
        }

        private void readPixelFormat() throws IOException {
            int pfSize = in_.readInt();
            if (pfSize != 32) {
                throw new IOException("Pixel format size is " + pfSize
                        + ", not 32");
            }

            int flags = in_.readInt();

            if (is(flags, DDPF_FOURCC)) {
                if (!is(flags_, DDSD_LINEARSIZE)) {
                    throw new IOException("Must use linear size with fourcc");
                }

                compressed_ = true;
                int fourcc = in_.readInt();
                in_.skipBytes(20);

                switch (fourcc) {
                case PF_DXT1:
                    bpp_ = 4;
                    if (is(flags, DDPF_ALPHAPIXELS)) {
                        pixelFormat_ = Image.DXT1A_NATIVE;
                    } else {
                        pixelFormat_ = Image.DXT1_NATIVE;
                    }
                    break;
                case PF_DXT3:
                    bpp_ = 8;
                    pixelFormat_ = Image.DXT3_NATIVE;
                    break;
                case PF_DXT5:
                    bpp_ = 8;
                    pixelFormat_ = Image.DXT5_NATIVE;
                    break;
                default:
                    throw new IOException("Unknown fourcc: " + string(fourcc));
                }

                int size = ((width_ + 3) / 4) * ((height_ + 3) / 4) * bpp_ * 2;
                if (pitchOrSize_ != size) {
                    throw new IOException("Expected size = " + size
                            + ", real = " + pitchOrSize_);
                }
            } else {
                compressed_ = false;
                throw new IOException("Uncompressed not supported");
            }
        }

        private void loadSizes() {
            int width = width_;
            int height = height_;

            sizes_ = new int[mipMapCount_];

            for (int i = 0; i < mipMapCount_; i++) {
                int size;

                if (compressed_) {
                    size = ((width + 3) / 4) * ((height + 3) / 4) * bpp_ * 2;
                } else {
                    throw new RuntimeException("Uncompressed not supported");
                }

                sizes_[i] = ((size + 3) / 4) * 4;

                width = Math.max(width / 2, 1);
                height = Math.max(height / 2, 1);
            }
        }

        public ByteBuffer readData() throws IOException {
            int totalSize = 0;

            for (int i = 0; i < sizes_.length; i++) {
                totalSize += sizes_[i];
            }

            byte[] data = new byte[totalSize];
            in_.readFully(data);

            ByteBuffer buffer = ByteBuffer.allocateDirect(totalSize);
            buffer.put(data);
            buffer.rewind();

            return buffer;
        }

        private static boolean is(int flags, int mask) {
            return (flags & mask) == mask;
        }

        private static String string(int value) {
            StringBuffer buf = new StringBuffer();

            buf.append((char) (value & 0xFF));
            buf.append((char) ((value & 0xFF00) >> 8));
            buf.append((char) ((value & 0xFF0000) >> 16));
            buf.append((char) ((value & 0xFF00000) >> 24));

            return buf.toString();
        }
    }
}
