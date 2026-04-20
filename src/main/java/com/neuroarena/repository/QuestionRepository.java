package com.neuroarena.repository;

import com.neuroarena.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByRoomType_RoomTypeId(Long roomTypeId);

}
