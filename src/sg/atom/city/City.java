/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sg.atom.city;

import sg.atom.city.creator.AbstractMapCreator;

/**
 *
 * @author hungcuong
 */
public class City {

    String name;
    String country;
    int id;
    int population;
    CityMap map;

    public City(String name) {
        this.name = name;
        this.country = "";
        this.id = 1;
        this.population = 1;
    }
    
    public City(String name, String country, int id, int population) {
        this.name = name;
        this.country = country;
        this.id = id;
        this.population = population;
    }

    CityMap createMap(AbstractMapCreator creator) {
        this.map = creator.createMap();
        return this.map;
    }
}
