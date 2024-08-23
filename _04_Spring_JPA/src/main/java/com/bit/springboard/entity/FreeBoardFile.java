package com.bit.springboard.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@Setter
@Table(name="freeboard_file")
@SequenceGenerator(
        name = "FreeBoardFileSeqGenerator",
        sequenceName = "freeboard_file_seq",
        initialValue = 1,
        allocationSize = 1
)
public class FreeBoardFile {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FreeBoardFileSeqGenerator")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "board_id")
    @JsonBackReference
    private Freeboard freeBoard;

    private String fileName;

    private String fileoriginname;

    private String filePath;

    private String filetype;
}
