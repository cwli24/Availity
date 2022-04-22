/* Coding exercise: You are tasked to write a checker that validates the parentheses of a LISP code. Write
a program (in Java or JavaScript) which takes in a string as an input and returns true if all the
parentheses in the string are properly closed and nested.
*/
const readline = require("readline");
const rl = readline.createInterface({
    input: process.stdin, output: process.stdout,
});

let on_input = function(input_string) {
    let opening_parenthesis = 0;
    for (let char of input_string){
        if (char === '(')
            opening_parenthesis += 1;
        else if (char === ')')
            opening_parenthesis -= 1;
        
        /*  For parenthesis to be properly closed, every one opened must be matched with a close to form a pair '(' and ')'.
            The number of opened is positive at any time, and must count back down to 0 for the entire string to be proper.
            If the count flips negative at any time, there are more ')' than '(' which doesn't make sense, so we're done. */
        if (opening_parenthesis < 0)
            break;
    }

    console.log("Parenthesis all properly closed...", opening_parenthesis == 0);
    rl.close();
}
rl.question("Enter the string of code to check:\n", on_input);