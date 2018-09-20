package kz.greetgo.sandbox.controller.model.model;



public class Charm {
    public int id;
    public String name;
    public String description;
    public int energy;
    public boolean actually;

    public Charm(){
        this.id=0;
        this.name="";
        this.energy=0;
        this.description="";
    }

    @Override
    public String toString() {

        return "Charm{" +
          "id=" + id +
          ", name='" + name + '\'' +
          ", description='" + description + '\'' +
          ", energy=" + energy +
          ", actually=" + actually +
          '}';
    }
}
