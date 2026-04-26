package com.neuroarena.config;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Seed data for NeuroArena database
 * Contains room types and questions that are loaded on application startup
 * This ensures the database always has the required reference data
 */
public class SeedData {

    // ============================================================================
    // ROOM TYPES
    // ============================================================================

    public static class RoomTypeData {
        public final int roomTypeId;
        public final String name;
        public final String description;
        public final String difficultyLevel;
        public final int basePoints;
        public final String iconPath;  // NEW: Icon path for room type

        public RoomTypeData(int roomTypeId, String name, String description, 
                           String difficultyLevel, int basePoints, String iconPath) {
            this.roomTypeId = roomTypeId;
            this.name = name;
            this.description = description;
            this.difficultyLevel = difficultyLevel;
            this.basePoints = basePoints;
            this.iconPath = iconPath;
        }
    }

    public static final List<RoomTypeData> ROOM_TYPES = Arrays.asList(
        new RoomTypeData(1, "Mixed Bag", 
            "A chaotic swirl of every topic. Perfect for the ultimate generalist.", 
            "Mixed", 100, "https://i.ibb.co/Pvr69GpF/mixed-bag.jpg"),
        
        new RoomTypeData(2, "Science & Tech", 
            "Quantum physics to Javascript frameworks. Not for the faint of heart.", 
            "Medium", 100, "https://i.ibb.co/C5kjnd3J/science-tech.jpg"),
        
        new RoomTypeData(3, "History & Culture", 
            "Uncover the secrets of civilizations and artistic revolutions.", 
            "Medium", 100, "https://i.ibb.co/8n1NQpDG/history-culture.jpg"),
        
        new RoomTypeData(4, "Pop Culture", 
            "Movies, music, and the digital zeitgeist. Stay relevant.", 
            "Easy", 80, "https://i.ibb.co/JjNn57B0/pop-culture.jpg"),
        
        new RoomTypeData(5, "Brain Teasers", 
            "Logic puzzles and lateral thinking challenges. Sharpen your mind.", 
            "Hard", 150, "https://i.ibb.co/nNwNKg7p/brain-teasers.jpg"),
        
        new RoomTypeData(6, "Hardcore Mode", 
            "One wrong answer and you're out. Maximum pressure.", 
            "Hard", 200, "https://i.ibb.co/YBfhPVCY/hardcore-mode.jpg"),
        
        new RoomTypeData(7, "Gaming & Esports", 
            "From 8-bit classics to the latest global tournament metas.", 
            "Medium", 100, "https://i.ibb.co/99fLmP8C/gaming-esports.jpg"),
        
        new RoomTypeData(8, "Sports", 
            "The physics of the field and the history of champions.", 
            "Medium", 100, "https://i.ibb.co/NdCqfmdc/sports.jpg")
    );

    // ============================================================================
    // QUESTION DATA
    // ============================================================================

    public static class QuestionData {
        public final String questionId;
        public final int roomTypeId;
        public final String questionText;
        public final String difficulty;
        public final int basePoints;
        public final int timeLimitSeconds;
        public final String category;
        public final List<AnswerData> answers;

        public QuestionData(int roomTypeId, String questionText, String difficulty, 
                           int basePoints, int timeLimitSeconds, String category, 
                           AnswerData... answers) {
            this.questionId = UUID.randomUUID().toString();
            this.roomTypeId = roomTypeId;
            this.questionText = questionText;
            this.difficulty = difficulty;
            this.basePoints = basePoints;
            this.timeLimitSeconds = timeLimitSeconds;
            this.category = category;
            this.answers = Arrays.asList(answers);
        }
    }

    public static class AnswerData {
        public final String answerId;
        public final String answerText;
        public final boolean isCorrect;
        public final int displayOrder;

        public AnswerData(String answerText, boolean isCorrect, int displayOrder) {
            this.answerId = UUID.randomUUID().toString();
            this.answerText = answerText;
            this.isCorrect = isCorrect;
            this.displayOrder = displayOrder;
        }
    }

