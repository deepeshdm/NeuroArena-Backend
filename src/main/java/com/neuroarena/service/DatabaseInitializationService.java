package com.neuroarena.service;

import com.neuroarena.config.SeedData;
import com.neuroarena.config.SeedData.QuestionData;
import com.neuroarena.config.SeedData.RoomTypeData;
import com.neuroarena.model.Answer;
import com.neuroarena.model.Question;
import com.neuroarena.model.RoomType;
import com.neuroarena.repository.AnswerRepository;
import com.neuroarena.repository.QuestionRepository;
import com.neuroarena.repository.RoomTypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service to initialize database with seed data on application startup
 * Automatically inserts room types and questions if they don't already exist
 */
@Slf4j
@Service
public class DatabaseInitializationService {

    private final RoomTypeRepository roomTypeRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    public DatabaseInitializationService(RoomTypeRepository roomTypeRepository,
                                        QuestionRepository questionRepository,
                                        AnswerRepository answerRepository) {
        this.roomTypeRepository = roomTypeRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
    }

    /**
     * Initialize database on application startup
     * This method runs automatically after the application is ready
     */
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initializeDatabase() {
        log.info("========================================");
        log.info("Starting Database Initialization...");
        log.info("========================================");

        // Initialize room types
        initializeRoomTypes();

        // Initialize questions and answers
        initializeQuestionsAndAnswers();

        // Verify initialization
        verifyInitialization();

        log.info("========================================");
        log.info("Database Initialization Complete!");
        log.info("========================================");
    }

    /**
     * Insert room types if they don't already exist
     */
    private void initializeRoomTypes() {
        log.info("Initializing Room Types...");

        for (RoomTypeData roomTypeData : SeedData.ROOM_TYPES) {
            // Check if room type already exists
            if (roomTypeRepository.findById(roomTypeData.roomTypeId).isEmpty()) {
                RoomType roomType = new RoomType();
                roomType.setRoomTypeId(roomTypeData.roomTypeId);
                roomType.setName(roomTypeData.name);
                roomType.setDescription(roomTypeData.description);
                roomType.setDifficultyLevel(roomTypeData.difficultyLevel);
                roomType.setBasePoints(roomTypeData.basePoints);
                roomType.setIconPath(roomTypeData.iconPath); // Can be set later

                roomTypeRepository.save(roomType);
                log.info("✓ Inserted Room Type: {} (ID: {})", roomTypeData.name, roomTypeData.roomTypeId);
            } else {
                log.info("✓ Room Type already exists: {} (ID: {})", roomTypeData.name, roomTypeData.roomTypeId);
            }
        }

        long totalRoomTypes = roomTypeRepository.count();
        log.info("Total Room Types in database: {}", totalRoomTypes);
    }

    /**
     * Insert questions and answers if they don't already exist
     */
    private void initializeQuestionsAndAnswers() {
        log.info("Initializing Questions and Answers...");

        long existingQuestionCount = questionRepository.count();

        // If questions already exist, skip initialization
        if (existingQuestionCount > 0) {
            log.info("✓ Questions already exist in database. Skipping insertion. (Total: {})", existingQuestionCount);
            return;
        }

        // Insert all questions from all room types
        List<QuestionData> allQuestions = SeedData.getAllQuestions();
        log.info("Inserting {} questions with answers...", allQuestions.size());

        int questionCount = 0;
        int answerCount = 0;

        for (QuestionData questionData : allQuestions) {
            // Create question entity
            Question question = new Question();
            question.setQuestionId(questionData.questionId);
            question.setRoomTypeId(questionData.roomTypeId);
            question.setQuestionText(questionData.questionText);
            question.setDifficulty(questionData.difficulty);
            question.setBasePoints(questionData.basePoints);
            question.setTimeLimitSeconds(questionData.timeLimitSeconds);
            question.setCategory(questionData.category);
            question.setCreatedAt(LocalDateTime.now());

            // Save question
            Question savedQuestion = questionRepository.save(question);
            questionCount++;

            // Create and save answers
            for (SeedData.AnswerData answerData : questionData.answers) {
                Answer answer = new Answer();
                answer.setAnswerId(answerData.answerId);
                answer.setQuestion(savedQuestion);
                answer.setAnswerText(answerData.answerText);
                answer.setIsCorrect(answerData.isCorrect);
                answer.setDisplayOrder(answerData.displayOrder);

                answerRepository.save(answer);
                answerCount++;
            }

            // Log progress every 10 questions
            if (questionCount % 10 == 0) {
                log.info("  Inserted {} questions...", questionCount);
            }
        }

        long totalQuestions = questionRepository.count();
        long totalAnswers = answerRepository.count();

        log.info("✓ Questions inserted: {}", questionCount);
        log.info("✓ Answers inserted: {}", answerCount);
        log.info("Total Questions in database: {}", totalQuestions);
        log.info("Total Answers in database: {}", totalAnswers);

        // Log breakdown by room type
        logQuestionsBreakdown();
    }

    /**
     * Log questions breakdown by room type
     */
    private void logQuestionsBreakdown() {
        log.info("");
        log.info("Questions Breakdown by Room Type:");
        log.info("─────────────────────────────────");

        for (SeedData.RoomTypeData roomTypeData : SeedData.ROOM_TYPES) {
            long count = questionRepository.countByRoomTypeId(roomTypeData.roomTypeId);
            log.info("  {} (ID: {}): {} questions",
                    String.format("%-20s", roomTypeData.name),
                    roomTypeData.roomTypeId,
                    count);
        }

        log.info("─────────────────────────────────");
        long total = questionRepository.count();
        log.info("  Total: {} questions", total);
        log.info("");
    }

    /**
     * Verify database initialization was successful
     */
    @Transactional(readOnly = true)
    public void verifyInitialization() {
        long roomTypeCount = roomTypeRepository.count();
        long questionCount = questionRepository.count();
        long answerCount = answerRepository.count();

        log.info("");
        log.info("Database Verification:");
        log.info("  Room Types: {}", roomTypeCount);
        log.info("  Questions: {}", questionCount);
        log.info("  Answers: {}", answerCount);
        
        // Expected counts: 8 room types, questions vary based on SeedData
        if (roomTypeCount == 8 && questionCount > 0 && answerCount > 0) {
            log.info("  Status: ✓ OK");
        } else {
            log.warn("  Status: ✗ INCOMPLETE - Please check seed data");
        }
        log.info("");
    }
}