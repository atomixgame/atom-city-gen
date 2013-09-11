uniform mat4 g_WorldViewProjectionMatrix;
uniform vec4 g_LightPosition;
uniform mat4 g_WorldMatrixInverse;
uniform mat4 g_WorldMatrix;

attribute vec3 inPosition;
attribute vec2 inTexCoord;
attribute vec3 inNormal;

varying vec2 vTexCoord;
varying vec3 vLightVec;
varying vec3 wallPos;
varying vec3 vNormalVec;

void main(void)
{
    gl_Position = g_WorldViewProjectionMatrix * vec4(inPosition, 1.0);
    vNormalVec = inNormal;
    vTexCoord = inTexCoord;
    vLightVec = g_LightPosition.xyz;
    wallPos=vec4(vec4(inPosition,1.0) * g_WorldMatrix).xyz;
    //wallPos=inPosition;
}
