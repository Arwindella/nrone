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

cd "C:\devJavaLab\javaLearningLab\SampleApps\target\classes"

start column_values_in.txt

pause

cd "C:\devJavaLab\javaLearningLab\SampleApps\target\classes"

:: Arg 1: 
:: Typ av citat-tecken som värdena ska wrappas med 
:: single quote då "'" 
:: dubbelfnuttar då "\""  OBS \behövs för denna!

:: Arg 2: 
::Efter hur många värden ska line-break sättas in

java com.examples.AddQuotesExcelColumnValues "'"  "4"

:: undrar om det behövs en pause här?
:: 
:: ping ourselves for x seconds (acts like a pause) 
ping -n 2 127.0.0.1 >nul:

cd "C:\devJavaLab\javaLearningLab\SampleApps\target\classes"

start column_values_out.txt
