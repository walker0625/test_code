package com.testcode.testcode.repository;

import com.testcode.testcode.domain.Study;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRepository extends JpaRepository<Study, Long> {
}
