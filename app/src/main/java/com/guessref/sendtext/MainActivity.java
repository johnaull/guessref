package com.guessref.sendtext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ScrollView ;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;

    //String book(66); // Full book names in canonical order
    int bookNo = 0 ; // Number of book to be guessed
    int verseNo = 0; // Line number of verse to be guessed in file
    String vc =null; // Verse being guessed
    int verseScore; //Max is 20
    int totScore = 0;
    String scoreFile=null;
    int nVerse =5 ; //Number of verses guessed at (1-5)
    boolean getNewVerse = true;
    boolean choseNewGame = false;
    boolean appJustStarted = true;
    String playAgain = "";
    String introA = "You will get 5 randomly chosen verses " +
            "from the ";
    String introB = ". You try to guess which book " +
            "in the Bible each verse is from.  Matthew, Mark, and Luke are all treated as one book."+
            " Each verse is worth at most 20 points.\n\nContinue?";
    String verseSource = null;
    String chapter=null; // The chapter of the verse being guessed
    String verseScoreText = null;


    String[] book = {"", "GENESIS", "EXODUS", "LEVITICUS", "NUMBERS", "DEUTERONOMY",
            "JOSHUA", "JUDGES", "RUTH", "1 SAMUEL", "2 SAMUEL", "1 KINGS", "2 KINGS",
            "1 CHRONICLES", "2 CHRONICLES", "EZRA", "NEHEMIAH", "ESTHER", "JOB", "PSALMS",
            "PROVERBS", "ECCLESIASTES", "SOLOMON", "ISAIAH", "JEREMIAH", "LAMENTATIONS",
            "EZEKIEL", "DANIEL", "HOSEA", "JOEL", "AMOS", "OBADIAH", "JONAH", "MICAH", "NAHUM",
            "HABAKKUK", "ZEPHANIAH", "HAGGAI", "ZECHARIAH", "MALACHI", "MATTHEW",
            "MARK", "LUKE", "JOHN", "ACTS", "ROMANS", "1 CORINTHIANS", "2 CORINTHIANS",
            "GALATIANS", "EPHESIANS", "PHILIPPIANS", "COLOSSIANS", "1 THESSALONIANS",
            "2 THESSALONIANS", "1 TIMOTHY", "2 TIMOTHY", "TITUS", "PHILEMON", "HEBREWS",
            "JAMES", "1 PETER", "2 PETER", "1 JOHN", "2 JOHN", "3 JOHN", "JUDE", "REVELATION"};

    String[] cat = {"", "cat1","cat2","cat3","cat4","cat5","cat6","cat7","cat8", };

    // Category book is in
    int bookCat[]={0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2,
            2, 2, 2, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6,
            6, 6, 6, 6, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 8, 8, 8, 8, 8, 8, 8, 8, 8};

    String[] buttonCat = {"","GE","EX","LE","NU","DE","JOS","JUDG","RU","SA1","SA2","KI1","KI2",
            "CH1","CH2","EZR","NE","ES","JOB","PS","PR","EC","SO","IS","JE","LA","EZE","DA","HO"
            ,"JOE","AM","OB","JON","MIC","NA","HAB","ZEP","HAG","ZEC","MAL","MT","MK","LK","JN",
            "AC","RO","CO1","CO2","GA","EP","PHIL","CO","TH1","TH2","TI1","TI2","TI","PHE","HE","" +
            "JA","PE1","PE2","JN1","JN2","JN3","JUDE","RE"};

    String longestVerse = "Then were the king's scribes called at that time in the third month, that is, " +
            "the month Sivan, on the three and twentieth day thereof; and " +
            "it was written according to all that Mordecai commanded " +
            "unto the Jews, and to the lieutenants, and the deputies and rulers of " +
            "the provinces which are from India unto Ethiopia, an hundred " +
            "twenty and seven provinces, unto every province according to the " +
            "writing thereof, and unto every people after their language, " +
            "and to the Jews according to their writing, and according to " +
            "their language.";


    String measure = "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890";

    File fileEvents = null;
    BufferedReader br = null;
    String prevScores = "";

    int[] hScores = {0,0,0,0,0,0} ;  // High scores
    String[] scoreDates = {"","","","","",""}; //Dates for those scores
    int nSince0 = 0; // Number right since you last got a 0 on a verse
    int maxInARow = 0; // Most verses ever guessed right in a row
    boolean catGuessed = false; //True if they have guessed the category of the verse
    int nCategory = 0; //The category they guessed.
    boolean onlyNT = false ; //if true then verses to be guessed only come from the New Testament
    boolean minorProphsAs1 = false; //If true then all minor prophets are treated as one book
    // i.e. if verse is in Micah and they guess Jonah then that will be counted as right but
    // a guess of Genesis would still be wrong.

    public final static String EXTRA_MESSAGE = "com.guessref.sendtext.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        View view = getWindow().findViewById(Window.ID_ANDROID_CONTENT);
        intro(view);

    }

    public void intro(View view){
        //TextView bookChap = findViewById(R.id.editText);
        //bookChap.setVisibility(View.GONE);
        TextView et2 = findViewById(R.id.editText2);
        et2.setVisibility(View.GONE);
//        TextView wb = findViewById(R.id.wrongBook);
//        wb.setVisibility(View.GONE);
        TextView intro = findViewById(R.id.intro);
        GridLayout gridButtons = findViewById(R.id.gridButtons);
        gridButtons.setVisibility(View.GONE);
        RadioGroup rg = findViewById(R.id.radioOTNT);
        rg.setVisibility(View.GONE);
        CheckBox cb = findViewById(R.id.chkBox);
        cb.setVisibility(View.GONE);
        Button save = findViewById(R.id.save);
        save.setVisibility(View.GONE);

        // Retrieve  settings
        String  inline = null;
        try{
            File myDir = getFilesDir();
            String myPath = myDir.getPath();
            fileEvents = new File(myPath+"/settings.txt");
            br = new BufferedReader(new FileReader(fileEvents));
            inline = br.readLine();
            if (inline.startsWith("O")){
                onlyNT=true;
            } else {
                onlyNT=false;
            }
            inline = br.readLine();
            if (inline.startsWith("T")){
                minorProphsAs1=true;
            } else {
                minorProphsAs1=false;
            }

        }catch (Exception e) {
            String you = "are here";
        }

        if(onlyNT){
            verseSource = "NEW TESTAMENT";
        } else {
            verseSource = "WHOLE BIBLE";
        }
        //intro.setText(inline);
        intro.setText(introA+verseSource+introB);
        intro.setVisibility(View.VISIBLE);  // for now
        GridLayout yesNoStart = findViewById(R.id.yesNoStart);
        yesNoStart.setVisibility(View.VISIBLE);

    }

    public void yesStart(View view){
        TextView intro = findViewById(R.id.intro);
        intro.setVisibility(View.GONE);
        GridLayout yesNoStart = findViewById(R.id.yesNoStart);
        yesNoStart.setVisibility(View.GONE);

        // Retrieve  settings
        String  inline = null;
        try{
            File myDir = getFilesDir();
            String myPath = myDir.getPath();
            fileEvents = new File(myPath+"/settings.txt");
            br = new BufferedReader(new FileReader(fileEvents));
            inline = br.readLine();
            if (inline.startsWith("O")){
                onlyNT=true;
            } else {
                onlyNT=false;
            }
            inline = br.readLine();
            if (inline.startsWith("T")){
                minorProphsAs1=true;
            } else {
                minorProphsAs1=false;
            }

        }catch (Exception e) {
        }


        newGame(view);

    }

    public void settings(View view){
        TextView intro = findViewById(R.id.intro);
        intro.setVisibility(View.GONE);
        GridLayout yesNoStart = findViewById(R.id.yesNoStart);
        yesNoStart.setVisibility(View.GONE);
        // Set radio buttons to display current settings
        RadioButton wb = findViewById(R.id.radioAll);
        RadioButton nt = findViewById(R.id.radioNT);
        if(onlyNT){
            wb.setChecked(false);
            nt.setChecked(true);
        } else {
            nt.setChecked(false);
            wb.setChecked(true);
        }

        // Set checkbox to display current setting
        CheckBox cb = findViewById(R.id.chkBox);
        cb.setChecked(minorProphsAs1);

        RadioGroup rg = findViewById(R.id.radioOTNT);
        rg.setVisibility(View.VISIBLE);
        cb.setVisibility(View.VISIBLE);
        Button save = findViewById(R.id.save);
        save.setVisibility(View.VISIBLE);
    }

    public void saveSettings(View view){
        Button save = findViewById(R.id.save);
        save.setVisibility(View.GONE);


        // get selected radio button from radioGroup
        RadioGroup rg = findViewById(R.id.radioOTNT);
        int selectedId = rg.getCheckedRadioButtonId();
        // find the radiobutton by returned id
        RadioButton selectedButton = findViewById(selectedId);
        String selectedText = (String) selectedButton.getText();
        rg.setVisibility(View.GONE);

        CheckBox cb = findViewById(R.id.chkBox);
        boolean cbChecked = cb.isChecked();
        String checkedStr = null;

        /**************************************
        if (cbChecked){
            checkedStr="You caved on MP's.  Wimp";
        } else {
            checkedStr="You can DO it, man";
        }

        Toast.makeText(this, checkedStr,
                Toast.LENGTH_LONG).show();
************************************************/
        if (cbChecked){
            checkedStr="T";
        } else {
            checkedStr="F";
        }

        cb.setVisibility(View.GONE);

        // Write settings
        try {

            OutputStream os = openFileOutput("settings.txt", MODE_PRIVATE);
            os.write( selectedText.getBytes());
            os.write( "\n".getBytes());
            os.write(checkedStr.getBytes());
            os.close();
        } catch (Exception e2) {

        }



        //Toast.makeText(this,"Settings saved: "+selectedText,Toast.LENGTH_LONG).show();

        //TextView intro = findViewById(R.id.intro);
        //intro.setVisibility(View.VISIBLE);  // for now
        GridLayout yesNoStart = findViewById(R.id.yesNoStart);
        yesNoStart.setVisibility(View.VISIBLE);
        intro(view);
    }

        // Return string of n spaces
    public String space(int n){
        String s = "";
        for(int i = 1; i <= n; i++ ) {
            s = s + " ";
        }
        int krap = s.length();
        return s;
    }
    // Return string of n underbars
    public String dot(int n){
        String s = "";
        for(int i = 1; i <= n; i++ ) {
            s = s + "_";
        }
        return s;
    }

    public void newGame(View view){

        // Retrieve  high scores
        try{
            File myDir = getFilesDir();
            String myPath = myDir.getPath(); // Path to the highScores.txt file
            if(onlyNT){
                scoreFile="highScoresNT.txt";
            } else {
                if (minorProphsAs1) {
                    scoreFile = "highScoresM.txt";
                } else {
                    scoreFile = "highScores.txt";
                }
            }
            fileEvents = new File(myPath+"/"+scoreFile);
            br = new BufferedReader(new FileReader(fileEvents));

            String inline = null;
            for(int i = 1; i <= 5; i = i+1) {
                inline = br.readLine();
                int tempComma = inline.indexOf(",");
                String tempStr = inline.substring(0, tempComma);
                int tempInt = Integer.valueOf(tempStr);
                scoreDates[i] = inline.substring(tempComma+1).trim();
                hScores[i] = tempInt;
                }

            prevScores = "Previous High Scores on "+verseSource;
            if (minorProphsAs1 && !onlyNT) {
                prevScores = prevScores + " treating minor prophets as one book.";
            }
            prevScores = prevScores+"\n\nScore          Date\n";
            for( int i = 1; i <= 5; i = i+1) {
                if(scoreDates[i].length()==0) {
                    break;
                }
                    String tempScore = String.valueOf(hScores[i]).trim();
                prevScores = prevScores +  tempScore + dot(10 - tempScore.length() ) + scoreDates[i] + "\n";
            }
            inline = br.readLine();
            prevScores = prevScores + "\nThe record for most number of verses guessed right in a row: "+inline + "\n";
            maxInARow =  Integer.valueOf(inline);
            inline = br.readLine();
            prevScores = prevScores + "\nCurrently you have guessed "+inline+" right in a row." ;
            nSince0 =  Integer.valueOf(inline);


            //line = br.readLine();
            br.close();

        }catch (Exception e) {
        }

        if(choseNewGame){  //already in new game
            nVerse=0;
            // Get rid of text box that was used to display prev. scores
            TextView tv2 = findViewById(R.id.textView2);
            tv2.setVisibility(View.GONE);

        } else { // User hasn't decided yet about a new game
            nVerse=5;
        }
        totScore=0;
        getNewVerse=true;
        // Hide the yesNo grid
        GridLayout yesNo = findViewById(R.id.yesNoButtons);
        yesNo.setVisibility(View.GONE);
        sendMessage(view)  ;

    }

    public void systemExit (View view){
        System.exit(0);
    }


        public void showBooks (View view) {
            // Show all books if they have not correctly guessed the category
            int istart = 1;
            if (onlyNT){
                istart = 40;
            }
            for( int ib = 1; ib < istart; ib = ib+1) {
                String btnID = buttonCat[ib];
                int resID = getResources().getIdentifier(btnID, "id", getPackageName());
                Button btn = findViewById(resID);
                btn.setVisibility(View.GONE);
            }


            /*****************************************************************************
            if(!catGuessed){
                 for( int ib = istart; ib < 67; ib = ib+1) {
                    String btnID = buttonCat[ib];
                    int resID = getResources().getIdentifier(btnID, "id", getPackageName());
                    Button btn = findViewById(resID);
                    btn.setVisibility(View.VISIBLE);
                }
            }
********************************************************************/
            GridLayout bookCatContext = findViewById(R.id.bookCatContext);
            bookCatContext.setVisibility(View.GONE);
            GridLayout gridButtons = findViewById(R.id.gridButtons);
            gridButtons.setVisibility(View.VISIBLE);
    }

    public void showCategories (View view) {
        int istart = 1;
        if (onlyNT){
            istart = 6;
        }
        for( int ib = 1; ib < istart; ib = ib+1) {
            String btnID = cat[ib];
            int resID = getResources().getIdentifier(btnID, "id", getPackageName());
            Button btn = findViewById(resID);
            btn.setVisibility(View.GONE);
        }
/*********************************************************************************************
        for( int ib = istart; ib < 9; ib = ib+1) {
            String btnID = cat[ib];
            int resID = getResources().getIdentifier(btnID, "id", getPackageName());
            Button btn = findViewById(resID);
            btn.setVisibility(View.VISIBLE);
        }
*******************************************************************************************/

        GridLayout bookCatContext = findViewById(R.id.bookCatContext);
        bookCatContext.setVisibility(View.GONE);
        GridLayout catButtons = findViewById(R.id.catButtons);
        catButtons.setVisibility(View.VISIBLE);
    };

    public void seeContext (View view) {
        try {
            verseScore=verseScore-4;
            if (verseScore <0){
                verseScore = 0;
            }
            TextView bookChap = findViewById(R.id.editText2);
            bookChap.setText("Score now down to" + verseScore);
            if (verseScore ==0){
                // Make 0 on verse sound`
                MediaPlayer mp = MediaPlayer.create(this,R.raw.zero_on_verse);
                mp.start();

                bookChap.setText("Verse is in "+ book[bookNo] +" "+chapter);
                getNewVerse=true;
                nVerse=nVerse+1;
                sendMessage(view);
            }

            String vc = "";
            String[] context =  {"","","","","","",""} ; // Group of lines containing the mystery verse
            String finalContext = "";
            String newLine = " ";
            BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("reverseNasb.txt")));
            for( int m = 1; m <= verseNo-3; m = m+1) {
                vc = reader.readLine();
            }
            // Read lines before and after
            for( int m = 1; m <= 6 ; m = m+1) {
                context[m] = reader.readLine();
            }
            // Print them out in correct order, skipping chapter or book headings
            for( int m = 6; m >= 1 ; m = m-1) {
                if (context[m].length() > 0){
                    int j = (int) context[m].charAt(0); //ASCII value of 1st character
                    // Verses start with digit or "digit (Ascii 49-57 or 34)
                    if ( j != 34 &&  !(j > 48 && j < 58)){
                        // Don't show it
                    } else {
                        // Drop first character
                        context[m] = context[m].substring(1);
                        //Drop last character
                        context[m] = context[m].substring(0,context[m].length()-1);
                        finalContext=finalContext+context[m]+newLine;
                    }
                }
            }
            finalContext = finalContext + "\n\n";
            TextView tv = new TextView(this);
            tv.setTextSize(16);
            tv.setText(finalContext);
            LinearLayout child = (LinearLayout)findViewById( R.id.child);

                child.removeAllViews();
                child.addView(tv);

            // Context button is no longer available
            Button btn = findViewById(R.id.context);
            btn.setVisibility(View.GONE);

        } catch (Exception e) {
            TextView bookChap = findViewById(R.id.editText2);
            bookChap.setVisibility(View.VISIBLE);
            bookChap.setText("Error reading Bible file");
            return;
        }

        };

    /** Called when the user taps a book grid button */
    public void checkCat1(View view) { checkCat(view, 1); }
    public void checkCat2(View view) { checkCat(view, 2); }
    public void checkCat3(View view) { checkCat(view, 3); }
    public void checkCat4(View view) { checkCat(view, 4); }
    public void checkCat5(View view) { checkCat(view, 5); }
    public void checkCat6(View view) { checkCat(view, 6); }
    public void checkCat7(View view) { checkCat(view, 7); }
    public void checkCat8(View view) { checkCat(view, 8); }

    public void checkCat(View view, int nCat) {
        TextView bookChap = findViewById(R.id.editText2);
        bookChap.setVisibility(View.VISIBLE);


        if(nCat == bookCat[bookNo]){
            verseScore=verseScore-3;
            if (verseScore <0){
                verseScore = 0;
            }
            if (verseScore ==0){
                // Make 0 on verse sound`
                MediaPlayer mp = MediaPlayer.create(this,R.raw.zero_on_verse);
                mp.start();

                bookChap.setText("0 on this one. It is " + book[bookNo] +" "+chapter);
                getNewVerse=true;
                nVerse=nVerse+1;
                sendMessage(view);
            }
            // Make right category sound`
            MediaPlayer mp = MediaPlayer.create(this,R.raw.glass);
            mp.start();

            bookChap.setText("Right category. Score on this one=" + verseScore);
            catGuessed=true;
            GridLayout catButtons = findViewById(R.id.catButtons);
            catButtons.setVisibility(View.GONE);
            GridLayout gridButtons = findViewById(R.id.gridButtons);
            gridButtons.setVisibility(View.VISIBLE);
            //Hide the Guess Category Button
            int guessCatBtnID = getResources().getIdentifier("cat", "id", getPackageName());
            Button btn = findViewById(guessCatBtnID);
            btn.setVisibility(View.GONE);

            // Hide all books outside the category
            for( int i = 1; i < 67; i = i+1) {
                if (bookCat[i] == nCat){
                    String btnID = buttonCat[i];
                    int resID = getResources().getIdentifier(btnID, "id", getPackageName());
                    btn = findViewById(resID);
                    btn.setVisibility(View.VISIBLE);
                } else {
                    String btnID = buttonCat[i];
                    int resID = getResources().getIdentifier(btnID, "id", getPackageName());
                    btn = findViewById(resID);
                    btn.setVisibility(View.GONE);
                }
            }

                getNewVerse=false;
            sendMessage(view);

        } else {
            verseScore=verseScore-5;
            if (verseScore <0){
                verseScore = 0;
            }

            // Make wrong choice sound`
            MediaPlayer mp = MediaPlayer.create(this,R.raw.basso);
            mp.start();

            bookChap.setText("Wrong category. Score on this verse is " + verseScore);

            //Hide the category that was wrong
            String btnID = cat[nCat];
            int resID = getResources().getIdentifier(btnID, "id", getPackageName());
            Button btn = findViewById(resID);
            btn.setVisibility(View.INVISIBLE);

            if (verseScore ==0){
                // Make 0 on verse  sound`
                mp = MediaPlayer.create(this,R.raw.zero_on_verse);
                mp.start();

                bookChap.setText("Wrong. 0 on this one. It is " + book[bookNo] +" "+chapter);
                getNewVerse=true;
                nVerse=nVerse+1;
                sendMessage(view);
            }
        }

    }


    /** Called when the user taps a book grid button */
    public void checkBook1(View view) { checkBook(view, 1); }
    public void checkBook2(View view) { checkBook(view, 2); }
    public void checkBook3(View view) { checkBook(view, 3); }
    public void checkBook4(View view) { checkBook(view, 4); }
    public void checkBook5(View view) { checkBook(view, 5); }
    public void checkBook6(View view) { checkBook(view, 6); }
    public void checkBook7(View view) { checkBook(view, 7); }
    public void checkBook8(View view) { checkBook(view, 8); }
    public void checkBook9(View view) { checkBook(view, 9); }

    public void checkBook10(View view) { checkBook(view,10 ); }
    public void checkBook11(View view) { checkBook(view,11 ); }
    public void checkBook12(View view) { checkBook(view,12 ); }
    public void checkBook13(View view) { checkBook(view,13 ); }
    public void checkBook14(View view) { checkBook(view,14 ); }
    public void checkBook15(View view) { checkBook(view,15 ); }
    public void checkBook16(View view) { checkBook(view,16 ); }
    public void checkBook17(View view) { checkBook(view,17 ); }
    public void checkBook18(View view) { checkBook(view,18 ); }
    public void checkBook19(View view) { checkBook(view,19 ); }

    public void checkBook20(View view) { checkBook(view,20 ); }
    public void checkBook21(View view) { checkBook(view,21 ); }
    public void checkBook22(View view) { checkBook(view,22 ); }
    public void checkBook23(View view) { checkBook(view,23 ); }
    public void checkBook24(View view) { checkBook(view,24 ); }
    public void checkBook25(View view) { checkBook(view,25 ); }
    public void checkBook26(View view) { checkBook(view,26 ); }
    public void checkBook27(View view) { checkBook(view,27 ); }
    public void checkBook28(View view) { checkBook(view,28 ); }
    public void checkBook29(View view) { checkBook(view,29 ); }

    public void checkBook30(View view) { checkBook(view,30 ); }
    public void checkBook31(View view) { checkBook(view,31 ); }
    public void checkBook32(View view) { checkBook(view,32 ); }
    public void checkBook33(View view) { checkBook(view,33 ); }
    public void checkBook34(View view) { checkBook(view,34 ); }
    public void checkBook35(View view) { checkBook(view,35 ); }
    public void checkBook36(View view) { checkBook(view,36 ); }
    public void checkBook37(View view) { checkBook(view,37 ); }
    public void checkBook38(View view) { checkBook(view,38 ); }
    public void checkBook39(View view) { checkBook(view,39 ); }

    public void checkBook40(View view) { checkBook(view,40 ); }
    public void checkBook41(View view) { checkBook(view,41 ); }
    public void checkBook42(View view) { checkBook(view,42 ); }
    public void checkBook43(View view) { checkBook(view,43 ); }
    public void checkBook44(View view) { checkBook(view,44 ); }
    public void checkBook45(View view) { checkBook(view,45 ); }
    public void checkBook46(View view) { checkBook(view,46 ); }
    public void checkBook47(View view) { checkBook(view,47 ); }
    public void checkBook48(View view) { checkBook(view,48 ); }
    public void checkBook49(View view) { checkBook(view,49 ); }

    public void checkBook50(View view) { checkBook(view,50 ); }
    public void checkBook51(View view) { checkBook(view,51 ); }
    public void checkBook52(View view) { checkBook(view,52 ); }
    public void checkBook53(View view) { checkBook(view,53 ); }
    public void checkBook54(View view) { checkBook(view,54 ); }
    public void checkBook55(View view) { checkBook(view,55 ); }
    public void checkBook56(View view) { checkBook(view,56 ); }
    public void checkBook57(View view) { checkBook(view,57 ); }
    public void checkBook58(View view) { checkBook(view,58 ); }
    public void checkBook59(View view) { checkBook(view,59 ); }


    public void checkBook60(View view) { checkBook(view,60 ); }
    public void checkBook61(View view) { checkBook(view,61 ); }
    public void checkBook62(View view) { checkBook(view,62 ); }
    public void checkBook63(View view) { checkBook(view,63 ); }
    public void checkBook64(View view) { checkBook(view,64 ); }
    public void checkBook65(View view) { checkBook(view,65 ); }
    public void checkBook66(View view) { checkBook(view,66 ); }

    public void checkBook(View view, int nbook) {
        //TextView bookChap = findViewById(R.id.editText);
        TextView et2 = findViewById(R.id.editText2);
//        TextView wb = findViewById(R.id.wrongBook);
        //bookChap.setVisibility(View.VISIBLE);
        if(nbook == 67){ // They chose X - going back to previous menu
            //wb.setVisibility(View.GONE);  // Don't show wrong book message
            et2.setVisibility(View.GONE);  // Don't show wrong book message
            return;
        }
        // If we are treating all minor prophets as one
        boolean minorProphetMatch = false;
        if(minorProphsAs1){
            if ((bookNo > 27 && bookNo <40) && (nbook > 27 && nbook <40)){
                minorProphetMatch=true;
            }
        }

        if(nbook == bookNo || minorProphetMatch ||
                (bookNo == 40 && (nbook == 41 || nbook == 42)) || //Count all synoptics the same
                (bookNo == 41 && (nbook == 40 || nbook == 42)) || //Count all synoptics the same
                (bookNo == 42 && (nbook == 40 || nbook == 41))  //Count all synoptics the same
        ){
            // Make got_verse  sound`
            MediaPlayer mp = MediaPlayer.create(this,R.raw.got_verse);
            mp.start();

            //wb.setVisibility(View.GONE);
            et2.setVisibility(View.VISIBLE);
            verseScoreText = "Good! " + book[bookNo] +" "+chapter+" Score on this one=" + verseScore;
            et2.setText( verseScoreText);
            GridLayout gridButtons = findViewById(R.id.gridButtons);
            gridButtons.setVisibility(View.GONE);
            totScore=totScore+verseScore;
            nSince0++;
            getNewVerse=true;
            nVerse=nVerse+1;
           sendMessage(view);
        } else {
            verseScore=verseScore-5;
            if (verseScore <0){
                verseScore = 0;
            }

            // Make wrong book sound`
            MediaPlayer mp = MediaPlayer.create(this,R.raw.basso);
            mp.start();

//            et2.setVisibility(View.GONE);
//            wb.setVisibility(View.VISIBLE);
//            wb.setText("Score on this one="+verseScore+".");
            et2.setVisibility(View.VISIBLE);
            et2.setText("Score on this one="+verseScore+".");

            if (verseScore ==0){
                // Make 0 on verse  sound`
                mp = MediaPlayer.create(this,R.raw.zero_on_verse);
                mp.start();

 //               wb.setVisibility(View.GONE);
                et2.setVisibility(View.VISIBLE);
                verseScoreText="0 on this one. It is " + book[bookNo] +" "+chapter;
                et2.setText(verseScoreText);
                nSince0=0;
                getNewVerse=true;
                nVerse=nVerse+1;
                sendMessage(view);
            }
            //Hide the book that was wrong
            String btnID = buttonCat[nbook];
            int resID = getResources().getIdentifier(btnID, "id", getPackageName());
            Button btn = findViewById(resID);
            btn.setVisibility(View.INVISIBLE);

        }

    }

