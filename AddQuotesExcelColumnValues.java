package com.examples;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/*

Latest:
no possible:
java com.examples.AddQuotesExcelColumnValues "'" "'" "4"
or
java com.examples.AddQuotesExcelColumnValues "INSERT" "');" "4"

What
 
Format Excel-column values so can put them, for example, in an sql in-clause
That is, wrapp them in quote signs and add comma sign at end and line break after every fourth(default) line
Exemple: cut values from a an Excel row eg: val1	val2 	val3
and put them in the in-file then the out-file will have 'val1', 'val2', 'val3'  
You can put them in select * from t where val in('val1', 'val2', 'val3').

Precondition
 
Här:
C:\devJavaLab\javaLearningLab\SampleApps\src\main\resources\
måste du, första gången, ha column_values_in.txt och column_values_out.txt !
För när man kör mvn clean install(dvs under byggsteget) då läggs kopior av dom till:
C:\devJavaLab\javaLearningLab\SampleApps\target\classes 

Kör denna java-klass i Netbeans(shift + F6) så att den blir ny-kompilerad 


Execution

Kör denna klass med hjälp av:
 C:\Users\hannukselar\excelColumValuesAddQuotes.bat

INFILEN (OBS dos-scriptet visar upp infilen i aktuell default texteditor .txt)
Klistra in tex Excel-kolumnvärdena här:
C:\dev\javaLearningLab\SampleApps\target\classes
i column_values_in.txt


OUTFILEN (OBS dos-scriptet visar upp utfilen!)
de formaterade raderna finns här:
C:\devJavaLab\javaLearningLab\SampleApps\target\classes
i column_values_out.txt 


excelColumValuesAddQuotes.bat contains:


@echo off 

echo.
echo ******This bat-file runs**********
echo %~dpnx0  
echo Right bat-file?
echo **********************************
echo.
echo Format Excel-column values so can put them in sql in-clause
echo That is, wrapp them in quote signs and add comma sign at end
echo Infile:  column_values_in.txt 
echo Outfile: column_values_out.txt 
echo.

cd "C:\dev\javaLearningLab\SampleApps\target\classes"

start column_values_in.txt

pause

cd "C:\dev\javaLearningLab\SampleApps\target\classes"

:: Arg 1: 
:: Typ av citat-tecken som värdena ska wrappas med 
:: single quote då "'" 
:: dubbelfnuttar då "\""  OBS \behövs för denna!

:: Arg 2: 
::Efter hur många värden ska line-break sättas in

java com.examples.AddQuotesExcelColumnValues "'" "'" "4"

:: undrar om det behövs en pause här?
:: 
:: ping ourselves for x seconds (acts like a pause) 
ping -n 2 127.0.0.1 >nul:

cd "C:\dev\javaLearningLab\SampleApps\target\classes"

start column_values_out.txt

 */
public class AddQuotesExcelColumnValues {

    public static void main(String[] args) throws IOException {

        AddQuotesExcelColumnValues n = new AddQuotesExcelColumnValues();;

        try {

            n.myMain(args[0], args[1], args[2]);
        } catch (ArrayIndexOutOfBoundsException e) {

            n.myMain("AA", "BB", ""); // om man kör utan argument, tex utan att köra igång via .bat-filen
        }
    }

    public void myMain(String qSign, String qSign2, String lineBreak) throws FileNotFoundException, IOException {

        // PRE-CONFIGURATION (default values) 
        // Quote sign to wrap the column values with ( "" or '' is your choice to make)
        String quoteSign = "\'"; // "\""
        String quoteSign2 = "\'"; // "\""

        // After how many values/rows should line break be added
        int modulusDenominator = 4; // denominator = nämnare, 4 = radbryt efter var fjärde kolumnvärde

        if (!qSign.isEmpty()) {
            quoteSign = qSign;
        }
        if (!qSign2.isEmpty()) {
            quoteSign2 = qSign2;
        }
        if (!lineBreak.isEmpty()) {
            modulusDenominator = Integer.valueOf(lineBreak);
        }

        String fileI = "column_values_in.txt"; // här i klistrar jag manuellt in kolumnvärdena
        String fileO = "column_values_out.txt"; // här i placeras de "formaterade" kolumnvärdena

        ClassLoader classLoader = new AddQuotesExcelColumnValues().getClass().getClassLoader();

//        Om ska funka ha appen i ihop packad .jar-fil, då lär du ha typs så här:
//        InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(fileI);
//        BufferedReader bf = new BufferedReader(new InputStreamReader(in));
// för att appen ska hitta .txt-filerna

        File fileIn = new File(classLoader.getResource(fileI).getFile()); // npiq_list_in.txt skapas pga new skapas en ny tom

        File fileOut = new File(classLoader.getResource(fileO).getFile());

        StringBuilder sb = new StringBuilder();

        Path pathToFile = fileIn.toPath();

        List<String> rows = null;

        List<String> formatedList = new ArrayList<>(); // nya listan

        try (BufferedReader br = Files.newBufferedReader(pathToFile, StandardCharsets.UTF_8)) { // StandardCharsets.US_ASCII

            rows = new ArrayList<>();
            String formatedRow = "";
            String line;

            // läs in alla rader så du har dom i en ArrayList med namn rows, lättare att jobba med dom då inbillar jag mig
            while ((line = br.readLine()) != null) {

                line = line.trim();

                if (!line.equals("")) { // bara om vi har något annat än tom rad

                    rows.add(line);
                }
            }

            // Vi går igenom Array-listan av kolumn-rader och sätter dit "
            for (String row : rows) {

                if (isCurrentRowLastRow(rows, row)) {

                    formatedRow = quoteSign + row.replace("\n\r", "") + quoteSign2;
                    formatedList.add(formatedRow);
                    break;
                } else {

                    formatedRow = quoteSign + row.replace("\n\r", "") + quoteSign2 + ",";
                    formatedList.add(formatedRow);
                    continue;
                }

            } // for-loop ends
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }

        putInSb(formatedList, sb, modulusDenominator); // dvs nu måste du bygga upp sb

//        System.out.println(sb);
        try (BufferedWriter bwr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileOut.getAbsoluteFile(), false), StandardCharsets.UTF_8))) {
//        try (BufferedWriter bwr = new BufferedWriter(new FileWriter(fileOut.getAbsoluteFile(), false))) {

//          write contents of StringBuffer to a file
//          Har fixat så det nuvarande output-fil-innehållet skrivs över, det är false ovan som fixar det
            bwr.write(String.valueOf(sb));

            bwr.flush();
//            bwr.close(); 
        }
        System.out.println("Content of " + fileI + " was successfully written to " + fileO);
    }

    private boolean isCurrentRowLastRow(List rows, String row) {

        return rows.indexOf(row) == rows.size() - 1;
    }

    private void putInSb(List<String> formatedList, StringBuilder sb, int modulusDenominator) {

        int i = 0;

        for (String item : formatedList) {

            i++;
            if (i % modulusDenominator == 0) { // sätt dit \n\r endast på fjärde kolumn-värdet

                sb.append(item).append(System.getProperty("line.separator")); // tror är operativsystems obunden!
            } else {
                sb.append(item);
            }
        }
    }
}
