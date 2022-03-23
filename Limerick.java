import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Semaphore;

/**
 * By "extending" the Poet class, our Limerick class inherits all the fields and
 * methods of the Poet class. (But not the private ones.)
 * 
 * Limerick is called a subclass of the Poet class.
 * 
 * Words of the day: inherit, extend, subclass, superclass
 * 
 * Subclasses: + Add functionality in the form of additional fields, additional
 * methods + Replace existing methods by "overriding" the method Do that by
 * redefining it in the subclass
 * 
 * This is the heart of Object-Oriented Programming
 */

class Limerick {
  /*
   * There once was a man from Nantucket 010 010 01(0) A Who kept all his cash in
   * a bucket 010 010 01(0) A (thanks, Max!) His daughter named Nan 01001 B Ran
   * away with a man 101001 B And as for the bucket, Nantucket 010 010 01(0) A
   */
  static Poet p;
  static DSHashMap<Integer> memo;
  static int count = 0;
  static boolean limerickFound = false;

  public static void main(String[] args) {
    writeGreatLimerickSerial();
    //writeGreatLimerickParallel();
  }

  /**
   * Serial Limerick Finder
   */
  static void writeGreatLimerickSerial() {
    p = new Poet();
    memo = new DSHashMap<Integer>();

    int score = 0;
    double ma = 0.0;
    int tc = 0;
    String line1, line2, line3, line4, line5;

    do {
      line1 = p.buildLineWithMeter("01001001", 2);
      line2 = p.writeLineWithMeterAndRhymeswith("01001001", line1);
      line3 = p.buildLineWithMeter("01001", 2);
      line4 = p.writeLineWithMeterAndRhymeswith("101001", line3);
      line5 = p.writeLineWithMeterAndRhymeswith("01001001", line1);

      score = rhymeAndToneScore(line1, line2, line3, line4, line5);
      ma = ma(new String[] { line1, line2, line3, line4, line5 });
      tc = tc(new String[] { line1, line2, line3, line4, line5 });

      System.out.println(count + ": Score = " + score + ", ma = " + ma + ", tc = " + tc);
      count++;
    } while (score < 100 || ma < 0 || tc < 55);

    System.out.println(line1);
    System.out.println(line2);
    System.out.println(line3);
    System.out.println(line4);
    System.out.println(line5);

  }

