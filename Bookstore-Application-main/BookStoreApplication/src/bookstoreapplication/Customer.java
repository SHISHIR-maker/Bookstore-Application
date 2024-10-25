package bookstoreapplication;

public class Customer
{
    //Instance variables
    private String username;
    private String password;
    private int points;
    private String status;

    //Constructor
    public Customer(String username, String password, int points)
    {
        this.username = username;
        this.password = password;
        this.points = points;
        
        if(this.points < 1000)
            this.status = "Silver";
        else
            this.status = "Gold";
    }

    //Getter methods
    
    public String getUsername()
    {
        return this.username;
    }
    
    public String getPassword()
    {
        return this.password;
    }

    public int getPoints()
    {
        return this.points;
    }

    public String getStatus()
    {
        this.updateStatus();
        return this.status;
    }
    
    //Point & Status modifier methods
    
    public void addPoints(int points)
    {
        this.points += points;
        this.updateStatus();
    }
    
    public void removePoints(int points)
    {
        this.points -= points;
        this.updateStatus();
    }
    
    private void updateStatus()
    {
        if(this.points < 1000)
            this.status = "Silver";
        else
            this.status = "Gold";
    }
    
}