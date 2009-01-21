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

package jmetest.scalarfields;

import java.util.Random;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;

class MetaBallScalarField implements ScalarField {

    private final MetaBall[] balls;
    private final Vector3f normal = new Vector3f();
    private final Vector2f textureCoords = new Vector2f();

    public MetaBallScalarField( MetaBall... balls ) {
        this.balls = balls;
    }

    public float calculate( Vector3f point ) {
        float sum = 0;
        for ( MetaBall ball : balls ) {
            float part = ball.getWeight() / ( ball.getPosition().distanceSquared( point ) + 0.001f );
            sum += part;
        }
        return sum;
    }

    public Vector3f normal( Vector3f point ) {
        normal.zero();
        for ( MetaBall ball : balls ) {
            Vector3f direction = point.subtract( ball.getPosition() );
            float lengthSquared = direction.lengthSquared() + 0.001f;
            normal.addLocal( direction.divideLocal( lengthSquared * lengthSquared ) );
        }
        return normal.normalizeLocal();
    }

    public Vector2f textureCoords( Vector3f point ) {
        // little trick: we know that normals have been computed before
        textureCoords.x = /*point.x/20 +*/ normal.x;
        textureCoords.y = /*point.x/20 +*/ normal.y;
        return textureCoords;
    }

    private ColorRGBA color = new ColorRGBA();

    public ColorRGBA color( Vector3f point ) {
        color.set( 0, 0, 0, 0 );
        for ( MetaBall ball : balls ) {
            float part = ball.getWeight() / ( ball.getPosition().distanceSquared( point ) + 0.001f ) * 2;
            if ( part > 1 )
            {
                part = 1;
            }
            color.interpolate( ball.getColor(), part );
        }
        return color;
    }

    public void updateBallLocations( final Vector3f boxSize ) {
        for ( MetaBall ball : balls ) {
            ball.getPosition().addLocal( ball.getSpeed() );
            if ( ball.getPosition().x < -boxSize.x || ball.getPosition().x > boxSize.x ) {
                ball.getSpeed().x = -ball.getSpeed().x;
            }
            if ( ball.getPosition().y < -boxSize.y || ball.getPosition().y > boxSize.y ) {
                ball.getSpeed().y = -ball.getSpeed().y;
            }
            if ( ball.getPosition().z < -boxSize.z || ball.getPosition().z > boxSize.z ) {
                ball.getSpeed().z = -ball.getSpeed().z;
            }
        }
    }

    static class MetaBall {

        private Vector3f position;
        private Vector3f speed;
        private ColorRGBA color;
        private float weight;
        private static Random random = new Random( 0 );

        public MetaBall( Vector3f position, float weight, Vector3f speed, ColorRGBA color ) {
            this.position = position;
            this.weight = weight;
            this.speed = speed;
            this.color = color;
        }

        public static MetaBall getRandomBall( Vector3f boxSize, float maxWeight, float maxSpeed, final ColorRGBA color ) {
            Vector3f position = new Vector3f(
                    ( nextRandomFloat() * boxSize.x * 2 ) - boxSize.x,
                    ( nextRandomFloat() * boxSize.y * 2 ) - boxSize.y,
                    ( nextRandomFloat() * boxSize.z * 2 ) - boxSize.z );
            Vector3f speed = new Vector3f(
                    nextRandomFloat() * maxSpeed,
                    nextRandomFloat() * maxSpeed,
                    nextRandomFloat() * maxSpeed );
            float weight = maxWeight * nextRandomFloat() + 1f;
            return new MetaBall( position, weight, speed, color );
        }

        private static float nextRandomFloat() {
            return random.nextFloat();
        }

        public Vector3f getPosition() {
            return position;
        }

        public void setPosition( Vector3f position ) {
            this.position = position;
        }

        public float getWeight() {
            return weight;
        }

        public void setWeight( float weight ) {
            this.weight = weight;
        }

        public Vector3f getSpeed() {
            return speed;
        }

        public void setSpeed( Vector3f speed ) {
            this.speed = speed;
        }

        public ColorRGBA getColor() {
            return color;
        }
    }
}