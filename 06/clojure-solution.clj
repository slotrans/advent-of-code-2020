; Each group's answers are separated by a blank line, and within each group, each person's answers are on a single line. For example:
; 
; abc
; 
; a
; b
; c
; 
; ab
; ac
; 
; a
; a
; a
; a
; 
; b
; 
; This list represents answers from five groups:
; 
;     The first group contains one person who answered "yes" to 3 questions: a, b, and c.
;     The second group contains three people; combined, they answered "yes" to 3 questions: a, b, and c.
;     The third group contains two people; combined, they answered "yes" to 3 questions: a, b, and c.
;     The fourth group contains four people; combined, they answered "yes" to only 1 question, a.
;     The last group contains one person who answered "yes" to only 1 question, b.
; 
; In this example, the sum of these counts is 3 + 3 + 3 + 1 + 1 = 11.
; 
; For each group, count the number of questions to which anyone answered "yes". What is the sum of those counts?

(ns net.blergh.advent2020
    (:require [clojure.string :as str]
    )
)

(def input06 (slurp "input06"))

(def input-groups
    (str/split input06 #"\n\n")
)

(defn count-yes-in-group [grp]
    (count (set (vec (str/replace grp "\n" ""))))
)

; quick test
(comment

(def example-input "abc

a
b
c

ab
ac

a
a
a
a

b
")
(def example-input-groups
    (str/split example-input #"\n\n")
)
(println (apply + (map count-yes-in-group example-input-groups)))

)


(def part1-answer
    (apply + (map count-yes-in-group input-groups))
)

(println (str "part 1: " part1-answer))
;answer=6809


;As you finish the last group's customs declaration, 
;you notice that you misread one word in the instructions:
;
;You don't need to identify the questions to which _anyone_ answered "yes"; 
;you need to identify the questions to which *EVERYONE* answered "yes"!

