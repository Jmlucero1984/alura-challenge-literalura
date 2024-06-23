
package com.aluralatam.literalura.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "authors", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String birthYear;
    private String deathYear;
    private String type;

    @ManyToMany(mappedBy = "authors")
    private Set<Book> book;

    public Author() {}

    public Author(String name, String birthYear, String deathYear, String type) {
        this.name = name;
        this.birthYear = birthYear;
        this.deathYear = deathYear;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(String birthYear) {
        this.birthYear = birthYear;
    }

    public String getDeathYear() {
        return deathYear;
    }

    public void setDeathYear(String deathYear) {
        this.deathYear = deathYear;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    @Override
    public String toString() {
        return "Name='" + name + '\'' +
                ", BirthYear='" + birthYear + '\'' +
                ", DeathYear='" + deathYear + '\'' +
                ", Type='" + type + '\'';
    }
}