(ns net.blergh.advent2020
    (:require [clojure.string :as str]
              [clojure.edn :as edn]
    )
)


;; assumes numbers are always only one digit, which appears to be true in the input
(defn tokenize-math-string [s]
    (str/split 
        (str/replace s " " "")
        #""
    )
)


;; there's probably a fancier way to do this but whatever
(defn apply-operator [op-string x y]
    (condp = op-string
        "+" (+ x y) ; add
        "*" (* x y) ; muliply
    )
)


(defn find-closing-paren [math-seq]
    (loop [ token (first math-seq)
          , remaining (rest math-seq)
          , position 0
          , paren-depth 0
          ]
        (assert (not (nil? token)) "ERROR: matching ) not found")
        (cond
            (and (= ")" token)
                 (= 0 paren-depth)) position ; found it
            (= "(" token)           (recur (first remaining) (rest remaining) (inc position) (inc paren-depth))
            (= ")" token)           (recur (first remaining) (rest remaining) (inc position) (dec paren-depth))
            :else                   (recur (first remaining) (rest remaining) (inc position) paren-depth) 
        )
    )
)


(defn evaluate-p1 ; -> result
    ;; 1-arg, for initial calling convenience
    ([math-seq]
        (evaluate-p1 math-seq "+" 0)
    )
    ;; 3-arg, for (most) recursive calls
    ([math-seq queued-operator accumulator]
        (let [ token (first math-seq)
             , remaining (rest math-seq)
             ]
            (if (nil? token)
                accumulator
                (condp = token
                    "+" (evaluate-p1 remaining "+" accumulator) ; queue addition
                    "*" (evaluate-p1 remaining "*" accumulator) ; queue multiplication
                    ")" (throw (AssertionError. "ERROR: unexpected )")) ; should never encounter a right paren, see below for sub-expression handling
                    "(" (let [ close-paren-pos (find-closing-paren remaining)
                             , inner-expr-tokens (take close-paren-pos remaining)
                             , tokens-after-inner-expr (drop (inc close-paren-pos) remaining)
                             ]
                            (evaluate-p1 tokens-after-inner-expr nil (apply-operator queued-operator accumulator (evaluate-p1 inner-expr-tokens)))
                        ) ; descend into parenthesized expression
                    ; else, a digit
                    (let [digit (edn/read-string token)]
                        (evaluate-p1 remaining nil (apply-operator queued-operator accumulator digit))
                    )
                )
            )
        )
    )
)

(def sample-expressions-p1
    [ {:expr "1 + 2 * 3 + 4 * 5 + 6" :expected 71}
    , {:expr "1 + (2 * 3) + (4 * (5 + 6))" :expected 51}
    , {:expr "2 * 3 + (4 * 5)" :expected 26}
    , {:expr "5 + (8 * 3 + 9 + 3 * 4 * 3)" :expected 437}
    , {:expr "5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4))" :expected 12240}
    , {:expr "((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2" :expected 13632}
    ]
)

(doseq [x sample-expressions-p1]
    (let [ {:keys [expr expected]} x
         , actual (evaluate-p1 (tokenize-math-string expr))
         ]
        (println (str "(p1) expression " expr " should equal " expected ", got " actual))
    )
)

(def input18 (slurp "input18.txt"))
(def p1-answer
    (reduce
        +
        (for [line (str/split-lines input18)]
            (evaluate-p1 (tokenize-math-string line))
        )
    )
)
(println "(p1) sum of expressions in input18 is" p1-answer)


;;; part 2

; pages I read looking for techniques...
;   https://ruslanspivak.com/lsbasi-part5/
;   (and then part 6 for parens)
;     Problem with this is that it's so stateful. The expr(), term(), and factor() methods all take zero args,
;   operating over object state instead of parameters. Then they return a value but also mutate the state of
;   the object. I found it very difficult to mentally trace through what's happening. 
;     I spent some time trying to translate this imperative/iterative/also-kinda-recursive solution to pure
;   functional/recursive with limited success. What I came up with would work for all-+ or all-* but as soon
;   as I mixed them it fell apart.
;
;   I blundered through several attempts to solve this statelessly with no success.
;   I'm sure a solution exists, but I have no clues at this point, I don't find this interesting anymore,
;   so I'm cutting my losses.
;
;   My solution is just a direct copy from the above lessons (part 6). I guess the one thing I got out of it
;   is the experience of writing something overtly stateful in Clojure, using atoms. Oh well.


;; yes this is lame
(defn digit? [s]
    (contains? #{"0" "1" "2" "3" "4" "5" "6" "7" "8" "9"} s)
)


;; destructively mutates our atom-wrapped sequence
;; by replacing it with itself minus its first element
(defn apop! [math-seq-atom]
    (swap! math-seq-atom rest)
)


(declare consume-low) ;; forward reference

(defn consume-digit-or-parens [math-seq-atom] ; -> result
    (let [token (first @math-seq-atom)]
        (cond 
            (digit? token)
                (let [ _ (apop! math-seq-atom)
                     , result (edn/read-string token)
                     ]
                    result  
                )
            (= "(" token)
                (let [ _ (apop! math-seq-atom)
                     , result (consume-low math-seq-atom)
                     , _ (apop! math-seq-atom)
                     ]
                    result
                )
            :else 
                (throw (AssertionError. (str "consume-digit-or-parens: unexpected token " token)))
        )
    )
)


(defn consume-high [math-seq-atom] ; -> result
    (let [ result (consume-digit-or-parens math-seq-atom)
         ]
        (loop [ token (first @math-seq-atom)
              , result result
              ]
            (if (= "+" token)
                (let [ _ (apop! math-seq-atom)
                     , result (+ result (consume-digit-or-parens math-seq-atom))
                     ]
                    (recur (first @math-seq-atom) result)
                )
                result
            )
        )
    )
)


(defn consume-low [math-seq-atom] ; -> result
    (let [ result (consume-high math-seq-atom)
         ]
        (loop [ token (first @math-seq-atom)
              , result result
              ]
            (if (= "*" token)
                (let [ _ (apop! math-seq-atom)
                     , result (* result (consume-high math-seq-atom))
                     ]
                    (recur (first @math-seq-atom) result)
                )
                result
            )
        )
    )
)


(defn evaluate-p2 [math-seq] ; -> result
    (let [math-seq-atom (atom math-seq)]
        (consume-low math-seq-atom)
    )
)



(def sample-expressions-p2
    [ {:expr "2 * 3 + 4 * 5" :expected 70}
    , {:expr "1 + 2 * 3 + 4 * 5 + 6" :expected 231}
    , {:expr "1 + (2 * 3) + (4 * (5 + 6))" :expected 51}
    , {:expr "2 * 3 + (4 * 5)" :expected 46}
    , {:expr "5 + (8 * 3 + 9 + 3 * 4 * 3)" :expected 1445}
    , {:expr "5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4))" :expected 669060}
    , {:expr "((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2" :expected 23340}
    ]
)

(doseq [x sample-expressions-p2]
    (let [ {:keys [expr expected]} x
         , actual (evaluate-p2 (tokenize-math-string expr))
         ]
        (println (str "(p2) expression " expr " should equal " expected ", got " actual))
    )
)

(def p2-answer
    (reduce
        +
        (for [line (str/split-lines input18)]
            (evaluate-p2 (tokenize-math-string line))
        )
    )
)
(println "(p2) sum of expressions in input18 is" p2-answer)
