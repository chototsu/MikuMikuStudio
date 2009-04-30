/*
 * Copyright (c) 2008-2009, OgreLoader
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     - Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     - Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     - Neither the name of the Gibbon Entertainment nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY 'Gibbon Entertainment' "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL 'Gibbon Entertainment' BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.radakan.jme;

import java.net.URL;

import com.jme.util.resource.ResourceLocator;

/**
 * A conservative ResourceLocator implementation that only resolves Classpath
 * resources with absolute resource name paths.
 * </P> <P>
 * Lookups succeed only for resources requested with absolute paths, and there
 * is no recursion (hence it is <I>conservative</I>).
 * </P> <P>
 * There is no benefit of ClasspathResourceLocator.locateResource(String)
 * over Class.getClassLoader().getResource(), except that this class
 * implements ResourceLocator, and you can therefore use it to control resource
 * loading of utility classes that use ResourceLocatorTool to flexibly load
 * resources.
 * </P>
 *
 * @see com.jme.util.resource.ResourceLocator
 * @author Blaine Simpson (blaine dot simpson at admc dot com)
 */
public class ClasspathResourceLocator implements ResourceLocator {
    protected ClassLoader resourceClassLoader =
            ClasspathResourceLocator.class.getClassLoader();

    /**
     * @see #setResourceClassLoader(ClassLoader)
     */
    public ClassLoader getResourceClassLoader() {
        return resourceClassLoader;
    }

    /**
     * For apps with classloader tree customization, or which run under an
     * infrastructure which customizes the classloader hierarchy, this method
     * lets you specify the exact ClassLoader to use to retrieve resource URLs.
     */
    public void setResourceClassLoader(ClassLoader resourceClassLoader) {
        this.resourceClassLoader = resourceClassLoader;
    }

    /*
     * @returns null if resource not found, according to algorithm specified
     *          in the class JavaDoc.
     * @resourceName Must begin with "/".
     *          The parameter is treated exactly the same as the String param
     *          to ClassLoader.getResource() (because we are using that
     *          method!).  We just eliminate the ambiguity of
     *          beginning with "/" by forcing you to specify it.
     *
     * @throws IllegalArgumentException if specified resourceName does not
     *         begin with "/".
     * @see ClassLoader#getResource(String)
     * @see com.jme.util.resource.ResourceLocator#locateResource(String)
     */
    public URL locateResource(String resourceName) {
        if (resourceName == null) return null;
        if (resourceName.length() < 1 || resourceName.charAt(0) != '/')
            throw new IllegalArgumentException("Resource name '"
                    + resourceName + "' does not begin with '/'");
        return resourceClassLoader.getResource(resourceName);
    }
}
