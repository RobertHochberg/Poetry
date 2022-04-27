import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Scanner;

class WordPlay {

  /*
   * There once was a man from Nantucket 010 010 01(0) A Who kept all his cash in
   * a bucket 010 010 01(0) A (thanks, Max!) His daughter named Nan 01001 B Ran
   * away with a man 101001 B And as for the bucket, Nantucket 010 010 01(0) A
   */
  static Poet p;

  public static void main(String[] args) {
    p = new Poet("Texts/flist.txt");
    //p = new Poet("Texts/JaneAusten.txt");
    //stress("[0, 1", "c");
    //pregrams();
    // writeGreatLimerickParallel();
    //rhyme("trails", "1]");
    //filterWords();
    //soundsLike("IH hard");
    HashSet<String> avoid = new HashSet<>();
    //avoid.add("fried");
    avoid.add("crack");
    avoid.add("peeling");
    avoid.add("burrs");
    avoid.add("burr");
    Scanner s = new Scanner(System.in);
    while(true){
      String start = s.next().trim();
      String end = s.next().trim();
      avoid.add(s.next().trim());
      wordLadder(start, end, 40, avoid);
    }
  }

  // Build a ladder from start to end, changing one letter at a time
  static ArrayList<Word> wordLadder(String start, String end, int scowl, HashSet<String> avoid){
    int min = start.length();
    int max = start.length();
    if(end.length() < min) min = end.length();
    if(end.length() > max) max = end.length();
    min--; max++; 
    HashMap<Word, ArrayList<Word>> g = buildWordGraph(min, max, scowl, avoid);

    if(!p.wordMap.containsKey(start)){
      System.out.println("Unstartable.");
      return null;
    }

    Word s = p.wordMap.get(start);
    LinkedList<Word> q = new LinkedList<>();
    HashMap<Word, Integer> distance = new HashMap<>();
    HashMap<Word, ArrayList<Word>> parent = new HashMap<>();

    q.add(s); // Put the start word on the queue
    parent.put(s, null);
    distance.put(s, 0);

    foundpath:
    while(q.size() > 0){
      Word w = q.pop();
      System.out.print(w.word + ", ");
      if(!g.containsKey(w)) continue;
      int d = distance.get(w);
      for(Word nbr : g.get(w)){
        if(parent.containsKey(nbr) && distance.get(nbr) <= d) continue;
        if(!parent.containsKey(nbr)){
          parent.put(nbr, new ArrayList<>());
          q.add(nbr);
          distance.put(nbr, d+1);
        }
        parent.get(nbr).add(w);
        if(nbr.word.equals(end)) break foundpath;
      }
    }

    // Now print the path
    if(!parent.containsKey(p.wordMap.get(end))){
      System.out.println("Unreachable.");
      return null;
    }
    System.out.println("");
    printAllPaths(p.wordMap.get(end), parent, end + " ");
    return null;
  }

  // Print all the shortest paths
  static void printAllPaths(Word end, HashMap<Word, ArrayList<Word>> parents, String root){
    // base case
    if(parents.get(end) == null){
      System.out.println(root);
      return;
    }

    // Try all neighbors
    for(Word p : parents.get(end)){
      String newRoot = p.word + " " + root;
      printAllPaths(p, parents, newRoot);
    }
  }

  // Build a graph (map of lists) from the poet
  static HashMap<Word, ArrayList<Word>> buildWordGraph(int min, int max, int scowl,
  HashSet<String> avoid){
    HashMap<Word, ArrayList<Word>> g = new HashMap<Word, ArrayList<Word>>();

    for(Word w : p.Words){
      w = p.wordMap.get(w.word);
      if(g.containsKey(w)) continue;
      if(avoid.contains(w.word)) continue;
      int wl = w.word.length();
      if(wl < min || wl > max) continue;
      if(w.getScowlValue() > scowl) continue;

      if(!g.containsKey(w)) g.put(w, new ArrayList<Word>());

      // Connect to words of the same length
      String word = w.word;
      for(int i = 0; i < wl; i++){
        for(char c = 'a'; c <= 'z'; c++){
          String neww = word.substring(0, i) + c + word.substring(i+1);
          if(neww.equals(word)) continue;
          if(!p.wordMap.containsKey(neww)) continue;
          Word newW = p.wordMap.get(neww);
          if(newW.getScowlValue() > scowl || newW.getScowlValue() < 1) continue;

          g.get(w).add(newW);
        }
      }

      // Connect to words that are one shorter, if possible
      if(wl > min){
        for(int i = 0; i < wl; i++){
          String neww = word.substring(0, i) + word.substring(i+1);
          if(!p.wordMap.containsKey(neww)) continue;
          if(avoid.contains(neww)) continue;
          Word newW = p.wordMap.get(neww);
          if(newW.getScowlValue() > scowl || newW.getScowlValue() < 1) continue;
          g.get(w).add(newW);
          if(!g.containsKey(newW)) g.put(newW, new ArrayList<Word>());
          g.get(newW).add(w);
        }
      }
    }
    return g;
  }

