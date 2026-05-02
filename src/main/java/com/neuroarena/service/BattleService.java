package com.neuroarena.service;

import com.neuroarena.model.*;
import com.neuroarena.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class BattleService {

    private static final Logger log = LoggerFactory.getLogger(BattleService.class);

    private final BattleRepository battleRepository;
    private final BattleQuestionRepository battleQuestionRepository;
    private final QuestionRepository questionRepository;
    private final BattlePlayerRepository battlePlayerRepository;
    private final AnswerRepository answerRepository;
    private final PlayerAnswerRepository playerAnswerRepository;

    public BattleService(BattleRepository battleRepository,
                         BattleQuestionRepository battleQuestionRepository,
                         QuestionRepository questionRepository,
                         BattlePlayerRepository battlePlayerRepository,
                         AnswerRepository answerRepository,
                         PlayerAnswerRepository playerAnswerRepository
                        ) {
        this.battleRepository = battleRepository;
        this.battleQuestionRepository = battleQuestionRepository;
        this.questionRepository = questionRepository;
        this.battlePlayerRepository = battlePlayerRepository;
        this.answerRepository = answerRepository;
        this.playerAnswerRepository = playerAnswerRepository;
    }

    @Transactional
    public void startBattle(String battleId, Integer roomTypeId) {

        log.info("Starting battle: {}", battleId);

        // Update all player statuses to IN_GAME
        battlePlayerRepository.updateAllPlayerStatus(battleId,"IN_GAME");

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


    /**
     * Get the next unanswered question for a player
     * Returns the actual Question object with its answers
     */
    public Map<String, Object> getNextUnansweredQuestion(String battleId, String playerId) {

        // 1. Get all question numbers this player has already answered
        Set<Integer> answeredQuestions = playerAnswerRepository.findAnsweredQuestionNumbers(battleId, playerId);

        // 2. Get all questions for this battle (ordered by question number)
        List<BattleQuestion> battleQuestions = battleQuestionRepository.findByBattleIdOrderByQuestionNumber(battleId);

        if (battleQuestions.isEmpty()) {
            log.warn("No questions found for battle: {}", battleId);
            return null;
        }

        // 3. Find the first question not in answered set
        BattleQuestion nextBattleQuestion = null;
        for (BattleQuestion bq : battleQuestions) {
            if (!answeredQuestions.contains(bq.getQuestionNumber())) {
                nextBattleQuestion = bq;
                break;
            }
        }

        // 4. If all questions answered, return null
        if (nextBattleQuestion == null) {
            log.info("Player {} has completed all questions for battle {}", playerId, battleId);
            return null;
        }

        // 5. Get the full question details
        Optional<Question> questionOpt = questionRepository.findById(nextBattleQuestion.getQuestionId());
        if (questionOpt.isEmpty()) {
            log.error("Question not found: {}", nextBattleQuestion.getQuestionId());
            return null;
        }

        Question question = questionOpt.get();

        // 6. Get answer options
        List<Answer> answers = answerRepository.findByQuestion_QuestionId(question.getQuestionId());

        // 7. Build response map (no DTO)
        Map<String, Object> response = new HashMap<>();
        response.put("questionId", question.getQuestionId());
        response.put("questionNumber", nextBattleQuestion.getQuestionNumber());
        response.put("totalQuestions", battleQuestions.size());
        response.put("questionText", question.getQuestionText());
        response.put("timeLimitSeconds", 30);
        response.put("category", question.getCategory());
        response.put("difficulty", question.getDifficulty());


        // Add options
        List<Map<String, Object>> optionsList = new ArrayList<>();
        for (Answer answer : answers) {
            Map<String, Object> option = new HashMap<>();
            option.put("answerId", answer.getAnswerId());
            option.put("answerText", answer.getAnswerText());
            option.put("displayOrder", answer.getDisplayOrder());
            optionsList.add(option);
        }
        response.put("options", optionsList);

        return response;
    }

    /**
     * Check if player has completed all questions
     */
    public boolean hasPlayerCompleted(String battleId, String playerId) {
        Set<Integer> answeredQuestions = playerAnswerRepository.findAnsweredQuestionNumbers(battleId, playerId);
        List<BattleQuestion> battleQuestions = battleQuestionRepository.findByBattleIdOrderByQuestionNumber(battleId);
        return answeredQuestions.size() >= battleQuestions.size();
    }


    @Transactional
    public Map<String, Object> submitAnswer(String battleId, String playerId,
                                            String questionId, String selectedAnswerId,
                                            Integer responseTimeMs) {

        // 1. Prevent double-submission for same question
        boolean alreadyAnswered = playerAnswerRepository
                .existsByBattleIdAndPlayerIdAndQuestionId(battleId, playerId, questionId);
        if (alreadyAnswered) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "Already answered this question");
            return err;
        }

        // 2. Get the BattleQuestion for pointsPossible + questionNumber
        BattleQuestion bq = battleQuestionRepository
                .findByBattleIdAndQuestionId(battleId, questionId)
                .orElseThrow(() -> new RuntimeException("Question not in this battle"));

        // 3. Check correctness
        Answer selected = answerRepository.findById(selectedAnswerId).orElse(null);
        boolean isCorrect = selected != null && Boolean.TRUE.equals(selected.getIsCorrect());

        // 4. Calculate points with time bonus
        int pointsEarned = 0;
        if (isCorrect) {
            int base = bq.getPointsPossible() != null ? bq.getPointsPossible() : 100;
            // Time bonus: full points under 5s, scales down to 0 bonus at 30s
            double timeBonus = Math.max(0, 1.0 - (responseTimeMs / 30000.0));
            pointsEarned = (int) Math.round(base * (1 + timeBonus));
        }

        // 5. Save PlayerAnswer
        PlayerAnswer pa = new PlayerAnswer();
        pa.setPlayerAnswerId(UUID.randomUUID().toString());
        pa.setBattleId(battleId);
        pa.setPlayerId(playerId);
        pa.setQuestionId(questionId);
        pa.setSelectedAnswerId(selectedAnswerId != null ? selectedAnswerId : "");
        pa.setQuestionNumber(bq.getQuestionNumber());
        pa.setResponseTimeMs(responseTimeMs);
        pa.setPointsEarned(pointsEarned);
        pa.setIsCorrect(isCorrect);
        pa.setSubmittedAt(java.time.LocalDateTime.now());
        playerAnswerRepository.save(pa);

        // 6. Build result response
        Map<String, Object> result = new HashMap<>();
        result.put("isCorrect", isCorrect);
        result.put("pointsEarned", pointsEarned);
        result.put("correctAnswerId", getCorrectAnswerId(questionId));
        result.put("questionNumber", bq.getQuestionNumber());
        return result;
    }

    private String getCorrectAnswerId(String questionId) {
        return answerRepository.findByQuestion_QuestionId(questionId)
                .stream()
                .filter(a -> Boolean.TRUE.equals(a.getIsCorrect()))
                .map(Answer::getAnswerId)
                .findFirst()
                .orElse(null);
    }

    public List<Map<String, Object>> getLeaderboard(String battleId) {
        // Start from ALL players in the battle, not just those who answered
        List<BattlePlayer> players = battlePlayerRepository.findByBattleId(battleId);

        List<Map<String, Object>> board = new ArrayList<>();
        for (BattlePlayer bp : players) {
            // Returns 0 if no answers yet (COALESCE handles null)
            int score = playerAnswerRepository.sumPointsByBattleIdAndPlayerId(battleId, bp.getPlayerId());

            Map<String, Object> entry = new HashMap<>();
            entry.put("playerId", bp.getPlayerId());
            entry.put("username", bp.getUsername());
            entry.put("avatar", bp.getAvatarIconUrl());
            entry.put("score", score);
            board.add(entry);
        }

        // Sort descending, assign rank
        board.sort(Comparator.comparingInt(e -> -((int) ((Map<String, Object>) e).get("score"))));
        for (int i = 0; i < board.size(); i++) {
            board.get(i).put("rank", i + 1);
        }

        return board;
    }

}