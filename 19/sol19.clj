(ns net.blergh.advent2020
    (:require [clojure.string :as str]
              [clojure.edn :as edn]
    )
)


(def sample-input-1
"0: 1 2
1: \"a\"
2: 1 3 | 3 1
3: \"b\"")


(def sample-input-2
"0: 4 1 5
1: 2 3 | 3 2
2: 4 4 | 5 5
3: 4 5 | 5 4
4: \"a\"
5: \"b\"

ababbb
bababa
abbbab
aaabbb
aaaabbb")

(def sample-rules-1
    { 0 {:sequence [1 2]}
    , 1 {:literal "a"}
    , 2 {:alternatives [ {:sequence [1 3]} 
                       , {:sequence [3 1]}
                       ]
        }
    , 3 {:literal "b"}
    }
)

(def sample-rules-2
    { 0 {:sequence [4 1 5]}
    , 1 {:alternatives [ {:sequence [2 3]}
                       , {:sequence [3 2]}
                       ]
        }
    , 2 {:alternatives [ {:sequence [4 4]}
                       , {:sequence [5 5]}
                       ]
        }
    , 3 {:alternatives [ {:sequence [4 5]}
                       , {:sequence [5 4]}
                       ]
        }
    , 4 {:literal "a"}
    , 5 {:literal "b"}
    }
)


(defn parse-sequence [s]
    (mapv edn/read-string (str/split s #" "))
)

(defn parse-one-rule [s] ; -> {rule-num {:rule-type rule-value}}
    (let [ [rule-num-part value-part] (str/split s #": ")
         , rule-num (edn/read-string rule-num-part)
         ]
        (cond
            (.contains value-part "\"")
                {rule-num {:literal (edn/read-string value-part)}}
            (.contains value-part "|")
                {rule-num {:alternatives (mapv (fn [x] {:sequence (parse-sequence x)}) (str/split value-part #" \| "))}}
            :else
                {rule-num {:sequence (parse-sequence value-part)}}
        )
    )
)


(defn parse-rules [s] ; -> full map of rules, where keys are rule numbers are values are rules
    (let [lines (str/split-lines s)]
        (into
            {}
            (for [line lines]
                (parse-one-rule line)
            )
        )
    )
)


(defn parse-input [s] ; -> [rules message-strings]
    (let [[rule-part message-part] (str/split s #"\n\n")]
        [(parse-rules rule-part) (str/split-lines message-part)]
    )
)


(declare match-rule-sequence) ; forward reference

(defn match [s all-rules rule-num] ; -> sequence of possible remaining strings (suffixes), or [] if there are none
    (let [rule (get all-rules rule-num)]
        ;(println (str "(m) testing " s " against " rule-num ": " rule))
        (cond
            (nil? s)
                []
            (= "" s)
                []
            (:literal rule)
                (if (str/starts-with? s (:literal rule))
                    [(subs s 1)] ; literals are always 1 character in the supplied input
                    []
                )
            (:sequence rule)
                (match-rule-sequence [s] all-rules (:sequence rule))
            (:alternatives rule)
                (let [ alternatives (:alternatives rule)
                     , left (:sequence (first alternatives))
                     , right (:sequence (second alternatives)) ; there are only ever two alternatives...
                     ]
                    (concat 
                        (match-rule-sequence [s] all-rules left) 
                        (match-rule-sequence [s] all-rules right)
                    )
                )
        )
    )
)

; match-rule-sequence used to be the following... reduce makes my head hurt and i'm not sure it even works 
; for the "return a list" case, so forget it.
;(defn match-rule-sequence [s all-rules rule-nums-in-sequence]
;    (reduce
;        (fn [s2 rule-num]
;            (match s2 all-rules (get all-rules rule-num))
;        )
;        s
;        rule-nums-in-sequence
;    )
;)

(defn match-rule-sequence [s-seq all-rules rule-nums-in-sequence] ; -> sequence of possible suffixes, as for `match`
    ;(println (str "(mrs) testing " s-seq " against " rule-nums-in-sequence))
    (if (empty? rule-nums-in-sequence)
        s-seq
        (let [ possible-suffixes (vec (flatten (map #(match % all-rules (first rule-nums-in-sequence)) s-seq)))
             ]
            (if (empty? possible-suffixes)
                []
                (match-rule-sequence possible-suffixes all-rules (rest rule-nums-in-sequence))
            )
        )
    )
)


(defn matches-rule-zero? [s all-rules]
    (.contains (match s all-rules 0) "")
)


(let [[rules messages] (parse-input sample-input-2)]
    (println rules)
    (doseq [m messages]
        (println (str "(sample) message '" m "' follows rules? " (matches-rule-zero? m rules)))
    )
)

(def input19 (slurp "input19.txt"))
(let [ [rules messages] (parse-input input19)
     , p1-answer (count (filter identity (map #(matches-rule-zero? % rules) messages)))
     ]
    (println "(p1) messages that match rule zero:" p1-answer)
)

; Came out to 406 which is wrong (too high, it says)!
; dumb mistake, counting true is done with (filter identity ...) not (filter some ...)
; correct answer is 122


;;; part 2

(def p2-sample-input (slurp "p2-sample-input.txt"))
(let [ [rules messages] (parse-input p2-sample-input)
     , answer (count (filter identity (map #(matches-rule-zero? % rules) messages)))
     ]
    (println "(p2 sample, unpatched) messages that match rule zero:" answer)

    (let [ patched-input (-> p2-sample-input 
                             (str/replace ,,, "8: 42\n" "8: 42 | 42 8\n")
                             (str/replace ,,, "11: 42 31\n" "11: 42 31 | 42 11 31\n"))
         , [rules messages] (parse-input patched-input)
         , answer (count (filter identity (map #(matches-rule-zero? % rules) messages)))
         ]
        ;(println patched-input)
        (println "(p2 sample, patched) messages that match rule zero:" answer)
    )
)


(def patched-input19 
    (-> input19
        (str/replace ,,, "8: 42\n" "8: 42 | 42 8\n")
        (str/replace ,,, "11: 42 31\n" "11: 42 31 | 42 11 31\n")
    )
)
(let [ [rules messages] (parse-input patched-input19)
     , p2-answer (count (filter identity (map #(matches-rule-zero? % rules) messages)))
     ]
    (println "(p2) messages that match rule zero:" p2-answer)
)

; gives 163 which is wrong (too low)
; dang, was hoping it would work unmodified!

; correct answer: 287
; yay!

; Reddit thread: https://www.reddit.com/r/adventofcode/comments/kg1mro/2020_day_19_solutions/
; This comment was key:
;   Eventually, the key was that every OR chain needed to be followed (rather than just 
;   choosing the first one which validated OK), and the remainders of the various chains 
;   needed to be passed through to match against an action (since a different number of 
;   values may have been consumed by different OR paths which were both valid).
;
; I ended up heavily consulting these solutions:
;   http://www.michaelcw.com/programming/2020/12/22/aoc-2020-d19.html (Haskell)
;   https://github.com/mebeim/aoc/blob/master/2020/README.md#day-19---monster-messages (Python)
;   https://github.com/andi0b/advent-of-code-2020/blob/main/src/Day19.cs (C#, actually I didn't get much from this, C# has really changed!)
;
; The Haskell and Python solutions both came with extensive commentary which helped
