package gui;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Game extends JPanel {
    private Card[][] cards;
    private Card[] players;
    private JButton Menu;
    private coinStack[] coinStacks;
    private JPanel coinField;
    private int cardHeight;
    private int cardWidth;
    private int Width;
    private int Height;
    private int x;
    private int y;
    public Game(JFrame frame, JSONObject board) throws JsonProcessingException {
        super(null);
        setBackground(Color.BLACK);
        cards=new Card[4][5];
        players=new Card[4];
        Menu=new JButton("Menu");
        cardHeight=frame.getHeight()/6;
        cardWidth=cardHeight*57/88;
        Width=frame.getWidth();
        Height=frame.getHeight();
        x=(Width-cardWidth*5)/2;
        y=(Height-cardHeight*4)/2;
        Menu.setLocation(0,0);
        Menu.setSize(Height/12,Height/12);
        add(Menu);
        coinStacks=new coinStack[6];
        coinField=new JPanel(null);
        JSONObject cash=board.getJSONObject("bankCash");

        for(int i=0;i<6;i++) {
            coinStacks[i] = new coinStack();
            coinField.add(coinStacks[i]);
            coinStacks[i].setLocation(0,i*cardHeight/6);
            coinStacks[i].setSize(cardHeight/6,cardHeight/6);
        }
        add(coinField);
        coinField.setLocation(x-cardHeight/6,y);
        coinField.setSize(cardHeight/6,cardHeight);
        coinStacks[0].set(cash.getInt("white"),"black",Color.WHITE);
        coinStacks[1].set(cash.getInt("blue"),"white",Color.BLUE);
        coinStacks[2].set(cash.getInt("green"),"black",Color.GREEN);
        coinStacks[3].set(cash.getInt("red"),"black",Color.RED);
        coinStacks[4].set(cash.getInt("black"),"white",Color.BLACK);
        coinStacks[5].set(cash.getInt("yellow"),"black",Color.YELLOW);
        for(int i=0;i<4;i++){
            JSONArray row;
            if(i==0)
                row=board.getJSONObject("nobles").getJSONArray("nobles");
            else
                row=board.getJSONObject("developmentCardsOnBoard").getJSONArray("level"+i);
            for(int j=0;j<row.length();j++){
                if(i!=0&&j==0){
                    cards[i][j]=new Card(-1,0,0,0,0,0,0,0,0,0,0,0,0,0,true,cardWidth,cardHeight,x+cardWidth*j,y+cardHeight*i,"");
                }
                else{
                    JSONObject card= row.getJSONObject(i!=0?j-1:j);
                    cards[i][j]=new Card(card,i==0,cardWidth,cardHeight,x+cardWidth*j,y+cardHeight*i,"");
                }
            }
        }
        cards[1][0].setVisible(true);
        cards[2][0].setVisible(true);
        cards[3][0].setVisible(true);
        for(Card card:cards[0]){
            if(card!=null) {
                card.setBackground(Color.RED);
                add(card);
            }
        }
        for(Card card:cards[1]) {
            if(card!=null) {
                card.setBackground(Color.BLUE);
                add(card);
            }
        }
        for(Card card:cards[2]) {
            if(card!=null) {
                card.setBackground(Color.YELLOW);
                add(card);
            }
        }
        for(Card card:cards[3]) {
            if(card!=null) {
                card.setBackground(Color.GREEN);
                add(card);
            }
        }
        frame.add(this);
        Menu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                frame.setContentPane(new Menu(frame).view);
            }
        });
    }
    void makePlayer(int id,JSONObject player){
        int[] cost=new int[6];
        int[] discount= new int[6];
        int points=0;
        players[id]=new Card(-1,cost[0],cost[1],cost[2],cost[3],cost[4],cost[5],discount[0],discount[1],discount[2],discount[3],discount[4],discount[5],points,true,cardWidth,cardHeight,Width-cardWidth,Height-cardHeight,player.getString("nick"));
        add(players[id]);
    }
}