public void callSendMessage(View view){
        // Called from the BACK buttons which put you back at the main menu
        getNewVerse=false;
        sendMessage(view);
}

    public void sendMessage(View view) {
        //TextView  scoreTV = findViewById(R.id.editText);
        //scoreTV.setVisibility(View.VISIBLE);
        TextView  scoreTV2 = findViewById(R.id.editText2);
        scoreTV2.setVisibility(View.VISIBLE);
        TextView  tv = findViewById(R.id.textView2);

        if(appJustStarted){
            TextView bookChap = findViewById(R.id.editText2);
            bookChap.setVisibility(View.GONE);
            TextView et2 = findViewById(R.id.editText2);
            et2.setVisibility(View.GONE);
 //           TextView wb = findViewById(R.id.wrongBook);
 //           wb.setVisibility(View.GONE);
            if (prevScores.length()<1){
                prevScores = "You do not have any previous scores.\n";
            }
            playAgain = playAgain + prevScores + "\n\nContinue?\n\n";
            tv = findViewById(R.id.textView2);
            tv.setText(playAgain);
            playAgain="";
            GridLayout bookCatContext = findViewById(R.id.bookCatContext);
            bookCatContext.setVisibility(View.GONE);
            GridLayout catButtons = findViewById(R.id.catButtons);
            catButtons.setVisibility(View.GONE);
            GridLayout gridButtons = findViewById(R.id.gridButtons);
            gridButtons.setVisibility(View.GONE);
            GridLayout yesNo = findViewById(R.id.yesNoButtons);
            yesNo.setVisibility(View.VISIBLE);
            appJustStarted=false;
            choseNewGame=true;
            return;
        }

        // Write total score
        if (nVerse == 0) {
            scoreTV2.setText("");
        }
        if (nVerse == 1 ) {
            //String tempStr = scoreTV2.getText()+" ";
            //scoreTV2.setText(tempStr+"Tot after " + nVerse + " verse = " + totScore);
            //appJustStarted=false;
            // Now we are in a new game so set flag to false which
            // means they have not yet chosen to play the next game
            choseNewGame = false;
        }
        if (nVerse > 1 && nVerse < 5) {
            String tempStr = scoreTV2.getText()+" ";
            scoreTV2.setText(tempStr+"Tot after " + nVerse + " verses = " + totScore+"\n");
        }
        if(nVerse==5 ){
            //scoreTV2.setText(verseScoreText+ ".\nTot after " + nVerse + " verses = " + totScore+"\n");
            prevScores = verseScoreText+ ".\nTot after " + nVerse + " verses = " + totScore+"\n";
        }

        if(nVerse==5 && !appJustStarted){
 //           if (!choseNewGame){
 //               scoreTV2.setText("Tot after " + nVerse + " verses = " + totScore+"\n");
 //           }


            // Write scores to highSores.txt file
            try {

                OutputStream os = openFileOutput(scoreFile, MODE_PRIVATE);

                // Insert new high score
                String curDate = new SimpleDateFormat("MM-dd-yyyy").format(new Date());
                for( int i = 1; i <= 5; i = i+1) {
                    if (totScore  >= hScores[i]) {
                        for( int j = 5; j > i; j = j-1) {
                            hScores[j] = hScores[j-1];
                            scoreDates[j] = scoreDates[j-1];
                        }
                        hScores[i] = totScore;
                        scoreDates[i] = curDate;
                        break;
                    }
                }
                // Write scores to file
                for( int i = 1; i <= 5; i = i+1) {
                    String tempStr = hScores[i] + ", " + '\t' + " " + scoreDates[i] + "\n";
                    os.write( tempStr.getBytes());
                }
                if (nSince0 > maxInARow) {
                    maxInARow = nSince0;
                }
                String tempStr = maxInARow + "\n";
                os.write( tempStr.getBytes());
                tempStr = nSince0 + "\n";
                os.write( tempStr.getBytes());
                os.close();
            } catch (Exception e2) {

            }

            //If they are not already in a new game, ask if they want to play another game
            if(!choseNewGame){

                TextView et2 = findViewById(R.id.editText2);
                et2.setVisibility(View.GONE);

                prevScores = prevScores + "Previous High Scores on "+verseSource;
                if (minorProphsAs1 && !onlyNT) {
                    prevScores = prevScores + " treating minor prophets as one book.";
                }
                prevScores = prevScores+"\n\nScore          Date\n";

                for( int i = 1; i <= 5; i = i+1) {
                    if(scoreDates[i].length()==0) {
                        break;
                    }
                    String tempScore = String.valueOf(hScores[i]).trim();
                    prevScores = prevScores +  tempScore + dot(10 - tempScore.length() ) + scoreDates[i] + "\n";
                }
                prevScores = prevScores + "\nThe record for most number of verses guessed right in a row: "+maxInARow + "\n";
                prevScores = prevScores + "\nCurrently you have guessed "+nSince0+" right in a row." ;
                playAgain = playAgain + prevScores + "\n Continue?\n\n";
                tv = findViewById(R.id.textView2);
                tv.setVisibility(View.VISIBLE);
                tv.setText(playAgain);
                playAgain="";
                ScrollView  scrollView = findViewById(R.id.sv);
                scrollView.setVisibility(View.GONE);
                GridLayout bookCatContext = findViewById(R.id.bookCatContext);
                bookCatContext.setVisibility(View.GONE);
                GridLayout catButtons = findViewById(R.id.catButtons);
                catButtons.setVisibility(View.GONE);
                GridLayout gridButtons = findViewById(R.id.gridButtons);
                gridButtons.setVisibility(View.GONE);
                GridLayout yesNo = findViewById(R.id.yesNoButtons);
                yesNo.setVisibility(View.VISIBLE);
                choseNewGame=true;
                return;
            }
            //return;
        }
        GridLayout bookCatContext = findViewById(R.id.bookCatContext);
        bookCatContext.setVisibility(View.VISIBLE);
        // Context button is now available
        Button btn = findViewById(R.id.context);
        btn.setVisibility(View.VISIBLE);

        GridLayout catButtons = findViewById(R.id.catButtons);
        catButtons.setVisibility(View.GONE);
        GridLayout gridButtons = findViewById(R.id.gridButtons);
        gridButtons.setVisibility(View.GONE);
        Random randomGenerator = new Random();
        int nv =0;
        if (onlyNT){
            nv = randomGenerator.nextInt(7957);
        } else {
            nv = randomGenerator.nextInt(30997);
        }
        String v =null;
        int i = 0 ;
        int j = 0;
        int k = 0;
        int ntemp=0;

        if (getNewVerse){
            if (nVerse==5){
                nVerse=0;
            }
           // Make all categories visible

          /////////////////  String you = "are here";
            for( i = 1; i < 9; i = i+1) {
                String btnID = cat[i];
                int resID = getResources().getIdentifier(btnID, "id", getPackageName());
                btn = findViewById(resID);
                btn.setVisibility(View.VISIBLE);
            }

            //~ Make all books visible
            for( int ib = 1; ib < 67; ib = ib+1) {
                String btnID = buttonCat[ib];
                int resID = getResources().getIdentifier(btnID, "id", getPackageName());
                Button butn = findViewById(resID);
                butn.setVisibility(View.VISIBLE);
            }

            catGuessed=false;
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("reverseNasb.txt")));

                for( i = 1; i < nv+1; i = i+1) {
                    vc = reader.readLine();
                    //System.out.println("i= "+i+" vc=|"+vc+"|");
                    k=k+1;
                    if (vc.length() > 0){
                        j = (int) vc.charAt(0); //ASCII value of 1st character
                        // Verses start with digit or "digit (Ascii 49-57 or 34)
                        if ( j != 34 &&  !(j > 48 && j < 58)){
                            //This is not a verse so decrement i
                            i=i-1;
                        }
                    } else {
                        //This is empty string so decrement i
                        i=i-1;
                    }
                }
                verseNo = k;

