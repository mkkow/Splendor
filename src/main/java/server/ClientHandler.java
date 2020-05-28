package server;


import org.json.JSONObject;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.UUID;

public class ClientHandler extends  Thread{

        final ObjectOutputStream outputStream;
        final ObjectInputStream inputStream;
        final Socket socket;
        final int playernumber;
        final Card card;
        final HashMap<String,String> hashMap;
        private final Server server;

    public ClientHandler(ObjectOutputStream outputStream, ObjectInputStream inputStream, Socket socket, int playernumber, Server server, Card card, HashMap<String,String> hashMap) {
        this.outputStream = outputStream;
        this.inputStream = inputStream;
        this.socket = socket;
        this.playernumber = playernumber;
        this.server = server;
        this.card = card;
        this.hashMap = hashMap;
    }
    /*
    public void sendClientMessage(String msg) {
        try {
            outputStream.writeObject(msg);
            outputStream.flush();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

     */
    public void sayHello() {
        try {
            String jsonString = new JSONObject()
                    .put("answer_type","server_hello")
                    .put("set_client_id", UUID.randomUUID().toString())
                    .put("number_of_players",server.playersnicks.size())
                    .put("connected_players",playernumber)
                    .put("nicknames",server.playersnicks.toString())
                    .toString();
            outputStream.writeObject(jsonString);
            outputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();

        }
    }
    private void checkClientnick() throws IOException, ClassNotFoundException {
        String input = (String) inputStream.readObject();
        JSONObject jsonObject = new JSONObject(input);
        //System.out.println(jsonObject.toString());
        while(server.playersnicks.contains(jsonObject.getString("set_nick"))){
            String response = new JSONObject()
                    .put("answer_type","nick_verification")
                    .put("result","invalid")
                    .put("number_of_players",server.playersnicks.size())
                    .put("connected_players",playernumber)
                    .put("nicknames",server.playersnicks.toString())
                    .put("message","Twój nick jest zły xd")
                    .toString();
            outputStream.writeObject(response);
            outputStream.flush();
            input = (String) inputStream.readObject();
            jsonObject = new JSONObject(input);
        }
        String response = new JSONObject()
                    .put("answer_type","nick_verification")
                    .put("result","ok")
                    .put("number_of_players",server.playersnicks.size())
                    .put("connected_players",playernumber)
                    .put("nicknames",server.playersnicks.toString())
                    .toString();
        outputStream.writeObject(response);
        outputStream.flush();
        //System.out.println(jsonObject.toString());
        server.playersnicks.add(jsonObject.getString("set_nick"));
        server.hashMap.put(jsonObject.getString("set_nick"),jsonObject.getString("client_id"));
        System.out.println(server.playersnicks.toString());
    }
    private void gameStart() throws IOException {
        String response = new JSONObject()
                    .put("answer_type","game_start")
                    .put("nicknames",server.playersnicks.toString())
                    .put("Board",card.toString())
                    .toString();
        outputStream.writeObject(response);
        outputStream.flush();
    }

    @Override
    public void run() {
        while (true) {
            try {
                sayHello();
                checkClientnick();
                synchronized (server){
                    if(server.playersnicks.size()==playernumber){
                        server.notifyAll();
                    }
                    else{
                        server.wait();
                    }
                }
                gameStart();
                System.out.println(hashMap.toString());
                Thread.sleep(100000000);
            }
            catch (InterruptedException | IOException | ClassNotFoundException e) {
                e.printStackTrace();

            }


        }

    }
}

