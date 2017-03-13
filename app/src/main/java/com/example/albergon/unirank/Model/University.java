package com.example.albergon.unirank.Model;

/**
 * This class models an university. It stores the same information that is present in the database
 * and provides access methods to them. It is an immutable class.
 */
public class University {

    private int id = 0;
    private String name = null;
    private String country = null;
    private boolean hasAcronym = false;
    private String acronym = null;

    /**
     * Base public constructor for universities without acronyms. It stores the parameters in the appropriate
     * class fields.
     *
     * @param id        unique university id, by which they are indexed in the database
     * @param name      complete name of the university
     * @param country   ISO 3166-1 country code
     */
    public University(int id, String name, String country) {
        this.id = id;
        this.name = name;
        this.country = country;
        hasAcronym = false;
    }

    /**
     * Public constructor that allows to set an acronym for a university's name. Calls the base
     * constructor for the other parameters.
     *
     * @param id        unique university id, by which they are indexed in the database
     * @param name      complete name of the university
     * @param country   ISO 3166-1 country code
     * @param acronym   acronym for the university's name
     */
    public University(int id, String name, String country, String acronym) {
        this(id, name, country);
        hasAcronym = true;
        this.acronym = acronym;
    }

    /**
     * Getter for the id field.
     *
     * @return  unique university id, by which they are stored in the database
     */
    public int getId() {
        return id;
    }

    /**
     * Getter for the name field.
     *
     * @return complete name of the university
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for the country field.
     *
     * @return ISO 3166-1 country code
     */
    public String getCountry() {
        return country;
    }

    /**
     * Method that allows to check if the university has a commonly used acronym.
     *
     * @return true if the university has an acronym, false otherwise
     */
    public boolean hasAcronym() {
        return hasAcronym;
    }

    /**
     * Getter for the acronym field. The method returns null if the university has no acronym.
     *
     * @return  acronym for the university's name
     */
    public String getAcronym() {
        return acronym;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (other == this) {
            return true;
        }else if (!(other instanceof University)) {
            return false;
        } else {
            return id == ((University) other).getId();
        }
    }

    @Override
    public int hashCode() {
        return this.getId();
    }
}
