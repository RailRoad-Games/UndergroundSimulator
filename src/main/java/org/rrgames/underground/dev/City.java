package org.rrgames.underground.dev;

public class City {
    private String name;
    private int population;
    private double percentageHasATicket; // z.B. 0.35 = 35%

    // Konstruktor
    public City(String name, int population, double percentageHasATicket) {
        this.name = name;
        this.population = population;
        this.percentageHasATicket = percentageHasATicket;
    }

    // Getter
    public String getName() {
        return name;
    }

    public int getPopulation() {
        return population;
    }

    public double getPercentageHasATicket() {
        return percentageHasATicket;
    }

    // Setter
    public void setPercentageHasATicket(double percentageHasATicket) {
        this.percentageHasATicket = percentageHasATicket;
    }

    // Logik: Wie viele Leute haben ein Ticket?
    public int getPeopleWithTicket() {
        return (int) (population * percentageHasATicket);
    }

    @Override
    public String toString() {
        return name + " (" + population + " Einwohner, " 
               + (percentageHasATicket * 100) + "% mit Ticket)";
    }
    public void growPopulation(int amount) {
        this.population += amount;
    }
    public void changeTicketPercentage(double delta){
        this.percentageHasATicket += delta;
        if (this.percentageHasATicket < 0) {
            this.percentageHasATicket = 0;
        } else if (this.percentageHasATicket > 1) {
            this.percentageHasATicket = 1;
        }
    }
}