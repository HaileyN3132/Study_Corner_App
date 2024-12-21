package cs477.gmu.project3_tngo26;

public class Set {
    private String id;          // New added

    private String name;
    private String about;
    private int totalCards;
    private double percentProficient;

    public Set(){}

    public Set(String id,String name, String about, int totalCards, double percentProficient){
        this.id = id;           //New added
        this.name = name;
        this.about = about;
        this.totalCards = totalCards;
        this.percentProficient = percentProficient;
    }

    //Getters
    public String getId(){return id;}               //New added

    public String getName(){
        return name;
    }

    public String getAbout(){
        return about;
    }


    public int getTotalCards(){
        return totalCards;
    }

    public double getPercentProficient(){
        return percentProficient;
    }



}
