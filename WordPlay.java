import java.util.ArrayList;
import java.util.Arrays;

class WordPlay {

  /*
   * There once was a man from Nantucket 010 010 01(0) A Who kept all his cash in
   * a bucket 010 010 01(0) A (thanks, Max!) His daughter named Nan 01001 B Ran
   * away with a man 101001 B And as for the bucket, Nantucket 010 010 01(0) A
   */
  static Poet p;

  public static void main(String[] args) {
    p = new Poet("Texts/flist.txt");
    //stress("[0, 1", "c");
    //pregrams();
    // writeGreatLimerickParallel();
    //rhyme("trails", "1]");
    //filterWords();
    soundsLike("IH hard");
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
  static void soundsLike(String patt){
    String[] hard = {"F", "K", "P", "S", "T"};
    String[] medium = {"B", "D", "G", "J", "L", "M", "N", "R", "V", "W", "Z"};

    String[] inputs = patt.trim().split(" ");
    String v = inputs[0];   // vowel sound
    String end = inputs[1]; // hard or soft
    
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