//				Find book and chapter.  All books start with ~
                chapter = "0";
                while ( true ) {
                    v = reader.readLine();
                    try {
                        if (v.length() > 0){
                            if(v.substring(0, 1).equals("~")){
                                break;
                            }
                            if (chapter.equals("0") && v.substring(0,7).equals("CHAPTER")){  //get chapter
                                ntemp  = v.indexOf(" ");
                                chapter = v.substring(ntemp  +1);
                            }
                        } //
                    } catch (Exception e) {
                        // We just read empty string.  Go on with life.
                    }

                }

                v = v.substring(1) ;
                for( i = 1; i <= 66; i = i+1) {
                    if (book[i].equals(v)) {
                        bookNo=i;
                        break;
                    }
                }
            }
            catch (Exception ex) {
                scoreTV2.setText("Error getting verse.");
            }
            //scoreTV.setText("You start with 20 points on this verse.");
            verseScore=20;

            //Show the Guess Category Button
            int guessCatBtnID = getResources().getIdentifier("cat", "id", getPackageName());
            btn = findViewById(guessCatBtnID);
            btn.setVisibility(View.VISIBLE);

        }  // endif whether to get a new verse

        String verse1 = vc+"\n";
         tv = new TextView(this);
        // verse1 = longestVerse+"\n";
        //verse1=measure;
        tv.setTextSize(16);
         tv.setText(verse1);
        LinearLayout child = (LinearLayout)findViewById( R.id.child);
        ScrollView  scrollView = findViewById(R.id.sv);
        scrollView.setVisibility(View.VISIBLE);

        try {
           // scrollView.removeAllViews();
            child.removeAllViews();
            child.addView(tv);
        } catch (Exception ex) {
            String errmsg = ex.getMessage();
        }
        //tv = findViewById(R.id.textView2);
        //tv.setText(verse1);
        String bookChapter = v + " " + chapter ;


    }
}