    // ============================================================================
    // ROOM TYPE 1: MIXED BAG (Mixed difficulty)
    // ============================================================================

    public static final List<QuestionData> MIXED_BAG_QUESTIONS = Arrays.asList(
        new QuestionData(1, "What is the largest planet in our solar system?", "Easy", 50, 30, "Science",
            new AnswerData("Venus", false, 1),
            new AnswerData("Jupiter", true, 2),
            new AnswerData("Saturn", false, 3),
            new AnswerData("Neptune", false, 4)
        ),
        new QuestionData(1, "Who wrote 'Romeo and Juliet'?", "Easy", 50, 30, "Literature",
            new AnswerData("Jane Austen", false, 1),
            new AnswerData("William Shakespeare", true, 2),
            new AnswerData("Charles Dickens", false, 3),
            new AnswerData("Mark Twain", false, 4)
        ),
        new QuestionData(1, "What is the chemical symbol for Gold?", "Medium", 100, 25, "Chemistry",
            new AnswerData("Gd", false, 1),
            new AnswerData("Go", false, 2),
            new AnswerData("Au", true, 3),
            new AnswerData("Ag", false, 4)
        ),
        new QuestionData(1, "In what year did the Titanic sink?", "Easy", 50, 30, "History",
            new AnswerData("1912", true, 1),
            new AnswerData("1905", false, 2),
            new AnswerData("1920", false, 3),
            new AnswerData("1898", false, 4)
        ),
        new QuestionData(1, "What does CPU stand for?", "Easy", 50, 25, "Technology",
            new AnswerData("Central Processing Unit", true, 1),
            new AnswerData("Central Program Utility", false, 2),
            new AnswerData("Computer Personal Unit", false, 3),
            new AnswerData("Central Processor Upgrade", false, 4)
        ),
        new QuestionData(1, "How many bones does an adult human have?", "Medium", 100, 28, "Biology",
            new AnswerData("186", false, 1),
            new AnswerData("206", true, 2),
            new AnswerData("226", false, 3),
            new AnswerData("246", false, 4)
        ),
        new QuestionData(1, "Which country is home to the kangaroo?", "Easy", 50, 30, "Geography",
            new AnswerData("New Zealand", false, 1),
            new AnswerData("Australia", true, 2),
            new AnswerData("Indonesia", false, 3),
            new AnswerData("South Africa", false, 4)
        ),
        new QuestionData(1, "What is the speed of light?", "Hard", 150, 20, "Physics",
            new AnswerData("300,000 km/s", true, 1),
            new AnswerData("150,000 km/s", false, 2),
            new AnswerData("450,000 km/s", false, 3),
            new AnswerData("600,000 km/s", false, 4)
        ),
        new QuestionData(1, "Who painted the Mona Lisa?", "Easy", 50, 30, "Art",
            new AnswerData("Vincent van Gogh", false, 1),
            new AnswerData("Leonardo da Vinci", true, 2),
            new AnswerData("Michelangelo", false, 3),
            new AnswerData("Raphael", false, 4)
        ),
        new QuestionData(1, "What is the smallest prime number?", "Medium", 100, 25, "Mathematics",
            new AnswerData("0", false, 1),
            new AnswerData("1", false, 2),
            new AnswerData("2", true, 3),
            new AnswerData("3", false, 4)
        )
    );

    // ============================================================================
    // ROOM TYPE 2: SCIENCE & TECH (Medium difficulty)
    // ============================================================================

