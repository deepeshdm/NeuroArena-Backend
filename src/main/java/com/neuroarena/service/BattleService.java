package com.neuroarena.service;

import com.neuroarena.model.Battle;
import com.neuroarena.model.BattleQuestion;
import com.neuroarena.model.Question;
import com.neuroarena.repository.BattleQuestionRepository;
import com.neuroarena.repository.BattleRepository;
import com.neuroarena.repository.QuestionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class BattleService {

    private static final Logger log = LoggerFactory.getLogger(BattleService.class);

    private final BattleRepository battleRepository;
    private final BattleQuestionRepository battleQuestionRepository;
    private final QuestionRepository questionRepository;

    public BattleService(BattleRepository battleRepository,
                         BattleQuestionRepository battleQuestionRepository,
                         QuestionRepository questionRepository) {
        this.battleRepository = battleRepository;
        this.battleQuestionRepository = battleQuestionRepository;
        this.questionRepository = questionRepository;
    }

    @Transactional
    public void startBattle(String battleId, Integer roomTypeId) {

        log.info("Starting battle: {}", battleId);

        // 1. Update battle status to IN_PROGRESS
        battleRepository.updateStatusToInProgress(battleId);

        // 2. Get 10 random questions based on room type
        List<Question> questions;
        if (roomTypeId == 1) {
            questions = questionRepository.findRandomMixedQuestions();
        } else {
            questions = questionRepository.findRandomQuestionsByRoomType(roomTypeId);
        }

        // 3. Insert into battle_questions table
        int order = 1;
        for (Question q : questions) {
            BattleQuestion bq = new BattleQuestion();
            bq.setBattleQuestionId(UUID.randomUUID().toString());
            bq.setBattleId(battleId);
            bq.setQuestionId(q.getQuestionId());
            bq.setQuestionNumber(order);
            bq.setPointsPossible(q.getBasePoints());
            battleQuestionRepository.save(bq);
            order++;
        }

        log.info("Battle {} started with {} questions", battleId, questions.size());
    }
}