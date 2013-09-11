package sg.atom.builder
import com.jme3.system.AppSettings
import java.util.concurrent.Callable
import com.jme3.asset.AssetManager
import com.jme3.math.*
import com.jme3.app.SimpleApplication

EmptySimpleApplication app = new EmptySimpleApplication();
AppSettings settings = new AppSettings(true);
settings.setWidth(800);
settings.setHeight(800);
app.setPauseOnLostFocus(true);
app.setShowSettings(false);
app.setSettings(settings);
app.start();


app.enqueue({
        app.flyCam.moveSpeed=20;
        AssetManager assetManager = app.assetManager
        def spb = new SpatialBuilder(assetManager);
        
        spb{
            (1..20).each{
                float x = FastMath.nextRandomFloat() * 100
                float y = 0//FastMath.nextFloat() * 100
                float z = FastMath.nextRandomFloat() * 100
                aNode = node(name:"Anode",pos: vec3(x,y,z)){
                    (1..20).each{
                        geo(name:"Sphere",pos: vec3(2 * it,2 * it,2 * it),mesh:sphere())
                    }
                }
                app.rootNode.attachChild(aNode);
            }
        }

        
    } as Callable)



	