    public static final List<QuestionData> SCIENCE_TECH_QUESTIONS = Arrays.asList(
        new QuestionData(2, "What is the chemical formula for water?", "Easy", 50, 30, "Chemistry",
            new AnswerData("H2O", true, 1),
            new AnswerData("H2O2", false, 2),
            new AnswerData("HO", false, 3),
            new AnswerData("H3O", false, 4)
        ),
        new QuestionData(2, "What does DNA stand for?", "Medium", 100, 25, "Biology",
            new AnswerData("Deoxyribonucleic Acid", true, 1),
            new AnswerData("Dynamic Nucleic Acid", false, 2),
            new AnswerData("Deoxy Nuclear Acid", false, 3),
            new AnswerData("Deoxyribose Nitrogen Acid", false, 4)
        ),
        new QuestionData(2, "What is the powerhouse of the cell?", "Easy", 50, 30, "Biology",
            new AnswerData("Nucleus", false, 1),
            new AnswerData("Mitochondria", true, 2),
            new AnswerData("Ribosome", false, 3),
            new AnswerData("Golgi Apparatus", false, 4)
        ),
        new QuestionData(2, "How many strings does a standard violin have?", "Medium", 100, 25, "Physics",
            new AnswerData("4", true, 1),
            new AnswerData("5", false, 2),
            new AnswerData("6", false, 3),
            new AnswerData("8", false, 4)
        ),
        new QuestionData(2, "What is the SI unit of electric current?", "Medium", 100, 25, "Physics",
            new AnswerData("Volt", false, 1),
            new AnswerData("Ampere", true, 2),
            new AnswerData("Ohm", false, 3),
            new AnswerData("Watt", false, 4)
        ),
        new QuestionData(2, "What does RAM stand for in computing?", "Easy", 50, 30, "Technology",
            new AnswerData("Read-only Access Memory", false, 1),
            new AnswerData("Random Access Memory", true, 2),
            new AnswerData("Rapid Application Memory", false, 3),
            new AnswerData("Registered Access Module", false, 4)
        ),
        new QuestionData(2, "What is the chemical symbol for Iron?", "Easy", 50, 30, "Chemistry",
            new AnswerData("Ir", false, 1),
            new AnswerData("In", false, 2),
            new AnswerData("Fe", true, 3),
            new AnswerData("Fn", false, 4)
        ),
        new QuestionData(2, "How many bits are in a byte?", "Medium", 100, 25, "Technology",
            new AnswerData("4", false, 1),
            new AnswerData("8", true, 2),
            new AnswerData("16", false, 3),
            new AnswerData("32", false, 4)
        ),
        new QuestionData(2, "What is the boiling point of water at sea level?", "Easy", 50, 30, "Chemistry",
            new AnswerData("90°C", false, 1),
            new AnswerData("100°C", true, 2),
            new AnswerData("110°C", false, 3),
            new AnswerData("120°C", false, 4)
        ),
        new QuestionData(2, "What does GPU stand for?", "Medium", 100, 25, "Technology",
            new AnswerData("Graphics Processing Unit", true, 1),
            new AnswerData("General Purpose Unit", false, 2),
            new AnswerData("Graphics Platform Utility", false, 3),
            new AnswerData("Global Processor Unit", false, 4)
        )
    );

    // ============================================================================
    // ROOM TYPE 3: HISTORY & CULTURE (Medium difficulty)
    // ============================================================================