  /**
   * Parallel Limerick Finder
   */
  static void writeGreatLimerickParallel() {
    p = new Poet();
    memo = new DSHashMap<Integer>();
    String[] limerick = new String[5];
    Semaphore success = new Semaphore(0);
    count = 0;

    int numThreads = 8;

    for (int i = 0; i < numThreads; i++) {
      writeGreatLimerickThread t = new writeGreatLimerickThread(limerick, success);
      t.start();
    }

    try {
      success.acquire();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    
    limerickFound = true;
    for(int i = 0; i < 5; i++)
      System.out.println(limerick[i]);
  }

  /**
   * The thread for the limerick writing
   */
  static class writeGreatLimerickThread extends Thread {
    Poet p;
    String[] rv;
    Semaphore sem;

    public writeGreatLimerickThread(String[] rv, Semaphore sem){
      this.p = new Poet();
      this.rv = rv;
      this.sem = sem;
    }

    public void run(){
      int score = 0;
      double ma = 0.0;
      int tc = 0;
      String line1, line2, line3, line4, line5;
      
      do {
        line1 = p.buildLineWithMeter("01001001", 2);
        line2 = p.writeLineWithMeterAndRhymeswith("01001001", line1);
        line3 = p.buildLineWithMeter("01001", 2);
        line4 = p.writeLineWithMeterAndRhymeswith("101001", line3);
        line5 = p.writeLineWithMeterAndRhymeswith("01001001", line1);

        score = rhymeAndToneScore(line1, line2, line3, line4, line5);
        ma = ma(new String[] {line1, line2, line3, line4, line5});
        tc = tc(new String[] {line1, line2, line3, line4, line5});

        System.out.println(count + ": Score = " + score + ", ma = " + ma + ", tc = " + tc);
        count++;
        if(Limerick.limerickFound) break;
      } while (score < 102 || ma <=0 || tc < 64);

      rv[0] = line1;
      rv[1] = line2;
      rv[2] = line3;
      rv[3] = line4;
      rv[4] = line5;
      sem.release();
    }
  }

  /**
   * Vince and Jacob Use part of speech information frequency Returns an int grade
   * for the input poem. This is based on the number of pos out of num present in
   * the poem, where pos is the part of speech symbol desired (refer to cmupron's
   * guide)
   *
   * @param poem the poem we want to grade (THIS MUST BE CONVERTED INTO POS
   *             ALREADY)
   * @param pos  the exact part of speech we want to grade based on number of.
   * @param num  the number of the pos we want to have.
   * @return an int out of 100 that is the % of pos present out of num
   */
  public static int nounNumberGrader(String[] poem, String pos, int num) {
    String sPoem = Arrays.toString(poem);
    int n = sPoem.length() - sPoem.replace(pos, "").length(); // the amount of pos is equal to the diff when removing
                                                              // all pos

    double grade = (double) n / num * 100;

    return Math.min(100, (int) grade);
  }

  /**
   * Creates a String array of the parts of speech of each word in the original
   * poem.
   *
   * @param poem        the poem itself that we need to read.
   * @param lineBreaker the character(s) being used in the poem to break up lines.
   * @return an array of all the partsOfSpeech being used in the poem
   */
  public static String[] poemToPOS(String[] poem, String lineBreaker) {
    ;

    for (int i = 0; i < poem.length; i++) {
      String[] ithLineByWord = poem[i].split(" "); // divide the line by words
      String ithLine = "";

      for (int j = 0; j < ithLineByWord.length; j++) {
        String jthWordPOS = getWordPOS(ithLineByWord[j]);
        ithLine += jthWordPOS + " ";
      }
      poem[i] = ithLine;
    }

    return poem;
  }

  /**
   * Creates a String array of the parts of speech of each word in the original
   * poem.
   *
   * @param poem        the poem itself that we need to read.
   * @param lineBreaker the character(s) being used in the poem to break up lines.
   * @return an array of all the partsOfSpeech being used in the poem
   */
  public static String[] poemToPOSString(String poem, String lineBreaker) {
    String[] poemByLine = poem.split(lineBreaker); // we split by ". " because our format for poems is each line ends
                                                   // with " ."

    for (int i = 0; i < poemByLine.length; i++) {
      String[] ithLineByWord = poemByLine[i].split(" "); // divide the line by words
      String ithLine = "";

      for (int j = 0; j < ithLineByWord.length; j++) {
        String jthWordPOS = getWordPOS(ithLineByWord[j]);
        ithLine += jthWordPOS + " ";
      }

      poemByLine[i] = ithLine;
    }

    return poemByLine;
  }

  /**
   * Turns a string into its part of speech, if one exists. Otherwise, returns "*
   * ".
   *
   * @param w the word we want the part of speech of
   * @return a String that is the part(s) of speech corresponding to w's Word
   *         object
   */
  public static String getWordPOS(String w) {
    if (p.wordMap.get(w) == null) {
      return "* "; // if we don't have an entry for this word, just assign a wildcard and return.
    }

    Word word = p.wordMap.get(w);
    String pos = "";

    if (word.partOfSpeech == null) {
      return "* "; // if we don't have a mpos entry for this word, assign a wildcard and return.
    } else {
      return word.partOfSpeech; // otherwise, just return the partOfSpeech
    }
  }

  /**
   * Therese R. and Allegra
   * 
   * Tone Words: +1 for every word that is a tone word (because that means it has
   * fEeLiNgS) Last Words: +1 for every line that rhymes and doesn't have the same
   * last word
   * 
   * @return score depending on above criteria
   */
  public static int rhymeAndToneScore(String line1, String line2, String line3, String line4, String line5) {
    DSArrayList<String> toneArray = new DSArrayList<String>();

    // Read FeelingWords.txt
    try {
      FileReader f = new FileReader("Texts/FeelingWords.txt");
      BufferedReader reader = new BufferedReader(f);
      String x = reader.readLine();

      while (x != null) {
        toneArray.add(x);
        x = reader.readLine();
      }
      reader.close();
    } catch (IOException e) {
      System.out.format("IOException %s\n", e);
    }

    DSArrayList<String> happy = new DSArrayList<String>();
    DSArrayList<String> sad = new DSArrayList<String>();
    for (int i = 0; i <= 121; i++)
      happy.add(toneArray.get(i));
    for (int i = 122; i < toneArray.size(); i++)
      sad.add(toneArray.get(i));
    int happyPts = 0;
    int sadPts = 0;

    DSArrayList<String> aline1 = new DSArrayList<String>();
    DSArrayList<String> aline2 = new DSArrayList<String>();
    DSArrayList<String> aline3 = new DSArrayList<String>();
    DSArrayList<String> aline4 = new DSArrayList<String>();
    DSArrayList<String> aline5 = new DSArrayList<String>();
    int score = 0;

    // LINE 1
    String[] line1arr = line1.split(" ");
    for (String w : line1arr) {
      aline1.add(w);
      if (toneArray.contains(w)) {
        //System.out.println("Tone Word Found: " + w);
        if (happy.contains(w))
          happyPts++;
        else if (sad.contains(w))
          sadPts++;
      }
    }
    String line1last = (line1arr[line1arr.length - 1]);

    // LINE 2
    String[] line2arr = line2.split(" ");
    for (String w : line2arr) {
      aline2.add(w);
      if (toneArray.contains(w)) {
        //System.out.println("Tone Word Found: " + w);
        if (happy.contains(w))
          happyPts++;
        else if (sad.contains(w))
          sadPts++;
      }
    }
    String line2last = (line2arr[line2arr.length - 1]);

    // LINE 3
    String[] line3arr = line3.split(" ");
    for (String w : line3arr) {
      aline3.add(w);
      if (toneArray.contains(w)) {
        //System.out.println("Tone Word Found: " + w);
        if (happy.contains(w))
          happyPts++;
        else if (sad.contains(w))
          sadPts++;
      }
    }
    String line3last = (line3arr[line3arr.length - 1]);

    // LINE 4
    String[] line4arr = line4.split(" ");
    for (String w : line4arr) {
      aline4.add(w);
      if (toneArray.contains(w)) {
        //System.out.println("Tone Word Found: " + w);
        if (happy.contains(w))
          happyPts++;
        else if (sad.contains(w))
          sadPts++;
      }
    }
    String line4last = (line4arr[line4arr.length - 1]);

    // LINE 5
    String[] line5arr = line5.split(" ");
    for (String w : line5arr) {
      aline5.add(w);
      if (toneArray.contains(w)) {
        //System.out.println("Tone Word Found: " + w);
        if (happy.contains(w))
          happyPts++;
        else if (sad.contains(w))
          sadPts++;
      }
    }
    String line5last = (line5arr[line5arr.length - 1]);

    // Check if any lines supposed to rhyme have the same last word (AABBA)
    if (!line1last.equals(line2last))
      score += 25;
    //else
    //  System.out.println("+0 points: 1 = 2");
    if (!line1last.equals(line5last))
      score += 25;
    //else
    //  System.out.println("+0 Points: 1 = 5");
    if (!line2last.equals(line5last))
      score += 25;
    //else
    //  System.out.println("+0 Points: 2 = 5");
    if (!line3last.equals(line4last))
      score += 25;
    //else
    //  System.out.println("+0 Points: 3 = 4");

    // Check how many tone words there are and score based on tone found
    //System.out.println("Poem has " + happyPts + " happy tone words and " + sadPts + " sad tone words.");
    // Score based on tone words; add for happy, subtract for sad
    if (happyPts == 0 && sadPts == 0){}
      //System.out.println("Poem contains 0 tone words. Bonus points: 0.");
    else if (happyPts > sadPts) {
      //System.out.println("Poem is more happy than sad :) Plus: " + happyPts);
      score += happyPts;
    } else if (happyPts < sadPts) {
      //System.out.println("Poem is more sad than happy :( Minus: " + sadPts);
      score -= sadPts;
    } else if (happyPts == sadPts && happyPts > 0){}
      //System.out.println("Poem is equally happy and sad. No points added or deducted.");

    return score;
  }

  /**
   * Michael and Andy Use the follow map to see if the words are linked.
   * 
   * This function grades the limerick based on the association between each pair
   * of words. If a one word is in the set of following words of it's previous
   * word, it will add to the score, averaging each lines score to find the total
   * grade of the limerick.
   */
  public static double ma(String[] limerick) {
    DSArrayList<Integer> scores = new DSArrayList<Integer>();
    for (String line:limerick) {
        String[] wordsArray = line.split(" ");
        int add = 100/(wordsArray.length - 1);
        int score = 0;
        for (int i = 0; i < wordsArray.length - 1; i++) {
            if (p.getFollowMap().get(wordsArray[i]).contains(wordsArray[i+1])) score += add;
        }
        scores.add(score);
    }
    double average = 0;
    for (int i = 0; i < scores.size(); i++) {
        average += scores.get(i)/scores.size();
    }
    return average;
  }

  
  /**
    * Lauren and Andi
    * Investigate word lengths to determine their values.
    */
    public static int la(String[] limerick){

      int totalScore = 0;
      int wordScore = 0;

      //run through all strings in limerick
      for (int i = 0; i < limerick.length; ++i) {

          wordScore = limerick[i].length();

          //add to score depending on specific variables
          if (wordScore > 5)
              ++wordScore;
          else if (wordScore > 10)
              wordScore = wordScore + 3;
          else if (wordScore > 17)
              wordScore = wordScore + 5;
          else if (wordScore == 17)
              wordScore = wordScore + 17;
          
          if (wordScore%2 != 0)
              wordScore = (int)((wordScore + 1) * 1.5);

          //add wordScore to totalScore
          totalScore = totalScore + wordScore;
      }

      return totalScore;
  }

  /**
   * Therese A. and Clarissa Skipworth
   * @param limerick
   * @return
   */
  static int tc(String[] lim) {
    // Put all words of the limerick into a DSArrayList of words
    DSArrayList<String> limerick = new DSArrayList<>();
    for (String line : lim) {
      String[] wordsArray = line.split(" ");
      for (int i = 0; i < wordsArray.length - 1; i++) {
        limerick.add(wordsArray[i]);
      }
    }
    // Run BFS to find the distance between the words
    // Loop over Limerick and grab consecutive word pairs, summing up their
    // distances as you go
    // A lower distance is better; would need an exact scoring system though
    // Maybe something to do with the total size of the words you pull from
    int totalDistance = 0;
    for (int i = 0; i < limerick.size() - 1; i++) {
      totalDistance += distanceCalc(limerick.get(i), limerick.get(i+1));
    }

    // Not necessarily bounded between 0 and 100
    return 100 - totalDistance;
  }

  // Example: "The dog flies." THE and DOG have a distance of 1, THE and FLIES
  // have a distance of 2.
  static int distanceCalc(String start, String end) {
    String key = start + "." + end;
    if(memo.containsKey(key)) return memo.get(key);

    DSArrayList<String> visited = new DSArrayList<String>();
    DSHashMap<Integer> distance = new DSHashMap<Integer>();

    DSArrayList<String> neighbors = new DSArrayList<String>();
    DSArrayList<String> queue = new DSArrayList<String>();
    String current;

    // Begin BFS with the start word
    visited.add(start);
    distance.put(start, 0);
    queue.pushQ(start);
    // Continue BFS until queue is empty
    do {
      // Pop off front of the queue
      current = queue.popQ();

      // Loop over neighbors
      neighbors = p.getFollowMap().get(current).toDSArrayList();
      for (int i = 0; i < neighbors.size(); i++) {
        String neighbor = neighbors.get(i);
        // Catalogue unvisited neighbors: add them to visited, memoize the parent
        if (!visited.contains(neighbor)) {
          queue.add(neighbor);
          visited.add(neighbor);
          // Calculate distance of neighbors
          distance.put(neighbor, distance.get(current) + 1);
          memo.put(start + "." + neighbor, distance.get(neighbor));
        }
      }
    } while (queue.sizeQ() > 0 && current != end);

    memo.put(key, distance.get(end));

    return distance.get(end);
  }

}
