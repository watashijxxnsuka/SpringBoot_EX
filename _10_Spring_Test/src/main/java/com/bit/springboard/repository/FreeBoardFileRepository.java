package com.bit.springboard.repository;

import com.bit.springboard.entity.FreeBoardFile;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface FreeBoardFileRepository extends JpaRepository<FreeBoardFile, Long> {
   Optional<FreeBoardFile> findByFreeBoardId(Long id);
}