    public static final List<QuestionData> HISTORY_CULTURE_QUESTIONS = Arrays.asList(
        new QuestionData(3, "In which year did World War II end?", "Easy", 50, 30, "History",
            new AnswerData("1943", false, 1),
            new AnswerData("1944", false, 2),
            new AnswerData("1945", true, 3),
            new AnswerData("1946", false, 4)
        ),
        new QuestionData(3, "Who was the first President of the United States?", "Easy", 50, 30, "History",
            new AnswerData("Thomas Jefferson", false, 1),
            new AnswerData("George Washington", true, 2),
            new AnswerData("John Adams", false, 3),
            new AnswerData("Benjamin Franklin", false, 4)
        ),
        new QuestionData(3, "Which ancient wonder is located in Egypt?", "Medium", 100, 25, "History",
            new AnswerData("Colossus of Rhodes", false, 1),
            new AnswerData("Great Pyramid of Giza", true, 2),
            new AnswerData("Hanging Gardens of Babylon", false, 3),
            new AnswerData("Lighthouse of Alexandria", false, 4)
        ),
        new QuestionData(3, "What is the capital of France?", "Easy", 50, 30, "Geography",
            new AnswerData("Lyon", false, 1),
            new AnswerData("Paris", true, 2),
            new AnswerData("Marseille", false, 3),
            new AnswerData("Nice", false, 4)
        ),
        new QuestionData(3, "In which country is Machu Picchu located?", "Medium", 100, 25, "Geography",
            new AnswerData("Brazil", false, 1),
            new AnswerData("Colombia", false, 2),
            new AnswerData("Peru", true, 3),
            new AnswerData("Bolivia", false, 4)
        ),
        new QuestionData(3, "Who wrote 'A Tale of Two Cities'?", "Easy", 50, 30, "Literature",
            new AnswerData("George Orwell", false, 1),
            new AnswerData("Charles Dickens", true, 2),
            new AnswerData("Emily Bronte", false, 3),
            new AnswerData("Jane Austen", false, 4)
        ),
        new QuestionData(3, "What year did the Berlin Wall fall?", "Medium", 100, 25, "History",
            new AnswerData("1987", false, 1),
            new AnswerData("1988", false, 2),
            new AnswerData("1989", true, 3),
            new AnswerData("1990", false, 4)
        ),
        new QuestionData(3, "Which empire built the Great Wall of China?", "Medium", 100, 25, "History",
            new AnswerData("Han Dynasty", true, 1),
            new AnswerData("Qin Dynasty", false, 2),
            new AnswerData("Ming Dynasty", false, 3),
            new AnswerData("Tang Dynasty", false, 4)
        ),
        new QuestionData(3, "What is the official language of Brazil?", "Easy", 50, 30, "Culture",
            new AnswerData("Spanish", false, 1),
            new AnswerData("Portuguese", true, 2),
            new AnswerData("French", false, 3),
            new AnswerData("Italian", false, 4)
        ),
        new QuestionData(3, "Who was the first person to walk on the moon?", "Easy", 50, 30, "History",
            new AnswerData("Buzz Aldrin", false, 1),
            new AnswerData("Neil Armstrong", true, 2),
            new AnswerData("Yuri Gagarin", false, 3),
            new AnswerData("John Glenn", false, 4)
        )
    );

    // ============================================================================
    // ROOM TYPE 4: POP CULTURE (Easy difficulty)
    // ============================================================================

    public static final List<QuestionData> POP_CULTURE_QUESTIONS = Arrays.asList(
        new QuestionData(4, "Who played Jack Dawson in Titanic?", "Easy", 50, 30, "Movies",
            new AnswerData("Brad Pitt", false, 1),
            new AnswerData("Leonardo DiCaprio", true, 2),
            new AnswerData("Johnny Depp", false, 3),
            new AnswerData("Tom Cruise", false, 4)
        ),
        new QuestionData(4, "Which band sang 'Yellow'?", "Easy", 50, 30, "Music",
            new AnswerData("Radiohead", false, 1),
            new AnswerData("Coldplay", true, 2),
            new AnswerData("The Killers", false, 3),
            new AnswerData("Arctic Monkeys", false, 4)
        ),
        new QuestionData(4, "Who is known as the 'King of Pop'?", "Easy", 50, 30, "Music",
            new AnswerData("Prince", false, 1),
            new AnswerData("David Bowie", false, 2),
            new AnswerData("Michael Jackson", true, 3),
            new AnswerData("Elvis Presley", false, 4)
        ),
        new QuestionData(4, "What is the real name of Taylor Swift?", "Easy", 50, 30, "Celebrities",
            new AnswerData("Taylor Nation Swift", false, 1),
            new AnswerData("Taylor Alison Swift", true, 2),
            new AnswerData("Taylor Amanda Swift", false, 3),
            new AnswerData("Taylor Alice Swift", false, 4)
        ),
        new QuestionData(4, "In which movie did Emma Watson play Hermione Granger?", "Easy", 50, 30, "Movies",
            new AnswerData("Harry Potter series", true, 1),
            new AnswerData("The Perks of Being a Wallflower", false, 2),
            new AnswerData("The Bling Ring", false, 3),
            new AnswerData("Beauty and the Beast", false, 4)
        ),
        new QuestionData(4, "How many seasons did 'Friends' have?", "Easy", 50, 30, "TV Shows",
            new AnswerData("8", false, 1),
            new AnswerData("9", false, 2),
            new AnswerData("10", true, 3),
            new AnswerData("11", false, 4)
        ),
        new QuestionData(4, "Who won the Grammy for Album of the Year in 2022?", "Easy", 50, 30, "Music",
            new AnswerData("Adele", false, 1),
            new AnswerData("Harry Styles", false, 2),
            new AnswerData("Olivia Rodrigo", false, 3),
            new AnswerData("Jon Batiste", true, 4)
        ),
        new QuestionData(4, "What is the highest-grossing movie of all time?", "Easy", 50, 30, "Movies",
            new AnswerData("Avatar", true, 1),
            new AnswerData("Avengers: Endgame", false, 2),
            new AnswerData("Titanic", false, 3),
            new AnswerData("Star Wars: The Force Awakens", false, 4)
        ),
        new QuestionData(4, "Which artist created the mural in the album cover of 'Abbey Road'?", "Easy", 50, 30, "Music",
            new AnswerData("John Pasche", false, 1),
            new AnswerData("Iain Macmillan", true, 2),
            new AnswerData("Peter Max", false, 3),
            new AnswerData("Andy Warhol", false, 4)
        ),
        new QuestionData(4, "Who is the host of 'The Late Show'?", "Easy", 50, 30, "TV Shows",
            new AnswerData("Jimmy Fallon", false, 1),
            new AnswerData("Stephen Colbert", true, 2),
            new AnswerData("James Corden", false, 3),
            new AnswerData("Seth Meyers", false, 4)
        )
    );

