#version 150

uniform sampler2D DiffuseSampler;
uniform float Saturation;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec4 color = texture(DiffuseSampler, texCoord);
    
    // Grayscale method (Luma BT.709)
    float gray = dot(color.rgb, vec3(0.2126, 0.7152, 0.0722));
    
    // Linear Interpolation between Grayscale and Original Color
    vec3 satColor = mix(vec3(gray), color.rgb, Saturation);
    
    fragColor = vec4(satColor, color.a);
}
