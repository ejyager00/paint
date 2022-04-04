/*
 * Eric Yager
 */
package paintcanvas;

/**
 * Interface to implement when telling an object how and where to save data it 
 * creates.
 * 
 * @author ericyager
 * @param <T> Type to save
 */
public interface Saveable<T> {
    
    /**
     * Method to run to save the data.
     * 
     * @param t data to save
     */
    public void save(T t);
    
}
