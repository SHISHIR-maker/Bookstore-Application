package bookstoreapplication;

import javafx.scene.control.CheckBox;

public class Book
{
    private String name;
    private float price;
    private CheckBox selected;

    public Book(String name, float price)
    {
        this.name = name;
        this.price = price;
        this.selected = new CheckBox();
    }

    public String getName()
    {
        return name;
    }

    public float getPrice()
    {
        return price;
    }
    
    //CheckBox Methods
    
    public CheckBox getSelected()
    {
        return this.selected;
    }
    
    public void setSelected(CheckBox selected)
    {
        this.selected = selected;
    }
}
