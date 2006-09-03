/*
 * Copyright (c) 2003-2006 jMonkeyEngine
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

uniform sampler2D screenTexture;

varying vec4 viewCoords;
varying vec2 velocity;

void main(void)
{
	vec2 projCoord = viewCoords.xy / viewCoords.q;
	projCoord = (projCoord + vec2(1.0)) * vec2(0.5);

	vec2 sampleOffset = velocity / vec2(15.0);

	vec4 a = texture2D(screenTexture, projCoord );
	a = a + texture2D(screenTexture, projCoord + sampleOffset );
	a = a + texture2D(screenTexture, projCoord + sampleOffset * vec2(2.0) );
	a = a + texture2D(screenTexture, projCoord + sampleOffset * vec2(3.0) );
	a = a + texture2D(screenTexture, projCoord + sampleOffset * vec2(4.0) );
	a = a + texture2D(screenTexture, projCoord + sampleOffset * vec2(5.0) );
	a = a + texture2D(screenTexture, projCoord + sampleOffset * vec2(6.0) );
	a = a + texture2D(screenTexture, projCoord + sampleOffset * vec2(7.0) );
	a = a + texture2D(screenTexture, projCoord + sampleOffset * vec2(8.0) );
	a = a + texture2D(screenTexture, projCoord + sampleOffset * vec2(9.0) );
	a = a + texture2D(screenTexture, projCoord + sampleOffset * vec2(10.0) );
	a = a + texture2D(screenTexture, projCoord + sampleOffset * vec2(11.0) );
	a = a + texture2D(screenTexture, projCoord + sampleOffset * vec2(12.0) );
	a = a + texture2D(screenTexture, projCoord + sampleOffset * vec2(13.0) );
	a = a + texture2D(screenTexture, projCoord + sampleOffset * vec2(14.0) );
	a = a + texture2D(screenTexture, projCoord + sampleOffset * vec2(15.0) );

	gl_FragColor = a / vec4(16.0);

/*
	//works on nvidia cards but not on "most" ati cards. appearantly they don't understand loop unrolling

	const float samples = 16.0;
	const float w = 1.0 / samples; // sample weight

	vec2 projCoord = viewCoords.xy / viewCoords.q;
	projCoord = (projCoord + vec2(1.0)) * vec2(0.5);

	vec4 a = vec4(0.0); // accumulator - fixed4

	float i;
	for(i=0.0; i<samples; i+=1.0) {
		float t = i / (samples-1.0);
		a = a + texture2D(screenTexture, projCoord + velocity * vec2(t) ) * vec4(w);
	}

	gl_FragColor = a;
*/
}