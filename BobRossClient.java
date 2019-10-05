/*
 * Eric Yager
 */
package paint;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author ericyager
 */
public class BobRossClient implements Runnable {
    
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String address;
    private int port;

    public BobRossClient(String address, int port) {
        this.address = address;
        this.port = port;
    }

    @Override
    public void run() {
        
        try {
            socket = new Socket(address, port);
        } catch (IOException ex) {
            System.out.println(ex);
        }
        
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(BobRossClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        while (true) {
            try {
                BufferedImage image = ImageIO.read(input);
                System.out.println(image);
            } catch (IOException ex) {
                Logger.getLogger(BobRossClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    
    
}
