import javafx.util.Pair;

import java.util.*;


public class Parse {
    private List<String> terms;
    private int currentWord;
    private String currentDocument;
    private Hashtable<String, String> months;
    private String[] words;
    HashSet<String> stopWords;
    public static long num;
    public static long tokens;


    public Parse(HashSet<String> stopWords) {
        currentWord = 0;
        createMonthsHash();
        this.stopWords = stopWords;
        num=0;
        tokens=0;
    }

    public void printTotalWords(){
        System.out.println(num);
        System.out.println(tokens);
    }

    public List<String> parse(String document){
        currentWord = 0;
        terms = new ArrayList<String>();
        currentDocument = document;
        document=document.trim();
        words = document.split("\\s+");
        num=num + words.length;
        mainParser();
        tokens=tokens + terms.size();
        return terms;
    }




    private void createMonthsHash() {
        months = new Hashtable<>();
        months.put("january", "01");
        months.put("february", "02");
        months.put("march", "03");
        months.put("april", "04");
        months.put("may", "05");
        months.put("june", "06");
        months.put("july", "07");
        months.put("august", "08");
        months.put("september", "09");
        months.put("october", "10");
        months.put("november", "11");
        months.put("december", "12");
    }


    public void mainParser() {
        while(currentWord<words.length){
            if(generateNumber())
                continue;
            else if(generatePhrases())
                continue;
            else {
                generateWord();
            }
        }

    }

    private boolean generateWord(){
        String word = words[currentWord].replaceAll("[\\[\\(&;'~`+|!*,.#\"\\)\\]]*","");
        word = word.replaceAll("-{2,}","");
        if(word.matches("-\\w*"))
            word = word.replaceAll("-","");
        if(word.isEmpty() || word.length()==1) {
            currentWord++;
            return false;
        }
        String currentWordToLower = word.toLowerCase();
        String dateText="";
        if(currentWord + 1 < words.length && months.containsKey(currentWordToLower)&&words[currentWord+1].matches("\\d+") ){
            dateText = addDate(words[currentWord + 1], currentWordToLower);
            terms.add(dateText);
            currentWord = currentWord + 2;
        }
        else if(word.matches("\\w+[/]\\w+")) {
            String[] splittedOrWord = word.split("/");
            terms.add(splittedOrWord[0]+" Or " + splittedOrWord[1]);
            currentWord++;
        }
        else if(word.matches("\\d{1,2}:\\d{2}")){
            String[] splittedHourWord = word.split(":");
            if(Integer.valueOf(splittedHourWord[0])>11 && Integer.valueOf(splittedHourWord[1])>=0){
                if(Integer.valueOf(splittedHourWord[0])==12)
                    terms.add(String.valueOf(Integer.valueOf(splittedHourWord[0]))+ "PM");
                else
                    terms.add(String.valueOf(Integer.valueOf(splittedHourWord[0])-12) +" PM");
            }
            else
                terms.add(splittedHourWord[0]+ " AM");
            currentWord++;
        }
        else if(stopWords.contains(currentWordToLower)){
            currentWord++;
            return false;
        }else {
            terms.add(word);
            currentWord++;
            return true;
        }
        return false;
    }

    private boolean ifContainsMoreDotsThanOne(String word){
        int counter = 0;
        for(int i=0;i<word.length();i++){
            if(word.charAt(i)=='.')
                counter++;
        }
        return (counter>1);
    }

    private boolean generatePhrases(){
        if(currentWord + 3 < words.length){
            if(words[currentWord].toLowerCase().equals("between") &&
                    words[currentWord + 1].matches("\\d+") &&
                    words[currentWord + 2].toLowerCase().equals("and") &&
                    words[currentWord + 3].matches("\\d+")){
                terms.add(words[currentWord + 1] + "-" + words[currentWord + 3]);
                currentWord+=4;
                return true;
            }
        }
        return false;
    }