  // Generic function to loop over and select words
  static void filterWords() {
    DSArrayList<Word> words = p.Words;
    DSArrayList<String> theList = new DSArrayList<>();
    ArrayList<Word> fw = new ArrayList<>();

    for (Word w : words) {
      //if (w.getScowlValue() == 0 || w.getScowlValue() > 60) continue; // less common words
      //if(w.stresses.size() < 2) continue;
      //if(! w.stresses.toString().equals("[0, 1, 0]")) continue;
      if (w.word.contains("e") || w.word.contains("i") || w.word.contains("u") || w.word.contains("o") || w.word.contains("a")) continue;
      //if (w.word.contains("e") || w.word.contains("u")) continue;
      
      //if(w.phonemes.size() < 3) continue;
      //if (!w.rhymesWith(p.wordMap.get("odd"))) continue;
      //if(!w.partOfSpeech.contains("A")) continue;
      theList.add(w.word);
      fw.add(w);
      //System.out.println(w.word + "---->" + w.getScowlValue() + "--->" + w.phonemes);
    }/*
    theList.sort();
    for (String s : theList) {
      System.out.println(s);
    }*/
    fw.sort((w1, w2) -> w1.rhymeReverese().compareTo(w2.rhymeReverese()));
    for (Word s : fw) {
      System.out.println(s.word);
    }
  }

  static void rhyme(String s, String endstresses) {
    DSArrayList<Word> words = p.Words;
    for (Word w : words) {
      if (
        w.rhymesWith(p.wordMap.get(s)) &&
        w.stresses.toString().contains(endstresses)
      ) {
        System.out.println(w.word);
      }
    }
  }

  // Look for words with specified stress patterns.
  static void pregrams() {
    DSArrayList<Word> words = p.Words;
    //System.out.println("1: " + p.wordMap.get("true").rhymesWith(p.wordMap.get("blue")));
    //System.out.println("2: " + p.wordMap.get("true").rhymesWith(p.wordMap.get("false")));

    DSArrayList<String> theList = new DSArrayList<>();
    for (Word w : words) {
      String pruned = w.word.substring(1);
      if (p.wordMap.containsKey(pruned)) {
        Word pw = p.wordMap.get(pruned);
        if (
          w.stresses.size() > 0 &&
          pw.stresses.size() > 0 &&
          w.word.length() >= 3 &&
          "nsprt".contains(w.word.substring(0, 1)) &&
          "nsprt".contains(pw.word.substring(0, 1)) &&
          //w.stresses.size() != pw.stresses.size() &&
          !w.rhymesWith(pw)
        ) {
          String s = String.format("%s --> %s", w.word, pruned);
          theList.add(s);
        }
      }
    }
    theList.sort();
    for (String s : theList) {
      System.out.println(s);
    }
  }

  // Look for words with specified stress patterns.
  static void stress(String patt, String contains_or_equals) {
    DSArrayList<Word> words = p.Words;
    DSArrayList<String> theList = new DSArrayList<>();
    ArrayList<Word> fw = new ArrayList<>();

    for (Word w : words) {
      if (w.word.contains("e") || w.word.contains("i") || w.word.contains("u") || w.word.contains("o")) continue;
      //if(w.word.length() < 5) continue;
      String s = w.stresses.toString();
      s.replaceAll("2", "1");
      if (contains_or_equals.equals("c")) {
        if (w.stresses.toString().contains(patt)) {
          theList.add(w.word);
          fw.add(w);
        }
      } else if (contains_or_equals.equals("e")) {
        if (w.stresses.toString().equals(patt)) {
          theList.add(w.word);
          fw.add(w);
        }
      }
    }
    theList.sort();
    fw.sort((w1, w2) -> w1.rhymeReverese().compareTo(w2.rhymeReverese()));
    for (Word s : fw) {
      System.out.println(s.word);
    }
  }


  // patt should be something like "IH soft" or "AY hard"
  static void soundsLike(String patt, String stresses){
    String[] hard = {"F", "K", "P", "S", "T"};
    String[] medium = {"B", "D", "G", "J", "L", "M", "N", "R", "V", "W", "Z"};

    String[] inputs = patt.trim().split(" ");
    String v, end;
    v = inputs[0];   // vowel sound
    end = inputs[1]; // hard or soft
    
    for(Word w : p.Words){
      int n = w.phonemes.size();
      if(n < 2) continue;
      
      String last = w.phonemes.get(0);
      String penu = w.phonemes.get(1);
    
      // Check to see if we end with the right type of consonant
      int endMatches = 0;
      if(end.equals("hard")){
        endMatches = Arrays.binarySearch(hard, last);
      } else {
        endMatches = Arrays.binarySearch(medium, last);
      }
      if(endMatches < 0) continue;

      // Now check the penultimate vowel sound
      if(!penu.equals(v)) continue;

      // Check if we match the stresses
      if (stresses.length() > 0 && !w.stresses.toString().contains(stresses)){
        continue;
      }
      System.out.println(w.word);
    }
  }

  static void play() {
    p = new Poet("Texts/bible.txt");
    DSArrayList<Word> words = p.Words;

    int col = 0;
    for (Word w : words) {
      //if (w.stresses.toString().equals("[1, 0, 0]") && w.partOfSpeech.contains("A")) {
      //if (w.stresses.toString().contains("0, 0, 1")) {
      if (w.stresses.toString().length() == 3 && w.word.length() > 7) {
        System.out.println(w);
        continue;
        //if (!(w.partOfSpeech.contains("i") || w.partOfSpeech.contains("t"))) continue;
        /*for (String next : p.followMap.get(w.word).toDSArrayList()) {
                    if (!p.wordMap.containsKey(next))
                        continue;
                    Word nw = p.wordMap.get(next);
                    if (nw.stresses.toString().equals("[0, 1]")) {
                        System.out.print(String.format("%20s", w.word + " " + nw.word));
                        col = (col + 1) % 5;
                        if (col == 0)
                            System.out.println("");
                    }
                }*/
      }
    }
  } //  ' - - ' ' - - '

  static void addToCMUpron() {
    p = new Poet("Texts/Shakespeare.txt");
  }
}
