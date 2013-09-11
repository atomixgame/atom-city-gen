package sg.atom.builder

import com.jme3.math.*
import org.codehaus.groovy.runtime.InvokerHelper;

import groovy.lang.Closure;
import groovy.lang.MissingMethodException;
import groovy.util.BuilderSupport;
import com.jme3.asset.AssetManager
import com.jme3.scene.Mesh
import com.jme3.scene.Geometry
import com.jme3.scene.Node
import com.jme3.scene.shape.*
import com.jme3.material.Material
import com.jme3.scene.Spatial

/**
 * A helper class for creating Swing childrenSpatials using GroovyMarkup
 * 
 */
public class SpatialBuilder extends BuilderSupport{

    //private Logger log = Logger.getLogger(getClass().getName());
    private Map factories = new HashMap();
    private Object constraints;
    private Map passThroughNodes = new HashMap();
    private Map childrenSpatials = new HashMap();
    private AssetManager assetManager;
    private LinkedList rootNode = new LinkedList();

    public SpatialBuilder(AssetManager assetManager) {
        this.assetManager = assetManager;
        registerBuilders();
    }
    /*
    public Object getProperty(String name) {
    Object spatial = childrenSpatials.get(name);
    if (spatial == null) {
    return super.getProperty(name);
    }
    return spatial;
    }

    protected void setParent(Object parent, Object child) {
        
    if (child instanceof Action) {
    Action action = (Action) child;
    try {
    InvokerHelper.setProperty(parent, "action", action);
    } catch (RuntimeException re) {
    // must not have an action property...
    // so we ignore it and go on
    }
    Object keyStroke = action.getValue("KeyStroke");
    //System.out.println("keystroke: " + keyStroke + " for: " + action);
    if (parent instanceof JComponent) {
    JComponent component = (JComponent) parent;
    KeyStroke stroke = null;
    if (keyStroke instanceof String) {
    stroke = KeyStroke.getKeyStroke((String) keyStroke);
    }
    else if (keyStroke instanceof KeyStroke) {
    stroke = (KeyStroke) keyStroke;
    }
    if (stroke != null) {
    String key = action.toString();
    component.getInputMap().put(stroke, key);
    component.getActionMap().put(key, action);
    }
    }
        
    } else if (child instanceof Window) {
    // do nothing.  owner of window is set elsewhere, and this 
    // shouldn't get added to any parent as a child 
    // if it is a top level component anyway
    }else { 
    Component component = null;
    if (child instanceof Component) {
    component = (Component) child;
    }else if (child instanceof ComponentFacade) {
    ComponentFacade facade = (ComponentFacade) child;
    component = facade.getComponent();
    }
    if (component != null) {
    if (parent instanceof ContainerFacade) {
    ContainerFacade facade = (ContainerFacade) parent;
    facade.addComponent(component);
    }
    }
    }
         
        
    }

    protected void nodeCompleted(Object parent, Object node) {
    // set models after the node has been completed
    if (node instanceof TableModel && parent instanceof JTable) {
    JTable table = (JTable) parent;
    TableModel model = (TableModel) node;
    table.setModel(model);
    }
    if (node instanceof Startable) {

    }
    }

    protected Object createNode(Object name) {
    return createNode(name, Collections.EMPTY_MAP);
    }

    protected Object createNode(Object name, Object value) {
    if (passThroughNodes.containsKey(name) && (value != null) && ((Class)passThroughNodes.get(name)).isAssignableFrom(value.getClass())) {
    // value may need to go into containing windows list
    if (value instanceof Window) {
    rootNode.attachChild(value);
    }
    return value;
    }
    else if (value instanceof String) {
    Object spatial = createNode(name);
    if (spatial != null) {
    InvokerHelper.invokeMethod(spatial, "setText", value);
    }
    return spatial;
    }
    else {
    throw new MissingMethodException((String) name, getClass(), new Object[] {value});
    }
    }

    protected Object createNode(Object name, Map attributes, Object value) {
    if (passThroughNodes.containsKey(name) && (value != null) && ((Class)passThroughNodes.get(name)).isAssignableFrom(value.getClass())) {
    // value may need to go into containing windows list
            
    if (value instanceof Window) {
    rootNode.attachChild(value);
    }
            
    handleWidgetAttributes(value, attributes);
    return value;
    }
    else { 
    Object spatial = createNode(name, attributes);
    if (spatial != null) {
    InvokerHelper.invokeMethod(spatial, "setText", value.toString());
    }
    return spatial;
    }
    }
    
    protected Object createNode(Object name, Map attributes) {
    String widgetName = (String) attributes.remove("id");
    constraints = attributes.remove("constraints");
    Object spatial = null;
        
    if (passThroughNodes.containsKey(name)) {
    spatial = attributes.get(name);
    if ((spatial != null) && ((Class)passThroughNodes.get(name)).isAssignableFrom(spatial.getClass())) {
    // value may need to go into containing windows list
    if (spatial instanceof Window) {
    rootNode.attachChild(spatial);
    }
    attributes.remove(name);
    }
    else {
    spatial = null;
    }
    }
        
    if (spatial == null) {
    Factory factory = (Factory) factories.get(name);
    if (factory != null) {
    try {
    spatial = factory.newInstance(attributes);
    if (widgetName != null) {
    childrenSpatials.put(widgetName, spatial);
    }
    if (spatial == null) {
    log.log(Level.WARNING, "Factory for name: " + name + " returned null");
    }
    else {
    if (log.isLoggable(Level.FINE)) {
    log.fine("For name: " + name + " created spatial: " + spatial);
    }
    }
    }
    catch (Exception e) {
    throw new RuntimeException("Failed to create component for" + name + " reason: " + e, e);
    }
    }
    else {
    log.log(Level.WARNING, "Could not find match for name: " + name);
    }
    }
    handleWidgetAttributes(spatial, attributes);
    return spatial;
    }

    protected void handleWidgetAttributes(Object spatial, Map attributes) {
    if (spatial != null) {
    if (spatial instanceof Action) {
    Action action = (Action) spatial;

    Closure closure = (Closure) attributes.remove("closure");
    if (closure != null && action instanceof DefaultAction) {
    DefaultAction defaultAction = (DefaultAction) action;
    defaultAction.setClosure(closure);
    }

    Object accel = attributes.remove("accelerator");
    KeyStroke stroke = null;
    if (accel instanceof KeyStroke) {
    stroke = (KeyStroke) accel;
    } else if (accel != null) {
    stroke = KeyStroke.getKeyStroke(accel.toString());
    }
    action.putValue(Action.ACCELERATOR_KEY, stroke);

    Object mnemonic = attributes.remove("mnemonic");
    if ((mnemonic != null) && !(mnemonic instanceof Number)) {
    mnemonic = new Integer(mnemonic.toString().charAt(0));
    }
    action.putValue(Action.MNEMONIC_KEY, mnemonic);

    for (Iterator iter = attributes.entrySet().iterator(); iter.hasNext();) {
    Map.Entry entry = (Map.Entry) iter.next();
    String actionName = (String) entry.getKey();    // todo dk: misleading naming. this can be any property name

    // typically standard Action names start with upper case, so lets upper case it            
    actionName = capitalize(actionName);            // todo dk: in general, this shouldn't be capitalized
    Object value = entry.getValue();

    action.putValue(actionName, value);
    }

    }
    else {

    // set the properties
    for (Iterator iter = attributes.entrySet().iterator(); iter.hasNext();) {
    Map.Entry entry = (Map.Entry) iter.next();
    String property = entry.getKey().toString();
    Object value = entry.getValue();
    InvokerHelper.setProperty(spatial, property, value);
    }
    }
    }
    }

    protected void registerBuilders() {
    // non-spatial support classes
    registerBeanFactory("action", DefaultAction.class);
    passThroughNodes.put("action", javax.swing.Action.class);
    registerFactory("map", new Factory() {      // todo dk: is that still needed?
    public Object newInstance(Map properties)
    throws InstantiationException, InstantiationException, IllegalAccessException {
    return properties;
    }
    });
    // ulimate pass through type
    passThroughNodes.put("spatial", java.awt.Component.class);

    // standalone window classes
    //
    registerFactory("dialog", new Factory() {
    public Object newInstance(Map properties)
    throws InstantiationException, InstantiationException, IllegalAccessException {
    return createDialog(properties);
    }
    });
    registerFactory("frame", new Factory() {
    public Object newInstance(Map properties)
    throws InstantiationException, InstantiationException, IllegalAccessException {
    return createFrame(properties);
    }
    });
    registerBeanFactory("fileChooser", JFileChooser.class);
      
    //
    // childrenSpatials
    //
    registerBeanFactory("button", JButton.class);
    registerBeanFactory("checkBox", JCheckBox.class);
    registerFactory("hstrut", new Factory() {
    public Object newInstance(Map properties) {
    try {
    Object num = properties.remove("width");
    if (num instanceof Number) {
    return Box.createHorizontalStrut(((Number)num).intValue());
    } else {
    return Box.createHorizontalStrut(6);
    }
    } catch (RuntimeException re) {
    re.printStackTrace(System.out);
    throw re;
    }
    }
    });     
    }

    protected Object createFrame(Map properties) {
    JFrame frame = new JFrame();
    rootNode.attachChild(frame);
    return frame;
    }
    
    protected void registerBeanFactory(String name, final Class beanClass) {
    registerFactory(name, new Factory() {
    public Object newInstance(Map properties) throws InstantiationException, IllegalAccessException {
    return beanClass.newInstance();
    }
    });

    }

    protected void registerFactory(String name, Factory factory) {
    factories.put(name, factory);
    }
     */
    
