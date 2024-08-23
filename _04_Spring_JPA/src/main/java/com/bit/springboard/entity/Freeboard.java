package com.bit.springboard.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
// @SequenceGenerator: 시퀀스에 대한 설정을 할 수 있는 어노테이션
@SequenceGenerator(
        name = "FreeBoardSeqGenerator",
        sequenceName = "freeboard_seq",
        initialValue = 1,
        allocationSize = 1
)
public class Freeboard {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FreeBoardSeqGenerator")
    private Long id;

    @Column(length = 500, nullable = false)
    private String title;

    @Column(length = 1000, nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "writer_id")
    private Member member;

    private LocalDateTime regdate;
    private LocalDateTime moddate;

    private int cnt;

    @OneToMany(mappedBy = "freeBoard", cascade = CascadeType.ALL)
    // 양방향 관계 매핑 시 순환참조 에러가 발생하는 경우가 많은데,
    // 순환참조 에러를 발생하기 위해서 부모 엔티티 필드에는 @JsonManagedReference 어노테이션을 사용하여
    // 데이터를 Json 변환 시 정상적으로 직렬화 되도록 설정.
    // 자식 엔티티 필드에는 @JsonBackReference 어노테이션을 사용하여
    // 직렬화가 되지 않도록 설정한다.
    @JsonManagedReference
    List<FreeBoardFile> boardFileList = new ArrayList<>();


}
