1. Lines of Code (LOC - Project metric): Total lines of code in the selected scope. Only counts non-blank and noncomment lines inside method bodies.

2. Number of Methods (METH - Project metric): Total number of methods defined in the selected scope.

3. Number of Static Methods (NSM): Total number of static methods in the selected scope.
NOTA: O plugin que usei não tem esta métrica mas a "Number of Methods" inclui os static.

4. Number of Classes (C - Project metric): Total number of classes in the selected scope

5. Number of Packages (P - Project metric): Total number of packages in the selected scope.

6. Method Lines of Code (LOC - Method metric): Total number of lines of code inside method bodies, excluding
blank lines and comments.

7. McCabe Cyclomatic Complexity (VG - Method metric): Counts the number of flows through a piece of code.
Each time a branch occurs (if, for, while, do, case, catch and the ?: ternary operator, as well as
the && and || conditional logic operators in expressions) this metric is incremented by one.
Calculated for methods only. For a full treatment of this metric see McCabe.
NOTA: No nosso caso chama-se só "Cyclomatic complexity".

8. Number of Parameters (NP - Method metric): Total number of parameters in the selected scope.

9. Weighted Methods per Class (WMC - Class metric): Sum of the McCabe Cyclomatic Complexity for all
methods in a class.

10. Number of Static Attributes (CSA e ISA - Class e Interface metrics): Total number of static attributes in the selected scope.
NOTA: O plugin que usei não tem esta métrica mas tem as "Class size (attributes)" e "Interface size (attributes)" que incluem os static.

11. Afferent Coupling (CA - Package metric): The number of classes outside a package that depend on classes inside
the package.

12. Normalized Distance (D - Package metric): | RMA + RMI - 1 |, this number should be small, close to zero for
good packaging design.
NOTA: No nosso caso chama-se "Distance from the main sequence".

13. Specialization Index (NOOC - Class metric): Average of the specialization index, defined as NORM * DIT / NOM.
This is a class level metric
NOTA: No nosso caso chama-se "Number of operations overriden".

14. Instability (I - Package metric): CE / (CA + CE)

15. Number of Attributes (CSA e ISA - Class e Interface metrics): Total number of attributes in the selected scope.
NOTA: O plugin que usei não tem esta métrica mas tem as "Class size (attributes)" e "Interface size (attributes)" que fazem o mesmo.

16. Number of Overridden Methods (OVER - Method metric): Total number of methods in the selected scope
that are overridden from an ancestor class.
NOTA: No nosso caso chama-se "Number of overriding methods".

17. Nested Block Depth (NBD - Method metric): The depth of nested blocks of code.

18. Lack of Cohesion of Methods (LCOM - Class metric): A measure for the Cohesiveness of a class. Calculated
with the Henderson-Sellers method: If m(A) is the number of methods accessing an attribute
A, calculate the average of m(A) for all attributes, subtract the number of methods m and
divide the result by (1-m). A low value indicates a cohesive class and a value close to 1 indicates
a lack of cohesion and suggests the class might better be split into a number of (sub)classes.

19. Abstractness (A - Package metric): The number of abstract classes (and interfaces) divided by the total
number of types in a package.

20. Number of Interfaces (IC - Project metric): Total number of interfaces in the selected scope.

21. Efferent Coupling (CE - Package metric): The number of classes inside a package that depend on classes outside
the package.

22. Number of Children (NOC - Class metric): Total number of direct subclasses of a class.

23. Depth of Inheritance Tree (DIT - Class metric): Distance from class Object in inheritance hierarchy.