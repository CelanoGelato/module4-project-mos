package com.codegym.mos.module4projectmos.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Collection;

@Entity
@Data
@NoArgsConstructor
@JsonIgnoreProperties(value = {"songs", "albums"}, allowGetters = true, ignoreUnknown = true)
public class Genre {

    @Id
    @GeneratedValue
    private Integer id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String name;

    @JsonBackReference("song-genre")
    @ManyToMany(mappedBy = "genres", fetch = FetchType.LAZY)
    private Collection<Song> songs;

    @JsonBackReference("album-genre")
    @ManyToMany(mappedBy = "genres", fetch = FetchType.LAZY)
    private Collection<Album> albums;

    public Genre(String name) {
        this.name = name;
    }
}
