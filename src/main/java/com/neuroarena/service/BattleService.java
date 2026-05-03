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
        response.put("pointsPossible", nextBattleQuestion.getPointsPossible());
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
            double multiplier;
            if (responseTimeMs <= 5000) {
                multiplier = 2.5;
            } else if (responseTimeMs >= 30000) {
                multiplier = 1.0;
            } else {
                // Linear scale from 2.5x at 5s down to 1.0x at 30s
                multiplier = 2.5 - (1.5 * (responseTimeMs - 5000.0) / 25000.0);
            }
            pointsEarned = (int) Math.round(base * multiplier);
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


    public Map<String, Object> getResultData(String battleId, String playerId) {

        // 1. Get all player answers for this player
        List<PlayerAnswer> answers = playerAnswerRepository
                .findByBattleIdAndPlayerId(battleId, playerId);

        // 2. Calculate stats
        int totalScore     = answers.stream().mapToInt(a -> a.getPointsEarned() != null ? a.getPointsEarned() : 0).sum();
        long correctCount  = answers.stream().filter(a -> Boolean.TRUE.equals(a.getIsCorrect())).count();
        int totalAnswered  = answers.size();
        double accuracy    = totalAnswered > 0 ? (correctCount * 100.0 / totalAnswered) : 0;
        double avgSpeedMs  = answers.stream()
                .mapToInt(a -> a.getResponseTimeMs() != null ? a.getResponseTimeMs() : 30000)
                .average().orElse(0);

        // 3. Get full leaderboard to determine rank
        List<Map<String, Object>> leaderboard = getLeaderboard(battleId);
        int finalRank    = leaderboard.stream()
                .filter(e -> playerId.equals(e.get("playerId")))
                .map(e -> (int) e.get("rank"))
                .findFirst().orElse(0);
        int totalPlayers = leaderboard.size();
        boolean userWon  = finalRank == 1;

        // 4. Calculate cognitive attributes (0–100 scale)
        // LOGIC     = accuracy
        // SPEED     = inverse of avg response time (30s = 0, 0s = 100)
        // FOCUS     = % of questions answered before timer expired
        // EXECUTION = correct answers in under 10s / total correct
        // MEMORY    = score relative to max possible
        int totalQuestions = battleQuestionRepository.findByBattleIdOrderByQuestionNumber(battleId).size();
        int maxPossible    = battleQuestionRepository.findByBattleIdOrderByQuestionNumber(battleId)
                .stream().mapToInt(bq -> bq.getPointsPossible() != null ? bq.getPointsPossible() : 100).sum();

        int logicVal     = (int) Math.round(accuracy);
        int speedVal     = (int) Math.round(Math.max(0, 100 - (avgSpeedMs / 300)));
        int focusVal     = totalQuestions > 0 ? (int) Math.round((totalAnswered * 100.0) / totalQuestions) : 0;
        int executionVal = (int) Math.round(answers.stream()
                .filter(a -> Boolean.TRUE.equals(a.getIsCorrect()) && a.getResponseTimeMs() != null && a.getResponseTimeMs() <= 10000)
                .count() * 100.0 / Math.max(1, correctCount));
        int memoryVal    = maxPossible > 0 ? (int) Math.round((totalScore * 100.0) / maxPossible) : 0;

        // 5. Derive persona
        Map<String, Object> persona = derivePersona(logicVal, speedVal, focusVal, userWon);

        // 6. Build result
        Map<String, Object> result = new HashMap<>();
        result.put("battleId",     battleId);
        result.put("playerId",     playerId);
        result.put("finalRank",    finalRank);
        result.put("totalPlayers", totalPlayers);
        result.put("score",        totalScore);
        result.put("accuracy",     Math.round(accuracy * 10.0) / 10.0);
        result.put("avgSpeedSecs", Math.round(avgSpeedMs / 100.0) / 10.0);
        result.put("correctAnswers", (int) correctCount);
        result.put("totalQuestions", totalQuestions);
        result.put("userWon",      userWon);
        result.put("leaderboard",  leaderboard);
        result.put("cognitiveAttributes", List.of(
                Map.of("name", "LOGIC",     "value", logicVal),
                Map.of("name", "SPEED",     "value", speedVal),
                Map.of("name", "FOCUS",     "value", focusVal),
                Map.of("name", "EXECUTION", "value", executionVal),
                Map.of("name", "MEMORY",    "value", memoryVal)
        ));
        result.put("persona", persona);

        return result;
    }

    private Map<String, Object> derivePersona(int logic, int speed, int focus, boolean won) {
        String name, description, topSkill, skillLevel, growthArea, growthValue;

        if (!won) {
            name        = "The Fragmented";
            description = "Your neural cohesion is faltering. Mental structures are collapsing under pressure, leading to critical inefficiencies in logic execution.";
            topSkill    = logic >= speed ? "Structural Logic" : "Neural Speed";
            skillLevel  = "Developing";
            growthArea  = "Cognitive Cohesion";
            growthValue = "Aura-3";
        } else if (logic >= 90 && speed >= 80) {
            name        = "The Architect";
            description = "You build mental structures with terrifying precision. Your logic is your strongest weapon, allowing you to bypass noise effortlessly.";
            topSkill    = "Structural Logic";
            skillLevel  = "Elite";
            growthArea  = "Speed-Focus Synergy";
            growthValue = "Aura-7";
        } else if (speed >= 85) {
            name        = "The Phantom";
            description = "You move faster than thought itself. Decisions are made before others even read the question. Pure reflex, pure dominance.";
            topSkill    = "Neural Speed";
            skillLevel  = "Elite";
            growthArea  = "Deep Logic";
            growthValue = "Aura-6";
        } else if (focus >= 90) {
            name        = "The Sentinel";
            description = "Unwavering. Every question met with locked focus and calculated precision. You leave nothing on the table.";
            topSkill    = "Focus Lock";
            skillLevel  = "Advanced";
            growthArea  = "Speed Burst";
            growthValue = "Aura-5";
        } else {
            name        = "The Strategist";
            description = "Measured, methodical, and effective. You balance speed and accuracy to consistently outperform the field.";
            topSkill    = "Balanced Execution";
            skillLevel  = "Advanced";
            growthArea  = "Peak Speed";
            growthValue = "Aura-5";
        }

        Map<String, Object> p = new HashMap<>();
        p.put("name", name);
        p.put("description", description);
        p.put("topSkill", topSkill);
        p.put("skillLevel", skillLevel);
        p.put("growthArea", growthArea);
        p.put("growthValue", growthValue);
        return p;
    }

}