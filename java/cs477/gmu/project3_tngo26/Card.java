package cs477.gmu.project3_tngo26;

public class Card {
    private String cardId, front, back;

    public Card(){}

    public Card(String cardId, String front, String back){
        this.cardId = cardId;
        this.front = front;
        this.back = back;
    }


    public String getCardId(){return cardId;}
    public String getFront(){return front;}
    public String getBack(){return back;}




}
