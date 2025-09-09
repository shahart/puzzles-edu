**Solving General Lattice Puzzles**

M.Sc. thesis (2010) - ![http://www.cs.technion.ac.il/~barequet/theses/tal-s-msc-thesis.pdf](https://github.com/shahart/puzzles-edu/blob/master/doc/tal-s-msc-thesis.pdf)

https://cris.technion.ac.il/en/publications/solving-general-lattice-puzzles-2

In this paper we describe implementations of two general methods for solving puzzles on any structured lattice. We define the puzzle as a graph induced by (finite portion of) the lattice, and apply a back-tracking method for iteratively find all solutions by identifying parts of the puzzle (or transformed versions of them) with subgraphs of the puzzle, such that the entire puzzle graph is covered without overlaps by the graphs of the parts. Alternatively, we reduce the puzzle problem to a submatrix-selection problem, and solve the latter problem by using the "dancing-links" trick of Knuth. A few expediting heuristics are discussed, and experimental results on various lattice puzzles are presented.

https://link.springer.com/chapter/10.1007/978-3-642-14553-7_14

פזלים מסוג הרכבה (ריצוף במרחב סופי) נפתרים לרוב ע"י שימוש בתכונות השבכה שלהם לייצוג; דוגמת מערך דו-מימדי לפזל משבצות דו-ממדי. בדרך כלל, שימוש בייצוג מאפשר התאמה ספציפית לפזל ועל כן שיפור זמן הפתרון. בהרצאה תוצג שיטה לייצוג בעיית הפזל באמצעות גרף ותתי-גרפים (=החלקים). באופן זה ניתן לפתור באופן כללי כל פזל אשר עבר התמרה לייצוג כגרף. הבעייה הופכת לשיכון תתי-גרפים בגרף. כמו-כן יוצגו שני אלגוריתמים כלליים מסוג נסיגה לאחור (הבעייה הינה NP שלמה): נסיגה לאחור קלסית, ורדוקציה לבעיית exact cover. שיטה זו תוצג בתוספת היוריסטיקה לצמצום מרחב החיפוש, הניתנת למימוש יעיל באמצעות dancing links – עדכון הקישורים ברשימה מקושרת עם אפשרות ל-undo יעיל. כן יתוארו שיטות למציאת הפתרונות היחודיים בלבד, לחילול אוריינטציות החלקים, ולצמצום מרחב החיפוש. כל השיטות נבחנו על שבכות שונות: משבצות, כדורים, משושים, טנגרם וכן שימוש במטריצת התאמות בין החלקים (מנעולים ומפתחות).

<br/>

[![Java CI with Gradle](https://github.com/shahart/puzzles-edu/actions/workflows/gradle.yml/badge.svg)](https://github.com/shahart/puzzles-edu/actions/workflows/gradle.yml)

[![pages-build-deployment](https://github.com/shahart/puzzles-edu/actions/workflows/pages/pages-build-deployment/badge.svg)](https://github.com/shahart/puzzles-edu/actions/workflows/pages/pages-build-deployment)


