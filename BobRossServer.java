/*
 * Eric Yager
 */
package paint;

import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;

/**
 *
 * @author ericyager
 */
public class BobRossServer implements Runnable {

    ArrayBlockingQueue<Image> queue;
    private ServerSocket server;
    private Socket socket;
    private ObjectOutputStream output;
    private int port;

    public BobRossServer(ArrayBlockingQueue<Image> queue) {
        port = -1;
        this.queue = queue;
    }

    public BobRossServer(int port, ArrayBlockingQueue<Image> queue) {
        this.port = port;
        this.queue = queue;
    }

    @Override
    public void run() {

        try {
            if (port == -1) {
                server = new ServerSocket();
            } else {
                server = new ServerSocket(port);
            }
            socket = server.accept();
            output = new ObjectOutputStream(socket.getOutputStream());
            while (true) {

                if (!queue.isEmpty()) {
                    RenderedImage renderedImage = SwingFXUtils.fromFXImage(queue.remove(), null);
                    ImageIO.write(renderedImage, "png", output);
                }

            }
        } catch (IOException ex) {
            System.out.println(ex);
        }

    }

}
