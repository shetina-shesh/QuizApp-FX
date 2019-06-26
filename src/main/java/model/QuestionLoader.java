
package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuestionLoader {

    List<Question> questions = new ArrayList<Question>();

    public List<Question> getQuestion(File file) {
        parseTestFile(file);
        shuffle(questions);
        return questions;
    }

    private void parseTestFile(File file) {
        try {
            int index = 0;
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String[] answers = new String[4];
            String soulution;
            Question question = new Question();
            while (reader.ready()) {
                String line = reader.readLine();
                if (index == 0)
                    question.setQuestion(line);
                else {
                    if (line.isEmpty())
                        continue;
                    boolean isSolution = false;
                    String substring = line.substring(0, line.length() - 2);
                    if (line.substring(line.length() - 2).equals("/t")) {
                        question.setSolution(substring);
                        isSolution = true;
                    }
                    if (index % 4 == 0) {
                        if (isSolution) {
                            answers[index - 1] = substring;
                        } else {
                            answers[index - 1] = line;
                        }
                        question.setAnswers(shuffleArrayOfAnswers(answers));
                        questions.add(question);
                        question = new Question();
                        index = 0;
                        continue;
                    }
                    if (isSolution) {
                        answers[index - 1] = substring;
                    } else {
                        answers[index - 1] = line;
                    }
                }
                index++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void shuffle(List list) {
        int totalElements = list.size();
        Random random = new Random();
        for (int loopCounter = 0; loopCounter < totalElements; loopCounter++) {
            Question currentElement = (Question) list.get(loopCounter);
            int randomIndex = loopCounter + random.nextInt(totalElements - loopCounter);
            list.set(loopCounter, list.get(randomIndex));
            list.set(randomIndex, currentElement);
        }
    }

    public String[] shuffleArrayOfAnswers(String[] input) {
        int[] index = getRandomNumber();
        String[] result = new String[4];

        for (int i = 0; i < 4; i++) {
            result[i] = input[index[i]];
        }

        return result;
    }

    public int[] getRandomNumber() {
        SecureRandom r = new SecureRandom();
        int[] v = new int[4];
        int n;
        int i = 1;
        boolean redundancy;
        v[0] = r.nextInt(4);
        while (i < 4) {
            redundancy = false;

            n = r.nextInt(4);
            for (int j = 0; j < i; j++) {
                if (v[j] == n) {
                    redundancy = true;
                    break;
                }
            }
            if (!redundancy) {
                v[i] = n;
                i++;
            }
        }

        return v;
    }

}
