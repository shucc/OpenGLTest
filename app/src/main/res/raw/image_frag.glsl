precision mediump float;

uniform sampler2D vTexture;
varying vec2 aCoordinate;
const highp vec3 weight = vec3(0.2125, 0.7154, 0.0721);

void main() {
    float luminance = dot(texture2D(vTexture, aCoordinate).rgb, weight);
    gl_FragColor = vec4(vec3(luminance), 1.0);
    //gl_FragColor = texture2D(vTexture, aCoordinate);
}