    // ============================================================================
    // ROOM TYPE 5: BRAIN TEASERS (Hard difficulty)
    // ============================================================================

    public static final List<QuestionData> BRAIN_TEASERS_QUESTIONS = Arrays.asList(
        new QuestionData(5, "I speak without a mouth and hear without ears. I have no body, but come alive with wind. What am I?", "Hard", 150, 20, "Riddles",
            new AnswerData("Wind", false, 1),
            new AnswerData("Echo", true, 2),
            new AnswerData("Sound", false, 3),
            new AnswerData("Air", false, 4)
        ),
        new QuestionData(5, "What has keys but cannot open locks?", "Hard", 150, 20, "Riddles",
            new AnswerData("A keyboard", true, 1),
            new AnswerData("A piano", false, 2),
            new AnswerData("A safe", false, 3),
            new AnswerData("A door", false, 4)
        ),
        new QuestionData(5, "The more you take, the more you leave behind. What am I?", "Hard", 150, 20, "Riddles",
            new AnswerData("Time", false, 1),
            new AnswerData("Footsteps", true, 2),
            new AnswerData("Money", false, 3),
            new AnswerData("Memories", false, 4)
        ),
        new QuestionData(5, "What can travel around the world while staying in a corner?", "Hard", 150, 20, "Riddles",
            new AnswerData("A boat", false, 1),
            new AnswerData("A stamp", true, 2),
            new AnswerData("An airplane", false, 3),
            new AnswerData("A person", false, 4)
        ),
        new QuestionData(5, "What gets wet while drying?", "Hard", 150, 20, "Riddles",
            new AnswerData("A sponge", false, 1),
            new AnswerData("A towel", true, 2),
            new AnswerData("A cloth", false, 3),
            new AnswerData("Paper", false, 4)
        ),
        new QuestionData(5, "What is always coming but never arrives?", "Hard", 150, 20, "Logic",
            new AnswerData("Tomorrow", true, 1),
            new AnswerData("Next week", false, 2),
            new AnswerData("Next year", false, 3),
            new AnswerData("The future", false, 4)
        ),
        new QuestionData(5, "If you have a bowl with six apples and you take away three, how many do you have?", "Hard", 150, 20, "Logic",
            new AnswerData("3", false, 1),
            new AnswerData("You have 3 apples", true, 2),
            new AnswerData("6", false, 3),
            new AnswerData("9", false, 4)
        ),
        new QuestionData(5, "What word becomes shorter when you add two letters to it?", "Hard", 150, 20, "Wordplay",
            new AnswerData("Long", true, 1),
            new AnswerData("Short", false, 2),
            new AnswerData("Word", false, 3),
            new AnswerData("Add", false, 4)
        ),
        new QuestionData(5, "A man pushes his car to a hotel and tells the owner he's bankrupt. What happened?", "Hard", 150, 20, "Logic",
            new AnswerData("He had an accident", false, 1),
            new AnswerData("He was playing Monopoly", true, 2),
            new AnswerData("He lost all his money", false, 3),
            new AnswerData("His car broke down", false, 4)
        ),
        new QuestionData(5, "What has a head and a tail but no body?", "Hard", 150, 20, "Riddles",
            new AnswerData("A coin", true, 1),
            new AnswerData("A snake", false, 2),
            new AnswerData("A fish", false, 3),
            new AnswerData("A kite", false, 4)
        )
    );