    private boolean generateNumber() {
        String firstWord = words[currentWord].replaceAll("[[(#)\"]]*","");
        firstWord = firstWord.replaceAll("[.]+$","");
        words[currentWord]=firstWord;
        String nextWord = "";
        String previousWord = "";
        firstWord = firstWord.toLowerCase();
        if (currentWord > 0) {
            previousWord = words[currentWord - 1].toLowerCase();
        }
        if (currentWord +1< words.length)
            nextWord = words[currentWord + 1].toLowerCase();
        if (firstWord.matches("\\d[\\d,.]*\\%")) {
            String percentWord = addPercentNumber(firstWord);
            terms.add(percentWord);
            currentWord++;
        } else if (firstWord.matches("\\d[\\d,.]*")) {
            if(!ifContainsMoreDotsThanOne(firstWord))
                numberIsTypeOne(firstWord, nextWord, previousWord);
            else
                return false;
        } else if (firstWord.matches("\\$\\d[\\d,.]*")) {
            if(firstWord.length() - firstWord.replaceAll("\\.","").length() > 1)
                return false;
            addPriceWithDollarSign(firstWord,nextWord);
        } else if (firstWord.matches("\\d[\\d,.]*bn")&& nextWord.equals("dollars")) {
            addPriceWithBnSuffix(firstWord);
        }
        else if(firstWord.matches("\\d[\\d,.]*m")&& nextWord.equals("dollars"))
            addPriceWithMSuffix(firstWord);
        else{
            return false;
        }
        return true;
    }

    private void addPriceWithMSuffix(String word){
        float floatNumber;
        int intNumber;
        String priceTerm=word.replaceAll("[,m]","");
        floatNumber = Float.parseFloat(priceTerm);
        priceTerm = priceTerm + " M Dollars";
        terms.add(priceTerm);
        currentWord = currentWord+2;
    }

    private void addPriceWithBnSuffix(String word){
        float floatNumber;
        long intNumber;
        String priceTerm=word.replaceAll("[,bn]*","");
        if(word.contains(".")){
            floatNumber = Float.parseFloat(priceTerm);
            priceTerm = String.valueOf((float)(floatNumber*1000));

        }else{
            intNumber = Integer.valueOf(priceTerm);
            priceTerm = String.valueOf((long)(intNumber*1000));

        }
        priceTerm = priceTerm + " M Dollars";
        terms.add(priceTerm);
        currentWord = currentWord+2;
    }

    private void addPriceWithUSandDollar(String word,String nextWord){
        float floatNumber;
        int intNumber;
        String priceTerm=word.replaceAll("[,]*","");
        if(word.contains(".")){
            floatNumber = Float.parseFloat(priceTerm);
            priceTerm = String.valueOf(floatNumber);
        }
        else{
            intNumber = Integer.valueOf(priceTerm);
            priceTerm = String.valueOf(intNumber);
        }
        if(nextWord.equals("million")){
            priceTerm = priceTerm + " M Dollars";
        }
        else{
            priceTerm = priceTerm+ "000 M Dollars";
        }
        terms.add(priceTerm);
    }

    private void numberIsTypeOne(String firstWord, String nextWord, String previousWord) {
        if (nextWord.equals("percent") || nextWord.equals("percentage")) {
            String percentWord = addPercentNumber(words[currentWord]);
            terms.add(percentWord);
            currentWord = currentWord + 2;
        }
        else if (nextWord.equals("thousand") || nextWord.equals("million") || nextWord.equals("billion")) {
            String checkIfDollar ="";
            String checkIfUs = "";
            if(currentWord+3<words.length){
                checkIfDollar = words[currentWord+3].toLowerCase();
                checkIfUs = words[currentWord+2].toLowerCase();
            }
            if(checkIfUs.equals("u.s.") && checkIfDollar.equals("dollars")){
                addPriceWithUSandDollar(firstWord,nextWord);
                currentWord = currentWord+4;
            }
            else{
                addLargeNumbers(firstWord, nextWord);
                currentWord = currentWord + 2;
            }
        } else if (nextWord.matches("\\d+/\\d")) {
            if (currentWord + 2 < words.length) {
                if (words[currentWord + 2].toLowerCase().equals("dollars")) {
                    addDollarWithFraction(firstWord, nextWord);
                    currentWord = currentWord + 3;
                } else {
                    String numberWithFraction = words[currentWord] + " " + nextWord;
                    terms.add(numberWithFraction);
                    currentWord=currentWord+2;
                }
            }
        } else if (nextWord.matches("dollars")) {
            addPriceWithDollar(words[currentWord]);
            currentWord = currentWord + 2;
        } else if (firstWord.matches("\\d+")) {
            String dateText = "";
            if (months.containsKey(nextWord) ) {
                if (months.containsKey(nextWord)) {
                    dateText=addDate(firstWord, nextWord);
                    terms.add(dateText);
                    currentWord = currentWord + 2;
                } else {
                    dateText=addDate(firstWord, previousWord);
                    terms.add(dateText);
                    currentWord++;
                }
            } else {
                if(words[currentWord].length() < 19){
                    addPlainNumber(words[currentWord]);
                currentWord++;
                }else
                    generateWord();
            }
        } else {
            if(words[currentWord].length() < 19){
                addPlainNumber(words[currentWord]);
                currentWord++;
            }else
                generateWord();
        }
    }

