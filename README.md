# The Cereal Language

A language interpreted in Java that borrows syntax from Javascript. 

## What it has

- The language is still in the works but currently, all basic operations involving floating-point, integer, boolean, and string types are functional. Cereal has 6 basic types, numbers, booleans, strings, nulls, lists, and functions. functions are callable and list elements can be retrived. 

- Variables can be made using the `var` keyword.

- The language has `if` statements, `while` loops, and `for` loops which can be made in a similar fashion to inline statements in Java and Javascript with the addition of a colon. Parentheses can also be ommited.

    - `if (x > 5): x = 5`
    - `while (x < 5): x += 1`
    - `for (times 5): x`
    - `for (var i = 0; i < 5; i += 1): i`

- Functions can be made using the `function` keyword
    - `function add(a, b) -> a + b`

- The language should mainly be used in the shell as of now but providing the exact path of a file as an argument to the cereal command will run the file.

## What it does not

- There are no classes, objects, or custom types as of yet and I don't know if I plan on adding them. 
- There is no API yet either although I am working on adding built-in functions similar to those in Python. 
- Multiline support still needs to be added which is why it is should really only be used in the shell as of now.

Download the installer [here](https://yashsavalia1.github.io/Website/other-projects/CerealLang/Cereal-Installer.zip)