    // ============================================================================
    // ROOM TYPE 6: HARDCORE MODE (Very Hard difficulty)
    // ============================================================================

    public static final List<QuestionData> HARDCORE_MODE_QUESTIONS = Arrays.asList(
        new QuestionData(6, "What is the only mammal that cannot jump?", "Hard", 200, 15, "Biology",
            new AnswerData("Sloth", false, 1),
            new AnswerData("Elephant", true, 2),
            new AnswerData("Hippopotamus", false, 3),
            new AnswerData("Rhinoceros", false, 4)
        ),
        new QuestionData(6, "Which planet is the hottest in our solar system?", "Hard", 200, 15, "Astronomy",
            new AnswerData("Mercury", false, 1),
            new AnswerData("Venus", true, 2),
            new AnswerData("Mars", false, 3),
            new AnswerData("Saturn", false, 4)
        ),
        new QuestionData(6, "What is the only country that has a rectangular flag?", "Hard", 200, 15, "Geography",
            new AnswerData("India", false, 1),
            new AnswerData("Switzerland", true, 2),
            new AnswerData("Nepal", false, 3),
            new AnswerData("Denmark", false, 4)
        ),
        new QuestionData(6, "In what year was the first iPhone released?", "Hard", 200, 15, "Technology",
            new AnswerData("2005", false, 1),
            new AnswerData("2006", false, 2),
            new AnswerData("2007", true, 3),
            new AnswerData("2008", false, 4)
        ),
        new QuestionData(6, "What is the capital of Bhutan?", "Hard", 200, 15, "Geography",
            new AnswerData("Kathmandu", false, 1),
            new AnswerData("Thimphu", true, 2),
            new AnswerData("Dhaka", false, 3),
            new AnswerData("Colombo", false, 4)
        ),
        new QuestionData(6, "Which artist cut off part of his own ear?", "Hard", 200, 15, "Art",
            new AnswerData("Pablo Picasso", false, 1),
            new AnswerData("Vincent van Gogh", true, 2),
            new AnswerData("Salvador Dali", false, 3),
            new AnswerData("Frida Kahlo", false, 4)
        ),
        new QuestionData(6, "What is the smallest bone in the human body?", "Hard", 200, 15, "Biology",
            new AnswerData("Radius", false, 1),
            new AnswerData("Stapes", true, 2),
            new AnswerData("Fibula", false, 3),
            new AnswerData("Tibia", false, 4)
        ),
        new QuestionData(6, "In what year did the Chernobyl disaster occur?", "Hard", 200, 15, "History",
            new AnswerData("1984", false, 1),
            new AnswerData("1985", false, 2),
            new AnswerData("1986", true, 3),
            new AnswerData("1987", false, 4)
        ),
        new QuestionData(6, "What is the only metal that is liquid at room temperature?", "Hard", 200, 15, "Chemistry",
            new AnswerData("Gallium", false, 1),
            new AnswerData("Mercury", true, 2),
            new AnswerData("Aluminum", false, 3),
            new AnswerData("Cesium", false, 4)
        ),
        new QuestionData(6, "How many strings does a viola have?", "Hard", 200, 15, "Music",
            new AnswerData("3", false, 1),
            new AnswerData("4", true, 2),
            new AnswerData("5", false, 3),
            new AnswerData("6", false, 4)
        )
    );

