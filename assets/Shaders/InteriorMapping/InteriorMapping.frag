uniform vec3 g_CameraPosition;
uniform mat4 g_WorldMatrixInverse;
uniform mat4 g_WorldMatrix;

uniform sampler2D m_FloorTex;
uniform sampler2D m_CeilingTex;
uniform sampler2D m_OutsideWallTex;
uniform sampler3D m_envMap;
uniform sampler2D m_RoofTex;
uniform int m_WindowInTex;
uniform float m_FloorHeight;
uniform float m_HouseSizeX;
uniform float m_HouseSizeY;

varying vec2 vTexCoord;
varying vec3 vLightVec;
varying vec3 vViewVec;
varying vec3 wallPos;
varying vec3 vNormalVec;

void main(void)
{
    /*
       // Standard lighting
       float diffuse = clamp( dot( lightVec, bump ), 0.0, 1.0 );
       float specular = pow( clamp( dot(reflect( -viewVec, bump ), lightVec ) ,0.0, 1.0 ), 12.0 );
    */
       // 
       vec3 viewPos = g_CameraPosition;
       vec3 traceVec = wallPos - viewPos; 

    // CHECK IF ROOF OF DOWN FACE
    if (dot(vNormalVec,vec3(0,1,0))>0.99 || dot(vNormalVec,vec3(0,-1,0))>0.99){
       vec2 uvRoof = vTexCoord;
       uvRoof.y /= 4;
       vec4 roofColor = texture2D(m_RoofTex, uvRoof);
       gl_FragColor = roofColor;
    } else {
       // FIND FLOOR
       vec3 floorPos;
       float dh1 = viewPos.y - wallPos.y;
       float hf= 0;
       float dh2=0;
       int floorNum = 0;
       if (dh1>=0){
          // Above, see floor
          floorNum = floor(wallPos.y / m_FloorHeight);
       } else {
          //Below, see ceil
          floorNum = ceil(wallPos.y / m_FloorHeight);
       }
       hf = floorNum * m_FloorHeight;
       dh2= wallPos.y - hf;
       vec3 wFloorPos = viewPos+ traceVec/dh1 * (dh2+dh1) ;
       floorPos = vec4(vec4(wFloorPos,1.0) * g_WorldMatrixInverse).xyz;

       vec2 uvFloorTex = floorPos.xz;
        //uvFloorTex.x = uvFloorTex.x /m_HouseSizeX;
        //uvFloorTex.y = uvFloorTex.y /m_HouseSizeY;
       vec4 floorColor;

       if (dh1>=0){
          floorColor = texture2D(m_FloorTex, uvFloorTex);
       } else {
          floorColor = texture2D(m_CeilingTex, uvFloorTex);
       }
       // REAL LIGHTING
       //gl_FragColor =  ( diffuse * base + 0.7 * specular ) * light + 0.3 * base;
       vec4 inColor;

       // FIND OUTSIDE WALL
       vec2 uvOutWall = vTexCoord;

       //uvOutWall.y= - wallPos.y / m_FloorHeight / m_WindowInTex;
       vec4 outWallColor = texture2D(m_OutsideWallTex, uvOutWall);

/*
       // REFLECTION

       vec3 reflectVec = reflect(traceVec,normalize(vNormalVec));
       vec4 outReflectColor = vec4(1,1,0,1);
       //outReflectColor = texture3D(m_envMap, reflectVec);


       // ALPHA THESHOLD
       if (outWallColor.a > 0.4){
          outWallColor.a = 0.42;
       } else {
           //outWallColor.a = 1;
           //outWallColor = mix(outWallColor,outReflectColor,outWallColor.a);
       }
*/
/*
       // IN OR OUT?
       if (floorPos.x>m_HouseSizeX || floorPos.x<-m_HouseSizeX || floorPos.z>m_HouseSizeY || floorPos.z<-m_HouseSizeY){
          vec2 uvInWall=uvOutWall;
          uvInWall.x = - uvInWall.x;

          vec4 inWallColor = texture2D(m_OutsideWallTex, uvInWall);
          inColor = inWallColor;
          //inColor= vec4(0.2,0.2,0.2,1);

          if (outWallColor.a > 0.4){
             //discard;
          }

       } else {
          inColor = floorColor;
       }
*/
        inColor = floorColor;
        // RANDOM light

        if (mod(floorNum,2) !=0){
           inColor *= 0.7; 
        }


        gl_FragColor = mix(outWallColor,inColor,outWallColor.a);
       gl_FragColor = inColor;


    // DEBUG
    if (wallPos.y<0){
           inColor =vec4(1,0,0,1);
    } else if (wallPos.y<1){
           inColor =vec4(1,0.5,0,1);
    } else if (wallPos.y<2){
           inColor =vec4(1,1,0,1);
    }
gl_FragColor = inColor;
    }
}
