#### Prompt 1:
> You are tasked to write a checker that validates the parentheses of a LISP code. Write
a program (in Java or JavaScript) which takes in a string as an input and returns true if all the
parentheses in the string are properly closed and nested.

Run node with _parenthesis-checker.js_

#### Prompt 2:
> For the files in CSV format, write a program in a language that makes
sense to you that will read the content of the file and separate enrollees by insurance company in its own
file. Additionally, sort the contents of each file by last and first name (ascending).  Lastly, if there are
duplicate User Ids for the same Insurance Company, then only the record with the highest version should
be included. The following data points are included in the file: 
>- User Id (string)
>- First Name (string)
>- Last Name (string)
>- Version (integer)
>- Insurance Company (string)

Files are in _ProcessCSV\\_. Compile & run _App.java_ with CLI inside _src\\_. Test file uses JUnit w/o other build tools (Maven/Gradle/Spring Boot).

Usage: App <file.csv> [<output_dir>]