    // ============================================================================
    // ROOM TYPE 7: GAMING & ESPORTS (Medium difficulty)
    // ============================================================================

    public static final List<QuestionData> GAMING_ESPORTS_QUESTIONS = Arrays.asList(
        new QuestionData(7, "In which year was the original Super Mario Bros. released?", "Easy", 100, 25, "Gaming",
            new AnswerData("1983", false, 1),
            new AnswerData("1985", true, 2),
            new AnswerData("1987", false, 3),
            new AnswerData("1989", false, 4)
        ),
        new QuestionData(7, "Which company developed The Legend of Zelda?", "Easy", 100, 25, "Gaming",
            new AnswerData("Sony", false, 1),
            new AnswerData("Microsoft", false, 2),
            new AnswerData("Nintendo", true, 3),
            new AnswerData("Sega", false, 4)
        ),
        new QuestionData(7, "What is the name of the protagonist in Final Fantasy VII?", "Medium", 100, 25, "Gaming",
            new AnswerData("Cloud Strife", true, 1),
            new AnswerData("Aerith Gainsborough", false, 2),
            new AnswerData("Barret Wallace", false, 3),
            new AnswerData("Tifa Lockhart", false, 4)
        ),
        new QuestionData(7, "In Counter-Strike, what is the most expensive weapon to purchase?", "Medium", 100, 25, "Esports",
            new AnswerData("AWP Dragon Lore", false, 1),
            new AnswerData("Negev", true, 2),
            new AnswerData("AK-47", false, 3),
            new AnswerData("M4A1-S", false, 4)
        ),
        new QuestionData(7, "Which game won the Game Awards 2021 'Game of the Year'?", "Medium", 100, 25, "Gaming",
            new AnswerData("Resident Evil Village", false, 1),
            new AnswerData("It Takes Two", true, 2),
            new AnswerData("Returnal", false, 3),
            new AnswerData("Deathloop", false, 4)
        ),
        new QuestionData(7, "In League of Legends, how many champions are in the game? (as of 2023)", "Medium", 100, 25, "Esports",
            new AnswerData("150+", true, 1),
            new AnswerData("100+", false, 2),
            new AnswerData("50+", false, 3),
            new AnswerData("200+", false, 4)
        ),
        new QuestionData(7, "What is the best-selling video game of all time?", "Easy", 100, 25, "Gaming",
            new AnswerData("Grand Theft Auto V", false, 1),
            new AnswerData("Minecraft", true, 2),
            new AnswerData("Tetris", false, 3),
            new AnswerData("Wii Sports", false, 4)
        ),
        new QuestionData(7, "In Dota 2, how many heroes are available to play?", "Medium", 100, 25, "Esports",
            new AnswerData("100+", false, 1),
            new AnswerData("150+", false, 2),
            new AnswerData("120+", true, 3),
            new AnswerData("130+", false, 4)
        ),
        new QuestionData(7, "Which console is the PlayStation 5?", "Easy", 100, 25, "Gaming",
            new AnswerData("9th generation", true, 1),
            new AnswerData("8th generation", false, 2),
            new AnswerData("10th generation", false, 3),
            new AnswerData("7th generation", false, 4)
        ),
        new QuestionData(7, "What year was Fortnite Battle Royale released?", "Easy", 100, 25, "Gaming",
            new AnswerData("2017", false, 1),
            new AnswerData("2018", true, 2),
            new AnswerData("2016", false, 3),
            new AnswerData("2019", false, 4)
        )
    );

    // ============================================================================
    // ROOM TYPE 8: SPORTS (Medium difficulty)
    // ============================================================================