    private void addPriceWithDollarSign(String word, String nextWord) {
        String priceTerm = "";
        float floatNumber;
        long longNumber;
        int currentCheck = currentWord;
        priceTerm = word.replaceAll("\\$*", "");
        if (nextWord.equals("million")) {
            priceTerm = priceTerm.replaceAll("\\,*", "");
            priceTerm = priceTerm + " M Dollars";
            currentWord = currentWord+2;
        } else if (nextWord.equals("billion")) {
            priceTerm = priceTerm.replaceAll("\\,*", "");
            priceTerm = priceTerm + "000" + " M Dollars";
            currentWord = currentWord+2;
        } else {
            if (priceTerm.contains(".")) {
                floatNumber = Float.parseFloat(priceTerm.replaceAll("\\,*", ""));
                if (floatNumber >= 1000000) {
                    priceTerm = String.valueOf(floatNumber / 1000000) + " M Dollars";
                } else
                    priceTerm = priceTerm + " Dollars";
            } else {
                longNumber = Long.parseLong(priceTerm.replaceAll("\\,*", ""));
                if (longNumber >= 1000000)
                    priceTerm = String.valueOf(longNumber / 1000000) + " M Dollars";
                else
                    priceTerm = priceTerm + " Dollars";
            }
            currentWord++;
        }
        terms.add(priceTerm);
    }

    private void addPriceWithDollar(String number) {
        number = number.replaceAll("[,]","");
        if(number.contains(".")){
            float price = Float.parseFloat(number);
            String priceWithDollar = "";
            if (price <1000000) {
                priceWithDollar = number + " Dollars";
            } else {
                priceWithDollar = String.valueOf(price/1000000) + " M Dollars";
            }
            terms.add(priceWithDollar);
        }
        else{
            long price = Long.parseLong(number);
            String priceWithDollar = "";
            if (price <1000000) {
                priceWithDollar = number + " Dollars";
            } else {
                priceWithDollar = String.valueOf(price/1000000) + " M Dollars";
            }
            terms.add(priceWithDollar);
        }
    }


    private void addDollarWithFraction(String number, String fraction) {
        String fractionTerm = number + " " + fraction + " Dollars";
        terms.add(fractionTerm);
    }


    private String addPercentNumber(String word) {
        if (word.contains("%"))
            return word;
        else
            return (word+"%");
    }

    private void addLargeNumbers(String word, String nextWord) {
        String termName;
        word = word.replaceAll(",","");
        if (word.contains(".")) {
            float tempoFloat = Float.parseFloat(word);
            termName = String.format("%.3f", tempoFloat);
        } else {
            termName = word;
        }
        if (nextWord.equals("thousand")) {
            termName = termName + "K";
            terms.add(termName);
        } else if (nextWord.equals("million")) {
            termName = termName + "M";
            terms.add(termName);
        } else if (nextWord.equals("billion")) {
            termName = termName + "B";
            terms.add(termName);
        }
    }

    private void addPlainNumber(String number) {
        String formattedNumber = number.replaceAll(",*", "");
        String numberTerm = "";
        long nativeNum;
        if (formattedNumber.contains(".")) {
            if (Float.parseFloat(formattedNumber) < 1000)
                numberTerm = String.valueOf(Float.parseFloat(formattedNumber));
            else if (Float.parseFloat(formattedNumber) < 1000000)
                numberTerm = String.valueOf(Float.parseFloat(formattedNumber) / 1000) + "K";
        } else {
            nativeNum = Long.parseLong(formattedNumber);
            if (nativeNum < 1000)
                numberTerm = String.valueOf(nativeNum);
            else if (nativeNum < 1000000)
                numberTerm = String.valueOf((float)nativeNum / 1000) + "K";
            else if (nativeNum < 1000000000)
                numberTerm = String.valueOf((float)nativeNum / 1000000) + "M";
            else
                numberTerm = String.valueOf((float)nativeNum / 1000000000) + "B";

        }
        terms.add(numberTerm);
    }


    private String addDate(String number, String month) {
        Term date;
        String dateText = "";
        if (number.matches("\\d{1}")) {
            dateText = months.get(month) + "-0" + number;
        } else if (number.matches("\\d{2}")) {
            dateText = months.get(month) + "-" + number;
        } else if (number.matches("\\d{4}")) {
            dateText = number + "-" + months.get(month);
        }
        return dateText;
    }


}
