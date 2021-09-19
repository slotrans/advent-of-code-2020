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


(defn evaluate [math-seq depth]
    (loop [ token (first math-seq)
          ;, next-token (second math-seq)
          , remaining (rest math-seq)
          , queued-operator nil
          , accum 0
          ]
        (if (nil? token)
            accum
            (condp = token
                "(" (evaluate remaining (inc depth)) ; descend into parenthesized expression
                ")" accum ; return
                "+" (recur (next remaining) (nthrest remaining 2) "+" accum) ; queue addition
                "*" (recur (next remaining) (nthrest remaining 2) "*" accum) ; queue multiplication
                ; else, a digit
                (let [digit (edn/read-string token)]
                    (condp = queued-operator
                        "+" (recur (next remaining) (nthrest remaining 2) nil (+ accum digit))
                        "*" (recur (next remaining) (nthrest remaining 2) nil (* accum digit))
                    )
                )
            )
        )
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


(defn evaluate ; -> result
    ;; 1-arg, for initial calling convenience
    ([math-seq]
        (evaluate math-seq "+" 0)
    )
    ;; 3-arg, for (most) recursive calls
    ([math-seq queued-operator accumulator]
        (let [ token (first math-seq)
             , remaining (rest math-seq)
             ]
            (if (nil? token)
                accumulator
                (condp = token
                    "+" (evaluate remaining "+" accumulator) ; queue addition
                    "*" (evaluate remaining "*" accumulator) ; queue multiplication
                    ")" (throw (AssertionError. "unexpected )")) ; should never encounter a right paren, see below for sub-expression handling
                    "(" (let [ close-paren-pos (find-closing-paren remaining)
                             , inner-expr-tokens (take close-paren-pos remaining)
                             , tokens-after-inner-expr (drop (inc close-paren-pos) remaining)
                             ]
                            (evaluate tokens-after-inner-expr nil (apply-operator queued-operator accumulator (evaluate inner-expr-tokens)))
                        ) ; descend into parenthesized expression
                    ; else, a digit
                    (let [digit (edn/read-string token)]
                        (evaluate remaining nil (apply-operator queued-operator accumulator digit))
                    )
                )
            )
        )
    )
)

(def sample-expressions
    [ {:expr "2 * 3 + (4 * 5)" :expected 26}
    , {:expr "5 + (8 * 3 + 9 + 3 * 4 * 3)" :expected 437}
    , {:expr "5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4))" :expected 12240}
    , {:expr "((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2" :expected 13632}
    ]
)

(doseq [x sample-expressions]
    (let [ {:keys [expr expected]} x
         , actual (evaluate (tokenize-math-string expr))
         ]
        (println (str "expression " expr " should equal " expected ", got " actual))
    )
)

(def input18 (slurp "input18.txt"))
(def p1-answer
    (reduce
        +
        (for [line (str/split-lines input18)]
            (evaluate (tokenize-math-string line))
        )
    )
)
(println "(p1) sum of expressions in input18 is" p1-answer)
