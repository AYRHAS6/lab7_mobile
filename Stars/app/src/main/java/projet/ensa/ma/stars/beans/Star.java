package projet.ensa.ma.stars.beans;

public class Star {
    private int id;
    private String name;
    private String img;
    private float rating;
    private static int counter = 0;

    public Star(String name, String img, float rating) {
        this.id = ++counter;
        this.name = name;
        this.img = img;
        // Clamp rating between 0 and 5
        this.rating = Math.max(0f, Math.min(5f, rating));
    }

    public int getId()            { return id; }
    public String getName()       { return name; }
    public String getImg()        { return img; }
    public float getRating()      { return rating; }

    public void setName(String name)   { this.name = name; }
    public void setImg(String img)     { this.img = img; }
    public void setRating(float r)     { this.rating = Math.max(0f, Math.min(5f, r)); }

    @Override
    public String toString() {
        return name + " (" + rating + "/5)";
    }
}