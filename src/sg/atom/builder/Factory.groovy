package sg.atom.builder
import java.util.Map;

/** 
 * An interface to represent a factory of beans
 *
 */
public interface Factory {

    /**
     * Create a new instance
     */    
    public Object newInstance(Map properties) throws InstantiationException, InstantiationException, IllegalAccessException;
}