    protected void setParent(Object parent, Object child){
        if (parent instanceof Node){
            if (child instanceof Spatial){
                parent.attachChild(child)
            }
        } else {
            
        }
    }
    protected Object createNode(Object name){
       
    }
    protected Object createNode(Object name, Object value){
       
    }
    protected Object createNode(Object name, Map attributes){
        if (name.equals("node")){
            //println "Insert a Node"
            Node result;
            if (attributes.get("path")!=null){
                println("Load asset" + attributes.get("path"));
                result= assetManager.loadModel(attributes.get("path"))
                    
            } else {
                result= new Node(attributes.get("name"))
            }
            if (result!=null){
                result.localTranslation = attributes.get("pos")

                return result
            }
        } else if (name.equals("geo")){
            //println "Insert a Node"
            Mesh mesh = (Mesh) attributes.get("mesh")
            Geometry result = new Geometry(attributes.get("name"),mesh)
            result.localTranslation = attributes.get("pos")
            if (attributes.get("mat")!=null){
                result.material= attributes.get("mat")
            } else {
                result.material = getDefaultMat()
            }
            
            return result
        } else {
            println name
        }
        return null
    }
    protected Object createNode(Object name, Map attributes, Object value){
       println name+" "+attributes+" goHere!";
    }
    
    public Mesh sphere(){
        return new Sphere(8,8,1)
    }
    
    public Vector3f vec3(x,y,z){
        return new Vector3f(x,y,z)
    }
    
    public Material getDefaultMat(){
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.White);
        return mat;
    }
}