    public static final List<QuestionData> SPORTS_QUESTIONS = Arrays.asList(
        new QuestionData(8, "How many players are on a basketball team on court?", "Easy", 100, 25, "Basketball",
            new AnswerData("6", false, 1),
            new AnswerData("5", true, 2),
            new AnswerData("7", false, 3),
            new AnswerData("4", false, 4)
        ),
        new QuestionData(8, "In football (soccer), what is the maximum length of a field in yards?", "Medium", 100, 25, "Football",
            new AnswerData("110", false, 1),
            new AnswerData("120", true, 2),
            new AnswerData("130", false, 3),
            new AnswerData("100", false, 4)
        ),
        new QuestionData(8, "How many players are on a cricket team on the field?", "Medium", 100, 25, "Cricket",
            new AnswerData("10", false, 1),
            new AnswerData("11", true, 2),
            new AnswerData("12", false, 3),
            new AnswerData("9", false, 4)
        ),
        new QuestionData(8, "Which sport is known as 'The Beautiful Game'?", "Easy", 100, 25, "Football",
            new AnswerData("Basketball", false, 1),
            new AnswerData("Tennis", false, 2),
            new AnswerData("Football (Soccer)", true, 3),
            new AnswerData("Baseball", false, 4)
        ),
        new QuestionData(8, "How many Olympic rings are there in the Olympic symbol?", "Easy", 100, 25, "Olympics",
            new AnswerData("5", true, 1),
            new AnswerData("6", false, 2),
            new AnswerData("7", false, 3),
            new AnswerData("4", false, 4)
        ),
        new QuestionData(8, "In tennis, what is the maximum number of sets in a men's Grand Slam match?", "Medium", 100, 25, "Tennis",
            new AnswerData("3", false, 1),
            new AnswerData("4", false, 2),
            new AnswerData("5", true, 3),
            new AnswerData("6", false, 4)
        ),
        new QuestionData(8, "How many innings does a baseball game have?", "Easy", 100, 25, "Baseball",
            new AnswerData("7", false, 1),
            new AnswerData("8", false, 2),
            new AnswerData("9", true, 3),
            new AnswerData("10", false, 4)
        ),
        new QuestionData(8, "Which country has won the most FIFA World Cups?", "Medium", 100, 25, "Football",
            new AnswerData("Germany", false, 1),
            new AnswerData("Italy", false, 2),
            new AnswerData("Brazil", true, 3),
            new AnswerData("France", false, 4)
        ),
        new QuestionData(8, "In American football, how many yards is a first down?", "Easy", 100, 25, "American Football",
            new AnswerData("5", false, 1),
            new AnswerData("10", true, 2),
            new AnswerData("15", false, 3),
            new AnswerData("20", false, 4)
        ),
        new QuestionData(8, "How many players are on a volleyball team on court?", "Easy", 100, 25, "Volleyball",
            new AnswerData("5", false, 1),
            new AnswerData("6", true, 2),
            new AnswerData("7", false, 3),
            new AnswerData("8", false, 4)
        )
    );

    // ============================================================================
    // GET ALL QUESTIONS BY ROOM TYPE
    // ============================================================================

    public static List<QuestionData> getQuestionsByRoomType(int roomTypeId) {
        return switch (roomTypeId) {
            case 1 -> MIXED_BAG_QUESTIONS;
            case 2 -> SCIENCE_TECH_QUESTIONS;
            case 3 -> HISTORY_CULTURE_QUESTIONS;
            case 4 -> POP_CULTURE_QUESTIONS;
            case 5 -> BRAIN_TEASERS_QUESTIONS;
            case 6 -> HARDCORE_MODE_QUESTIONS;
            case 7 -> GAMING_ESPORTS_QUESTIONS;
            case 8 -> SPORTS_QUESTIONS;
            default -> Arrays.asList();
        };
    }

    /**
     * Get all questions from all room types
     */
    public static List<QuestionData> getAllQuestions() {
        List<QuestionData> allQuestions = new java.util.ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            allQuestions.addAll(getQuestionsByRoomType(i));
        }
        return allQuestions;
    }
}