package test;
import test.Commands.DefaultIO;
import test.Server.ClientHandler;

import java.io.*;


public class AnomalyDetectionHandler implements ClientHandler {


    @Override
    public void clientHandler(InputStream in, OutputStream out) {
        SocketIO SIO = new SocketIO(in, out);
        new CLI(SIO).start();
        try {
            SIO.br.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        SIO.pr.close();

    }

    public class SocketIO implements DefaultIO {
        BufferedReader br;
        PrintWriter pr;

        SocketIO(InputStream in, OutputStream out) {
            br = new BufferedReader(new InputStreamReader(in));
            pr = new PrintWriter(out);
        }

        @Override
        public String readText() {
            try {
                return br.readLine();
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public void write(String text) {
            pr.write(text);
            pr.flush();
        }

        @Override
        public float readVal() {
            return 0;
        }

        @Override
        public void write(float val) {

        }

    }


}
